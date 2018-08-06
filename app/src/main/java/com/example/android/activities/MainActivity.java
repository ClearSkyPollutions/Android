package com.example.android.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.android.fragments.HomeFragment;
import com.example.android.fragments.InfoFragment;
import com.example.android.fragments.MapFragment;
import com.example.android.fragments.SettingsFragment;
import com.example.android.services.NotificationJobService;


public class MainActivity extends AppCompatActivity {


    //Permission code that will be checked in the method onRequestPermissionsResult
    private int STORAGE_PERMISSION_CODE = 23;


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = new HomeFragment();
                    setTitle(R.string.title_home);
                    break;
                case R.id.navigation_map:
                    selectedFragment = new MapFragment();
                    setTitle(R.string.title_map);
                    requestStoragePermission();
                    break;
                case R.id.navigation_info:
                    selectedFragment = new InfoFragment();
                    setTitle(R.string.title_info);
                    break;
                case R.id.navigation_settings:
                    selectedFragment = new SettingsFragment();
                    setTitle(R.string.title_settings);
                    break;
            }
            loadFragment(selectedFragment);
            return true;
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(navListener);

        loadFragment(new HomeFragment());
        NotificationJobService.scheduleFirebaseJobDispatcherNotification(this.getApplicationContext());
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadFragment(new MapFragment());
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, R.string.toast_permission_denied, Toast.LENGTH_LONG).show();
            }
        }
    }

    //We are calling this method to check the permission status
    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //If permission is granted return true else return false
        return result == PackageManager.PERMISSION_GRANTED;
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (!isReadStorageAllowed()
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                //If the user has denied the permission previously your code will come to this block
                //Here you can explain why you need this permission
                    this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}
