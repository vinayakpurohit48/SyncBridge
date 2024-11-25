package com.unknowndev.syncbridge.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unknowndev.syncbridge.R;

public class ConnectedDeviceFragment extends Fragment {

    public ConnectedDeviceFragment() {
        // Required empty public constructor
    }

        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connected_device, container, false);
        return inflater.inflate(R.layout.fragment_connected_device, container, false);
    }
}