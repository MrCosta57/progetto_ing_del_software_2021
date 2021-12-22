package com.balckbuffalos.familiesshareextended;

import static com.balckbuffalos.familiesshareextended.Utility.Utility.showMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toolbar;

import com.balckbuffalos.familiesshareextended.Adapters.ActivityRecycleAdapter;
import com.balckbuffalos.familiesshareextended.Adapters.GroupRecycleAdapter;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;

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

    private INodeJS myAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MaterialToolbar toolbar;

    private final ArrayList<String> mGroupId = new ArrayList<>();
    private final ArrayList<String> mGroupName = new ArrayList<>();
    private final ArrayList<String> mMembers = new ArrayList<>();
    private final ArrayList<Boolean> mVisible = new ArrayList<>();
    private final ArrayList<Boolean> mNotifications = new ArrayList<>();

    private final ArrayList<String> mDate = new ArrayList<>();
    private final ArrayList<String> mName = new ArrayList<>();
    private final ArrayList<Integer> mNAdult = new ArrayList<>();
    private final ArrayList<Integer> mNChildren = new ArrayList<>();
    private final ArrayList<Boolean> mGreenPass = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //Init API
        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        toolbar = findViewById(R.id.topAppBar);
        toolbar.setOnClickListener (v->{
            showMenu(v, R.menu.top_app_bar, this, getApplicationContext());});

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
        GroupRecycleAdapter adapter = new GroupRecycleAdapter(mGroupId, this, mGroupName, mMembers, mVisible, mNotifications);
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

                        groupSettings(token, group_id, user_id, obj.getBoolean("has_notifications"));
                        activityList(token,group_id,user_id);
                    }
                }, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }

    private void groupSettings(String token, String id, String user_id, Boolean has_notifications) {
        compositeDisposable.add(myAPI.groupSettings(token, id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONObject obj = new JSONObject(s);

                    groupInfo(token, id, user_id,has_notifications,obj.getBoolean("open"));
                }, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }

    private void groupInfo(String token, String group_id, String user_id, Boolean has_notifications, Boolean visible) {
        compositeDisposable.add(myAPI.groupInfo(token, group_id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONObject obj = new JSONObject(s);
                    mGroupName.add(obj.getString("name"));
                    mMembers.add(obj.getString("members"));
                    mGroupId.add(group_id);
                    mNotifications.add(has_notifications);
                    mVisible.add(visible);
                    initGroupRecycler();
                }, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
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

                        timeslotsActivity(token, group_id, obj.getString("activity_id"), user_id, obj.getString("name"), obj.getBoolean("greenpass_isrequired"));
                    }
                }, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }
    private void timeslotsActivity(String token, String group_id, String activity_id, String user_id, String name, Boolean green_pass_is_required) {
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

                            mName.add(name);
                            mGreenPass.add(green_pass_is_required);
                            mDate.add(insertDate);
                            mNAdult.add(prop.getString("children").equals("[]") ? 0 : prop.getString("children").split(",").length);
                            mNChildren.add(prop.getString("parents").equals("[]") ? 0 : prop.getString("parents").split(",").length);

                            initActivityRecycler();
                            break;
                        }
                    }

                }, t -> {
                    if(Objects.requireNonNull(t.getMessage()).contains("404")) {
                        mName.add(name);
                        mGreenPass.add(green_pass_is_required);
                        mDate.add("N/D");
                        mNAdult.add(0);
                        mNChildren.add(0);
                        initActivityRecycler();
                    }
                    else
                        Log.d("HTTP REQUEST ERROR: ", t.getMessage());
                })
        );
    }
}