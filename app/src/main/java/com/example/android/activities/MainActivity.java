package com.example.android.activities;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.fragments.HomeFragment;
import com.example.android.fragments.InfoFragment;
import com.example.android.fragments.MapFragment;
import com.example.android.fragments.SettingsFragment;
import com.example.android.network.RequestQueueSingleton;
import com.example.android.viewModels.AQIModel;


public class MainActivity extends AppCompatActivity {

    //Permission code that will be checked in the method onRequestPermissionsResult
    private int STORAGE_PERMISSION_CODE = 23;

    private BottomNavigationView.OnNavigationItemSelectedListener navListener
            = item -> {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        selectedFragment = new HomeFragment();
                        setTitle(R.string.title_home);
                        break;
                    case R.id.navigation_map:
                        selectedFragment = new MapFragment();
                        setTitle(R.string.title_map);
                        //If the app has not the permission then asking for the permission
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

        // Create a Request queue with application lifecycle
        RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        BottomNavigationView navigation =  findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(navListener);
        loadFragment(new HomeFragment());

        createNotificationAQI();
    }

    private void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    //Requesting permission
    private void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == STORAGE_PERMISSION_CODE){
            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                loadFragment(new MapFragment());
            }else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission But is need for Maps",Toast.LENGTH_LONG).show();
            }
        }
    }

    private final void createNotificationAQI(){
        final NotificationManager mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        final Intent launchNotificationIntent = new Intent(this, MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, launchNotificationIntent,
                0);
        AQIModel aqiModel = ViewModelProviders.of(this).get(AQIModel.class);
        aqiModel.loadAQI(this);

        aqiModel.getAqi().observe(this, aqi -> {
            Notification.Builder builder = new Notification.Builder(this)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getResources().getString(R.string.notification_title))
                    .setContentText(getResources().getString(R.string.notification_desc) + aqiModel.getLabel().getValue() + " (" + aqi + ")" )
                    .setContentIntent(pendingIntent);

            mNotification.notify(1, builder.build());
        });

    }
}