package com.balckbuffalos.familiesshareextended.Adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.balckbuffalos.familiesshareextended.Fragments.ActivitiesCreation1Fragment;
import com.balckbuffalos.familiesshareextended.Fragments.ActivitiesCreation2Fragment;
import com.balckbuffalos.familiesshareextended.Fragments.ActivitiesCreation3Fragment;
import com.balckbuffalos.familiesshareextended.Fragments.SignUp1Fragment;
import com.balckbuffalos.familiesshareextended.Fragments.SignUp2Fragment;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

public class ActivitiesCreationFragmentAdapter extends AbstractFragmentStepAdapter {

    public ActivitiesCreationFragmentAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        final ActivitiesCreation1Fragment step = new ActivitiesCreation1Fragment();
        final ActivitiesCreation2Fragment step2 = new ActivitiesCreation2Fragment();
        final ActivitiesCreation3Fragment step3 = new ActivitiesCreation3Fragment();

        Bundle b = new Bundle();

        b.putInt("CURRENT_STEP_POSITION_KEY", position);
        if(position == 0) {
            step.setArguments(b);
            return step;
        }
        else if(position == 1){
            step2.setArguments(b);
            return step2;
        }
        else if(position == 2){
            step3.setArguments(b);
            return step3;
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(int position) {
        return new StepViewModel.Builder(context)
                .setTitle("Create Activity") //can be a CharSequence instead
                .create();
    }
}
