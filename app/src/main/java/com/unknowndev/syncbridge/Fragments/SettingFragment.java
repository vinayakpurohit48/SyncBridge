package com.unknowndev.syncbridge.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.unknowndev.syncbridge.AuthorizationActivity.LoginActivity;
import com.unknowndev.syncbridge.R;

public class SettingFragment extends Fragment {

    TextView view_details, Logout, Username, Email;
    LinearLayout sendMessageTextView, about_us, faqs;
    FirebaseAuth mAuth;
    Switch copyPasteSwitch;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_setting, container, false);
        view_details = view.findViewById(R.id.full_details);
        about_us = view.findViewById(R.id.about_us);
        sendMessageTextView = view.findViewById(R.id.send_message_textview);
        faqs = view.findViewById(R.id.faqs);
        Logout = view.findViewById(R.id.Logout);
        copyPasteSwitch = view.findViewById(R.id.copyPasteSwitch);
        mAuth = FirebaseAuth.getInstance();



        Logout.setOnClickListener( v -> {
            showLogoutConfirmationDialog();
        });

        return view;
    }

    void showLogoutConfirmationDialog(){
        View customDialog = getLayoutInflater().inflate(R.layout.custom_dialog, null);

        Button positiveButton = customDialog.findViewById(R.id.dialog_positive);
        Button negativeButton = customDialog.findViewById(R.id.dialog_negative);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(customDialog)
                .setCancelable(false)
                .create();

        positiveButton.setOnClickListener( v -> {
            mAuth.signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        negativeButton.setOnClickListener( v -> {
            dialog.dismiss();
        });

        dialog.show();
    }
}