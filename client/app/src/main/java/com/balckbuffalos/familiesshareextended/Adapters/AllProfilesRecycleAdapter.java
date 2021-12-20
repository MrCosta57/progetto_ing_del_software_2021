package com.balckbuffalos.familiesshareextended.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balckbuffalos.familiesshareextended.R;

import java.util.ArrayList;

public class AllProfilesRecycleAdapter extends RecyclerView.Adapter<AllProfilesRecycleAdapter.ViewHolder> {

    private final ArrayList<String> mProfileName;
    private final ArrayList<String> mProfileEmail;
    private final ArrayList<String> mProfileId;
    private final ArrayList<String> ids=new ArrayList<>();

    //private final Context mContext;

    public AllProfilesRecycleAdapter(Context mContext, ArrayList<String> mProfileName, ArrayList<String> mProfileEmail, ArrayList<String> mProfileId) {
        this.mProfileName = mProfileName;
        this.mProfileEmail = mProfileEmail;
        //this.mContext = mContext;
        this.mProfileId = mProfileId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.profileName.setText(mProfileName.get(position));
        holder.profileEmail.setText(mProfileEmail.get(position));
        holder.profileIcon.setImageResource(R.drawable.user_icon);
        //TODO
        /*holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curr_pos=position;
                if (holder.checkBox.isChecked()){
                    ids.add(mProfileId.get(position));
                }else {
                    ids.remove(mProfileId.get(position));
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mProfileName.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView profileName, profileEmail;
        ImageView profileIcon;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileName = itemView.findViewById(R.id.member_name);
            profileEmail = itemView.findViewById(R.id.profile_email);
            profileIcon = itemView.findViewById(R.id.member_icon);
            checkBox=itemView.findViewById(R.id.profile_checkBox);
        }
    }
}
