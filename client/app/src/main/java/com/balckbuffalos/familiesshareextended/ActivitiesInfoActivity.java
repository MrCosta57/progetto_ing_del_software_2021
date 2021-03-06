package com.balckbuffalos.familiesshareextended;

import static com.balckbuffalos.familiesshareextended.Utility.Utility.showMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ActivitiesInfoActivity extends AppCompatActivity {

    private INodeJS myAPI;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String group_id, token, user_id, child_id = "", activity_id, timeslot_id, summary, description, location, start, end, nChildren, nAdults;
    private JSONObject extprop;
    private SwitchMaterial switch_activity_partecipate, switch_activity_info_partecipate_child;
    private TextView tv_nAdults, tv_nChildren;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_info);

        // Gets the group_id and activity_id fields from the caller (previous) screen
        Bundle extras = getIntent().getExtras();
        group_id = extras.getString("group_id");
        activity_id = extras.getString("activity_id");

        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setOnClickListener (v-> showMenu(v, R.menu.top_app_bar, this, getApplicationContext()));

        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    "secret_shared_prefs",
                    masterKeyAlias,
                    getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            token = sharedPreferences.getString("token", "none");
            user_id = sharedPreferences.getString("user_id", "none");
        } catch (GeneralSecurityException | IOException e) { e.printStackTrace(); }

        // Calls the function that will get the user's child's id and start the population of the fields inside the screen
        getChildId(token, user_id);

        // Sets all the listeners needed to catch that will be made by the user on the default values
        switch_activity_partecipate = findViewById(R.id.switch_activity_info_partecipate);
        switch_activity_partecipate.setOnCheckedChangeListener((buttonView, isChecked) -> onClickEditPartecipants("parents", user_id, isChecked));

        switch_activity_info_partecipate_child = findViewById(R.id.switch_activity_info_partecipate_child);
        switch_activity_info_partecipate_child.setOnCheckedChangeListener((buttonView, isChecked) -> onClickEditPartecipants("children", child_id, isChecked));
    }

    // Gets the user's child's id and calls the function that populates the fields inside the screen
    private void getChildId(String token, String user_id) {
        compositeDisposable.add(myAPI.getChildren(token, user_id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    // For our application, we assume that every parent always has one child
                    JSONArray children = new JSONArray(s);
                    JSONObject child = children.getJSONObject(0);

                    child_id = child.getString("child_id");

                    activityInfo(token,group_id,user_id,activity_id);
                }, t -> {
                    activityInfo(token,group_id,user_id,activity_id);
                    Log.d("HTTP GET CHILD ID FROM PARENT ["+user_id+"] REQUEST ERROR", t.getMessage());
                })
        );
    }

    // Depending on the source and the isChecked argument, inserts/removes the user or the user's child into/from the participants
    private void onClickEditPartecipants(String source, String id, boolean isChecked) {
        try {
            JSONObject prop = extprop.getJSONObject("shared");
            JSONArray array = new JSONArray(prop.getString(source));
            start = start.substring(0,16) + ":00:000Z";
            end = end.substring(0,16) + ":00:000Z";

            if (isChecked) {
                if (!contains(array, id)) {
                    if (source.equals("parents")) {
                        nAdults = String.valueOf(Integer.parseInt(nAdults) + 1);
                        tv_nAdults.setText(nAdults);
                    } else {
                        nChildren = String.valueOf(Integer.parseInt(nChildren) + 1);
                        tv_nChildren.setText(nChildren);
                    }
                    array.put(id);
                    prop.put(source, array.toString());
                    editPartecipants(token, group_id, activity_id, timeslot_id, user_id, summary, description, location, start, end, extprop);
                }
            } else {
                if (contains(array, id)) {
                    if (source.equals("parents")) {
                        nAdults = String.valueOf(Integer.parseInt(nAdults) - 1);
                        tv_nAdults.setText(nAdults);
                    } else {
                        nChildren = String.valueOf(Integer.parseInt(nChildren) - 1);
                        tv_nChildren.setText(nChildren);
                    }
                    int index = findIndex(array, id);
                    array.remove(index);
                    prop.put(source, array.toString());
                    editPartecipants(token, group_id, activity_id, timeslot_id, user_id, summary, description, location, start, end, extprop);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Function used to check if an id is contained inside an array
    private boolean contains(JSONArray v, String id) throws JSONException {
        for (int i = 0; i < v.length(); i++) {
            if (v.getString(i).equals(id)) {
                return true;
            }
        }
        return false;
    }

    // Function used to find the index of an id inside an array
    private int findIndex(JSONArray v, String id) throws JSONException {
        for (int i = 0; i < v.length(); i++) {
            if (v.getString(i).equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private void editPartecipants(String token, String group_id, String activity_id, String timeslot_id, String user_id, String summary, String description, String location, String start, String end, JSONObject extprop) {
        compositeDisposable.add(myAPI.editPartecipants(token, group_id, activity_id, timeslot_id, user_id, false, summary, description, location, start, end, extprop, false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> { }, t -> Log.d("HTTP PATCH PARTECIPANTS OF ACTIVITY ["+activity_id+"] REQUEST ERROR", t.getMessage()))
        );
    }

    @SuppressLint("SetTextI18n")
    // Shows the activity info that will be displayed in the top half of the screen
    private void activityInfo(String token, String group_id, String user_id, String activity_id) {
        compositeDisposable.add(myAPI.activityInfo(token, group_id, activity_id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONObject obj = new JSONObject(s);

                    description = obj.getString("description");
                    location = obj.getString("location");

                    TextView activityName = findViewById(R.id.activity_info_name);
                    TextView activityGroup = findViewById(R.id.activity_info_group_name);
                    TextView activityDescription = findViewById(R.id.activity_info_description);
                    TextView activityLocation = findViewById(R.id.activity_info_location);
                    TextView activityGPText = findViewById(R.id.activity_info_green_pass_text);
                    ImageView green_pass_icon = findViewById(R.id.activity_info_green_pass_icon);

                    activityName.setText(obj.getString("name"));
                    activityGroup.setText(obj.getString("group_name"));
                    activityDescription.setText(description);
                    activityLocation.setText(location);

                    // If the GREEN PASS certification is required, then it displays the "!" icon
                    if ((obj.getBoolean("greenpass_isrequired"))) {
                        green_pass_icon.setVisibility(View.VISIBLE);
                        activityGPText.setText("GREEN PASS is mandatory");
                    } else {
                        green_pass_icon.setVisibility(View.INVISIBLE);
                        activityGPText.setText("GREEN PASS is NOT mandatory");
                    }

                    GradientDrawable background = (GradientDrawable) findViewById(R.id.powerCircle).getBackground();
                    background.setColor(Color.parseColor(obj.getString("color")));


                    timeslotsActivity(token, group_id, activity_id, user_id);
                }, t -> Log.d("HTTP GET ACTIVITIES FROM GROUPS ["+group_id+"] REQUEST ERROR", t.getMessage()))
        );
    }

    // Shows the activity info that will be taken from the timeslot and that will be displayed in the bottom half of the screen
    private void timeslotsActivity(String token, String group_id, String activity_id, String user_id) {
        compositeDisposable.add(myAPI.timeslotsActivity(token, group_id, activity_id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    /* Since we generate only one event when creating an activity, we can assume that s always contains a single element */
                    JSONArray arr = new JSONArray(s);

                    JSONObject event = arr.getJSONObject(0);

                    timeslot_id = event.getString("id");
                    summary = event.getString("summary");
                    start = event.getJSONObject("start").getString("dateTime");
                    end = event.getJSONObject("end").getString("dateTime");

                    String startDateTime = start;
                    String endDateTime = end;
                    String startDay = startDateTime.substring(8,10);
                    String startMonth = startDateTime.substring(5,7);
                    String startTime = startDateTime.substring(11,13) + ":" + startDateTime.substring(14,16);
                    String endDay = endDateTime.substring(8,10);
                    String endMonth = endDateTime.substring(5,7);
                    String endTime = endDateTime.substring(11,13) + ":" + endDateTime.substring(14,16);

                    extprop = event.getJSONObject("extendedProperties");
                    JSONObject prop = event.getJSONObject("extendedProperties").getJSONObject("shared");
                    nChildren = prop.getString("children").equals("[]") ? "0" : String.valueOf(prop.getString("children").split(",").length);
                    nAdults = prop.getString("parents").equals("[]") ? "0" : String.valueOf(prop.getString("parents").split(",").length);

                    switch_activity_partecipate = findViewById(R.id.switch_activity_info_partecipate);
                    JSONArray oldParents = new JSONArray(prop.getString("parents"));
                    if (contains(oldParents,user_id)) {
                        switch_activity_partecipate.setChecked(true);
                    }

                    switch_activity_info_partecipate_child = findViewById(R.id.switch_activity_info_partecipate_child);
                    if (!child_id.equals("")) {
                        switch_activity_info_partecipate_child.setVisibility(View.VISIBLE);
                        JSONArray oldChildren = new JSONArray(prop.getString("children"));
                        if (contains(oldChildren, child_id)) {
                            switch_activity_info_partecipate_child.setChecked(true);
                        }
                    } else {
                        switch_activity_info_partecipate_child.setVisibility(View.INVISIBLE);
                    }

                    TextView tv_startDay = findViewById(R.id.activity_info_day_text);
                    TextView tv_startMonth = findViewById(R.id.month_text);
                    TextView tv_endDay = findViewById(R.id.activity_info_day_text2);
                    TextView tv_endMonth = findViewById(R.id.month_text2);
                    TextView tv_startTime = findViewById(R.id.activity_info_hours_text);
                    TextView tv_endTime = findViewById(R.id.activity_info_hours_text2);
                    tv_nAdults = findViewById(R.id.activity_info_n_adult_text);
                    tv_nChildren = findViewById(R.id.activity_info_n_children_text);

                    tv_startDay.setText(startDay);
                    tv_startMonth.setText(startMonth);
                    tv_endDay.setText(endDay);
                    tv_endMonth.setText(endMonth);
                    tv_startTime.setText(startTime);
                    tv_endTime.setText(endTime);
                    tv_nAdults.setText(nAdults);
                    tv_nChildren.setText(nChildren);

                }, t -> Log.d("HTTP GET TIMESLOTS FROM ACTIVITY ["+activity_id+"] REQUEST ERROR", t.getMessage()))
        );
    }
}