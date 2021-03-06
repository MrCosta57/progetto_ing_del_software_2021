package com.balckbuffalos.familiesshareextended;

import static com.balckbuffalos.familiesshareextended.Utility.Utility.showMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.balckbuffalos.familiesshareextended.Adapters.AllProfilesRecycleAdapter;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class GroupCreationActivity extends AppCompatActivity {
    private INodeJS myAPI;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private JSONArray mProfileInfo;
    private ArrayList<String> ids;
    private AllProfilesRecycleAdapter adapter;

    private String token, user_id;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation);

        //Init API
        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setOnClickListener (v-> showMenu(v, R.menu.top_app_bar, this, getApplicationContext()));


        Button createButton = findViewById(R.id.create_group_btn);
        TextView name=findViewById(R.id.group_name_text);
        TextView desc=findViewById(R.id.group_description_text);

        //Button for group creation, POST all info to server
        createButton.setOnClickListener((v)->{
            ids=adapter.getSelectedIds();
            String[] ids_array=new String[ids.size()];
            ids_array=ids.toArray(ids_array);

            createGroup(token, user_id, ids_array, user_id,
                    name.getText().toString(), desc.getText().toString());
        });

        //Search bar for searching users by email
        SearchView searchView=findViewById(R.id.profiles_searchbar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

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

            profilesInfo(token, user_id);
        } catch (GeneralSecurityException | IOException e) { e.printStackTrace(); }

    }


    private void initProfliesRecycler() {
        RecyclerView profilesRecyclerView = findViewById(R.id.profilesRecycler);
        try {
            adapter = new AllProfilesRecycleAdapter(mProfileInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        profilesRecyclerView.addItemDecoration(new DividerItemDecoration(profilesRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        profilesRecyclerView.setAdapter(adapter);
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private void profilesInfo(String token, String user_id) {
        //Call server's endpoit
        compositeDisposable.add(myAPI.profilesInfo(token,"visibility", null, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONArray tmp_profiles=new JSONArray(s);

                    mProfileInfo = new JSONArray();
                    for (int i = 0; i < tmp_profiles.length(); i++) {
                        JSONObject tmp=tmp_profiles.getJSONObject(i);
                        if (!tmp.getString("user_id").equals(user_id))
                            mProfileInfo.put(tmp);
                    }

                    initProfliesRecycler();
                }, t -> Log.d("HTTP GET PROFILE INFO ["+user_id+"] REQUEST ERROR", t.getMessage()))
        );
    }



    private void createGroup(String token, String user_id, String[] ids, String owner_id, String name, String description) {
        //Call server's endpoit
        compositeDisposable.add(myAPI.createGroup(token, user_id, ids, "location", owner_id, "email", "email", true, name, description)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                        Toast.makeText(GroupCreationActivity.this, "Group created with success", Toast.LENGTH_LONG).show();
                        Intent myIntent = new Intent(GroupCreationActivity.this, HomePageActivity.class);
                        GroupCreationActivity.this.startActivity(myIntent);
                    },
                    t -> {
                        Log.d("HTTP POST GROUP REQUEST ERROR", t.getMessage());
                        Toast.makeText(GroupCreationActivity.this, "INVALID GROUP ATTRIBUTE(S)", Toast.LENGTH_LONG).show();
                        Intent myIntent = new Intent(GroupCreationActivity.this, HomePageActivity.class);
                        GroupCreationActivity.this.startActivity(myIntent);
                    }) );

    }



}