package com.unknowndev.syncbridge.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.unknowndev.syncbridge.Model.AllowedDeviceModel;
import com.unknowndev.syncbridge.Model.SessionModel;
import com.unknowndev.syncbridge.MyServices.MyFirebase;
import com.unknowndev.syncbridge.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AddDeviceFragment extends Fragment {
    public static int CAMERA_REQUEST_CODE = 1000;
    public static int IMAGE_ACCESS_CODE = 100;
    CompoundBarcodeView barcodeScanner;
    ImageButton btnFlash, btnGallery;
    public static String[] Permission = { Manifest.permission.CAMERA };
    boolean isFlashed = false;
    ProgressDialog progressDialog;
    FirebaseDatabase firebaseDatabase;

    public AddDeviceFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_device, container, false);
        barcodeScanner = view.findViewById(R.id.barcodeScanner);
        btnFlash = view.findViewById(R.id.btnFlash);
        btnGallery = view.findViewById(R.id.btnGallery);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Connecting....");
        progressDialog.setCancelable(false);

        if (isPermissionGranted()){
            Toast.makeText(getContext(), "Permission Already Granted", Toast.LENGTH_SHORT).show();
            startScanningCode();
        } else {
            ActivityCompat.requestPermissions(getActivity(), Permission, CAMERA_REQUEST_CODE);
        }

        btnGallery.setOnClickListener( v->{
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_ACCESS_CODE);
        });

        btnFlash.setOnClickListener( v->{
            if (isFlashed){
                barcodeScanner.setTorchOff();
                isFlashed = false;
                btnFlash.setImageResource(R.drawable.flash_off);
            } else {
                isFlashed = true;
                barcodeScanner.setTorchOn();
                btnFlash.setImageResource(R.drawable.flash_on);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_ACCESS_CODE){
            Toast.makeText(getContext(), "Qr Note Available At this time", Toast.LENGTH_SHORT).show();
        }
    }

    void startScanningCode(){
        if (barcodeScanner != null)
            barcodeScanner.decodeContinuous(new BarcodeCallback() {
                @Override
                public void barcodeResult(BarcodeResult result) {
                    progressDialog.show();

                    String Data = result.toString();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(Data);
                                String sessionId = jsonObject.getString("SessionID");
                                String userId = jsonObject.getString("UserID");
                                String deviceType = jsonObject.getString("DeviceType");
                                String accessUrl = jsonObject.getString("AccessUrl");

                                //update Session of current User
                                SessionModel sessionModel = new SessionModel(sessionId, userId, deviceType, accessUrl);
                                MyFirebase.getMySessionRef().child(sessionId).setValue(sessionModel);

                                //update Session of Another User
                                AllowedDeviceModel allowedDeviceModel = new AllowedDeviceModel(sessionId, FirebaseAuth.getInstance().getUid());
                                MyFirebase.getUserSessionRef(userId).child(sessionId).setValue(allowedDeviceModel);

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "Device Added Successfully...", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });


                            } catch (JSONException e) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "The QR is Code Not Valid", Toast.LENGTH_SHORT).show();
                                        barcodeScanner.resume();
                                    }
                                });
                            }

                        }
                    }).start();

                    barcodeScanner.pause();
                }

                @Override
                public void possibleResultPoints(List<ResultPoint> resultPoints) {
                    BarcodeCallback.super.possibleResultPoints(resultPoints);
                }
            });
        else
            Toast.makeText(getContext(), "BarCode not Available", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeScanner.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeScanner.pause();
    }

    boolean isPermissionGranted(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE){
            if (isPermissionGranted()) {
                Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Please Allow Permission to continue ...", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(), Permission, CAMERA_REQUEST_CODE);
        }
    }
}