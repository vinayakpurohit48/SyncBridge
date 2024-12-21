package com.unknowndev.syncbridge;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unknowndev.syncbridge.Adapters.ListDriveAdapter;
import com.unknowndev.syncbridge.Adapters.ListFileAdapter;
import com.unknowndev.syncbridge.Model.DriveModel;
import com.unknowndev.syncbridge.Model.FileData;
import com.unknowndev.syncbridge.Model.SessionModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OtherDeviceDriveActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<FileData> fileList = new ArrayList<>();
    private Stack<String> pathStack = new Stack<>();
    private TextView tvNoFiles, tvDeviceName;
    private ProgressBar progressBar;
    private SessionModel sessionModel;
    private RelativeLayout noInternetLayout;
    private ListDriveAdapter listDriveAdapter;
    private String accessUrl;
    private String TAG = "Kya hua";
    private List<DriveModel> driveModelList = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_other_device_files);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
        tvNoFiles = findViewById(R.id.tvNoFiles);
        progressBar = findViewById(R.id.progressBar);
        tvDeviceName = findViewById(R.id.tvDeviceName);
        noInternetLayout = findViewById(R.id.noInternetLayout);

        listDriveAdapter = new ListDriveAdapter(this, driveModelList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Make sure it's a valid context
        recyclerView.setAdapter(listDriveAdapter);

        if (getIntent() != null){

            sessionModel = (SessionModel) getIntent().getSerializableExtra("SessionModel");
            tvDeviceName.setText(sessionModel.getUserID() + "'s "+sessionModel.getDeviceType());


            if (isInternetConnected()){
                setInProgress(true, false);
                //MyFirebase.getMySessionRef().child(sessionModel.getSessionID()).child("accessUrl")
                FirebaseDatabase.getInstance().getReference("ServerUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        sessionModel.setAccessUrl(snapshot.getValue(String.class));
                        Toast.makeText(OtherDeviceDriveActivity.this, "Changed at: " + sessionModel.getAccessUrl(), Toast.LENGTH_SHORT).show();
                        try {
                            loadDrives();
                        } catch (Exception e){
                            Toast.makeText(OtherDeviceDriveActivity.this, "Error occurs" + e, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onDataChange: Error Loading Drive" + e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else
                setInternetNotAvailable();
        } else {
            Toast.makeText(this, "Something getting wrong", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }
    void loadDrives(){
        Request request = new Request.Builder()
                .url(sessionModel.getAccessUrl() + "/listdrive")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    setInProgress(false, false);
                    Toast.makeText(OtherDeviceDriveActivity.this, "Error Occur" + e, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response successful");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            String json = null;
                            setInProgress(false, true);
                            try {
                                json = response.body().string();
                                Log.d(TAG, "Response JSON: " + json);
                                Gson gson = new Gson();
                                List<DriveModel> newDriveList = gson.fromJson(json, new TypeToken<List<DriveModel>>(){}.getType());

                                driveModelList.clear();
                                driveModelList.addAll(newDriveList);
                                listDriveAdapter.notifyDataSetChanged();
                                Log.d(TAG, "run: Notified with length" + driveModelList.size());

                            } catch (IOException e) {
                                Log.d(TAG, "Error parsing response: " + e);
                                throw new RuntimeException(e);
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "Response failed");
                }
            }
        });

    }
    void setInProgress(boolean inProgress, boolean isContainsFile){
        if (inProgress){
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            tvNoFiles.setVisibility(View.GONE);
            noInternetLayout.setVisibility(View.GONE);
        } else {
            if (isContainsFile){
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                tvNoFiles.setVisibility(View.GONE);
                noInternetLayout.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                tvNoFiles.setVisibility(View.VISIBLE);
                noInternetLayout.setVisibility(View.GONE);
            }
        }
    }

    boolean isInternetConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    void setInternetNotAvailable(){
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tvNoFiles.setVisibility(View.GONE);
        noInternetLayout.setVisibility(View.VISIBLE);
    }
}