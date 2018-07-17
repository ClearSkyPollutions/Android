package com.example.android.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.fragments.SliderFragment;

public class WelcomeActivity extends AppCompatActivity {

    SlideAdapter mAdapter;
    ViewPager mPager;

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
        setContentView(R.layout.activity_welcome);

        mAdapter = new SlideAdapter(getSupportFragmentManager());

        mPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mPager, true);

        mPager.setAdapter(mAdapter);

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
