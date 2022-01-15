package com.balckbuffalos.familiesshareextended;

import static com.balckbuffalos.familiesshareextended.Utility.Utility.showMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;
import org.json.JSONObject;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    private INodeJS myAPI;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private EditText edt_mail, edt_password;

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Init API
        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setOnClickListener (v-> showMenu(v, R.menu.top_app_bar, this, getApplicationContext()));

        Button btn_login = findViewById(R.id.login_button);
        TextView sign_up = findViewById(R.id.SignUp);

        edt_mail = findViewById(R.id.emailText);
        edt_password = findViewById(R.id.passwordText);

        btn_login.setOnClickListener(v -> loginUser(edt_mail.getText().toString(), edt_password.getText().toString()));

        sign_up.setOnClickListener(v -> {
            Intent myIntent = new Intent(LoginActivity.this, SignUpActivity.class);
            LoginActivity.this.startActivity(myIntent);
        });
    }

    @SuppressWarnings("deprecation")
    private void loginUser(String email, String password) {
        compositeDisposable.add(myAPI.authenticateUser(email, password, "deviceToken", "en", "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                            JSONObject obj = new JSONObject(s);
                            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

                            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                                    "secret_shared_prefs",
                                    masterKeyAlias,
                                    getApplicationContext(),
                                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                            );

                            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("token", obj.getString("token"));
                            editor.putString("user_id", obj.getString("id"));

                            editor.apply();
                            Intent myIntent = new Intent(LoginActivity.this, HomePageActivity.class);
                            LoginActivity.this.startActivity(myIntent);
                            finish();},
                        t -> {Log.d("HTTP LOGIN REQUEST ERROR, mail:["+email+"] password:["+password+"]", t.getMessage());
                            Toast.makeText(this, "Email or Password incorrect", Toast.LENGTH_LONG).show();})
        );
    }
}