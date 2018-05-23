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
import com.example.android.activities.databinding.FragmentHomeBinding;
import com.example.android.helpers.ChartHelper;
import com.example.android.models.DataHT;
import com.example.android.models.DataModel;
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
        FragmentHomeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        binding.setLifecycleOwner(this);

        // Get the root view and the LineChart view
        View rootView= binding.getRoot();
        mChart = rootView.findViewById(R.id.lineChart);

       int backgroundColor = Color.WHITE;
       int textColor = getResources().getColor(R.color.primaryTextColor);
       int lineColor = getResources().getColor(R.color.secondaryDarkColor);

       ChartHelper chartHelper = new ChartHelper();
       chartHelper.initChart(mChart, backgroundColor, textColor);

        // Create or get the ViewModel for our date
        dataPM = ViewModelProviders.of(getActivity()).get(DataPM.class);
        dataHT = ViewModelProviders.of(getActivity()).get(DataHT.class);

        // Bind the UI elements to the viewmodel
        binding.setLastDataPM(dataPM);
        binding.setLastDataHT(dataHT);

        dataPM.pmEntries.observe(this, pmEntries -> {
            mChart.clearValues();
            for(Float[] pmEntry : pmEntries) {
                chartHelper.addEntry(mChart, ChartHelper.DESCRIPTION, pmEntry, lineColor);
            }
        });

        dataHT.pmEntries.observe(this, pmEntries -> {
            mChart.clearValues();
            for(Float[] pmEntry : pmEntries) {
                chartHelper.addEntry(mChart, ChartHelper.DESCRIPTION, pmEntry, lineColor);
            }
        });

        //load the data from the server
        setScale("AVG_HOUR", "24");
        dataPM.loadLastData(getContext());
        dataHT.loadLastData(getContext());
        dataPM.fillGraph(getContext(), DataPM.col_pm25);


        Button mButtonRefresh = rootView.findViewById(R.id.buttonRefresh);
        mButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempTableName = DataModel.currentTableName;
                String tempNum = DataModel.numberOfValues;
                setScale("AVG_HOUR", "24");
                dataPM.loadLastData(getContext());
                dataHT.loadLastData(getContext());
                setScale(tempTableName, tempNum);
            }
        });

        Button mButtonTemp = rootView.findViewById(R.id.temperature_bt);
        mButtonTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataHT.fillGraph(getContext(), DataHT.col_temperature);
            }
        });

        Button mButtonHum = rootView.findViewById(R.id.humidity_bt);
        mButtonHum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataHT.fillGraph(getContext(), DataHT.col_humidity);
            }
        });

        Button mButtonPM25 = rootView.findViewById(R.id.pm25_bt);
        mButtonPM25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataPM.fillGraph(getContext(), DataPM.col_pm25);
            }
        });

        Button mButtonPM10 = rootView.findViewById(R.id.pm10_bt);
        mButtonPM10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataPM.fillGraph(getContext(), DataPM.col_pm10);
            }
        });

        Button mButtonDay = rootView.findViewById(R.id.day_bt);
        mButtonDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScale("AVG_HOUR", "24");
                dataPM.fillGraph(getContext(), DataModel.currentColumnName);
            }
        });

        Button mButtonMonth = rootView.findViewById(R.id.month_bt);
        mButtonMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScale("AVG_DAY", "30");
                dataPM.fillGraph(getContext(), DataModel.currentColumnName);
            }
        });

        Button mButtonYear = rootView.findViewById(R.id.year_bt);
        mButtonYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScale("AVG_MONTH", "24");
                dataPM.fillGraph(getContext(), DataModel.currentColumnName);
            }
        });

        return rootView;
    }

    private void setScale(String tableName, String numberOfValues) {
        DataModel.currentTableName = tableName;
        DataModel.numberOfValues = numberOfValues;
    }

}
