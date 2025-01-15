package com.unknowndev.syncbridge.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unknowndev.syncbridge.Model.FileData;
import com.unknowndev.syncbridge.R;

import java.util.ArrayList;

public class ListFileAdapter extends RecyclerView.Adapter<ListFileAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FileData> fileList;
    private OnFileClickListener listener;
    private OnFileLongClickListener longClickListener;

    public interface OnFileClickListener {
        void onFileClick(int position);
    }

    public interface OnFileLongClickListener {
        void onFileLongClick(int position, View view);
    }

    public ListFileAdapter(Context context, ArrayList<FileData> fileList, OnFileClickListener listener) {
        this.context = context;
        this.fileList = fileList;
        this.listener = listener;
        this.longClickListener = (OnFileLongClickListener) listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_file_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileData fileData = fileList.get(position);
        holder.tvFileName.setText(fileData.getName());

        if (fileData.isFolder()){
            holder.tvFileDetail.setText(fileData.getSubFolderCount()+" Items");
            holder.fileIcon.setImageResource(R.drawable.folder_icon);

        } else {
            holder.tvFileDetail.setText(formatSize(fileData.getSize()));
            holder.fileIcon.setImageResource(R.drawable.file_icon);
        }

        holder.itemView.setOnClickListener(v -> {
            if (fileData.isFolder()) {
                listener.onFileClick(position);
            } else {
                longClickListener.onFileLongClick(position, v);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onFileLongClick(position, v);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    private String formatSize(String size) {
        try {
            long sizeInBytes = Long.parseLong(size);
            if (sizeInBytes >= 1 << 30) {
                return String.format("%.2f GB", sizeInBytes / (double) (1 << 30));
            } else if (sizeInBytes >= 1 << 20) {
                return String.format("%.2f MB", sizeInBytes / (double) (1 << 20));
            } else if (sizeInBytes >= 1 << 10) {
                return String.format("%.2f KB", sizeInBytes / (double) (1 << 10));
            } else {
                return sizeInBytes + " bytes";
            }
        } catch (NumberFormatException e) {
            return size; // Return the original size if parsing fails
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileName, tvFileDetail;
        ImageView fileIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvFileDetail = itemView.findViewById(R.id.tvFileDetail);
            fileIcon = itemView.findViewById(R.id.fileIcon);
        }
    }
}
