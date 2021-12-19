package com.balckbuffalos.familiesshareextended;

import static com.balckbuffalos.familiesshareextended.Utility.Utility.showMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.balckbuffalos.familiesshareextended.Fragments.ActivitiesGroupFragment;
import com.balckbuffalos.familiesshareextended.Fragments.CabinetGroupFragment;
import com.balckbuffalos.familiesshareextended.Fragments.InfoGroupFragment;
import com.balckbuffalos.familiesshareextended.Fragments.MarketGroupFragment;
import com.balckbuffalos.familiesshareextended.Fragments.MembersGroupFragment;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.http.Field;

public class GroupActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    String group_id, token, user_id;

    ActivitiesGroupFragment activitiesGroupFragment = new ActivitiesGroupFragment();
    CabinetGroupFragment cabinetGroupFragment = new CabinetGroupFragment();
    InfoGroupFragment infoGroupFragment = new InfoGroupFragment();
    MarketGroupFragment marketGroupFragment = new MarketGroupFragment();
    MembersGroupFragment membersGroupFragment = new MembersGroupFragment();
    Bundle bundle = new Bundle();

    MaterialToolbar toolbar;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            group_id = extras.getString("group_id");
        }

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

        toolbar = findViewById(R.id.topAppBar);
        toolbar.setOnClickListener (v->{
            showMenu(v, R.menu.top_app_bar, this, getApplicationContext());});


        groupInfo(token,group_id,user_id);

        activitiesGroupFragment.setArguments(bundle);
        cabinetGroupFragment.setArguments(bundle);
        infoGroupFragment.setArguments(bundle);
        marketGroupFragment.setArguments(bundle);
        membersGroupFragment.setArguments(bundle);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.page_activities:
                getSupportFragmentManager().beginTransaction().replace(R.id.fr_container, activitiesGroupFragment).commit();
                return true;
            case R.id.page_cabinet:
                getSupportFragmentManager().beginTransaction().replace(R.id.fr_container, cabinetGroupFragment).commit();
                return true;
            case R.id.page_info:
                getSupportFragmentManager().beginTransaction().replace(R.id.fr_container, infoGroupFragment).commit();
                return true;
            case R.id.page_market:
                getSupportFragmentManager().beginTransaction().replace(R.id.fr_container, marketGroupFragment).commit();
                return true;
            case R.id.page_members:
                getSupportFragmentManager().beginTransaction().replace(R.id.fr_container, membersGroupFragment).commit();
                return true;
        }
        return false;
    }

    private void groupInfo(String token, String group_id, String user_id) {
        compositeDisposable.add(myAPI.groupInfo(token, group_id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONObject obj = new JSONObject(s);
                    Log.d("TEST", obj.toString());
                    bundle.putString("description", obj.getString("description"));
                    bundle.putString("name", obj.getString("name"));
                    bundle.putString("location", obj.getString("location"));
                    bundle.putString("background", obj.getString("background"));
                    bundle.putString("contact_type", obj.getString("contact_type"));
                    groupSettings(token,group_id,user_id);

                }, t -> Toast.makeText(GroupActivity.this, "ERROR "+t.getMessage(), Toast.LENGTH_LONG).show())
        );
    }
    private void groupSettings(String token, String id, String user_id) {
        compositeDisposable.add(myAPI.groupSettings(token, id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONObject obj = new JSONObject(s);
                    bundle.putBoolean("visible", obj.getBoolean("visible"));

                    bottomNavigationView.setOnNavigationItemSelectedListener(this);
                    bottomNavigationView.setSelectedItemId(R.id.page_activities);
                }, t -> Toast.makeText(GroupActivity.this, "ERROR "+t.getMessage(), Toast.LENGTH_LONG).show())
        );
    }
}