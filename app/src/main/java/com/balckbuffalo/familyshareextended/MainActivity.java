package com.balckbuffalo.familyshareextended;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.balckbuffalo.familyshareextended.Retrofit.INodeJS;
import com.balckbuffalo.familyshareextended.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    Button btn_login, btn_register;
    EditText edt_mail, edt_password;

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
        setContentView(R.layout.activity_main);

        //Init API
        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        btn_login = findViewById(R.id.login_button);
        btn_register = findViewById(R.id.reg_button);

        edt_mail = findViewById(R.id.email);
        edt_password = findViewById(R.id.password);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(edt_mail.getText().toString(), edt_password.getText().toString());
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(edt_mail.getText().toString(), edt_password.getText().toString());
            }
        });
    }

    private void registerUser(String email, String password) {
        compositeDisposable.add(myAPI.registerUser("given_name", "famiglia",email, password,true,"a")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if(s.contains(""))
                            Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, ""+s, Toast.LENGTH_LONG).show();
                    }
                })
        );
    }

    private void loginUser(String email, String password) {
        compositeDisposable.add(myAPI.loginUser(email,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if(s.contains(""))
                            Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, ""+s, Toast.LENGTH_LONG).show();
                    }
                })
        );
    }
}