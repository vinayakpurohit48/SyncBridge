package com.unknowndev.syncbridge.Adapters;

import android.content.Context;
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


    // Constructor with listener
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
        holder.tvFileSize.setText(fileData.getSize());
        holder.tvSubFolder.setText(fileData.getSubFolderInfo());
        holder.fileIcon.setImageResource(fileData.isFolder() ? R.drawable.folder_icon : R.drawable.file_icon);

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileName, tvFileSize, tvSubFolder;
        ImageView fileIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            tvSubFolder = itemView.findViewById(R.id.tvSubFolder);
            fileIcon = itemView.findViewById(R.id.fileIcon);
        }
    }
}
