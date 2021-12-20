package com.balckbuffalos.familiesshareextended.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.balckbuffalos.familiesshareextended.R;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ActivitiesCreation1Fragment extends Fragment implements Step {
    private Button mPickColorButton;
    private View mColorPreview;
    private int mDefaultColor;

    public ActivitiesCreation1Fragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activities_creation1, container, false);
        mPickColorButton = view.findViewById(R.id.pick_color_button);
        mColorPreview = view.findViewById(R.id.preview_selected_color);
        mDefaultColor = 0;

        mPickColorButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openColorPickerDialogue();
                    }
                });
        return view;
    }

    private void openColorPickerDialogue() {
        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(getActivity(), mDefaultColor,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) { }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        mDefaultColor = color;
                        mColorPreview.setBackgroundColor(mDefaultColor);
                    }
                });
        colorPickerDialogue.show();
    }

    /*public int getmDefaultColor() {
        return mDefaultColor;
    }*/

    @Nullable
    @Override
    public VerificationError verifyStep() { return null; }

    @Override
    public void onSelected() {}

    @Override
    public void onError(@NonNull VerificationError error) {}
}