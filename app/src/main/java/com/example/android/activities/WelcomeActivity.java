package com.example.android.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.fragments.SliderFragment;
import com.example.android.network.RequestQueueSingleton;
import com.example.android.viewModels.SettingsModel;

public class WelcomeActivity extends AppCompatActivity {

    private SlideAdapter mAdapter;
    private ViewPager mPager;
    private SettingsModel mSettingsModel;

    // Using an array here in case we need to do differents layouts.
    // If not, should probably just inflate the right layout in SliderFragment.java
    private static final int[] LAYOUTS = {
            R.layout.fragment_slider,
            R.layout.fragment_slider,
            R.layout.fragment_slider_sensor,
            R.layout.fragment_slider,
            R.layout.fragment_slider_rpi,
            R.layout.fragment_slider_web}; ;
    static final int NUM_SLIDES = LAYOUTS.length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if we need to show the introduction slides
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.settings_rpi_file_key),
                Context.MODE_PRIVATE);
        if(sharedPref.contains("raspberryPiAddressIp")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_welcome);

        // Create or get the ViewModel for our charts, and put defaults values in it
        mSettingsModel = ViewModelProviders.of(this).get(SettingsModel.class);
        mSettingsModel.getLocalSettings(sharedPref);

        RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        //Setup sliders
        mPager = findViewById(R.id.viewPager);
        mAdapter = new SlideAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        // "Little dots" for navigation
        TabLayout tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mPager, true);
    }

    public static class SlideAdapter extends FragmentPagerAdapter {
        public SlideAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_SLIDES;
        }

        @Override
        public Fragment getItem(int position) {
            return SliderFragment.newInstance(LAYOUTS[position], position);
        }
    }
}
