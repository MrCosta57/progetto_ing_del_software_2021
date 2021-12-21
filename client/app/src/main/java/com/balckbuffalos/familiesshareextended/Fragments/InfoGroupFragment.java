package com.balckbuffalos.familiesshareextended.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.balckbuffalos.familiesshareextended.R;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class InfoGroupFragment extends Fragment {

    private INodeJS myAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String group_id, token, user_id;

    private TextView visibility;
    private boolean current_visibility;

    public InfoGroupFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_info_group, container, false);

        Bundle extras = this.getArguments();

        group_id = extras.getString("group_id");
        token = extras.getString("token");
        user_id = extras.getString("user_id");
        current_visibility = extras.getBoolean("visible");


        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);


        /*INFO*/
        TextView description = view.findViewById(R.id.description_text);
        visibility = view.findViewById(R.id.visibility_text);
        Button changeVisibility = view.findViewById(R.id.change_visibility_button);

        description.setText(extras.getString("description"));
        visibility.setText(current_visibility?"Public group":"Private group");
        changeVisibility.setOnClickListener(view2 -> {
            current_visibility = !current_visibility;
            editGroup(token, group_id, user_id, current_visibility, extras.getString("name"), extras.getString("description"), extras.getString("location"), extras.getString("background"), extras.getString("contact_type"));
            visibility.setText(current_visibility?"Public group":"Private group");
        });

        return view;
    }

    private void editGroup(String token, String id, String user_id, Boolean visible, String name, String description, String location, String background, String contact_type) {
        compositeDisposable.add(myAPI.editGroup(token, id, user_id, visible, name, description, location, background, contact_type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> { }, t -> Log.d("ERROR ",  t.getMessage())
                ));
    }
}