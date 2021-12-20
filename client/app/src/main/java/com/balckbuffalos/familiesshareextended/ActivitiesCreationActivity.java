package com.balckbuffalos.familiesshareextended;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.balckbuffalos.familiesshareextended.Adapters.ActivitiesCreationFragmentAdapter;
import com.balckbuffalos.familiesshareextended.Adapters.SignUpFragmentAdapter;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import retrofit2.Retrofit;
import yuku.ambilwarna.AmbilWarnaDialog;

public class ActivitiesCreationActivity extends AppCompatActivity implements StepperLayout.StepperListener {
    INodeJS myAPI;
    EditText edt_qualcosa;
    private Button mPickColorButton;
    private View mColorPreview;
    private int mDefaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_creation);

        //Init API
        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        StepperLayout mStepperLayout = findViewById(R.id.stepper_layout_activities);
        mStepperLayout.setListener(this);
        mStepperLayout.setAdapter(new ActivitiesCreationFragmentAdapter(getSupportFragmentManager(), this));
    }

    @Override
    public void onCompleted(View completeButton) {
        /*edt_name = findViewById(R.id.name_children_text);
        edt_surname = findViewById(R.id.surname_children_text);
        edt_mail = findViewById(R.id.emailText);
        edt_password = findViewById(R.id.passwordText);
        edt_confirm_password = findViewById(R.id.confirmPasswordText);

        if(!((edt_password.getText().toString()).equals(edt_confirm_password.getText().toString()))){
            Log.d("HTTP REQUEST ERROR: ", t.getMessage())
            return;
        }

        signUpUser(edt_name.getText().toString(), edt_surname.getText().toString(), edt_mail.getText().toString(), edt_password.getText().toString());*/
    }

    @Override
    public void onError(VerificationError verificationError) { }

    @Override
    public void onStepSelected(int newStepPosition) { }

    @Override
    public void onReturn() { }
}