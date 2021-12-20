package com.balckbuffalos.familiesshareextended.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balckbuffalos.familiesshareextended.GroupActivity;
import com.balckbuffalos.familiesshareextended.R;

import java.util.ArrayList;

public class FileRecycleAdapter extends  RecyclerView.Adapter<FileRecycleAdapter.ViewHolder>{

    private final ArrayList<String> mFileId;
    private final ArrayList<String> mMemberName;
    private final ArrayList<String> mDescription;
    private final ArrayList<String> mDate;
    private final ArrayList<String> mFileType;

    private final Context mContext;

    public FileRecycleAdapter(Context mContext, ArrayList<String> mFileId, ArrayList<String> mMemberName, ArrayList<String> mDescription, ArrayList<String> mDate, ArrayList<String> mFileType) {
        this.mFileId = mFileId;
        this.mMemberName = mMemberName;
        this.mDescription = mDescription;
        this.mDate = mDate;
        this.mFileType = mFileType;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.memberName.setText(mMemberName.get(position));
        holder.description.setText(mDescription.get(position));
        holder.date.setText(mDate.get(position));

        if(mFileType.get(position).contains("mp3"))
            holder.file_image.setImageResource(R.drawable.file_audio_icon);
        else if(mFileType.get(position).contains("jpeg"))
            holder.file_image.setImageResource(R.drawable.file_picture_icon);
        else
            holder.file_image.setImageResource(R.drawable.file_icon);

        holder.download.setOnClickListener(v -> {
            //TODO: mFileId download
        });
    }

    @Override
    public int getItemCount() {
        return mMemberName.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView memberName, description, date;
        ImageView file_image;
        Button download;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.member_name);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date_text);
            file_image = itemView.findViewById(R.id.file_icon);
            download = itemView.findViewById(R.id.download_button);
        }
    }
}
