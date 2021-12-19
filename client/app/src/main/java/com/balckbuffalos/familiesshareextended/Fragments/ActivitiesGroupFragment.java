package com.balckbuffalos.familiesshareextended.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.balckbuffalos.familiesshareextended.ActivitiesCreationActivity;
import com.balckbuffalos.familiesshareextended.LoginActivity;
import com.balckbuffalos.familiesshareextended.R;
import com.balckbuffalos.familiesshareextended.SplashScreenActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ActivitiesGroupFragment extends Fragment {

    FloatingActionButton createActivity;
    public ActivitiesGroupFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activities_group, container, false);
        createActivity = view.findViewById(R.id.floating_activity_button);
        createActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivitiesCreationActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}