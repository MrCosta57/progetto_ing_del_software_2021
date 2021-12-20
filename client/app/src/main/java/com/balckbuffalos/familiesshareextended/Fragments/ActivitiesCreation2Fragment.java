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
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

public class ActivitiesCreation2Fragment extends Fragment implements Step {

    public ActivitiesCreation2Fragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activities_creation2, container, false);
        DatePicker startDate = view.findViewById(R.id.activity_start_date_picker);
        startDate.setSpinnersShown(false);


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