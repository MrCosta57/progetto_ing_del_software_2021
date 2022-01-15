package com.balckbuffalos.familiesshareextended;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.balckbuffalos.familiesshareextended.Adapters.ActivitiesCreationFragmentAdapter;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ActivitiesCreationActivity extends AppCompatActivity implements StepperLayout.StepperListener {
    private INodeJS myAPI;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String token,group_id,user_id;
    // Used to be able to get the step fields from the ActivitiesCreationFragmentAdapter class' public getters
    private final ActivitiesCreationFragmentAdapter adapter = new ActivitiesCreationFragmentAdapter(getSupportFragmentManager(), this);
    @ColorInt
    private int color;

    @Override
    protected void onStop() { super.onStop(); }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_creation);

        //Init API
        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        // Gets the token, group_id, and user_id fields from the caller (previous) screen
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            token = extras.getString("token");
            group_id = extras.getString("group_id");
            user_id = extras.getString("user_id");
        }

        StepperLayout mStepperLayout = findViewById(R.id.stepper_layout_activities);
        mStepperLayout.setListener(this);
        mStepperLayout.setAdapter(adapter);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCompleted(View completeButton) {
        boolean guard = true;
        // When the user finishes the insertion of the parameters, it saves them into the private fields and local variables
        String title = adapter.getStep().getactivityTitle();
        String description = adapter.getStep().getactivityDescription();
        String position = adapter.getStep().getactivityPosition();
        color = adapter.getStep().getmDefaultColor();
        boolean green_pass = adapter.getStep().getactivityGPR();

        Date data_inizio = adapter.getStep2().getActivityStartDate();
        int ora_inizio = adapter.getStep2().getActivityStartHour();
        int minuto_inizio = adapter.getStep2().getActivityStartMinute();

        Date data_fine = adapter.getStep3().getActivityEndDate();
        int ora_fine = adapter.getStep3().getActivityEndHour();
        int minuto_fine = adapter.getStep3().getActivityEndMinute();

        // Check needed to verify that the received data is valid (enters the branch if it isn't)
        if(title.equals("") || data_inizio.after(data_fine) || (data_inizio.equals(data_fine) && ora_inizio > ora_fine) || (data_inizio.equals(data_fine) && ora_inizio == ora_fine && minuto_inizio >= minuto_fine)){
            guard = false;
            Toast.makeText(ActivitiesCreationActivity.this, "INVALID ACTIVITY ATTRIBUTE(S)", Toast.LENGTH_LONG).show();
            reset();
        }

        JSONObject activity = new JSONObject();
        JSONObject event = new JSONObject();
        JSONArray events = new JSONArray();

        try {
            // Creates and populates the objects that will be sent to the server so that the activity is created officially
            String hexColor = String.format("#%06X", (0xFFFFFF & color));

            activity.put("group_id", group_id);
            activity.put("creator_id", user_id);
            activity.put("name", title);
            activity.put("color", hexColor);
            activity.put("description", description);
            activity.put("location", position);
            activity.put("repetition", false);
            activity.put("repetition_type", "weekly");
            activity.put("different_timeslots", false);
            activity.put("greenpass_isrequired", green_pass);

            JSONObject startTime = new JSONObject();
            JSONObject endTime = new JSONObject();
            JSONObject extendedProperties = new JSONObject();
            JSONObject sharedProperties = new JSONObject();

            startTime.put("dateTime", (data_inizio.getYear() + 1900) + "-" + ((data_inizio.getMonth() + 1)<10?"0":"") + (data_inizio.getMonth() + 1) + "-" + data_inizio.getDate() + "T" + ora_inizio + ":" + (minuto_inizio<10?"0":"") + minuto_inizio + ":00.000Z");
            startTime.put("date", null);
            endTime.put("dateTime", (data_fine.getYear() + 1900) + "-" + ((data_fine.getMonth() + 1)<10?"0":"") + (data_fine.getMonth() + 1) + "-" + data_fine.getDate() + "T" + ora_fine + ":" + (minuto_fine<10?"0":"") + minuto_fine + ":00.000Z");
            endTime.put("date", null);

            sharedProperties.put("requiredParents", 1);
            sharedProperties.put("requiredChildren", 1);
            sharedProperties.put("cost", 10);
            sharedProperties.put("parents", "[]");
            sharedProperties.put("children", "[]");
            sharedProperties.put("status", "ongoing");
            sharedProperties.put("activityColor", hexColor);
            sharedProperties.put("groupId", group_id);
            sharedProperties.put("repetition", "weekly");

            extendedProperties.put("shared", sharedProperties);

            event.put("description", description);
            event.put("location", position);
            event.put("summary", title + " timeslot");
            event.put("start", startTime);
            event.put("end", endTime);
            event.put("extendedProperties", extendedProperties);

            events.put(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (guard) {
            createActivity(token, group_id, user_id, activity, events);
        }
    }

    @Override
    public void onError(VerificationError verificationError) { }

    @Override
    public void onStepSelected(int newStepPosition) { }

    @Override
    public void onReturn() { }

    private void createActivity(String token, String id, String user_id, JSONObject activity, JSONArray events) {
        compositeDisposable.add(myAPI.createActivity(token, id, user_id, activity, events)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->{
                            Toast.makeText(ActivitiesCreationActivity.this, "Activity created with success", Toast.LENGTH_LONG).show();
                            Intent myIntent = new Intent(ActivitiesCreationActivity.this, GroupActivity.class);
                            myIntent.putExtra("group_id", group_id);
                            ActivitiesCreationActivity.this.startActivity(myIntent);
                        },
                        t -> {
                            Toast.makeText(this, "GREENPASS required but not available", Toast.LENGTH_LONG).show();
                            Log.d("HTTP POST ACTIVITY REQUEST ERROR", t.getMessage());
                            reset();
                })
        );
    }

    private void reset() {
        Intent myIntent = new Intent(ActivitiesCreationActivity.this, GroupActivity.class);
        myIntent.putExtra("group_id", group_id);
        ActivitiesCreationActivity.this.startActivity(myIntent);
    }
}