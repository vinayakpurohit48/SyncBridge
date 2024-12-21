package com.unknowndev.syncbridge.MyServices;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class MyFirebase {

    public static DatabaseReference getMyDetails(){
        return FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("UserDetails");
    }
    public static DatabaseReference getUserDetails(String UserID){
        return FirebaseDatabase.getInstance().getReference("Users").child(UserID);
    }
    public static DatabaseReference getMySessionRef(){
        return FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid())
                .child("Sessions");
    }

    public static DatabaseReference getMyAllowedDeviceRef(){
        return FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid())
                .child("Allowed Devices");
    }

    public static DatabaseReference getUserSessionRef(String UserID){
        return FirebaseDatabase.getInstance().getReference("Users").child(UserID)
               .child("Connected Device");
    }

    public static DatabaseReference getUserAllowedDeviceRef(String UserID){
        return FirebaseDatabase.getInstance().getReference("Users").child(UserID)
                .child("Sessions");
    }
}
