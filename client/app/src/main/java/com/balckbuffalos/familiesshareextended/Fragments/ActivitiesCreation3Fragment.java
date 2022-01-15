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

public class ActivitiesCreation3Fragment extends Fragment implements Step {
    private Date activityEndDate = Calendar.getInstance().getTime();
    private int activityEndHour = Calendar.getInstance().getTime().getHours(), activityEndMinute = Calendar.getInstance().getTime().getMinutes();

    public ActivitiesCreation3Fragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflates the layout so that it can be displayed on the screen
        View view = inflater.inflate(R.layout.fragment_activities_creation3, container, false);

        DatePicker endDate = view.findViewById(R.id.activity_end_date_picker);
        endDate.setSpinnersShown(false);

        // Sets all the listeners needed to catch that will be made by the user on the default values
        endDate.setOnDateChangedListener((view12, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            activityEndDate = calendar.getTime();
        });

        TimePicker endTime = view.findViewById(R.id.activity_end_time_picker);
        endTime.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
            activityEndHour = hourOfDay;
            activityEndMinute = minute;
        });


        return view;
    }

    public Date getActivityEndDate() {
        return activityEndDate;
    }

    public int getActivityEndHour() {
        return activityEndHour;
    }

    public int getActivityEndMinute() {
        return activityEndMinute;
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