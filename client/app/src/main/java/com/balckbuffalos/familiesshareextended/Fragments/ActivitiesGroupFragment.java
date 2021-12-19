package com.balckbuffalos.familiesshareextended.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.balckbuffalos.familiesshareextended.R;

public class ActivitiesGroupFragment extends Fragment {


    public ActivitiesGroupFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activities_group, container, false);
    }
}