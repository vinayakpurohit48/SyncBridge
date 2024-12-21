package com.unknowndev.syncbridge;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.unknowndev.syncbridge.AuthorizationActivity.LoginActivity;
import com.unknowndev.syncbridge.AuthorizationActivity.RegisterActivity;
import com.unknowndev.syncbridge.Fragments.AddDeviceFragment;
import com.unknowndev.syncbridge.Fragments.ConnectedDeviceFragment;
import com.unknowndev.syncbridge.Fragments.HomeFragment;
import com.unknowndev.syncbridge.Fragments.MyDeviceFragment;
import com.unknowndev.syncbridge.Fragments.SettingFragment;
import com.unknowndev.syncbridge.Model.UserModel;
import com.unknowndev.syncbridge.MyServices.MyFirebase;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottom_navigation;
    FirebaseAuth mAuth;
    public UserModel MyModel = new UserModel();
    ImageView splash_logo;
    ProgressBar progressBar;
    FrameLayout fragmentContainer;

    RelativeLayout HomeLayout, SplashLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottom_navigation = findViewById(R.id.bottom_navigation);
        fragmentContainer = findViewById(R.id.fragmentContainer);
        progressBar = findViewById(R.id.progressBar);
        splash_logo = findViewById(R.id.splash_logo);
        SplashLayout = findViewById(R.id.SplashLayout);
        HomeLayout = findViewById(R.id.HomeLayout);

        inProcess(true);

        mAuth = FirebaseAuth.getInstance();
        Window window = MainActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.yellow));

        if (mAuth.getCurrentUser() == null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            },3000);
            return;
        }

        //nav_view handler
        bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (id == R.id.nav_mydevice) {
                    selectedFragment = new MyDeviceFragment();
                } else if (id == R.id.nav_add_device) {
                    selectedFragment = new AddDeviceFragment();
                } else if (id == R.id.nav_connected_device) {
                    selectedFragment = new ConnectedDeviceFragment();
                } else if (id == R.id.nav_setting) {
                    selectedFragment = new SettingFragment();
                }

                if (selectedFragment != null) {
                    openFragment(selectedFragment);
                }
                return true;
            }
        });

        MyFirebase.getMyDetails().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    DataSnapshot data = task.getResult();
                    if (data != null && data.exists() ){
                        MyModel = data.getValue(UserModel.class);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.add(R.id.fragmentContainer, new HomeFragment());
                        ft.commit();
                        inProcess(false);
                    } else {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                                user.delete().addOnCompleteListener(deleteTask -> {
                                            if (deleteTask.isSuccessful()) {
                                                // User deleted successfully
                                                Toast.makeText(getApplication(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Error while deleting the user
                                                Toast.makeText(getApplicationContext(), "Error deleting user: " + deleteTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                Log.i("Deleting User", "onComplete: " + deleteTask.getException().getMessage());
                                            }
                                        });
                            } else {
                                Toast.makeText(getApplicationContext(), "No user signed in", Toast.LENGTH_SHORT).show();
                            }
                        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        Toast.makeText(MainActivity.this, "Your Account has been suspended\n Please Register Again to continue...", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    }
                } else {
                    inProcess(false);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.fragmentContainer, new HomeFragment());
                    ft.commit();
                    Toast.makeText(MainActivity.this, "Please Check Your Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        if (currentFragment == null || !currentFragment.getClass().getName().equals(fragment.getClass().getName())) {
            fragmentTransaction.replace(R.id.fragmentContainer, fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (currentFragment instanceof HomeFragment){
            super.onBackPressed();
        } else if (currentFragment instanceof MyDeviceFragment) {
            if (((MyDeviceFragment) currentFragment).getPathStackSize()>1){
                ((MyDeviceFragment) currentFragment).goToParentDirectory();
            } else {
                bottom_navigation.setSelectedItemId(R.id.nav_home);
                openFragment(new HomeFragment());
            }
        } else {
            bottom_navigation.setSelectedItemId(R.id.nav_home);
            openFragment(new HomeFragment());
        }
    }

    void inProcess(boolean inProcess){
        if (inProcess){
            SplashLayout.setVisibility(View.VISIBLE);
            HomeLayout.setVisibility(View.GONE);
        } else {
           HomeLayout.setVisibility(View.VISIBLE);
           SplashLayout.setVisibility(View.GONE);
        }
    }
}
