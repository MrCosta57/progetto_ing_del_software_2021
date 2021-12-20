package com.balckbuffalos.familiesshareextended.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.balckbuffalos.familiesshareextended.R;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

public class ActivitiesCreation3Fragment extends Fragment implements Step {

    public ActivitiesCreation3Fragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activities_creation3, container, false);
        DatePicker endDate = view.findViewById(R.id.activity_end_date_picker);
        endDate.setSpinnersShown(false);


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