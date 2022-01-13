package com.balckbuffalos.familiesshareextended.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.balckbuffalos.familiesshareextended.Adapters.FileRecycleAdapter;
import com.balckbuffalos.familiesshareextended.R;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;
import com.balckbuffalos.familiesshareextended.Utility.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

public class CabinetGroupFragment extends Fragment {

    private INodeJS myAPI;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String group_id, token, user_id;

    private String description = "";

    private final ArrayList<String> mFileId = new ArrayList<>();
    private final ArrayList<String> mFileName = new ArrayList<>();
    private final ArrayList<String> mMemberName = new ArrayList<>();
    private final ArrayList<String> mDescription = new ArrayList<>();
    private final ArrayList<String> mDate = new ArrayList<>();
    private final ArrayList<String> mFileType = new ArrayList<>();
    private final ArrayList<String> mCreatorId = new ArrayList<>();

    private View view;

    public CabinetGroupFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_cabinet_group, container, false);

        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);

        if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

        Bundle extras = this.getArguments();

        assert extras != null;
        group_id = extras.getString("group_id");
        token = extras.getString("token");
        user_id = extras.getString("user_id");

        view.findViewById(R.id.floating_file_button).setOnClickListener(view -> showPopup());

        mFileId.clear();
        mFileName.clear();
        mMemberName.clear();
        mDescription.clear();
        mDate.clear();
        mFileType.clear();
        fileList(token, group_id, user_id);
        return view;
    }

    private void showPopup(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity()).setTitle("Select File");

        final EditText input = new EditText (getActivity());
        input.setHint("description");
        alertDialogBuilder.setView(input);

        alertDialogBuilder.setCancelable(false).setPositiveButton("SELECT FILE", (dialog, id) -> {
            description = input.getText().toString();
            openFileDialog();

        });

        alertDialogBuilder.setCancelable(false).setNegativeButton("CANCEL", (dialog, id) -> { });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    ActivityResultLauncher<Intent> sActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK)
                    {
                        assert result.getData() != null;
                        Uri uri = result.getData().getData();
                        File file = FileUtils.getFile(getActivity(), uri);

                        RequestBody requestFile = RequestBody.create(
                                MediaType.parse(requireActivity().getContentResolver().getType(uri)),
                                file);

                        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file",file.getName(),requestFile);
                        addFile(token, group_id, user_id, description, multipartBody);
                    }
                }
            }
    );

    public void openFileDialog() {
        Intent data = new Intent(Intent.ACTION_GET_CONTENT);
        data.setType("*/*");
        data = Intent.createChooser(data, "Choose a file");
        sActivityResultLauncher.launch(data);
    }

    private void initFileRecycler(){
        RecyclerView fileRecyclerView = view.findViewById(R.id.file_recycler);
        FileRecycleAdapter adapter = new FileRecycleAdapter(getActivity(), mFileId, mFileName, mMemberName, mDescription, mDate, mFileType, mCreatorId, group_id, user_id, token);
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
                        mFileName.add(obj.getString("name"));
                        mDescription.add(obj.getString("description"));
                        mDate.add(obj.getString("date").substring(0,10));
                        mFileType.add(obj.getString("contentType"));
                        mMemberName.add(obj.getString("creator_name"));
                        mCreatorId.add(obj.getString("creator_id"));
                    }
                    initFileRecycler();
                }, t -> Log.d("HTTP GET FILE FROM GROUP ["+id+"]REQUEST ERROR", t.getMessage()))
        );
    }

    private void addFile(String token, String id, String user_id, String description, MultipartBody.Part file) {
        compositeDisposable.add(myAPI.addFile(token, id, user_id, description, file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    mFileId.clear();
                    mFileName.clear();
                    mMemberName.clear();
                    mDescription.clear();
                    mDate.clear();
                    mFileType.clear();
                    mCreatorId.clear();
                    fileList(token, group_id, user_id);
                }, t -> Log.d("HTTP POST FILE REQUEST ERROR", t.getMessage()))
        );
    }
}