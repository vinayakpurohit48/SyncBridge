package com.unknowndev.syncbridge.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.unknowndev.syncbridge.MainActivity;
import com.unknowndev.syncbridge.Model.UserModel;
import com.unknowndev.syncbridge.R;

public class HomeFragment extends Fragment {
    UserModel MyModel = new UserModel();
    TextView username;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        username = view.findViewById(R.id.username);
        if (getActivity() != null) {
            MyModel = ((MainActivity) getActivity()).MyModel;
            username.setText(MyModel.getFirstName());
        } else {
            Toast.makeText(getContext(), "Check Your Internet", Toast.LENGTH_SHORT).show();
        }
        return view;
    }
}