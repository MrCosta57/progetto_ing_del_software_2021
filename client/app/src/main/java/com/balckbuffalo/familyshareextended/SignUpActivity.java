package com.balckbuffalo.familyshareextended;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.balckbuffalo.familyshareextended.Adapters.SignUpFragmentAdapter;
import com.balckbuffalo.familyshareextended.Retrofit.INodeJS;
import com.balckbuffalo.familyshareextended.Retrofit.RetrofitClient;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SignUpActivity extends AppCompatActivity implements StepperLayout.StepperListener {

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    EditText edt_name, edt_surname, edt_mail, edt_password, edt_confirm_password, edt_children_name, edt_children_surname, edt_gender, edt_allergy;
    SwitchMaterial add_child;
    DatePicker birthdate;
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

        StepperLayout mStepperLayout = findViewById(R.id.stepperLayout);
        mStepperLayout.setListener(this);
        mStepperLayout.setAdapter(new SignUpFragmentAdapter(getSupportFragmentManager(), this));
    }

    @Override
    public void onCompleted(View completeButton) {
        edt_name = findViewById(R.id.name_children_text);
        edt_surname = findViewById(R.id.surname_children_text);
        edt_mail = findViewById(R.id.emailText);
        edt_password = findViewById(R.id.passwordText);
        edt_confirm_password = findViewById(R.id.confirmPasswordText);
        if(!((edt_password.getText().toString()).equals(edt_confirm_password.getText().toString()))){
            Toast.makeText(SignUpActivity.this, "DIFFERENT PASSWORDS", Toast.LENGTH_LONG).show();
            return;
        }


        signUpUser(edt_name.getText().toString(), edt_surname.getText().toString(), edt_mail.getText().toString(), edt_password.getText().toString());

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
                .subscribe(s ->{
                            Toast.makeText(SignUpActivity.this, "You register with success", Toast.LENGTH_LONG).show();

                            add_child = findViewById(R.id.switch_child);
                            if(add_child.isChecked()) {
                                edt_children_name = findViewById(R.id.name_children_text);
                                edt_children_surname = findViewById(R.id.surname_children_text);
                                edt_gender = findViewById(R.id.gender_text);
                                edt_allergy = findViewById(R.id.allergy_text);
                                birthdate = findViewById(R.id.date);

                                JSONObject obj = new JSONObject(s);

                                insertChild(obj.getString("token"), obj.getString("id"), obj.getString("id"), new Date(birthdate.getCalendarView().getDate()), edt_children_name.getText().toString(), edt_children_surname.getText().toString(), edt_gender.getText().toString(), edt_allergy.getText().toString(), "", "", "", "");

                            }
                        },
                        t -> Toast.makeText(SignUpActivity.this, "ERROR "+t.getMessage(), Toast.LENGTH_LONG).show())
        );
    }

    private void insertChild(String token, String id, String user_id, Date date, String name, String surname, String gender, String allergies, String other_info, String special_needs, String background, String image_path) {
        compositeDisposable.add(myAPI.insertChild(token, id, user_id, date, name, surname, gender, allergies, other_info, special_needs, background, image_path)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->{
                            Toast.makeText(SignUpActivity.this, "You register with success", Toast.LENGTH_LONG).show();
                            Intent myIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                            SignUpActivity.this.startActivity(myIntent);},
                        t -> Toast.makeText(SignUpActivity.this, "ERROR "+t.getMessage(), Toast.LENGTH_LONG).show())
        );
    }
}