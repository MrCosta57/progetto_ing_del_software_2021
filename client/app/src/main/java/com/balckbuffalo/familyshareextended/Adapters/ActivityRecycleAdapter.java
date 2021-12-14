package com.balckbuffalo.familyshareextended.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balckbuffalo.familyshareextended.R;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

public class ActivityRecycleAdapter extends  RecyclerView.Adapter<ActivityRecycleAdapter.ViewHolder>{
    private final ArrayList<String> mDate;
    private final ArrayList<String> mName;
    private final ArrayList<Integer> mNAdult;
    private final ArrayList<Integer> mNChildren;
    private final ArrayList<Boolean> mGreenPass;

    private final Context mContext;

    public ActivityRecycleAdapter(Context mContext, ArrayList<String> mDate, ArrayList<String> mName, ArrayList<Integer> mNAdult, ArrayList<Integer> mNChildren, ArrayList<Boolean> mGreenPass) {
        this.mDate = mDate;
        this.mName = mName;
        this.mNAdult = mNAdult;
        this.mNChildren = mNChildren;
        this.mContext = mContext;
        this.mGreenPass = mGreenPass;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_item, parent, false);
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

        holder.parent_layout.setOnClickListener(v -> {
            //TODO: manda alla schermata del gruppo relativo a mGroupID(position)
        });
    }

    @Override
    public int getItemCount() {
        return mName.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout parent_layout;
        ImageView green_pass_icon;
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
        }
    }
}
