package com.balckbuffalos.familiesshareextended;

import static com.balckbuffalos.familiesshareextended.Utility.Utility.showMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class GroupCreationActivity extends AppCompatActivity {
    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation);

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

        } catch (GeneralSecurityException | IOException e) { e.printStackTrace(); }


        /*TODO: - creare una recycle view con checkbox e search bar che usi le get di tutti i profili
        *       - programmare il pulsante switch che cambi la visibility e modificarlo
        *       - programmare il pulsante create group
        *
        *
        * */
        /*INFO dello switch DA MODIFICARE*/
        /*TextView description = findViewById(R.id.description_text);
        visibility = view.findViewById(R.id.visibility_text);
        Button changeVisibility = view.findViewById(R.id.change_visibility_button);

        description.setText(extras.getString("description"));
        visibility.setText(current_visibility?"Public group":"Private group");
        changeVisibility.setOnClickListener(view2 -> {
            current_visibility = !current_visibility;
            editGroup(token, group_id, user_id, current_visibility, extras.getString("name"), extras.getString("description"), extras.getString("location"), extras.getString("background"), extras.getString("contact_type"));
            visibility.setText(current_visibility?"Public group":"Private group");
        });*/
    }

    private void editGroup(String token, String id, String user_id, Boolean visible, String name, String description, String location, String background, String contact_type) {
        compositeDisposable.add(myAPI.editGroup(token, id, user_id, visible, name, description, location, background, contact_type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> { }, t -> Log.d("ERROR ",  t.getMessage())
                ));
    }

    /*private void profileInfo(String token, String[] ids) {
        compositeDisposable.add(myAPI.profilesInfo(token,"ids", ids, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONArray arr = new JSONArray(s);
                    for(int i = 0; i<arr.length();i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        mMemberName.add(obj.getString("given_name") + obj.getString("family_name"));
                    }
                    initMemberRecycler();
                }, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }

    private void createGroup(String token, String id, String user_id) {
        compositeDisposable.add(myAPI.createGroup(token, id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONArray arr = new JSONArray(s);
                    for(int i = 0; i<arr.length();i++)
                    {
                        JSONObject obj = arr.getJSONObject(i);
                        String group_id = obj.getString("group_id");
                        mGroupId.add(group_id);
                        mNotifications.add(obj.getBoolean("has_notifications"));

                        groupSettings(token, group_id, user_id);
                        activityList(token,group_id,user_id);
                    }
                }, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }*/
}