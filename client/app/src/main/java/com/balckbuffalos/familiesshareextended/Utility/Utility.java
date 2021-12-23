package com.balckbuffalos.familiesshareextended.Utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.MenuRes;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.balckbuffalos.familiesshareextended.HomePageActivity;
import com.balckbuffalos.familiesshareextended.InfoUserActivity;
import com.balckbuffalos.familiesshareextended.LoginActivity;
import com.balckbuffalos.familiesshareextended.R;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Utility {
    public static void showMenu(View v, @MenuRes int menuRes, Context ctx, Context app) {
        PopupMenu popup = new PopupMenu(ctx, v);
        popup.inflate(menuRes);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                String token = "none";
                String user_id = "none";
                try {
                    String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
                    SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                            "secret_shared_prefs",
                            masterKeyAlias,
                            app,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    );
                    token = sharedPreferences.getString("token", "none");
                    user_id = sharedPreferences.getString("user_id", "none");
                } catch (GeneralSecurityException | IOException e) { e.printStackTrace(); }

                switch(menuItem.getItemId()){
                    case R.id.home_menu:
                        Log.d("PROVA", token+" "+user_id);
                        if(!(token.equals("none")||user_id.equals("none"))) {
                            Intent myIntent = new Intent(ctx, HomePageActivity.class);
                            ctx.startActivity(myIntent);
                        }
                        return true;
                    case R.id.profile_menu:
                        TODO: if(!(token.equals("none")||user_id.equals("none"))) {
                           Intent myIntent = new Intent(ctx, InfoUserActivity.class);
                           ctx.startActivity(myIntent);
                        }
                        return true;
                    case R.id.create_group_menu:
                        /*TODO: if(!(token.equals("none")||user_id.equals("none"))) {
                           Intent myIntent = new Intent(ctx, CreateGroupActivity.class);
                           ctx.startActivity(myIntent);
                        }*/
                        return true;
                    case R.id.join_group_menu:
                        /*TODO: if(!(token.equals("none")||user_id.equals("none"))) {
                           Intent myIntent = new Intent(ctx, JoinGroupActivity.class);
                           ctx.startActivity(myIntent);
                        }*/
                        return true;
                    case R.id.guide_menu:
                        /*TODO: if(!(token.equals("none")||user_id.equals("none"))) {
                           Intent myIntent = new Intent(ctx, GuideActivity.class);
                           ctx.startActivity(myIntent);
                        }*/
                        return true;
                    case R.id.logout_menu:
                        String masterKeyAlias = null;
                        try {
                            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

                            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                                    "secret_shared_prefs",
                                    masterKeyAlias,
                                    app,
                                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                            );

                            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("token", "none");
                            editor.putString("user_id", "none");
                            editor.apply();
                        } catch (GeneralSecurityException | IOException e) { e.printStackTrace(); }
                        Intent myIntent = new Intent(ctx, LoginActivity.class);
                        ctx.startActivity(myIntent);
                        return true;
                    case R.id.quit_menu:
                        System.exit(0);
                        return true;
                }
                return false;
            }
        });
        // Show the popup menu.
        popup.show();
    }
}
