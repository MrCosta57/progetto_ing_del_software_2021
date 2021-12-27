package com.balckbuffalos.familiesshareextended.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.balckbuffalos.familiesshareextended.ActivitiesCreationActivity;
import com.balckbuffalos.familiesshareextended.Adapters.ActivityRecycleAdapter;
import com.balckbuffalos.familiesshareextended.R;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ActivitiesGroupFragment extends Fragment {

    private INodeJS myAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private FloatingActionButton createActivity;
    private final ArrayList<String> mActivityId = new ArrayList<>();
    private final ArrayList<String> mGroupId = new ArrayList<>();
    private final ArrayList<String> mDate = new ArrayList<>();
    private final ArrayList<String> mName = new ArrayList<>();
    private final ArrayList<Integer> mNAdult = new ArrayList<>();
    private final ArrayList<Integer> mNChildren = new ArrayList<>();
    private final ArrayList<Boolean> mGreenPass = new ArrayList<>();

    private String group_id, token, user_id;

    private View view;

    public ActivitiesGroupFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_activities_group, container, false);

        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        Bundle extras = this.getArguments();

        group_id = extras.getString("group_id");
        token = extras.getString("token");
        user_id = extras.getString("user_id");

        createActivity = view.findViewById(R.id.floating_activity_button);
        createActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivitiesCreationActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("group_id", group_id);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
        });
        activityList(token, group_id, user_id);

        return view;
    }

    private void initActivityRecycler(){
        RecyclerView activityRecyclerView = view.findViewById(R.id.activityRecycler);
        ActivityRecycleAdapter adapter = new ActivityRecycleAdapter(getActivity(), mActivityId, mGroupId, mDate, mName, mNAdult, mNChildren, mGreenPass);
        activityRecyclerView.addItemDecoration(new DividerItemDecoration(activityRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        activityRecyclerView.setAdapter(adapter);
        activityRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    private void activityList(String token, String group_id, String user_id) {
        compositeDisposable.add(myAPI.activityList(token, group_id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONArray arr = new JSONArray(s);
                    for(int i = 0; i<arr.length();i++)
                    {
                        JSONObject obj = arr.getJSONObject(i);

                        timeslotsActivity(token, group_id, obj.getString("activity_id"), user_id, obj.getString("name"), obj.getBoolean("greenpass_isrequired"));
                    }
                }, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }
    private void timeslotsActivity(String token, String group_id, String activity_id, String user_id, String name, Boolean green_pass_is_required) {
        compositeDisposable.add(myAPI.timeslotsActivity(token, group_id, activity_id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONArray arr = new JSONArray(s);
                    Date maxDate = null;
                    String insertDate = "";
                    JSONObject prop = null;

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        String date = obj.getJSONObject("start").getString("dateTime") + obj.getJSONObject("end").getString("dateTime");

                        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALY);
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALY);
                        Date myDate = dateFormat.parse(date.substring(28, 30) + "/" + date.substring(25, 27) + "/" + date.substring(20, 24));
                        assert myDate != null;
                        if ((maxDate == null) || (maxDate.before(myDate))) {
                            maxDate = myDate;
                            insertDate = date;
                            prop = obj.getJSONObject("extendedProperties").getJSONObject("shared");
                        }
                        if ((myDate.after(calendar.getTime())) || (i == arr.length() - 1)) {
                            mActivityId.add(activity_id);
                            mName.add(name);
                            mGreenPass.add(green_pass_is_required);
                            mDate.add(insertDate);
                            mNAdult.add(prop.getString("children").equals("[]") ? 0 : prop.getString("children").split(",").length);
                            mNChildren.add(prop.getString("parents").equals("[]") ? 0 : prop.getString("parents").split(",").length);

                            initActivityRecycler();
                            break;
                        }
                    }

                }, t -> {
                    if (Objects.requireNonNull(t.getMessage()).contains("404")) {
                        mActivityId.add(activity_id);
                        mName.add(name);
                        mGreenPass.add(green_pass_is_required);
                        mDate.add("N/D");
                        mNAdult.add(0);
                        mNChildren.add(0);
                        initActivityRecycler();
                    } else
                        Log.d("HTTP REQUEST ERROR: ", t.getMessage());
                })
        );
    }
}