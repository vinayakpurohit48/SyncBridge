package com.unknowndev.syncbridge;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unknowndev.syncbridge.Adapters.ListFileAdapter;
import com.unknowndev.syncbridge.Model.DriveModel;
import com.unknowndev.syncbridge.Model.FileData;
import com.unknowndev.syncbridge.Model.SessionModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ListFilesActivity extends AppCompatActivity implements ListFileAdapter.OnFileClickListener, ListFileAdapter.OnFileLongClickListener {

    private RecyclerView recyclerView;
    private ArrayList<FileData> fileList = new ArrayList<>();
    private TextView tvNoFiles, tvDeviceName;
    private ProgressBar progressBar;
    private SessionModel sessionModel;
    private RelativeLayout noInternetLayout;
    private ListFileAdapter listFileAdapter;
    private String TAG = "Kya hua";
    DriveModel driveModel;
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_files);

        recyclerView = findViewById(R.id.recyclerView);
        tvNoFiles = findViewById(R.id.tvNoFiles);
        progressBar = findViewById(R.id.progressBar);
        tvDeviceName = findViewById(R.id.tvDeviceName);
        noInternetLayout = findViewById(R.id.noInternetLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        if (getIntent() != null){
            sessionModel = (SessionModel) getIntent().getSerializableExtra("SessionModel");
            tvDeviceName.setText(sessionModel.getUserID() + "'s "+sessionModel.getDeviceType());
            driveModel = (DriveModel) getIntent().getSerializableExtra("DriveModel");

            if (isInternetConnected()){
                setInProgress(true, false);
                FirebaseDatabase.getInstance().getReference("ServerUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        sessionModel.setAccessUrl(snapshot.getValue(String.class));
                        try {
                            loadFiles(driveModel.getDrivePath());
                        } catch (Exception e){
                            Toast.makeText(ListFilesActivity.this, "Error occurs" + e, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Something is wrong", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }

    void loadFiles(String path) {
        Log.d(TAG, "Request: " + path);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"Path\": \"" + path + "/\"}";
        RequestBody requestBody = RequestBody.create(json, mediaType);
        Log.d(TAG, "Request: " + requestBody);

        Request request = new Request.Builder()
                .url(sessionModel.getAccessUrl() + "/listfile")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    setInProgress(false, false);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String responseBody = response.body().string();
                            Log.d(TAG, "Response: " + responseBody);

                            if (response.isSuccessful() && !responseBody.isEmpty()){
                                setInProgress(false, true);
                                Gson gson = new Gson();
                                fileList.clear();
                                fileList = gson.fromJson(responseBody, new TypeToken<ArrayList<FileData>>(){}.getType());
                                Toast.makeText(ListFilesActivity.this, "Length: " + fileList.size(), Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "run: FileList: " + fileList);
                                listFileAdapter = new ListFileAdapter(ListFilesActivity.this, fileList, ListFilesActivity.this);
                                recyclerView.setAdapter(listFileAdapter);
                                setInProgress(false, true);
                            } else {
                                setInProgress(false, false);
                                Toast.makeText(ListFilesActivity.this, "No files or Empty", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            Toast.makeText(ListFilesActivity.this, "Error occurs", Toast.LENGTH_SHORT).show();
                            setInProgress(false, false);
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }

    void setInProgress(boolean inProgress, boolean isContainsFile) {
        if (inProgress) {
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            tvNoFiles.setVisibility(View.GONE);
            noInternetLayout.setVisibility(View.GONE);
        } else {
            if (isContainsFile) {
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

    void setInternetNotAvailable() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tvNoFiles.setVisibility(View.GONE);
        noInternetLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFileClick(int position) {
        // Handle file click if needed
    }

    @Override
    public void onFileLongClick(int position, View view) {
        // Handle long click if needed
    }
}
