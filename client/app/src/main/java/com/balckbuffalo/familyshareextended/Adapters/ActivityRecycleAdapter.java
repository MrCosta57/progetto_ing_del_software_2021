package com.balckbuffalo.familyshareextended.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balckbuffalo.familyshareextended.R;

import java.util.ArrayList;

public class ActivityRecycleAdapter extends  RecyclerView.Adapter<ActivityRecycleAdapter.ViewHolder>{
    private final ArrayList<String> mDate;
    private final ArrayList<String> mName;
    private final ArrayList<String> mNAdult;
    private final ArrayList<String> mNChildren;

    private final Context mContext;

    public ActivityRecycleAdapter(Context mContext, ArrayList<String> mDate, ArrayList<String> mName, ArrayList<String> mNAdult, ArrayList<String> mNChildren) {
        this.mDate = mDate;
        this.mName = mName;
        this.mNAdult = mNAdult;
        this.mNChildren = mNChildren;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /*holder.hours.setText(mDate.get(position).getHours());
        holder.day.setText(mDate.get(position).getDay());
        holder.month.setText(mDate.get(position).getMonth());*/

        holder.name_activity.setText(mName.get(position));
        holder.n_adult.setText(mNAdult.get(position));
        holder.n_children.setText(mNChildren.get(position));


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
        }
    }
}
