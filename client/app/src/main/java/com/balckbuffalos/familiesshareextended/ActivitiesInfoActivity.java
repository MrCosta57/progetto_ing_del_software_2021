package com.balckbuffalos.familiesshareextended;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.balckbuffalos.familiesshareextended.Fragments.ActivitiesGroupFragment;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
       /* compositeDisposable.add(myAPI.timeslotsActivity(token, group_id, activity_id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONArray arr = new JSONArray(s);
                    Date maxDate = null;
                    String insertDate = "";
                    JSONObject prop = null;

                    for (int i = 0; i < arr.length(); i++) {
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
                    }
                }, t -> Log.d("HTTP REQUEST ERROR ACTIVITYSLOTS: ", t.getMessage()))
        );*/
    }
}