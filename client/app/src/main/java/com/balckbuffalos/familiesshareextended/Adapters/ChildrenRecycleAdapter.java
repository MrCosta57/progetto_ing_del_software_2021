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

public class ChildrenRecycleAdapter extends  RecyclerView.Adapter<ChildrenRecycleAdapter.ViewHolder> {

    private final ArrayList<String> mChildrenName;
    private final ArrayList<String> mChildrenBirthdate;

    private final Context mContext;

    public ChildrenRecycleAdapter(Context mContext, ArrayList<String> mChildrenName, ArrayList<String> mChildrenBirthdate) {
        this.mChildrenName = mChildrenName;
        this.mChildrenBirthdate = mChildrenBirthdate;
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
        holder.memberName.setText(mChildrenName.get(position));
        holder.memberRole.setText(mChildrenBirthdate.get(position));
        holder.memberIcon.setImageResource(R.drawable.user_icon);
    }

    @Override
    public int getItemCount() {
        return mChildrenName.size();
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
