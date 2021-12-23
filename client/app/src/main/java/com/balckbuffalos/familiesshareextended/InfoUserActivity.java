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
    Boolean greenpass_state = false, positivity = false;
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

        //Greenpass state
        TextView text_greenpass_state = findViewById(R.id.textViewGreenPassState);
        Switch switchGreenpass = findViewById(R.id.switchGreenPass);

        if(greenpass_state){
            text_greenpass_state.setText("Available");
            switchGreenpass.setChecked(true);
        }
        else{
            text_greenpass_state.setText("Not available");
            switchGreenpass.setChecked(false);
        }

        switchGreenpass.setOnClickListener(v->{
            if(switchGreenpass.isChecked()){
                text_greenpass_state.setText("Available");
                greenpass_state = true;
                changeGreenpassState(token, user_id, true);
            }
            else{
                text_greenpass_state.setText("Not available");
                greenpass_state = false;
                changeGreenpassState(token, user_id, false);
            }
        });

        //Positivity state
        /*CheckBox check_posisitity = findViewById(R.id.checkBoxPositivity);

        if(positivity)
            check_posisitity.setChecked(true);
        else
            check_posisitity.setChecked(false);

        check_posisitity.setOnClickListener(v->{
            //chiamare route
            if(check_posisitity.isChecked()){

            }
            else{

            }
        });*/
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
                    String g = obj.getString("greenpass_available");
                    if(g.equals("true"))
                        greenpass_state = true;
                    else
                        greenpass_state = false;
                    //Toast.makeText(InfoUserActivity.this, g, Toast.LENGTH_LONG).show();
                    //greenpass_state = obj.getBoolean("greenpass_available");
                    //positivity = obj.getBoolean("is_positive");
                }, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }

    private void changeGreenpassState(String token, String user_id, Boolean greenpass_available) {
        compositeDisposable.add(myAPI.changeGreenpassState(token, user_id, greenpass_available)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->{
                            Toast.makeText(InfoUserActivity.this, "Greenpass state changed", Toast.LENGTH_LONG).show();
                        },
                t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }

    /*private void changePositivity(String user_id, Boolean is_positive) {
        compositeDisposable.add(myAPI.changeGreenpassState(user_id, is_positive)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->{
                            Toast.makeText(InfoUserActivity.this, "Positivity changed", Toast.LENGTH_LONG).show();
                        },
                        t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }*/
}