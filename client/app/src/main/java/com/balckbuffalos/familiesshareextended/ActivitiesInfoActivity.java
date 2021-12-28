package com.balckbuffalos.familiesshareextended;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.balckbuffalos.familiesshareextended.Fragments.ActivitiesGroupFragment;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ActivitiesInfoActivity extends AppCompatActivity {

    private INodeJS myAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String group_id, token, user_id, activity_id, timeslot_id, summary, description, location, start, end;
    private JSONObject extprop;
    private SwitchMaterial switch_activity_partecipate, switch_activity_info_partecipate_child;
    private ActivitiesGroupFragment activitiesGroupFragment = new ActivitiesGroupFragment();
    private Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_info);

        Bundle extras = getIntent().getExtras();
        group_id = extras.getString("group_id");
        activity_id = extras.getString("activity_id");

        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

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

        switch_activity_partecipate = findViewById(R.id.switch_activity_info_partecipate);
        switch_activity_partecipate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    JSONObject prop = extprop.getJSONObject("shared");
                    Log.d("LEGGO PROP","PROP : " + prop.toString());
                    String[] oldParents = prop.getString("parents").split(",");

                    if (isChecked) {
                        if (!contains(oldParents, user_id)) {
                            String[] newParentsArr = add(oldParents, user_id);
                            String newParents = "[" + String.join(",", newParentsArr) + "]";
                            prop.put("parents", newParents);
                            Log.d("LEGGO newParents","newParents : " + newParents);
                            Log.d("LEGGO extprop","extprop : " + extprop.toString());
                            editPartecipants(token, group_id, activity_id, timeslot_id, user_id, false, summary, description, location, start, end, extprop, false);
                        }
                    } else {
                        if (contains(oldParents, user_id)) {
                            String[] newParentsArr = remove(oldParents, user_id);
                            String newParents = "[" + String.join(",", newParentsArr) + "]";
                            prop.put("parents", newParents);
                            Log.d("LEGGO newParents","newParents : " + newParents);
                            Log.d("LEGGO extprop","extprop : " + extprop.toString());
                            editPartecipants(token, group_id, activity_id, timeslot_id, user_id, false, summary, description, location, start, end, extprop, false);
                        }
                    }
                } catch (JSONException e) { e.printStackTrace(); }
            }
        });

        switch_activity_info_partecipate_child = findViewById(R.id.switch_activity_info_partecipate_child);
        switch_activity_info_partecipate_child.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                compositeDisposable.add(myAPI.getChildren(token, user_id, user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(s -> {
                            Log.d("LEGGO s","s : " + s.toString());
                            /* For our application, we assume that every parent always has one child */
                            JSONArray children = new JSONArray(s);
                            Log.d("LEGGO CHILDREN","CHILDREN : " + children.toString());
                            JSONObject child = children.getJSONObject(0);
                            Log.d("LEGGO child","child : " + child.toString());

                            String child_id = child.getString("child_id");

                            editChildrenPartecipants(child_id, isChecked);
                        }, t -> Log.d("HTTP REQUEST ERROR getChildren: ", t.getMessage()))
                );
            }
        });

        activityInfo(token,group_id,user_id,activity_id);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void editChildrenPartecipants(String child_id, boolean isChecked) {
        try {
            JSONObject prop = extprop.getJSONObject("shared");
            Log.d("LEGGO prop2","prop2 : " + prop.toString());
            String[] oldChildren = prop.getString("children").split(",");

            if (isChecked) {
                if (!contains(oldChildren, child_id)) {
                    String[] newChildrenArr = add(oldChildren, child_id);
                    String newChildren = "[" + String.join(",", newChildrenArr) + "]";
                    prop.put("children", newChildren);
                    editPartecipants(token, group_id, activity_id, timeslot_id, user_id, false, summary, description, location, start, end, extprop, false);
                }
            } else {
                if (contains(oldChildren, child_id)) {
                    String[] newChildrenArr = remove(oldChildren, child_id);
                    String newChildren = "[" + String.join(",", newChildrenArr) + "]";
                    prop.put("children", newChildren);
                    editPartecipants(token, group_id, activity_id, timeslot_id, user_id, false, summary, description, location, start, end, extprop, false);
                }
            }
        } catch (JSONException e) { e.printStackTrace(); }
    }

    private boolean contains(String[] v, String id) {
        for (String s : v) {
            if (s.equals(id)) {
                return true;
            }
        }
        return false;
    }

    private String[] remove(String[] v, String id) {
        String[] res = new String[v.length-1];
        int j = 0;
        for (String s : v) {
            if (!s.equals(id)) {
                res[j] = s;
                j++;
            }
        }
        return res;
    }

    private String[] add(String[] v, String id) {
        String[] res = new String[v.length];
        int j = 0;
        for (int i = 0; i < v.length; i++) {
            if (!(v[i].equals("") || v[i].equals(" ") || v[i].equals("[]"))){
                res[j] = v[i];
                j++;
            }
        }
        res[res.length-1] = id;
        Log.d("LEGGO res","res : " + Arrays.toString(res));
        return res;
    }

    private void editPartecipants(String token, String group_id, String activity_id, String timeslot_id, String user_id, Boolean adminChanges, String summary, String description, String location, String start, String end, JSONObject extprop, Boolean notifyUsers) {
        compositeDisposable.add(myAPI.editPartecipants(token, group_id, activity_id, timeslot_id, user_id,adminChanges, summary, description, location, start, end, extprop, notifyUsers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {

                }, t -> Log.d("HTTP REQUEST ERROR addPartecipantParent: ", t.getMessage()))
        );
    }

    @SuppressLint("SetTextI18n")
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

                    if ((obj.getBoolean("greenpass_isrequired"))) {
                        green_pass_icon.setVisibility(View.VISIBLE);
                        activityGPText.setText("GREEN PASS is mandatory");
                    } else {
                        green_pass_icon.setVisibility(View.INVISIBLE);
                        activityGPText.setText("GREEN PASS is NOT mandatory");
                    }

                    timeslotsActivity(token, group_id, activity_id, user_id);
                }, t -> Log.d("HTTP REQUEST ERROR ACTIVITYINFO: ", t.getMessage()))
        );
    }

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
                    String nChildren = prop.getString("children").equals("[]") ? "0" : String.valueOf(prop.getString("children").split(",").length);
                    String nAdults = prop.getString("parents").equals("[]") ? "0" : String.valueOf(prop.getString("parents").split(",").length);

                    TextView tv_startDay = findViewById(R.id.activity_info_day_text);
                    TextView tv_startMonth = findViewById(R.id.month_text);
                    TextView tv_endDay = findViewById(R.id.activity_info_day_text2);
                    TextView tv_endMonth = findViewById(R.id.month_text2);
                    TextView tv_startTime = findViewById(R.id.activity_info_hours_text);
                    TextView tv_endTime = findViewById(R.id.activity_info_hours_text2);
                    TextView tv_nAdults = findViewById(R.id.activity_info_n_adult_text);
                    TextView tv_nChildren = findViewById(R.id.activity_info_n_children_text);

                    tv_startDay.setText(startDay);
                    tv_startMonth.setText(startMonth);
                    tv_endDay.setText(endDay);
                    tv_endMonth.setText(endMonth);
                    tv_startTime.setText(startTime);
                    tv_endTime.setText(endTime);
                    tv_nAdults.setText(nAdults);
                    tv_nChildren.setText(nChildren);
                }, t -> Log.d("HTTP REQUEST ERROR ACTIVITYSLOTS: ", t.getMessage()))
        );
    }
}