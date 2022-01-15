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

import com.balckbuffalos.familiesshareextended.GroupActivity;
import com.balckbuffalos.familiesshareextended.R;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class GroupRecycleAdapter extends  RecyclerView.Adapter<GroupRecycleAdapter.ViewHolder>{

    private INodeJS myAPI;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final ArrayList<String> mGroupId;
    private final ArrayList<String> mGroupName;
    private final ArrayList<String> mMembers;
    private final ArrayList<Boolean> mVisible;
    private final ArrayList<Boolean> mNotifications;
    private final ArrayList<Boolean> mAdmin;

    private final Context mContext;

    private final String token, user_id;

    public GroupRecycleAdapter(ArrayList<String> mGroupId, Context mContext, ArrayList<String> mGroupName, ArrayList<String> mMembers, ArrayList<Boolean> mVisible, ArrayList<Boolean> mNotifications, ArrayList<Boolean> mAdmin, String token, String user_id) {
        this.mGroupId = mGroupId;
        this.mGroupName = mGroupName;
        this.mMembers = mMembers;
        this.mVisible = mVisible;
        this.mNotifications = mNotifications;
        this.mAdmin = mAdmin;
        this.mContext = mContext;
        this.token = token;
        this.user_id = user_id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_item, parent, false);

        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

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

        //if the user has notifications not read
        if(mNotifications.get(position))
            holder.bell_icon.setVisibility(View.VISIBLE);
        else
            holder.bell_icon.setVisibility(View.INVISIBLE);

        //clicking of recycleview item send you to group page
        holder.parent_layout.setOnClickListener(v -> {
            Intent myIntent = new Intent(mContext, GroupActivity.class);
            myIntent.putExtra("group_id", mGroupId.get(position));
            mContext.startActivity(myIntent);
        });

        //if the user is the admin of the group --> he can delete it
        if(mAdmin.get(position))
            holder.trash_image.setVisibility(View.VISIBLE);

        holder.trash_image.setOnClickListener(view -> {
            if(mAdmin.get(position))
                deleteGroup(token, mGroupId.get(position), user_id, position);
        });
    }

    @Override
    public int getItemCount() {
        return mGroupName.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView groupName, members, info;
        ImageView bell_icon, group_icon, trash_image;
        RelativeLayout parent_layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            group_icon = itemView.findViewById(R.id.group_icon);
            bell_icon = itemView.findViewById(R.id.bell_icon);
            groupName = itemView.findViewById(R.id.group_name);
            members = itemView.findViewById(R.id.n_member);
            info = itemView.findViewById(R.id.group_info);
            trash_image = itemView.findViewById(R.id.trash_bin_icon);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteGroup(String token, String group_id, String user_id, int position) {
        compositeDisposable.add(myAPI.deleteGroup(token, group_id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    mGroupId.remove(position);
                    mGroupName.remove(position);
                    mMembers.remove(position);
                    mVisible.remove(position);
                    mNotifications.remove(position);
                    mAdmin.remove(position);
                    this.notifyDataSetChanged();
                }, t -> Log.d("HTTP DELETE GROUP ["+group_id+"] REQUEST ERROR", t.getMessage()))
        );
    }
}
