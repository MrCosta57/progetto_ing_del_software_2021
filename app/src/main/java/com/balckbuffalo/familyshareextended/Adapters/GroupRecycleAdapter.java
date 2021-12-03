package com.balckbuffalo.familyshareextended.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balckbuffalo.familyshareextended.R;

import java.util.ArrayList;

public class GroupRecycleAdapter extends  RecyclerView.Adapter<GroupRecycleAdapter.ViewHolder>{

    private ArrayList<String> mGroupId = new ArrayList<>();
    private ArrayList<String> mMembers = new ArrayList<>();
    private ArrayList<Boolean> mVisible = new ArrayList<>();
    private ArrayList<Boolean> mNotifications = new ArrayList<>();

    private Context mContext;

    public GroupRecycleAdapter(Context mContext, ArrayList<String> mGroupId, ArrayList<String> mMembers, ArrayList<Boolean> mVisible, ArrayList<Boolean> mNotifications) {
        this.mGroupId = mGroupId;
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
        String groupId = "Group #"+ mGroupId.get(position);
        String members = "Members: "+ mMembers.get(position);
        String info = mVisible.get(position)?"Info: Everybody is welcome":"Info: Close friends only";

        holder.groupId.setText(groupId);
        holder.members.setText(members);
        holder.info.setText(info);

        if(mNotifications.get(position))
            holder.bell.setVisibility(View.VISIBLE);
        else
            holder.bell.setVisibility(View.INVISIBLE);

        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: manda alla schermata del gruppo relativo a mGroupID(pposition)
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGroupId.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView groupId, members, info;
        ImageView bell;
        RelativeLayout parent_layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bell = itemView.findViewById(R.id.bell_icon);
            groupId = itemView.findViewById(R.id.group_id);
            members = itemView.findViewById(R.id.n_members);
            info = itemView.findViewById(R.id.group_info);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
