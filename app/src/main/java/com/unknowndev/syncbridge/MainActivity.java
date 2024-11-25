package com.unknowndev.syncbridge;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unknowndev.syncbridge.Fragments.AddDeviceFragment;
import com.unknowndev.syncbridge.Fragments.ConnectedDeviceFragment;
import com.unknowndev.syncbridge.Fragments.HomeFragment;
import com.unknowndev.syncbridge.Fragments.MyDeviceFragment;
import com.unknowndev.syncbridge.Fragments.SettingFragment;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottom_navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Window window = MainActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.yellow));

        bottom_navigation = findViewById(R.id.bottom_navigation);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragmentContainer, new HomeFragment());
        ft.commit();
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
}
