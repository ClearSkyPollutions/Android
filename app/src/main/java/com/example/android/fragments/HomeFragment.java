package com.example.android.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.android.activities.R;
import com.example.android.activities.databinding.FragmentLastDataBinding;
import com.example.android.helpers.ChartHelper;
import com.example.android.models.DataHT;
import com.example.android.models.DataPM;
import com.github.mikephil.charting.charts.LineChart;


public class HomeFragment extends Fragment {

    private DataHT dataHT;
    private DataPM dataPM;
    private LineChart mChart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // Inflate using Databinding library
        FragmentLastDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_last_data, container, false);
        binding.setLifecycleOwner(this);

        // Get the root view and the LineChart view
        View rootView= binding.getRoot();
        mChart = rootView.findViewById(R.id.lineChart);

       int backgroundColor = Color.WHITE;
       int textColor = getResources().getColor(R.color.primaryTextColor);
       int pm25lineColor = getResources().getColor(R.color.primaryColor);
       int pm10lineColor = getResources().getColor(R.color.secondaryDarkColor);

        ChartHelper.initChart(mChart, backgroundColor, textColor);

        // Create or get the ViewModel for our date, load the data from server
        dataPM = ViewModelProviders.of(this).get(DataPM.class);
        dataPM.LoadLastData(getContext());

        dataHT = ViewModelProviders.of(this).get(DataHT.class);
        dataHT.LoadLastData(getContext());


        // Bind the UI elements to the viewmodel
        binding.setLastDataPM(dataPM);
        binding.setLastDataHT(dataHT);

        dataPM.pmEntries.observe(this, pmEntries -> {
            mChart.clearValues();
            for(Float[] pmEntry : pmEntries) {
                ChartHelper.addEntry(mChart, pmEntry, pm25lineColor, pm10lineColor);
            }
        });

        Button mButtonRefresh = rootView.findViewById(R.id.buttonRefresh);
        mButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataPM.LoadLastData(getContext());
                dataHT.LoadLastData(getContext());
            }
        });

        return rootView;
    }

}
