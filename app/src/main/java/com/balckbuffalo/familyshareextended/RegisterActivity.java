package com.balckbuffalo.familyshareextended;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.balckbuffalo.familyshareextended.Retrofit.INodeJS;
import com.balckbuffalo.familyshareextended.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    Button btn_login, btn_register;
    EditText edt_mail, edt_password, edt_name, edt_surname;

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
        setContentView(R.layout.activity_register);

        //Init API
        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        btn_login = findViewById(R.id.login_button);
        btn_register = findViewById(R.id.reg_button);

        edt_mail = findViewById(R.id.email);
        edt_password = findViewById(R.id.password);
        edt_name = findViewById(R.id.name);
        edt_surname = findViewById(R.id.surname);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(myIntent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(edt_name.getText().toString(), edt_surname.getText().toString(), edt_mail.getText().toString(), edt_password.getText().toString());
            }
        });
    }

    private void registerUser(String name, String surname, String email, String password) {
        compositeDisposable.add(myAPI.registerUser(name, surname, email, password,true,"english")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> Toast.makeText(RegisterActivity.this, "You register with success", Toast.LENGTH_LONG).show(),
                        t -> Toast.makeText(RegisterActivity.this, "ERROR "+t.getMessage(), Toast.LENGTH_LONG).show())
        );
    }
}