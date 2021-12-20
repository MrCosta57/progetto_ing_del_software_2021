package com.balckbuffalos.familiesshareextended;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.balckbuffalos.familiesshareextended.Adapters.ActivitiesCreationFragmentAdapter;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ActivitiesCreationActivity extends AppCompatActivity implements StepperLayout.StepperListener {
    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    EditText edt_title, edt_description, edt_position;
    DatePicker startDate, endDate;
    TimePicker startTime, endTime;
    //ActivitiesCreationFragmentAdapter adapter = new ActivitiesCreationFragmentAdapter(getSupportFragmentManager(), this);
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

        StepperLayout mStepperLayout = findViewById(R.id.stepper_layout_activities);
        mStepperLayout.setListener(this);
        //mStepperLayout.setAdapter(adapter);
        mStepperLayout.setAdapter(new ActivitiesCreationFragmentAdapter(getSupportFragmentManager(), this));
    }

    @Override
    public void onCompleted(View completeButton) {
        edt_title = findViewById(R.id.activity_title_text);
        if (edt_title == null) {
            Log.d("Test ", "FUNZIONAAAA");
        }
        edt_description = findViewById(R.id.activity_description_text);
        edt_position = findViewById(R.id.activity_position_text);
        color = findViewById(R.id.preview_selected_color).getSolidColor();
        //color = adapter.step.getmDefaultColor();
        startDate = findViewById(R.id.activity_start_date_picker);
        startTime = findViewById(R.id.activity_start_time_picker);
        endDate = findViewById(R.id.activity_end_date_picker);
        endTime = findViewById(R.id.activity_end_time_picker);

        Date data_inizio = new Date(startDate.getCalendarView().getDate());
        Date data_fine = new Date(endDate.getCalendarView().getDate());
        int ora_inizio = startTime.getCurrentHour();
        int ora_fine = endTime.getCurrentHour();
        int minuto_inizio = startTime.getCurrentMinute();
        int minuto_fine = endTime.getCurrentMinute();

        if(data_inizio.after(data_fine) || (data_inizio.equals(data_fine) && ora_inizio > ora_fine) || (data_inizio.equals(data_fine) && ora_inizio == ora_fine && minuto_inizio >= minuto_fine)){
            Toast.makeText(ActivitiesCreationActivity.this, "INVALID START OR END TIME ATTRIBUTES", Toast.LENGTH_LONG).show();
            return;
        }

        createActivity(edt_title.getText().toString(), edt_description.getText().toString(), edt_position.getText().toString(), color,
                data_inizio, ora_inizio, minuto_inizio, data_fine, ora_fine, minuto_fine);
    }

    @Override
    public void onError(VerificationError verificationError) { }

    @Override
    public void onStepSelected(int newStepPosition) { }

    @Override
    public void onReturn() { }

    private void createActivity(String title, String description, String position, @ColorInt int color,
                                Date startDate, int startHour, int startMinute , Date endDate, int endHour, int endMinute) {
        compositeDisposable.add(myAPI.createActivity(title, description, position, color, startDate, startHour, startMinute, endDate, endHour, endMinute)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->{
                            Toast.makeText(ActivitiesCreationActivity.this, "Activity created with success", Toast.LENGTH_LONG).show();
                        },
                        t -> Toast.makeText(ActivitiesCreationActivity.this, "ERROR "+t.getMessage(), Toast.LENGTH_LONG).show())
        );
    }
}