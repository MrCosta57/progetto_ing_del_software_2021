package com.balckbuffalos.familiesshareextended;

import static com.balckbuffalos.familiesshareextended.Utility.Utility.showMenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;

import com.balckbuffalos.familiesshareextended.Adapters.ChildrenRecycleAdapter;
import com.balckbuffalos.familiesshareextended.Adapters.MemberRecycleAdapter;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

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
import retrofit2.http.Header;
import retrofit2.http.Path;

public class InfoUserActivity extends AppCompatActivity {

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    MaterialToolbar toolbar;

    String user_id, token;
    private final ArrayList<String> mChildrenName = new ArrayList<>();
    private final ArrayList<String> mChildrenBirthdate = new ArrayList<>();
    PopupWindow popupEditWindow;

    //User information
    String req_givenName, req_familyName, req_email, req_phone, req_phoneType, req_street, req_number, req_city, req_description, req_contactOption;
    Boolean req_visible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user);

        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        toolbar = findViewById(R.id.topAppBar3);
        toolbar.setOnClickListener (v->{
            showMenu(v, R.menu.top_app_bar, this, getApplicationContext());
        });

        SwitchMaterial switchGreenpass = findViewById(R.id.switchGreenPass);
        CheckBox check_posisitity = findViewById(R.id.checkBoxPositivity);
        TextView textViewEditing = findViewById(R.id.textViewEditing);

        textViewEditing.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

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
            String[] ids = {user_id};
            userInfo(token, ids);
        } catch (GeneralSecurityException | IOException e) { e.printStackTrace(); }

        //Children population
        mChildrenName.clear();
        mChildrenBirthdate.clear();
        childrenList(token, user_id, user_id);

        //Greenpass state change
        switchGreenpass.setOnClickListener(v->{
            if(switchGreenpass.isChecked()){
                changeGreenpassState(token, user_id, true);
                setGreenPass(true);
            }
            else{
                changeGreenpassState(token, user_id, false);
                setGreenPass(false);
            }
        });

        //Positivity state change
        check_posisitity.setOnClickListener(v->{
            if(check_posisitity.isChecked()){
                changePositivity(token, user_id, true);
                setPositivity(true);
            }
            else{
                changePositivity(token, user_id, false);
                setPositivity(false);
            }
        });

        //Editing
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupEditView = inflater.inflate(R.layout.popup_edit_user, null);
        int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        //Editing popup appearance
        textViewEditing.setOnClickListener(v->{
            popupEditWindow = new PopupWindow(popupEditView, width, height, true);
            popupEditWindow.showAtLocation(new ConstraintLayout(this), Gravity.CENTER, 0, 0);
        });

        RadioButton rdbName = popupEditView.findViewById(R.id.rdbName);
        RadioButton rdbSurname = popupEditView.findViewById(R.id.rdbSurname);
        TextView editInfo = popupEditView.findViewById(R.id.edit_user_text);
        Button buttonEdit = popupEditView.findViewById(R.id.buttonEdit);

        buttonEdit.setOnClickListener(z->{
            //Checking what information has to be changed
            if(rdbName.isChecked())
                editUser(token, user_id, editInfo.getText().toString(), req_familyName, req_email, req_phone, req_phoneType, req_visible, req_street, req_number, req_city, req_description, req_contactOption);
            else if(rdbSurname.isChecked())
                editUser(token, user_id, req_givenName, editInfo.getText().toString(), req_email, req_phone, req_phoneType, req_visible, req_street, req_number, req_city, req_description, req_contactOption);
            else
                editUser(token, user_id, req_givenName, req_familyName, editInfo.getText().toString(), req_phone, req_phoneType, req_visible, req_street, req_number, req_city, req_description, req_contactOption);
            popupEditWindow.dismiss();

            //Reload MyProfile
            finish();
            startActivity(getIntent());
        });
    }

    private void userInfo(String token, String[] ids) {  //Get all user information
        compositeDisposable.add(myAPI.profilesInfo(token,"ids", ids, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONArray arr = new JSONArray(s);
                    JSONObject obj = arr.getJSONObject(0);
                    TextView textViewNome = findViewById(R.id.textViewNomeUser);
                    TextView textViewCognome = findViewById(R.id.textViewCognomeUser);
                    TextView textViewEmail = findViewById(R.id.textViewEmailUser);

                    //Setting global variables for an eventually editing
                    req_givenName = obj.getString("given_name");
                    req_familyName = obj.getString("family_name");
                    req_email = obj.getString("email");
                    req_phone = obj.getString("phone");
                    req_phoneType = obj.getString("phone_type");
                    req_visible = obj.getBoolean("visible");
                    req_description = obj.getString("description");
                    req_contactOption = obj.getString("contact_option");
                    //Setting profile info
                    textViewNome.setText(req_givenName);
                    textViewCognome.setText(req_familyName);
                    textViewEmail.setText(req_email);
                    setGreenPass(obj.getBoolean("greenpass_available"));  //Setting Greeenpass initial state
                    setPositivity(obj.getBoolean("is_positive"));  //Setting Positivity initial state
                }, t -> Log.d("HTTP GET INFO PROFILES ["+ids.toString()+"] REQUEST ERROR", t.getMessage()))
        );
    }

    private void setGreenPass(Boolean state){  //Set greenpass switch correctly
        TextView text_greenpass_state = findViewById(R.id.textViewGreenPassState);
        SwitchMaterial switchGreenpass = findViewById(R.id.switchGreenPass);

        if(state){
            if(!switchGreenpass.isChecked())
                switchGreenpass.setChecked(true);
            text_greenpass_state.setText("Available");
        }
        else{
            if(switchGreenpass.isChecked())
                switchGreenpass.setChecked(false);
            text_greenpass_state.setText("Not available");
        }
    }

    private void setPositivity(Boolean state){  //Set positivity checkbox correctly
        CheckBox check_posisitity = findViewById(R.id.checkBoxPositivity);

        if(state && !check_posisitity.isChecked())
                check_posisitity.setChecked(true);
        else if(!state && check_posisitity.isChecked())
                check_posisitity.setChecked(false);
    }

    private void childrenList(String token, String id, String user_id) {  //Get all the children
        compositeDisposable.add(myAPI.childrenList(token, id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONArray arr = new JSONArray(s);
                    for(int i = 0; i<arr.length();i++)
                    {
                        JSONObject obj = arr.getJSONObject(i);
                        String[] ids = {obj.getString("child_id")};
                        childrenInfo(token, ids);
                    }
                }, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }

    private void childrenInfo(String token, String[] ids) {  //Get children information
        compositeDisposable.add(myAPI.childrenInfo(token, ids)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONArray arr = new JSONArray(s);
                    for(int i = 0; i<arr.length();i++)
                    {
                        JSONObject obj = arr.getJSONObject(i);
                        mChildrenName.add(obj.getString("given_name") + obj.getString("family_name"));
                        mChildrenBirthdate.add(obj.getString("birthdate").substring(0, 10));  //Returns the birthdate (format YYYY-MM-DD)
                    }
                    initChildrenRecycler();
                }, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }

    private void initChildrenRecycler(){ 
        RecyclerView childrenRecyclerView = findViewById(R.id.childrenRecyclerView);
        childrenRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ChildrenRecycleAdapter adapter = new ChildrenRecycleAdapter(this, mChildrenName, mChildrenBirthdate);
        childrenRecyclerView.setAdapter(adapter);
    }

    private void changeGreenpassState(String token, String user_id, Boolean greenpass_available) {
        compositeDisposable.add(myAPI.changeGreenpassState(token, user_id, greenpass_available)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->{}, t -> Log.d("HTTP PATCH GREEN PASS OF USER ["+user_id+"] REQUEST ERROR", t.getMessage()))
        );
    }

    private void changePositivity(String token, String user_id, Boolean is_positive) {
        compositeDisposable.add(myAPI.changePositivity(token, user_id, is_positive)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->{}, t -> Log.d("HTTP PATCH POSITIVITY OF USER ["+user_id+"] REQUEST ERROR", t.getMessage()))
        );
    }

    private void changeChildsPositivity(String token, String parent_id, Boolean is_positive) {
        compositeDisposable.add(myAPI.changeChildsPositivity(token, parent_id, is_positive)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->{}, t -> Log.d("HTTP PATCH POSITIVITY OF CHILD ["+parent_id+"] REQUEST ERROR", t.getMessage()))
        );
    }

    private void editUser(String token, String user_id, String given_name, String family_name, String email, String phone, String phone_type, Boolean visible, String street, String number, String city, String description, String contact_option) {
        compositeDisposable.add(myAPI.editUser(token, user_id, given_name, family_name, email, phone, phone_type, visible, street, number, city, description, contact_option)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->{}, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }
}