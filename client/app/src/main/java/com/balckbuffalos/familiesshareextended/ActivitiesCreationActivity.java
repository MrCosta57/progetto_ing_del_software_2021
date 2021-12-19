package com.balckbuffalos.familiesshareextended;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

        StepperLayout mStepperLayout = findViewById(R.id.stepperLayout);
        mStepperLayout.setListener(this);
        mStepperLayout.setAdapter(new SignUpFragmentAdapter(getSupportFragmentManager(), this));

        mPickColorButton = findViewById(R.id.pick_color_button);
        mColorPreview = findViewById(R.id.preview_selected_color);
        mDefaultColor = 0;

        mPickColorButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openColorPickerDialogue();
                    }
                });
    }

    public void openColorPickerDialogue() {
        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(this, mDefaultColor,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) { }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        mDefaultColor = color;
                        mColorPreview.setBackgroundColor(mDefaultColor);
                    }
                });
        colorPickerDialogue.show();
    }

    @Override
    public void onCompleted(View completeButton) {
        /*edt_name = findViewById(R.id.name_children_text);
        edt_surname = findViewById(R.id.surname_children_text);
        edt_mail = findViewById(R.id.emailText);
        edt_password = findViewById(R.id.passwordText);
        edt_confirm_password = findViewById(R.id.confirmPasswordText);

        if(!((edt_password.getText().toString()).equals(edt_confirm_password.getText().toString()))){
            Toast.makeText(SignUpActivity.this, "DIFFERENT PASSWORDS", Toast.LENGTH_LONG).show();
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