package com.unknowndev.syncbridge.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unknowndev.syncbridge.Model.SessionModel;
import com.unknowndev.syncbridge.OtherDeviceDriveActivity;
import com.unknowndev.syncbridge.R;

import java.util.ArrayList;
public class ListDeviceAdapter extends RecyclerView.Adapter<ListDeviceAdapter.ViewHolder> {

    Context context;
    ArrayList<SessionModel> deviceList = new ArrayList<>();

    public ListDeviceAdapter(Context context, ArrayList<SessionModel> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
    }

    @NonNull
    @Override
    public ListDeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_device_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListDeviceAdapter.ViewHolder holder, int position) {
        holder.tvSessionID.setText(deviceList.get(position).getSessionID());
        holder.tvDeviceName.setText(deviceList.get(position).getUserID() +"'s "+ deviceList.get(position).getDeviceType());
        holder.tvStatus.setText("Online");
        if (deviceList.get(position).getDeviceType().equals("Mobile")){
            holder.deviceIcon.setImageResource(R.drawable.mobile_icon);
        } else
            holder.deviceIcon.setImageResource(R.drawable.pc_icon);

        holder.optionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Menu called", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener( v -> {
            Intent intent = new Intent(context, OtherDeviceDriveActivity.class);
            intent.putExtra("SessionModel", deviceList.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView deviceIcon, optionMenu;
        TextView tvDeviceName, tvSessionID, tvStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceIcon = itemView.findViewById(R.id.deviceIcon);
            tvDeviceName = itemView.findViewById(R.id.tvDeviceName);
            tvSessionID = itemView.findViewById(R.id.tvSessionID);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            optionMenu = itemView.findViewById(R.id.optionMenu);
        }
    }
}
