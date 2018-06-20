package com.example.android.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.fragments.HomeFragment;
import com.example.android.fragments.ListPollutantsFragment;
import com.example.android.fragments.MapFragment;
import com.example.android.fragments.SettingsFragment;
import com.example.android.network.RequestQueueSingleton;


public class MainActivity extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener navListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = new HomeFragment();
                    setTitle(R.string.title_home);
                    break;
                case R.id.navigation_map:
                    selectedFragment = new MapFragment();
                    setTitle(R.string.title_map);
                    break;
                case R.id.navigation_pollutants:
                    selectedFragment = new ListPollutantsFragment();
                    setTitle(R.string.title_pollutants);
                    break;
                case R.id.navigation_settings:
                    selectedFragment = new SettingsFragment();
                    setTitle(R.string.title_settings);
                    break;
            }
            loadFragment(selectedFragment);
            return true;
        }
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
    }

    private void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}
