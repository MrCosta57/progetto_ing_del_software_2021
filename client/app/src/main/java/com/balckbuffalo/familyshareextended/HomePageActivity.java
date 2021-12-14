package com.balckbuffalo.familyshareextended;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.balckbuffalo.familyshareextended.Adapters.ActivityRecycleAdapter;
import com.balckbuffalo.familyshareextended.Adapters.GroupRecycleAdapter;
import com.balckbuffalo.familyshareextended.Retrofit.INodeJS;
import com.balckbuffalo.familyshareextended.Retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class HomePageActivity extends AppCompatActivity {

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final ArrayList<String> mGroupName = new ArrayList<>();
    private final ArrayList<String> mMembers = new ArrayList<>();
    private final ArrayList<Boolean> mVisible = new ArrayList<>();
    private final ArrayList<Boolean> mNotifications = new ArrayList<>();

    private final ArrayList<String> mDate = new ArrayList<>();
    private final ArrayList<String> mName = new ArrayList<>();
    private final ArrayList<Integer> mNAdult = new ArrayList<Integer>();
    private final ArrayList<Integer> mNChildren = new ArrayList<Integer>();
    private final ArrayList<Boolean> mGreenPass = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //Init API
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
            String token = sharedPreferences.getString("token", "none");
            String user_id = sharedPreferences.getString("user_id", "none");
            groupList(token,user_id,user_id);
        } catch (GeneralSecurityException | IOException e) { e.printStackTrace(); }


    }

    private void initGroupRecycler(){
        RecyclerView groupRecyclerView = findViewById(R.id.groupsRecycler);
        GroupRecycleAdapter adapter = new GroupRecycleAdapter(this, mGroupName, mMembers, mVisible, mNotifications);
        groupRecyclerView.addItemDecoration(new DividerItemDecoration(groupRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        groupRecyclerView.setAdapter(adapter);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void initActivityRecycler(){
        RecyclerView activityRecyclerView = findViewById(R.id.activityRecycler);
        ActivityRecycleAdapter adapter = new ActivityRecycleAdapter(this, mDate, mName, mNAdult, mNChildren, mGreenPass);
        activityRecyclerView.addItemDecoration(new DividerItemDecoration(activityRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        activityRecyclerView.setAdapter(adapter);
        activityRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void groupList(String token, String id, String user_id) {
        compositeDisposable.add(myAPI.groupList(token, id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONArray arr = new JSONArray(s);
                    for(int i = 0; i<arr.length();i++)
                    {
                        JSONObject obj = arr.getJSONObject(i);
                        String group_id = obj.getString("group_id");

                        mVisible.add(obj.getBoolean("user_accepted"));
                        mNotifications.add(obj.getBoolean("has_notifications"));

                        groupInfo(token, group_id, user_id);
                        activityList(token,group_id,user_id);
                    }
                }, t -> Toast.makeText(HomePageActivity.this, "ERROR "+t.getMessage(), Toast.LENGTH_LONG).show())
        );
    }

    private void groupInfo(String token, String group_id, String user_id) {
        compositeDisposable.add(myAPI.groupInfo(token, group_id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONObject obj = new JSONObject(s);
                    mGroupName.add(obj.getString("name"));
                    mMembers.add(obj.getString("members"));
                    initGroupRecycler();
                }, t -> Toast.makeText(HomePageActivity.this, "ERROR "+t.getMessage(), Toast.LENGTH_LONG).show())
        );
    }

    private void activityList(String token, String group_id, String user_id) {
        compositeDisposable.add(myAPI.activityList(token, group_id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONArray arr = new JSONArray(s);
                    for(int i = 0; i<arr.length();i++)
                    {
                        JSONObject obj = arr.getJSONObject(i);
                        mName.add(obj.getString("name"));
                        mGreenPass.add(obj.getBoolean("greenpass_isrequired"));

                        timeslotsActivity(token, group_id, obj.getString("activity_id"), user_id);
                    }
                }, t -> Toast.makeText(HomePageActivity.this, "ERROR "+t.getMessage(), Toast.LENGTH_LONG).show())
        );
    }
    private void timeslotsActivity(String token, String group_id, String activity_id, String user_id) {
        compositeDisposable.add(myAPI.timeslotsActivity(token, group_id, activity_id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONArray arr = new JSONArray(s);
                    Date maxDate = null;
                    String insertDate = "";
                    JSONObject prop = null;

                    for(int i = 0; i<arr.length();i++)
                    {
                        JSONObject obj = arr.getJSONObject(i);
                        String date = obj.getJSONObject("start").getString("dateTime")+obj.getJSONObject("end").getString("dateTime");

                        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALY);
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"),Locale.ITALY);
                        Date myDate = dateFormat.parse(date.substring(28,30)+"/"+date.substring(25,27)+"/"+date.substring(20,24));
                        assert myDate != null;
                        if((maxDate == null) || (maxDate.before(myDate))){
                            maxDate = myDate;
                            insertDate = date;
                            prop = obj.getJSONObject("extendedProperties").getJSONObject("shared");
                        }
                        if((myDate.after(calendar.getTime())) || (i == arr.length()-1)) {
                            mDate.add(insertDate);

                            mNAdult.add(prop.getString("children").equals("[]") ? 0 : prop.getString("children").split(",").length);
                            mNChildren.add(prop.getString("parents").equals("[]") ? 0 : prop.getString("parents").split(",").length);
                            initActivityRecycler();
                            break;
                        }
                    }

                }, t -> {
                    if(Objects.requireNonNull(t.getMessage()).contains("404")) {
                        mDate.add("N/D");
                        initActivityRecycler();
                    }
                    else
                        Toast.makeText(HomePageActivity.this, "ERROR "+t.getMessage(), Toast.LENGTH_LONG).show();
                })
        );
    }
}