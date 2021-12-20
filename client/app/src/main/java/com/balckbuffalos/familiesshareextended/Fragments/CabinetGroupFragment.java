package com.balckbuffalos.familiesshareextended.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.balckbuffalos.familiesshareextended.Adapters.ActivityRecycleAdapter;
import com.balckbuffalos.familiesshareextended.Adapters.FileRecycleAdapter;
import com.balckbuffalos.familiesshareextended.Adapters.GroupRecycleAdapter;
import com.balckbuffalos.familiesshareextended.R;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

public class CabinetGroupFragment extends Fragment {

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    String group_id, token, user_id;

    Uri uri;
    String description = "";

    private final ArrayList<String> mFileId = new ArrayList<>();
    private final ArrayList<String> mMemberName = new ArrayList<>();
    private final ArrayList<String> mDescription = new ArrayList<>();
    private final ArrayList<String> mDate = new ArrayList<>();
    private final ArrayList<String> mFileType = new ArrayList<>();

    View view;

    public CabinetGroupFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_cabinet_group, container, false);

        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        Bundle extras = this.getArguments();

        group_id = extras.getString("group_id");
        token = extras.getString("token");
        user_id = extras.getString("user_id");

        view.findViewById(R.id.floating_file_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup();
            }
        });

        mFileId.clear();
        mMemberName.clear();
        mDescription.clear();
        mDate.clear();
        mFileType.clear();
        fileList(token, group_id, user_id);
        return view;
    }

    private void showPopup(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity()).setTitle("Load File");
        @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.popup_insert_file, null);


        final EditText input = new EditText (getActivity());
        input.setHint("description");
        alertDialogBuilder.setView(input);

        alertDialogBuilder.setCancelable(false).setPositiveButton("LOAD FILE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                description = input.getText().toString();
                performFileSearch("LOAD FILE");
            }
        });

        alertDialogBuilder.setCancelable(false).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent resultData) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK)
        {
            if (resultData != null && resultData.getData() != null) {
                uri = resultData.getData();

                File file = new File(getPath(uri));
                ContentResolver cR = getActivity().getContentResolver();

                RequestBody requestFile = RequestBody.create(MediaType.parse(cR.getType(uri)), file);
                Log.d("FILENAME", file.getName());
                MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file",file.getName(),requestFile);
                addFile(token, group_id, user_id, description, multipartBody);
            } else {
                Log.d("TEST","File uri not found {}");
            }
        }
        else {
            Log.d("TEST","User cancelled file browsing {}");
        }
    }

    public String getPath(Uri uri) {

        String path = null;
        String[] projection = { MediaStore.Files.FileColumns.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);

        if(cursor == null){
            path = uri.getPath();
        }
        else{
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return (path == null || path.isEmpty())?uri.getPath():path;
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void performFileSearch(String messageTitle) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.setType("*/*");
        /*String[] mimeTypes = new String[]{"application/x-binary,application/octet-stream"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);*/

        startActivityForResult(Intent.createChooser(intent, messageTitle), 0);

    }







    private void initFileRecycler(){
        RecyclerView fileRecyclerView = view.findViewById(R.id.file_recycler);
        FileRecycleAdapter adapter = new FileRecycleAdapter(getActivity(), mFileId, mMemberName, mDescription, mDate, mFileType, group_id, user_id, token);
        fileRecyclerView.addItemDecoration(new DividerItemDecoration(fileRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        fileRecyclerView.setAdapter(adapter);
        fileRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    private void fileList(String token, String id, String user_id) {
        compositeDisposable.add(myAPI.listFiles(token, id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONArray arr = new JSONArray(s);
                    for(int i = 0; i<arr.length();i++)
                    {
                        JSONObject obj = arr.getJSONObject(i);
                        mFileId.add(obj.getString("file_id"));
                        mDescription.add(obj.getString("description"));
                        mDate.add(obj.getString("date").substring(0,10));
                        mFileType.add(obj.getString("contentType"));
                        mMemberName.add(obj.getString("creator_name"));
                    }
                    initFileRecycler();
                }, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }

    private void addFile(String token, String id, String user_id, String description, MultipartBody.Part file) {
        compositeDisposable.add(myAPI.addFile(token, id, user_id, description, file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    mFileId.clear();
                    mMemberName.clear();
                    mDescription.clear();
                    mDate.clear();
                    mFileType.clear();
                    fileList(token, group_id, user_id);
                }, t -> Log.d("HTTP REQUEST ERROR: ", t.getMessage()))
        );
    }
}