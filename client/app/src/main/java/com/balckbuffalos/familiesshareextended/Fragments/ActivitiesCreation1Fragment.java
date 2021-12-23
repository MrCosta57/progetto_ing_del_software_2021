package com.balckbuffalos.familiesshareextended.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.balckbuffalos.familiesshareextended.R;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ActivitiesCreation1Fragment extends Fragment implements Step {
    private Button mPickColorButton;
    private View mColorPreview;
    private int mDefaultColor;
    private EditText edt_title, edt_description, edt_position;
    private SwitchMaterial switch_green_pass;
    private CharSequence activityTitle, activityDescription, activityPosition;
    private boolean activityGPR;

    public ActivitiesCreation1Fragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activities_creation1, container, false);
        mPickColorButton = view.findViewById(R.id.pick_color_button);
        mColorPreview = view.findViewById(R.id.preview_selected_color);
        mDefaultColor = 0;

        mPickColorButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openColorPickerDialogue();
                    }
                });

        edt_title = view.findViewById(R.id.activity_title_text);
        edt_title.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) { }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                activityTitle = s;
            }
        });

        edt_description = view.findViewById(R.id.activity_description_text);
        edt_description.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) { }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                activityDescription = s;
            }
        });

        edt_position = view.findViewById(R.id.activity_position_text);
        edt_position.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) { }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                activityPosition = s;
            }
        });

        switch_green_pass = view.findViewById(R.id.switch_green_pass_cert);
        switch_green_pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                activityGPR = isChecked;
            }
        });

        return view;
    }

    private void openColorPickerDialogue() {
        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(getActivity(), mDefaultColor,
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

    public int getmDefaultColor() {
        return mDefaultColor;
    }

    public String getactivityTitle() {
        return activityTitle.toString();
    }

    public String getactivityDescription() {
        return activityDescription.toString();
    }

    public String getactivityPosition() {
        return activityPosition.toString();
    }

    public boolean getactivityGPR() {
        return activityGPR;
    }

    @Nullable
    @Override
    public VerificationError verifyStep() { return null; }

    @Override
    public void onSelected() {}

    @Override
    public void onError(@NonNull VerificationError error) {}
}