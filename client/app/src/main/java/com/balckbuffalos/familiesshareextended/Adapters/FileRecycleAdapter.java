package com.balckbuffalos.familiesshareextended.Adapters;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.balckbuffalos.familiesshareextended.R;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

public class FileRecycleAdapter extends  RecyclerView.Adapter<FileRecycleAdapter.ViewHolder>{

    private INodeJS myAPI;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final ArrayList<String> mFileId;
    private final ArrayList<String> mFileName;
    private final ArrayList<String> mMemberName;
    private final ArrayList<String> mDescription;
    private final ArrayList<String> mDate;
    private final ArrayList<String> mFileType;
    private final ArrayList<String> mCreatorId;

    private NotificationManager mNotificationManager;

    private final String group_id, token, user_id;

    private final Context mContext;

    public FileRecycleAdapter(Context mContext, ArrayList<String> mFileId, ArrayList<String> mFileName, ArrayList<String> mMemberName, ArrayList<String> mDescription, ArrayList<String> mDate, ArrayList<String> mFileType, ArrayList<String> mCreatorId, String group_id, String user_id, String token) {
        this.mFileId = mFileId;
        this.mFileName = mFileName;
        this.mMemberName = mMemberName;
        this.mDescription = mDescription;
        this.mDate = mDate;
        this.mFileType = mFileType;
        this.mContext = mContext;
        this.mCreatorId = mCreatorId;
        this.group_id = group_id;
        this.token=token;
        this.user_id=user_id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_item, parent, false);

        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);
        mNotificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.memberName.setText(mMemberName.get(position));
        holder.description.setText(mDescription.get(position));
        holder.date.setText(mDate.get(position));

        //if the user is the creator of the file --> he can delete it
        String file_id = mFileId.get(position);
        String creator_id = mCreatorId.get(position);
        if(creator_id.equals(user_id))
            holder.trash_image.setVisibility(View.VISIBLE);
        holder.trash_image.setOnClickListener(view -> {
            if(creator_id.equals(user_id))
                deleteFile(token, group_id, user_id, file_id, position);
        });

        //setting the icon related to MimeType
        if(mFileType.get(position).contains("audio"))
            holder.file_image.setImageResource(R.drawable.file_audio_icon);
        else if(mFileType.get(position).contains("image"))
            holder.file_image.setImageResource(R.drawable.file_picture_icon);
        else
            holder.file_image.setImageResource(R.drawable.file_icon);

        holder.download.setOnClickListener(v -> getFile(token, group_id, user_id, file_id, mFileName.get(position), position));
    }

    @Override
    public int getItemCount() {
        return mMemberName.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView memberName, description, date;
        ImageView file_image, trash_image;
        Button download;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.member_name);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date_text);
            file_image = itemView.findViewById(R.id.file_icon);
            download = itemView.findViewById(R.id.download_button);
            trash_image = itemView.findViewById(R.id.trash_bin_icon);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteFile(String token, String group_id, String user_id, String file_id, int position) {
        compositeDisposable.add(myAPI.deleteFile(token, group_id, file_id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    mFileId.remove(position);
                    mMemberName.remove(position);
                    mDescription.remove(position);
                    mDate.remove(position);
                    mFileType.remove(position);
                    mCreatorId.remove(position);
                    this.notifyDataSetChanged();
                }, t -> Log.d("HTTP DELETE FILE ["+file_id+"] REQUEST ERROR", t.getMessage()))
        );
    }

    @SuppressWarnings("deprecation")
    @SuppressLint({"StaticFieldLeak", "ShowToast"})
    private void getFile(String token, String group_id, String user_id, String file_id, String file_name, int id) {
        compositeDisposable.add(myAPI.getFile(token, group_id, file_id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    //download del file in background
                    new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        //notification of downloading
                        createNotification(file_name, id);
                        assert s.body() != null;
                        boolean writtenToDisk = writeResponseBodyToDisk(s.body(), file_name);

                        Log.d("DOWNLOAD INFO", "file download was a success? " + writtenToDisk);
                        //notification of end download
                        finishNotifications(file_name, id);
                        return null;
                    }
                }.execute();},
                        t ->Log.d("HTTP GET FILE ["+file_id+"] REQUEST ERROR", t.getMessage())));
    }

    // usign inputStream and Output stream to save picture in download folder
    private boolean writeResponseBodyToDisk(ResponseBody body, String file_name) {
        try {
            File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + file_name);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                }

                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    private void createNotification(String contentTitle, int id) {

        mNotificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //Build the notification using Notification.Builder
        Notification.Builder builder = new Notification.Builder(mContext)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setAutoCancel(true)
                .setContentTitle(contentTitle)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentText("downloading");

        //Show the notification

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = String.valueOf(id);
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        mNotificationManager.notify(id, builder.build());
    }

    private void finishNotifications(String contentTitle, int id) {

        mNotificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //Build the notification using Notification.Builder
        Notification.Builder builder = new Notification.Builder(mContext)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setAutoCancel(true)
                .setContentTitle(contentTitle)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentText("download completed");

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = String.valueOf(id);
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        //Show the notification
        mNotificationManager.notify(id, builder.build());
    }
}
