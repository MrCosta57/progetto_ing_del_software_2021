package com.balckbuffalos.familiesshareextended.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.balckbuffalos.familiesshareextended.GroupActivity;
import com.balckbuffalos.familiesshareextended.HomePageActivity;
import com.balckbuffalos.familiesshareextended.R;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.balckbuffalos.familiesshareextended.SplashScreenActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.reactivestreams.Subscriber;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FileRecycleAdapter extends  RecyclerView.Adapter<FileRecycleAdapter.ViewHolder>{

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final ArrayList<String> mFileId;
    private final ArrayList<String> mMemberName;
    private final ArrayList<String> mDescription;
    private final ArrayList<String> mDate;
    private final ArrayList<String> mFileType;
    private final ArrayList<String> mCreatorId;

    String group_id, token, user_id;

    private final Context mContext;

    public FileRecycleAdapter(Context mContext, ArrayList<String> mFileId, ArrayList<String> mMemberName, ArrayList<String> mDescription, ArrayList<String> mDate, ArrayList<String> mFileType, ArrayList<String> mCreatorId, String group_id, String user_id, String token) {
        this.mFileId = mFileId;
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

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.memberName.setText(mMemberName.get(position));
        holder.description.setText(mDescription.get(position));
        holder.date.setText(mDate.get(position));
        String file_id = mFileId.get(position);
        String creator_id = mCreatorId.get(position);
        int pos = position;
        if(creator_id.equals(user_id))
            holder.trash_image.setVisibility(View.VISIBLE);

        holder.trash_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(creator_id.equals(user_id))
                    deleteFile(token, group_id, user_id, file_id, pos);
            }
        });

        if(mFileType.get(position).contains("audio"))
            holder.file_image.setImageResource(R.drawable.file_audio_icon);
        else if(mFileType.get(position).contains("image"))
            holder.file_image.setImageResource(R.drawable.file_picture_icon);
        else
            holder.file_image.setImageResource(R.drawable.file_icon);

        holder.download.setOnClickListener(v -> {
            getFile(token, group_id, user_id, file_id);
        });
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
                }, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }

    private void getFile(String token, String group_id, String user_id, String file_id) {
        myAPI.getFile(token, group_id, file_id, user_id)
                .flatMap(new Function<Response<ResponseBody>, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<File>() {
                            @Override
                            public void subscribe(@NonNull ObservableEmitter<File> emitter) throws Exception {
                                try {
                                    // you can access headers of response
                                    String header = responseBodyResponse.headers().get("Content-Disposition");
                                    // this is specific case, it's up to you how you want to save your file
                                    // if you are not downloading file from direct link, you might be lucky to obtain file name from header
                                    String fileName = header.replace("attachment; filename=", "");
                                    // will create file in global Music directory, can be any other directory, just don't forget to handle permissions
                                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile(), fileName);

                                    BufferedSink sink = Okio.buffer(Okio.sink(file));
                                    // you can access body of response
                                    assert responseBodyResponse.body() != null;
                                    sink.writeAll(responseBodyResponse.body().source());
                                    sink.close();
                                    emitter.onNext(file);
                                    emitter.onComplete();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    emitter.onError(e);
                                }
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<File>() {
                               @Override
                               public void onSubscribe(@NonNull Disposable d) {

                               }

                               @Override
                               public void onNext(@NonNull File file) {
                                   Log.d("downloadZipFile", "File downloaded to " + file.getAbsolutePath());
                               }

                               @Override
                               public void onError(@NonNull Throwable e) {
                                   e.printStackTrace();
                                   Log.d("downloadZipFile", "Error " + e.getMessage());
                               }

                               @Override
                               public void onComplete() {
                                   Log.d("downloadZipFile", "onCompleted");
                               }
                           });
    }
}
