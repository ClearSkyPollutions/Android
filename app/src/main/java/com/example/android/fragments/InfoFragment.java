package com.example.android.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.activities.R;

public class InfoFragment extends Fragment {
    static int num_pages = 2;
    static final int POLLUTANT_NUM = 0;
    static final int SENSOR_NUM = 1;
    InfoAdapter mAdapter;
    View infoView;
    ViewPager pager;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mAdapter = new InfoAdapter(getFragmentManager());
    }

    @Override
    public void onPause(){
        num_pages = 0;
        mAdapter.notifyDataSetChanged();
        super.onPause();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        infoView = inflater.inflate(R.layout.fragment_info, container, false);

        pager = infoView.findViewById(R.id.pager);

        PagerTitleStrip mTitle = infoView.findViewById(R.id.pager_title_strip);
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        pager.setAdapter(mAdapter);
        pager.setCurrentItem(0);

        return infoView;
    }

    public static class InfoAdapter extends FragmentStatePagerAdapter {
        public InfoAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return num_pages;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case POLLUTANT_NUM:
                    return ListPollutantsFragment.newInstance(position);
                case SENSOR_NUM:
                    return ListSensorsFragment.newInstance();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position){
            switch(position){
                case POLLUTANT_NUM:
                    return "Pollutants";
                case SENSOR_NUM:
                    return "Sensors";
            }
            return null;
        }
    }

}