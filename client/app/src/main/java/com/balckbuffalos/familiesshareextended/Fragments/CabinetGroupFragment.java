package com.balckbuffalos.familiesshareextended.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.balckbuffalos.familiesshareextended.R;

public class CabinetGroupFragment extends Fragment {

    public CabinetGroupFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cabinet_group, container, false);
    }
}