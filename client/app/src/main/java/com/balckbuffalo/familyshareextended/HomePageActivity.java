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
import java.util.ArrayList;

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
    private final ArrayList<String> mNAdult = new ArrayList<>();
    private final ArrayList<String> mNChildren = new ArrayList<>();

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
        ActivityRecycleAdapter adapter = new ActivityRecycleAdapter(this, mDate, mName, mNAdult, mNChildren);
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
                        String group_id = arr.getJSONObject(i).getString("group_id");

                        mVisible.add(arr.getJSONObject(i).getBoolean("user_accepted"));
                        //TODO: modficare endpoint in modo che restituisca se ci sono notifiche o no
                        mNotifications.add(false);

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
                        //TODO: mDate.add()
                        //TODO:
                        mNAdult.add("4");
                        //TODO:
                        mNChildren.add("12");
                    }
                    initActivityRecycler();
                }, t -> Toast.makeText(HomePageActivity.this, "ERROR "+t.getMessage(), Toast.LENGTH_LONG).show())
        );
    }
}