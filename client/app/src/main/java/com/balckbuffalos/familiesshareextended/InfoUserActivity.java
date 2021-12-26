package com.balckbuffalos.familiesshareextended;

import static com.balckbuffalos.familiesshareextended.Utility.Utility.showMenu;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class InfoUserActivity extends AppCompatActivity {

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    MaterialToolbar toolbar;
    String user_id, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user);

        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        toolbar = findViewById(R.id.topAppBar3);
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
            token = sharedPreferences.getString("token", "none");
            user_id = sharedPreferences.getString("user_id", "none");
            String[] ids = new String[1];
            ids[0] = user_id;
            profileInfo(token, ids);
        } catch (GeneralSecurityException | IOException e) { e.printStackTrace(); }

        //Greenpass state change
        Switch switchGreenpass = findViewById(R.id.switchGreenPass);

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
        CheckBox check_posisitity = findViewById(R.id.checkBoxPositivity);

        check_posisitity.setOnClickListener(v->{
            //chiamare route
            if(check_posisitity.isChecked()){
                changePositivity(token, user_id, true);
                setPositivity(true);
            }
            else{
                changePositivity(token, user_id, false);
                setPositivity(false);
            }
        });
    }

    private void profileInfo(String token, String[] ids) {
        compositeDisposable.add(myAPI.profilesInfo(token,"ids", ids, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONArray arr = new JSONArray(s);
                    JSONObject obj = arr.getJSONObject(0);
                    TextView nome = findViewById(R.id.textViewNomeUser);
                    TextView cognome = findViewById(R.id.textViewCognomeUser);
                    TextView email = findViewById(R.id.textViewEmailUser);
                    nome.setText(obj.getString("given_name"));
                    cognome.setText(obj.getString("family_name"));
                    email.setText(obj.getString("email"));
                    setGreenPass(obj.getBoolean("greenpass_available"));  //Setting Greeenpass initial state
                    setPositivity(obj.getBoolean("is_positive"));  //Setting Positivity initial state
                }, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }

    private void setGreenPass(Boolean state){
        TextView text_greenpass_state = findViewById(R.id.textViewGreenPassState);
        Switch switchGreenpass = findViewById(R.id.switchGreenPass);

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

    private void setPositivity(Boolean state){
        CheckBox check_posisitity = findViewById(R.id.checkBoxPositivity);

        if(state && !check_posisitity.isChecked())
                check_posisitity.setChecked(true);
        else if(!state && check_posisitity.isChecked())
                check_posisitity.setChecked(false);
    }

    private void changeGreenpassState(String token, String user_id, Boolean greenpass_available) {
        compositeDisposable.add(myAPI.changeGreenpassState(token, user_id, greenpass_available)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->{}, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }

    private void changePositivity(String token, String user_id, Boolean is_positive) {
        compositeDisposable.add(myAPI.changePositivity(token, user_id, is_positive)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->{}, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }
}