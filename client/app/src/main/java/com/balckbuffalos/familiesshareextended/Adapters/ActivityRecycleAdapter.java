package com.balckbuffalos.familiesshareextended.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balckbuffalos.familiesshareextended.ActivitiesInfoActivity;
import com.balckbuffalos.familiesshareextended.R;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ActivityRecycleAdapter extends  RecyclerView.Adapter<ActivityRecycleAdapter.ViewHolder>{

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final ArrayList<String> mActivityId;
    private final ArrayList<String> mGroupId;
    private final ArrayList<String> mCreatorId;
    private final ArrayList<String> mDate;
    private final ArrayList<String> mName;
    private final ArrayList<Integer> mNAdult;
    private final ArrayList<Integer> mNChildren;
    private final ArrayList<Boolean> mGreenPass;

    String token, user_id;

    private final Context mContext;

    public ActivityRecycleAdapter(Context mContext, ArrayList<String> mActivityId, ArrayList<String> mGroupId, ArrayList<String> mCreatorId, ArrayList<String> mDate, ArrayList<String> mName, ArrayList<Integer> mNAdult, ArrayList<Integer> mNChildren, ArrayList<Boolean> mGreenPass, String user_id, String token) {
        this.mActivityId = mActivityId;
        this.mGroupId = mGroupId;
        this.mCreatorId = mCreatorId;
        this.mDate = mDate;
        this.mName = mName;
        this.mNAdult = mNAdult;
        this.mNChildren = mNChildren;
        this.mContext = mContext;
        this.mGreenPass = mGreenPass;
        this.token=token;
        this.user_id=user_id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_item, parent, false);

        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mDate.get(position).equals("N/D")) {
            holder.hours.setText("N/D");
            holder.day.setText("N/D");
            holder.month.setText("N/D");
        }
        else {
            String dateTime = mDate.get(position).substring(11, 16) + " - " + mDate.get(position).substring(31, 36);
            holder.hours.setText(dateTime);
            holder.day.setText(mDate.get(position).substring(8, 10));
            holder.month.setText(new DateFormatSymbols().getMonths()[Integer.parseInt(mDate.get(position).substring(5, 7)) - 1]);
        }

        holder.name_activity.setText(mName.get(position));
        holder.n_adult.setText(mNAdult.get(position).toString());
        holder.n_children.setText(mNChildren.get(position).toString());

        if (mGreenPass.get(position)) {
            holder.green_pass_icon.setVisibility(View.VISIBLE);
        } else {
            holder.green_pass_icon.setVisibility(View.INVISIBLE);
        }

        String creator_id = mCreatorId.get(position);
        String activity_id = mActivityId.get(position);
        int pos = position;
        if(creator_id.equals(user_id))
            holder.trash_image.setVisibility(View.VISIBLE);

        holder.trash_image.setOnClickListener(view -> {
            if(creator_id.equals(user_id))
                deleteActivity(token, mGroupId.get(pos), user_id, activity_id, pos);
        });

        holder.parent_layout.setOnClickListener(v -> {
            Intent myIntent = new Intent(mContext, ActivitiesInfoActivity.class);
            myIntent.putExtra("group_id", mGroupId.get(position));
            myIntent.putExtra("activity_id", mActivityId.get(position));
            mContext.startActivity(myIntent);
        });
    }

    @Override
    public int getItemCount() {
        return mName.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout parent_layout;
        ImageView green_pass_icon, trash_image;
        TextView hours, day, month, name_activity, n_adult, n_children;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            hours = itemView.findViewById(R.id.hours_text);
            day = itemView.findViewById(R.id.day_text);
            month = itemView.findViewById(R.id.month_text);
            name_activity = itemView.findViewById(R.id.activity_text);
            n_adult = itemView.findViewById(R.id.n_adult_text);
            n_children = itemView.findViewById(R.id.n_children_text);
            green_pass_icon = itemView.findViewById(R.id.green_pass_icon);
            trash_image = itemView.findViewById(R.id.trash_bin_icon);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteActivity(String token, String group_id, String user_id, String activity_id, int position) {
        compositeDisposable.add(myAPI.deleteActivity(token, group_id, activity_id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    mActivityId.remove(position);
                    mGroupId.remove(position);
                    mCreatorId.remove(position);
                    mDate.remove(position);
                    mName.remove(position);
                    mNAdult.remove(position);
                    mNChildren.remove(position);
                    mGreenPass.remove(position);
                    this.notifyDataSetChanged();
                }, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }
}
