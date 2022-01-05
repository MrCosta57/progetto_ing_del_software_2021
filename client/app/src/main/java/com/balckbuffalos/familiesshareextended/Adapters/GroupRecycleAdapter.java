package com.balckbuffalos.familiesshareextended.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balckbuffalos.familiesshareextended.GroupActivity;
import com.balckbuffalos.familiesshareextended.R;

import java.util.ArrayList;

public class GroupRecycleAdapter extends  RecyclerView.Adapter<GroupRecycleAdapter.ViewHolder>{

    private final ArrayList<String> mGroupId;
    private final ArrayList<String> mGroupName;
    private final ArrayList<String> mMembers;
    private final ArrayList<Boolean> mVisible;
    private final ArrayList<Boolean> mNotifications;

    private final Context mContext;

    public GroupRecycleAdapter(ArrayList<String> mGroupId, Context mContext, ArrayList<String> mGroupName, ArrayList<String> mMembers, ArrayList<Boolean> mVisible, ArrayList<Boolean> mNotifications) {
        this.mGroupId = mGroupId;
        this.mGroupName = mGroupName;
        this.mMembers = mMembers;
        this.mVisible = mVisible;
        this.mNotifications = mNotifications;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String groupName = mGroupName.get(position);
        String members = "Members: "+ mMembers.get(position);
        String info = mVisible.get(position)?"Info: Everybody is welcome":"Info: Close friends only";

        holder.groupName.setText(groupName);
        holder.members.setText(members);
        holder.info.setText(info);
        holder.group_icon.setImageResource(R.drawable.group_icon);

        if(mNotifications.get(position))
            holder.bell_icon.setVisibility(View.VISIBLE);
        else
            holder.bell_icon.setVisibility(View.INVISIBLE);

        holder.parent_layout.setOnClickListener(v -> {
            Intent myIntent = new Intent(mContext, GroupActivity.class);
            myIntent.putExtra("group_id", mGroupId.get(position));
            mContext.startActivity(myIntent);
        });
    }

    @Override
    public int getItemCount() {
        return mGroupName.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView groupName, members, info;
        ImageView bell_icon, group_icon;
        RelativeLayout parent_layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            group_icon = itemView.findViewById(R.id.group_icon);
            bell_icon = itemView.findViewById(R.id.bell_icon);
            groupName = itemView.findViewById(R.id.edit_user_input);
            members = itemView.findViewById(R.id.n_member);
            info = itemView.findViewById(R.id.group_info);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
