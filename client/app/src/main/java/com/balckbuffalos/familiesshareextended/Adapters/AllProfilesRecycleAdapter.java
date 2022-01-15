package com.balckbuffalos.familiesshareextended.Adapters;

import android.annotation.SuppressLint;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Locale;

public class AllProfilesRecycleAdapter extends RecyclerView.Adapter<AllProfilesRecycleAdapter.ViewHolder> implements Filterable {

    private JSONArray mProfileInfo;
    private final JSONArray mProfileInfoAll;
    private final ArrayList<String> ids=new ArrayList<>();


    public AllProfilesRecycleAdapter(JSONArray mProfileInfo) throws JSONException {
        this.mProfileInfoAll=new JSONArray();
        this.mProfileInfo=mProfileInfo;

        for (int i = 0; i < mProfileInfo.length(); i++) {
            this.mProfileInfoAll.put(mProfileInfo.getJSONObject(i));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try{
            JSONObject tmp=mProfileInfo.getJSONObject(position);
            String name_surname=tmp.getString("given_name")+tmp.getString("family_name");

            holder.profileName.setText(name_surname);
            holder.profileEmail.setText(tmp.getString("email"));
            holder.profileIcon.setImageResource(R.drawable.user_icon);
            String tmp_id = tmp.getString("user_id");

            holder.checkBox.setChecked(ids.contains(tmp_id));

            //Check/uncheck the checkbox to add/remove user to the group initial members
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
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public ArrayList<String> getSelectedIds(){
        return ids;
    }

    @Override
    public int getItemCount() {
        return mProfileInfo.length();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    //Perform searching between all users using the query typed in the UI search bar by the user
    private final Filter filter=new Filter() {

        //run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults=null;
            try {
                JSONArray filteredArray = new JSONArray();

                if (constraint.toString().isEmpty()) {
                    for (int i = 0; i < mProfileInfoAll.length(); i++) {
                        filteredArray.put(mProfileInfoAll.getJSONObject(i));
                    }
                } else {
                    for (int i = 0; i < mProfileInfoAll.length(); i++) {
                        JSONObject profile=(JSONObject) mProfileInfoAll.get(i);
                        if (profile.getString("email").toLowerCase(Locale.ROOT).contains(constraint.toString().toLowerCase(Locale.ROOT))) {
                            filteredArray.put(profile);
                        }
                    }
                }

                filterResults = new FilterResults();
                filterResults.values = filteredArray;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return filterResults;
        }

        //run on ui thread
        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mProfileInfo=new JSONArray();
            try {
                for (int i = 0; i < ((JSONArray) results.values).length(); i++) {
                    mProfileInfo.put(((JSONArray)results.values).getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
