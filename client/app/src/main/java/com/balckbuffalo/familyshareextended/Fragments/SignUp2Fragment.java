package com.balckbuffalo.familyshareextended.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.balckbuffalo.familyshareextended.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUp2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUp2Fragment extends Fragment implements Step {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextInputLayout name, surname, gender, allergy;
    private DatePicker date;

    public SignUp2Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUp2Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUp2Fragment newInstance(String param1, String param2) {
        SignUp2Fragment fragment = new SignUp2Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up2, container, false);

        SwitchMaterial sw = view.findViewById(R.id.switch_child);
        name = view.findViewById(R.id.name);
        surname = view.findViewById(R.id.surname);
        gender = view.findViewById(R.id.gender);
        allergy = view.findViewById(R.id.allergy);
        date = view.findViewById(R.id.date);


        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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