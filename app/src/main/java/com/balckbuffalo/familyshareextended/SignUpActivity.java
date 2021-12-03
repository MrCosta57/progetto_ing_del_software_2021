package com.balckbuffalo.familyshareextended;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.balckbuffalo.familyshareextended.Adapters.SignUpFragmentAdapter;
import com.balckbuffalo.familyshareextended.Retrofit.INodeJS;
import com.balckbuffalo.familyshareextended.Retrofit.RetrofitClient;
import com.google.android.material.textfield.TextInputLayout;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SignUpActivity extends AppCompatActivity implements StepperLayout.StepperListener {

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    EditText edt_mail, edt_password, edt_confirm_password, edt_name, edt_surname;
    private StepperLayout mStepperLayout;

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
        setContentView(R.layout.activity_sing_up);

        //Init API
        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        mStepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);
        mStepperLayout.setListener(this);
        mStepperLayout.setAdapter(new SignUpFragmentAdapter(getSupportFragmentManager(), this));
    }

    @Override
    public void onCompleted(View completeButton) {
        edt_name = findViewById(R.id.nameText);
        edt_surname = findViewById(R.id.surnameText);
        edt_mail = findViewById(R.id.emailText);
        edt_password = findViewById(R.id.passwordText);
        edt_confirm_password = findViewById(R.id.confirmPasswordText);
        if(!((edt_password.getText().toString()).equals(edt_confirm_password.getText().toString()))){
            Toast.makeText(SignUpActivity.this, "DIFFERENT PASSWORDS", Toast.LENGTH_LONG).show();
            return;
        }


        signUpUser(edt_name.getText().toString(), edt_surname.getText().toString(), edt_mail.getText().toString(), edt_password.getText().toString());
        Intent myIntent = new Intent(SignUpActivity.this, LoginActivity.class);
        SignUpActivity.this.startActivity(myIntent);

    }

    @Override
    public void onError(VerificationError verificationError) {

    }

    @Override
    public void onStepSelected(int newStepPosition) {

    }

    @Override
    public void onReturn() {

    }

    private void signUpUser(String name, String surname, String email, String password) {
        compositeDisposable.add(myAPI.signUpUser(name, surname, email, password,true,"english")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> Toast.makeText(SignUpActivity.this, "You register with success", Toast.LENGTH_LONG).show(),
                        t -> Toast.makeText(SignUpActivity.this, "ERROR "+t.getMessage(), Toast.LENGTH_LONG).show())
        );
    }
}