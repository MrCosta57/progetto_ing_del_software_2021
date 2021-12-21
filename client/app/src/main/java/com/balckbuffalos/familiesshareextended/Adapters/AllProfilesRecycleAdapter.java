package com.balckbuffalos.familiesshareextended.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balckbuffalos.familiesshareextended.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class AllProfilesRecycleAdapter extends RecyclerView.Adapter<AllProfilesRecycleAdapter.ViewHolder> implements Filterable {

    private final ArrayList<String> mProfileName;
    private final ArrayList<String> mProfileNameAll;
    private final ArrayList<String> mProfileEmail;
    private final ArrayList<String> mProfileId;
    private final ArrayList<String> ids=new ArrayList<>();

    //private final Context mContext;

    public AllProfilesRecycleAdapter(/*Context mContext,*/ArrayList<String> mProfileName, ArrayList<String> mProfileEmail, ArrayList<String> mProfileId) {
        this.mProfileName = mProfileName;
        this.mProfileEmail = mProfileEmail;
        this.mProfileNameAll=new ArrayList<>(mProfileName);
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
        String tmp_id=mProfileId.get(position);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            final String id=tmp_id;

            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()){
                    ids.add(id);
                }else {
                    ids.remove(id);
                }
            }
        });
    }

    public ArrayList<String> getSelectedIds(){
        return ids;
    }

    @Override
    public int getItemCount() {
        return mProfileName.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter=new Filter() {

        //run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList=new ArrayList<>(mProfileNameAll);

            if (constraint.toString().isEmpty()){
                filteredList.addAll(mProfileNameAll);
            }else{
                for(String profile : mProfileNameAll){
                    if(profile.toLowerCase(Locale.ROOT).contains(constraint.toString().toLowerCase(Locale.ROOT))){
                        filteredList.add(profile);
                    }
                }
            }

            FilterResults filterResults=new FilterResults();
            filterResults.values=filteredList;

            return filterResults;
        }

        //run on ui thread
        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mProfileName.clear();
            mProfileName.addAll((Collection<? extends String>) results.values);
            notifyDataSetChanged();
        }
    };


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
