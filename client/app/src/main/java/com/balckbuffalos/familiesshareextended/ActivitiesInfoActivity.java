package com.balckbuffalos.familiesshareextended;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.balckbuffalos.familiesshareextended.Fragments.ActivitiesGroupFragment;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;

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
    private BottomNavigationView bottomNavigationView;

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

        bundle.putString("group_id", group_id);
        bundle.putString("token", token);
        bundle.putString("user_id", user_id);
        bundle.putString("activity_id", activity_id);

        activityInfo(token,group_id,user_id,activity_id);

        activitiesGroupFragment.setArguments(bundle);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void activityInfo(String token, String group_id, String user_id, String activity_id) {
        compositeDisposable.add(myAPI.activityInfo(token, group_id, activity_id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONObject obj = new JSONObject(s);
                    bundle.putBoolean("greenpass_isrequired", obj.getBoolean("greenpass_isrequired"));
                    bundle.putString("description", obj.getString("description"));
                    bundle.putString("group_name", obj.getString("group_name"));
                    bundle.putString("name", obj.getString("name"));
                    bundle.putString("location", obj.getString("location"));
                    //activitySettings(token,group_id,user_id,activity_id, obj);

                }, t -> Log.d("HTTP REQUEST ERROR ACTIVITYINFO: ", t.getMessage()))
        );
    }
}