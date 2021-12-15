package com.balckbuffalos.familiesshareextended;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;
import java.security.GeneralSecurityException;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        int SPLASH_TIME_OUT = 1500;
        new Handler().postDelayed(() -> {
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
                if(token.equals("none")||user_id.equals("none"))
                {
                    Intent homeIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(homeIntent);
                    finish();
                }
                else
                {
                    Intent homeIntent = new Intent(SplashScreenActivity.this, HomePageActivity.class);
                    startActivity(homeIntent);
                    finish();
                }
            } catch (GeneralSecurityException | IOException e) { e.printStackTrace(); }

        }, SPLASH_TIME_OUT);
    }
}