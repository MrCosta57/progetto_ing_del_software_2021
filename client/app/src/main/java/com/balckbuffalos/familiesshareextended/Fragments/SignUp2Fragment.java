package com.balckbuffalos.familiesshareextended.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import com.balckbuffalos.familiesshareextended.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

public class SignUp2Fragment extends Fragment implements Step {

    private TextInputLayout name, surname, gender, allergy;
    private DatePicker date;

    public SignUp2Fragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up2, container, false);

        SwitchMaterial sw = view.findViewById(R.id.switch_child);
        name = view.findViewById(R.id.name_children);
        surname = view.findViewById(R.id.surname_children);
        gender = view.findViewById(R.id.gender);
        allergy = view.findViewById(R.id.allergy);
        date = view.findViewById(R.id.date);


        sw.setOnClickListener(v -> {
            if(name.getVisibility() == View.INVISIBLE) {
                name.setVisibility(View.VISIBLE);
                surname.setVisibility(View.VISIBLE);
                gender.setVisibility(View.VISIBLE);
                allergy.setVisibility(View.VISIBLE);
                date.setVisibility(View.VISIBLE);
            }
            else {
                name.setVisibility(View.INVISIBLE);
                surname.setVisibility(View.INVISIBLE);
                gender.setVisibility(View.INVISIBLE);
                allergy.setVisibility(View.INVISIBLE);
                date.setVisibility(View.INVISIBLE);
            }
        });
        return view;
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}