package com.balckbuffalos.familiesshareextended.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balckbuffalos.familiesshareextended.R;

import java.util.ArrayList;

public class MemberRecycleAdapter extends  RecyclerView.Adapter<MemberRecycleAdapter.ViewHolder> {

    private final ArrayList<String> mMemberName;
    private final ArrayList<Boolean> mMemberAdmin;

    private final Context mContext;

    public MemberRecycleAdapter(Context mContext, ArrayList<String> mMemberName, ArrayList<Boolean> mMemberAdmin) {
        this.mMemberName = mMemberName;
        this.mMemberAdmin = mMemberAdmin;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.memberName.setText(mMemberName.get(position));
        holder.memberRole.setText(mMemberAdmin.get(position) ? "Role: group admin" : "Role: group partecipant");
        holder.memberIcon.setImageResource(R.drawable.user_icon);
    }

    @Override
    public int getItemCount() {
        return mMemberName.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView memberName, memberRole;
        ImageView memberIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.member_name);
            memberRole = itemView.findViewById(R.id.role_member);
            memberIcon = itemView.findViewById(R.id.member_icon);
        }
    }
}
