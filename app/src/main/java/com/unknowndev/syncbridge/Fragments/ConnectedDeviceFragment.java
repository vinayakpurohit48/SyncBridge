package com.unknowndev.syncbridge.Fragments;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.unknowndev.syncbridge.Adapters.ListDeviceAdapter;
import com.unknowndev.syncbridge.Model.SessionModel;
import com.unknowndev.syncbridge.MyServices.MyFirebase;
import com.unknowndev.syncbridge.R;

import java.util.ArrayList;

public class ConnectedDeviceFragment extends Fragment {

    RelativeLayout noDeviceLayout,noInternetLayout;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    ListDeviceAdapter deviceAdapter;

    ArrayList<SessionModel> deviceList = new ArrayList<>();

    public ConnectedDeviceFragment() {

    }

        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connected_device, container, false);
        noDeviceLayout = view.findViewById(R.id.noDeviceLayout);
        noInternetLayout = view.findViewById(R.id.noInternetLayout);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);

        if (isInternetConnected()){
            inProcess(true, false);
            deviceAdapter = new ListDeviceAdapter(getActivity(), deviceList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(deviceAdapter);

            MyFirebase.getMySessionRef().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()){
                        DataSnapshot dataSnapshot = task.getResult();
                        if (dataSnapshot != null && dataSnapshot.hasChildren()){
                            for (DataSnapshot result: dataSnapshot.getChildren()){
                                deviceList.add(result.getValue(SessionModel.class));
                            }
                            deviceAdapter.notifyDataSetChanged();
                            inProcess(false, true);
                        } else {
                            Toast.makeText(getContext(), "No Data found", Toast.LENGTH_SHORT).show();
                            inProcess(false, false);
                        }
                    } else {
                        Log.d("In Firebase", "onComplete: " + task.getException());
                    }
                }
            });

        } else{
            Toast.makeText(getContext(), "Internet not Available", Toast.LENGTH_SHORT).show();
            noDeviceLayout.setVisibility(View.GONE);
            noInternetLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
        return view;
    }
    boolean isInternetConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo()!=null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    void inProcess(boolean inProcess, boolean isContainsData){
        if (inProcess){
            noDeviceLayout.setVisibility(View.GONE);
            noInternetLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            if (isContainsData){
                noDeviceLayout.setVisibility(View.GONE);
                noInternetLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                noDeviceLayout.setVisibility(View.VISIBLE);
                noInternetLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        }
    }
}