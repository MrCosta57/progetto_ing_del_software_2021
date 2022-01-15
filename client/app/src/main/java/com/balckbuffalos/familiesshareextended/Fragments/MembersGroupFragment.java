package com.balckbuffalos.familiesshareextended.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.balckbuffalos.familiesshareextended.Adapters.MemberRecycleAdapter;
import com.balckbuffalos.familiesshareextended.R;
import com.balckbuffalos.familiesshareextended.Retrofit.INodeJS;
import com.balckbuffalos.familiesshareextended.Retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MembersGroupFragment extends Fragment {

    private INodeJS myAPI;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final ArrayList<String> mMemberName = new ArrayList<>();
    private final ArrayList<Boolean> mMemberRole = new ArrayList<>();

    private View view;

    public MembersGroupFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_members_group, container, false);

        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        Bundle extras = this.getArguments();

        assert extras != null;
        String group_id = extras.getString("group_id");
        String token = extras.getString("token");
        String user_id = extras.getString("user_id");

        mMemberName.clear();
        mMemberRole.clear();
        memberList(token, group_id, user_id);
        return view;
    }

    private void initMemberRecycler(){
        RecyclerView memberRecyclerView = view.findViewById(R.id.memberRecycler);
        MemberRecycleAdapter adapter = new MemberRecycleAdapter(mMemberName, mMemberRole);
        memberRecyclerView.addItemDecoration(new DividerItemDecoration(memberRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        memberRecyclerView.setAdapter(adapter);
        memberRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void memberList(String token, String id, String user_id) {
        compositeDisposable.add(myAPI.membersList(token, id, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONArray arr = new JSONArray(s);
                    String[] ids = new String[arr.length()];
                    for(int i = 0; i<arr.length();i++)
                    {
                        JSONObject obj = arr.getJSONObject(i);
                        mMemberRole.add(obj.getBoolean("admin"));
                        ids[i]=obj.getString("user_id");
                    }
                    profileInfo(token, ids);
                }, t -> Log.d("HTTP GET MEMBERS OF GROUP ["+id+"] REQUEST ERROR", t.getMessage()))
        );
    }

    private void profileInfo(String token, String[] ids) {
        compositeDisposable.add(myAPI.profilesInfo(token,"ids", ids, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    JSONArray arr = new JSONArray(s);
                    for(int i = 0; i<arr.length();i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        mMemberName.add(obj.getString("given_name") + obj.getString("family_name"));
                    }
                    initMemberRecycler();
                }, t -> Log.d("HTTP GET PROFILEINFOS "+ Arrays.toString(ids) +" REQUEST ERROR", t.getMessage()))
        );
    }
}