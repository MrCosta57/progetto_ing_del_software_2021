package com.balckbuffalos.familiesshareextended.Adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.balckbuffalos.familiesshareextended.Fragments.ActivitiesCreation1Fragment;
import com.balckbuffalos.familiesshareextended.Fragments.ActivitiesCreation2Fragment;
import com.balckbuffalos.familiesshareextended.Fragments.ActivitiesCreation3Fragment;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

public class ActivitiesCreationFragmentAdapter extends AbstractFragmentStepAdapter {
    // Created an ActivitiesCreationFragment field for each one of the single fragments of the stepper so that they can be referenced easily
    private final ActivitiesCreation1Fragment step = new ActivitiesCreation1Fragment();
    private final ActivitiesCreation2Fragment step2 = new ActivitiesCreation2Fragment();
    private final ActivitiesCreation3Fragment step3 = new ActivitiesCreation3Fragment();

    public ActivitiesCreationFragmentAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        Bundle b = new Bundle();

        b.putInt("CURRENT_STEP_POSITION_KEY", position);
        // Returns the fragment corresponding to the position inside the stepper (there are 3 fragments so position ranges from 0 to 2)
        if(position == 0) {
            step.setArguments(b);
            return getStep();
        }
        else if(position == 1){
            step2.setArguments(b);
            return getStep2();
        }
        else if(position == 2){
            step3.setArguments(b);
            return getStep3();
        } else {
            return null;
        }
    }

    // Public getters so that the private fields can be read also from other classes such as ActivitiesCreationActivity
    public ActivitiesCreation1Fragment getStep() {
        return step;
    }

    public ActivitiesCreation2Fragment getStep2() {
        return step2;
    }

    public ActivitiesCreation3Fragment getStep3() {
        return step3;
    }

    // Returns the number of fragments (3 in this case)
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
