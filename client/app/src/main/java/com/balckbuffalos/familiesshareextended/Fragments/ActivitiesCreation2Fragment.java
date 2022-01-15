package com.balckbuffalos.familiesshareextended.Fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.balckbuffalos.familiesshareextended.R;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.util.Calendar;
import java.util.Date;

public class ActivitiesCreation2Fragment extends Fragment implements Step {
    private Date activityStartDate = Calendar.getInstance().getTime();
    private int activityStartHour = Calendar.getInstance().getTime().getHours(), activityStartMinute = Calendar.getInstance().getTime().getMinutes();

    public ActivitiesCreation2Fragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflates the layout so that it can be displayed on the screen
        View view = inflater.inflate(R.layout.fragment_activities_creation2, container, false);

        DatePicker startDate = view.findViewById(R.id.activity_start_date_picker);
        startDate.setSpinnersShown(false);

        // Sets all the listeners needed to catch that will be made by the user on the default values
        startDate.setOnDateChangedListener((view1, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            activityStartDate = calendar.getTime();
        });

        TimePicker startTime = view.findViewById(R.id.activity_start_time_picker);
        startTime.setOnTimeChangedListener((view12, hourOfDay, minute) -> {
            activityStartHour = hourOfDay;
            activityStartMinute = minute;
        });

        return view;
    }

    public Date getActivityStartDate() {
        return activityStartDate;
    }

    public int getActivityStartHour() {
        return activityStartHour;
    }

    public int getActivityStartMinute() {
        return activityStartMinute;
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