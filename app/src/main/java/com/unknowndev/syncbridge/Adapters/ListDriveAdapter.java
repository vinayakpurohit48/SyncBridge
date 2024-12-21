package com.unknowndev.syncbridge.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unknowndev.syncbridge.Model.DriveModel;
import com.unknowndev.syncbridge.R;

import java.util.List;

public class ListDriveAdapter extends RecyclerView.Adapter<ListDriveAdapter.ViewHolder> {

    Context context;
    List<DriveModel> driveList;

    public ListDriveAdapter(Context context, List<DriveModel> driveList) {
        this.context = context;
        this.driveList = driveList;
    }

    @NonNull
    @Override
    public ListDriveAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_drive_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListDriveAdapter.ViewHolder holder, int position) {
        if (!driveList.get(position).getDriveName().isEmpty()){
            holder.tvDriveName.setText(driveList.get(position).getDriveName() + "Drive");

            long availableSpace = driveList.get(position).getAvailableSpace();
            long totalStorage = driveList.get(position).getTotalSpace();

            String availableSpaceFormatted = formatFileSize(availableSpace);
            String totalStorageFormatted = formatFileSize(totalStorage);

            holder.tvStorageDetails.setText(availableSpaceFormatted + "/" + totalStorageFormatted);
            int progressPercentage = (int) ((double) availableSpace / totalStorage * 100);
            holder.progressBar.setProgress(progressPercentage);
        } else
            Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show();
        
    }

    @Override
    public int getItemCount() {
        return driveList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDriveName, tvStorageDetails;
        ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDriveName = itemView.findViewById(R.id.tvDeviceName);
            tvStorageDetails = itemView.findViewById(R.id.tvStorageDetails);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    String formatFileSize(Long sizeInBytes) {
        final long KB = 1024;
        final long MB = KB * 1024;
        final long GB = MB * 1024;

        if (sizeInBytes >= GB) {
            return String.format("%.2f GB", (double) sizeInBytes / GB);
        } else if (sizeInBytes >= MB) {
            return String.format("%.2f MB", (double) sizeInBytes / MB);
        } else if (sizeInBytes >= KB) {
            return String.format("%.2f KB", (double) sizeInBytes / KB);
        } else {
            return sizeInBytes + " Bytes";
        }
    }
}
