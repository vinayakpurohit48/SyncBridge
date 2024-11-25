package com.unknowndev.syncbridge.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unknowndev.syncbridge.Adapters.ListFileAdapter;
import com.unknowndev.syncbridge.Model.FileData;
import com.unknowndev.syncbridge.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyDeviceFragment extends Fragment implements ListFileAdapter.OnFileClickListener, ListFileAdapter.OnFileLongClickListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PAGE_SIZE = 20; // Number of files to load at a time
    private RecyclerView recyclerView;
    private ListFileAdapter listFileAdapter;
    private ArrayList<FileData> fileList = new ArrayList<>();
    private Stack<String> pathStack = new Stack<>();
    private TextView tvNoFiles;
    private ExecutorService executorService;

    private int currentPage = 0;
    private boolean isLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_device, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tvNoFiles = view.findViewById(R.id.tvNoFiles);

        listFileAdapter = new ListFileAdapter(getContext(), fileList, this);
        recyclerView.setAdapter(listFileAdapter);

        executorService = Executors.newCachedThreadPool();

        if (checkPermission()) {
            loadFiles(Environment.getExternalStorageDirectory().getPath());
            pathStack.push(Environment.getExternalStorageDirectory().getPath());
        } else {
            askPermission();
        }
        return view;
    }

    private void loadFiles(String path) {
        if (isLoading) return;
        isLoading = true;

        File root = new File(path);
        executorService.execute(() -> {
            File[] files = root.listFiles();
            if (files == null || files.length == 0) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    tvNoFiles.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                });
                isLoading = false;
                return;
            } else {
                new Handler(Looper.getMainLooper()).post(() -> {
                    tvNoFiles.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                });
            }

            Arrays.sort(files, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
            ArrayList<FileData> tempFileList = new ArrayList<>();
            int start = currentPage * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, files.length);

            for (int i = start; i < end; i++) {
                File file = files[i];
                if (file.getName().startsWith(".")) continue;

                FileData fileData;
                if (file.isDirectory()) {
                    fileData = new FileData(file.getName(), "0 MB", "0 Items", true, file.getAbsolutePath());
                    loadDirectoryDetailsAsync(file);
                } else {
                    fileData = new FileData(file.getName(), getFileSize(file.length()), "", false, file.getAbsolutePath());
                }
                tempFileList.add(fileData);
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                fileList.addAll(tempFileList);
                listFileAdapter.notifyDataSetChanged();
                currentPage++; // Move to next page
                isLoading = false;
            });
        });
    }

    private void loadDirectoryDetailsAsync(File directory) {
        executorService.execute(() -> {
            long size = calculateDirectorySize(directory);
            int itemCount = directory.listFiles() != null ? directory.listFiles().length : 0;

            // Update the UI with the size and item count
            new Handler(Looper.getMainLooper()).post(() -> {
                for (int i = 0; i < fileList.size(); i++) {
                    FileData fileData = fileList.get(i);
                    if (fileData.getName().equals(directory.getName())) {
                        fileData.setSize(getFileSize(size));
                        fileData.setSubFolderInfo(itemCount + " Items");
                        listFileAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            });
        });
    }

    private long calculateDirectorySize(File directory) {
        long size = 0;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                size += file.isDirectory() ? calculateDirectorySize(file) : file.length();
            }
        }
        return size;
    }

    private String getFileSize(long size) {
        if (size >= 1 << 30) {
            return String.format("%.2f GB", size / (double) (1 << 30));
        } else if (size >= 1 << 20) {
            return String.format("%.2f MB", size / (double) (1 << 20));
        } else if (size >= 1 << 10) {
            return String.format("%.2f KB", size / (double) (1 << 10));
        } else {
            return size + " bytes";
        }
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
        Log.d("Checking Log", "++++++++ BackPressed ++++++++++");

        Log.d("Checking Log", "Before Remove Stack: " + pathStack);
        if (pathStack.size() > 1) {

            pathStack.pop();
            Log.d("Checking Log", "After Remove Stack: " + pathStack);
            String parentPath = pathStack.peek();
            currentPage = 0;
            fileList.clear();
            loadFiles(parentPath);
        } else {
            Toast.makeText(getContext(), "No parent directory found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFileClick(int position) {
        if (fileList.get(position).isFolder()) {
            String path = fileList.get(position).getCurrentPath();
            pathStack.push(path);
            currentPage = 0;
            fileList.clear();
            loadFiles(path);
        } else {
            Toast.makeText(getContext(), "File clicked: " + fileList.get(position).getName(), Toast.LENGTH_SHORT).show();
        }
    }
    public int getPathStackSize() {
        return pathStack.size();
    }

    @Override
    public void onFileLongClick(int position, View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu_items, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.item_open) {
                    Toast.makeText(getContext(), "Open clicked", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.item_copy) {
                    Toast.makeText(getContext(), "Copy clicked", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.item_cut) {
                    Toast.makeText(getContext(), "Cut clicked", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.item_rename) {
                    Toast.makeText(getContext(), "Rename clicked", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.item_delete) {
                    Toast.makeText(getContext(), "Delete clicked", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.item_share) {
                    Toast.makeText(getContext(), "Share clicked", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false; // Return false if none of the conditions matched
                }
            }
        });
    }

    private String getMimeType(String filePath) {
        String mimeType = "*/*"; // Default type
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        if (!extension.isEmpty()) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        return mimeType;
    }


}
