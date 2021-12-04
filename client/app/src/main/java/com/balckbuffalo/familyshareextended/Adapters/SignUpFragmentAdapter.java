package com.balckbuffalo.familyshareextended.Adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.balckbuffalo.familyshareextended.Fragments.SignUp1Fragment;
import com.balckbuffalo.familyshareextended.Fragments.SignUp2Fragment;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

public class SignUpFragmentAdapter extends AbstractFragmentStepAdapter {

    public SignUpFragmentAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        final SignUp1Fragment step = new SignUp1Fragment();
        final SignUp2Fragment step2 = new SignUp2Fragment();

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
        else{
            return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(int position) {
        return new StepViewModel.Builder(context)
                .setTitle("Sign Up") //can be a CharSequence instead
                .create();
    }
}
