package com.balckbuffalos.familiesshareextended;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
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
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
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
    private String group_id, token, user_id, activity_id;
    private SwitchMaterial switch_activity_partecipate;
    private ActivitiesGroupFragment activitiesGroupFragment = new ActivitiesGroupFragment();
    private Bundle bundle = new Bundle();
    private boolean will_partecipate;

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

        switch_activity_partecipate= findViewById(R.id.switch_activity_info_partecipate);
        switch_activity_partecipate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                will_partecipate = isChecked;
            }
        });

        activityInfo(token,group_id,user_id,activity_id);
    }

    @SuppressLint("SetTextI18n")
    private void activityInfo(String token, String group_id, String user_id, String activity_id) {
        compositeDisposable.add(myAPI.activityInfo(token, group_id, activity_id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONObject obj = new JSONObject(s);
                    TextView activityName = findViewById(R.id.activity_info_name);
                    TextView activityGroup = findViewById(R.id.activity_info_group_name);
                    TextView activityDescription = findViewById(R.id.activity_info_description);
                    TextView activityLocation = findViewById(R.id.activity_info_location);
                    TextView activityGPText = findViewById(R.id.activity_info_green_pass_text);
                    ImageView green_pass_icon = findViewById(R.id.activity_info_green_pass_icon);

                    activityName.setText(obj.getString("name"));
                    activityGroup.setText(obj.getString("group_name"));
                    activityDescription.setText(obj.getString("description"));
                    activityLocation.setText(obj.getString("location"));

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
                    //Date maxDate = null;
                    //String insertDate = "";
                    //JSONObject prop = null;
                    Log.d("AODNAOIFNAIO","AOFIAOF" + " " + s.toString() +" \n\n" + arr.length());

                    JSONObject event = arr.getJSONObject(0);
                    String startDateTime = event.getJSONObject("start").getString("dateTime");
                    String endDateTime = event.getJSONObject("end").getString("dateTime");
                    String startDay = startDateTime.substring(8,10);
                    String startMonth = startDateTime.substring(5,7);
                    String startTime = startDateTime.substring(11,13) + ":" + startDateTime.substring(14,16);
                    String endDay = endDateTime.substring(8,10);
                    String endMonth = endDateTime.substring(5,7);
                    String endTime = endDateTime.substring(11,13) + ":" + endDateTime.substring(14,16);

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
                    /*for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        String date = obj.getJSONObject("start").getString("dateTime") + obj.getJSONObject("end").getString("dateTime");

                        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALY);
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALY);
                        Date myDate = dateFormat.parse(date.substring(28, 30) + "/" + date.substring(25, 27) + "/" + date.substring(20, 24));
                        assert myDate != null;
                        if ((maxDate == null) || (maxDate.before(myDate))) {
                            maxDate = myDate;
                            insertDate = date;
                            prop = obj.getJSONObject("extendedProperties").getJSONObject("shared");
                        }
                        if ((myDate.after(calendar.getTime())) || (i == arr.length() - 1)) {
                            mActivityId.add(activity_id);
                            mName.add(name);
                            mGreenPass.add(green_pass_is_required);
                            mDate.add(insertDate);
                            mNAdult.add(prop.getString("children").equals("[]") ? 0 : prop.getString("children").split(",").length);
                            mNChildren.add(prop.getString("parents").equals("[]") ? 0 : prop.getString("parents").split(",").length);

                            initActivityRecycler();
                            break;
                        }
                    }*/
                }, t -> Log.d("HTTP REQUEST ERROR ACTIVITYSLOTS: ", t.getMessage()))
        );
    }
}