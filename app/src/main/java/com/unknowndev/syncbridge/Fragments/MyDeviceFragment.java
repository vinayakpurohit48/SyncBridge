package com.unknowndev.syncbridge.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.unknowndev.syncbridge.Adapters.ListFileAdapter;
import com.unknowndev.syncbridge.Model.FileData;
import com.unknowndev.syncbridge.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class MyDeviceFragment extends Fragment implements ListFileAdapter.OnFileClickListener, ListFileAdapter.OnFileLongClickListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private RecyclerView recyclerView;
    private ListFileAdapter listFileAdapter;
    private ArrayList<FileData> fileList = new ArrayList<>();
    private Stack<String> pathStack = new Stack<>();
    private TextView tvNoFiles;
    private BottomSheetDialog bottomSheetDialog;
    private LinearLayout layoutOpenButton, layoutCopyButton, layoutDeleteButton, layoutShareButton, layoutInfoButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_device, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tvNoFiles = view.findViewById(R.id.tvNoFiles);

        loadBottomSheetFragment();

        listFileAdapter = new ListFileAdapter(getContext(), fileList, this);
        recyclerView.setAdapter(listFileAdapter);

        if (checkPermission()) {
            String initialPath = Environment.getExternalStorageDirectory().getPath();
            pathStack.push(initialPath);
            loadFiles(initialPath);
        } else {
            askPermission();
        }

        setupBottomSheetButtons();

        return view;
    }

    private void loadBottomSheetFragment() {
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.menu_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);

        layoutOpenButton = view.findViewById(R.id.layoutOpenButton);
        layoutCopyButton = view.findViewById(R.id.layoutCopyButton);
        layoutDeleteButton = view.findViewById(R.id.layoutDeleteButton);
        layoutShareButton = view.findViewById(R.id.layoutShareButton);
        layoutInfoButton = view.findViewById(R.id.layoutInfoButton);
    }

    private void setupBottomSheetButtons() {
        layoutOpenButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Open Called", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        layoutCopyButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Copy Called", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        layoutDeleteButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Delete Called", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        layoutShareButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Share Called", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        layoutInfoButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Info Called", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });
    }

    private void loadFiles(String path) {
        File root = new File(path);
        File[] files = root.listFiles();

        if (files == null || files.length == 0) {
            tvNoFiles.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        } else {
            tvNoFiles.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        Arrays.sort(files, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        fileList.clear();

        for (File file : files) {
            if (file.getName().startsWith(".")) continue;

            FileData fileData;
            if (file.isDirectory()) {
                fileData = new FileData(file.getName(), "0", String.valueOf(file.listFiles() != null ? file.listFiles().length : 0), true, file.getAbsolutePath());
            } else {
                fileData = new FileData(file.getName(), String.valueOf(file.length()), "0", false, file.getAbsolutePath());
            }
            fileList.add(fileData);
        }

        listFileAdapter.notifyDataSetChanged();
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int readPermission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                startActivity(intent);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String initialPath = Environment.getExternalStorageDirectory().getPath();
                pathStack.push(initialPath);
                loadFiles(initialPath);
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void goToParentDirectory() {
        if (pathStack.size() > 1) {
            pathStack.pop();
            String parentPath = pathStack.peek();
            loadFiles(parentPath);
        } else {
            Toast.makeText(getContext(), "No parent directory found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFileClick(int position) {
        if (fileList.get(position).isFolder()) {
            String path = fileList.get(position).getPath();
            pathStack.push(path);
            loadFiles(path);
        } else {
            Toast.makeText(getContext(), "File clicked: " + fileList.get(position).getName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFileLongClick(int position, View view) {
        bottomSheetDialog.show();
    }

    public int getPathStackSize() {
        return pathStack.size();
    }

}
