package com.example.android.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
    private LineChart mChart1, mChart2, mChart3, mChart4;

    private static final String TAG = HomeFragment.class.toString();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // Inflate using DataBinding library
        FragmentHomeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        binding.setLifecycleOwner(this);
        View rootView= binding.getRoot();

        // Create or get the ViewModel for our date
        dataPM = ViewModelProviders.of(getActivity()).get(DataPM.class);
        dataHT = ViewModelProviders.of(getActivity()).get(DataHT.class);

        // Bind the UI elements to the viewModel
        binding.setLastDataPM(dataPM);
        binding.setLastDataHT(dataHT);

        // Init charts
        int backgroundColor = Color.WHITE;
        int textColor = getResources().getColor(R.color.primaryTextColor);
        int lineColor = getResources().getColor(R.color.secondaryDarkColor);

        mChart1 = rootView.findViewById(R.id.lineChart1);
        mChart2 = rootView.findViewById(R.id.lineChart2);
        mChart3 = rootView.findViewById(R.id.lineChart3);
        mChart4 = rootView.findViewById(R.id.lineChart4);

        ChartHelper chartHelper = new ChartHelper();
        chartHelper.initChart(mChart1, backgroundColor, textColor);
        chartHelper.initChart(mChart2, backgroundColor, textColor);
        chartHelper.initChart(mChart3, backgroundColor, textColor);
        chartHelper.initChart(mChart4, backgroundColor, textColor);

        dataPM.pm10Entries.observe(this, pm10Entries -> {
            mChart1.clearValues();
            DataModel.currentColumnName = DataPM.col_pm10;
            for(Float[] pmEntry : pm10Entries) {
                chartHelper.addEntry(mChart1, pmEntry, lineColor);
            }
        });
        dataHT.fillGraph(DataModel.currentTableName, DataHT.col_temperature );

        dataPM.pm25Entries.observe(this, pm25Entries -> {
            mChart2.clearValues();
            DataModel.currentColumnName = DataPM.col_pm25;
            for(Float[] pmEntry : pm25Entries) {
                chartHelper.addEntry(mChart2, pmEntry, lineColor);
            }
        });
        dataHT.fillGraph(DataModel.currentTableName, DataHT.col_humidity );

        dataHT.humEntries.observe(this, humEntries -> {
            mChart3.clearValues();
            DataModel.currentColumnName = DataHT.col_humidity;
            for(Float[] pmEntry : humEntries) {
                chartHelper.addEntry(mChart3, pmEntry, lineColor);
            }
        });
        dataPM.fillGraph(DataModel.currentTableName, DataPM.col_pm25 );

        dataHT.tempEntries.observe(this, tempEntries -> {
            mChart4.clearValues();
            DataModel.currentColumnName = DataHT.col_temperature;
            for(Float[] pmEntry : tempEntries) {
                chartHelper.addEntry(mChart4, pmEntry, lineColor);
            }
        });
        dataPM.fillGraph(DataModel.currentTableName, DataPM.col_pm10 );

        /*
        // Set listeners for the different buttons
        Button mButtonRefresh = rootView.findViewById(R.id.buttonRefresh);
        mButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });

        Button mButtonTemp = rootView.findViewById(R.id.temperature_bt);
        mButtonTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataHT.fillGraph(DataModel.currentTableName, DataHT.col_temperature);
            }
        });

        Button mButtonHum = rootView.findViewById(R.id.humidity_bt);
        mButtonHum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataHT.fillGraph(DataModel.currentTableName, DataHT.col_humidity);
            }
        });

        Button mButtonPM25 = rootView.findViewById(R.id.pm25_bt);
        mButtonPM25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataPM.fillGraph(DataModel.currentTableName, DataPM.col_pm25);
            }
        });

        Button mButtonPM10 = rootView.findViewById(R.id.pm10_bt);
        mButtonPM10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataPM.fillGraph(DataModel.currentTableName, DataPM.col_pm10);
            }
        });

        Button mButtonDay = rootView.findViewById(R.id.day_bt);
        mButtonDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeGraphScale("AVG_HOUR", "24", DataModel.currentColumnName);
            }
        });<

        Button mButtonMonth = rootView.findViewById(R.id.month_bt);
        mButtonMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeGraphScale("AVG_DAY", "30", DataModel.currentColumnName);
            }
        });

        Button mButtonYear = rootView.findViewById(R.id.year_bt);
        mButtonYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeGraphScale("AVG_MONTH", "12", DataModel.currentColumnName);
            }
        });
*/


        return rootView;
    }

    private void refreshData() {
        String tempTableName = DataModel.currentTableName;
        String tempNum = DataModel.currentNumberOfValues;
        DataModel.setScale("AVG_HOUR", "24");
        dataPM.loadLastData(DataModel.currentTableName);
        dataHT.loadLastData(DataModel.currentTableName);
        changeGraphScale(tempTableName, tempNum, DataModel.currentColumnName);
    }

    private void changeGraphScale(String tableName, String numberOfValues, String columnName) {
        DataModel.setScale(tableName, numberOfValues);
        if (columnName == DataPM.col_pm10) {
            dataPM.fillGraph(tableName, DataPM.col_pm10);
        } else if (columnName == DataPM.col_pm25){
            dataPM.fillGraph(tableName, DataPM.col_pm25);
        } else if (columnName == DataHT.col_humidity) {
            dataHT.fillGraph(tableName, DataHT.col_humidity);
        } else if (columnName == DataHT.col_temperature) {
            dataHT.fillGraph(tableName, DataHT.col_temperature );
        }
    }

}
