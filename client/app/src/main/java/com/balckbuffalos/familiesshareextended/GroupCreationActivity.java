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
import android.view.View;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.balckbuffalos.familiesshareextended.Adapters.AllProfilesRecycleAdapter;
import com.balckbuffalos.familiesshareextended.Adapters.MemberRecycleAdapter;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.Query;

public class GroupCreationActivity extends AppCompatActivity {
    private INodeJS myAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MaterialToolbar toolbar;

    private ArrayList<String> mProfileName=new ArrayList<>();
    private ArrayList<String> mProfileEmail=new ArrayList<>();
    private ArrayList<String> mProfileId= new ArrayList<>();
    private ArrayList<String> ids;
    private AllProfilesRecycleAdapter adapter;

    private String token, user_id;
    private Button createButton;

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


        createButton=findViewById(R.id.create_button);
        TextView name=findViewById(R.id.group_name_text);
        TextView desc=findViewById(R.id.group_description_text);
        createButton.setOnClickListener((v)->{
            createGroup(token, user_id, (String[])ids.toArray(), "location", user_id,
                    "email", "", true, name.getText().toString(), desc.getText().toString());
        });


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

            profilesInfo(token);
        } catch (GeneralSecurityException | IOException e) { e.printStackTrace(); }


        /*TODO: - creare una recycle view con checkbox e search bar che usi le get di tutti i profili
                  (https://www.youtube.com/watch?v=CTvzoVtKoJ8&ab_channel=yoursTRULY)
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


    private void initProfliesRecycler(){
        RecyclerView profilesRecyclerView = findViewById(R.id.profilesRecycler);
        adapter = new AllProfilesRecycleAdapter(mProfileName, mProfileEmail, mProfileId);
        profilesRecyclerView.addItemDecoration(new DividerItemDecoration(profilesRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        profilesRecyclerView.setAdapter(adapter);
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ids=adapter.getSelectedIds();
    }


    private void profilesInfo(String token) {
        compositeDisposable.add(myAPI.profilesInfo(token,"visibility", null, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONArray arr = new JSONArray(s);
                    for(int i = 0; i<arr.length();i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        mProfileName.add(obj.getString("given_name") + obj.getString("family_name"));
                        mProfileEmail.add(obj.getString("email"));
                        mProfileId.add(obj.getString("user_id"));
                    }
                    initProfliesRecycler();
                }, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }



    private void createGroup(String token, String user_id, String[] ids, String location, String owner_id,
                             String contact_type, String contact_info, Boolean visible, String name, String description) {

        compositeDisposable.add(myAPI.createGroup(token, user_id, ids, location, owner_id, contact_type,
                contact_info, visible, name, description)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                        Toast.makeText(GroupCreationActivity.this, "Group created with success", Toast.LENGTH_LONG).show();
                        Intent myIntent = new Intent(GroupCreationActivity.this, HomePageActivity.class);
                        GroupCreationActivity.this.startActivity(myIntent);
                    },
                    t -> {
                        Toast.makeText(GroupCreationActivity.this, "ERROR " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Intent myIntent = new Intent(GroupCreationActivity.this, HomePageActivity.class);
                        GroupCreationActivity.this.startActivity(myIntent);
                    }) );

    }



}