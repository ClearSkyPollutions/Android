package com.example.android.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.nfc.Tag;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.activities.R;
import com.example.android.activities.databinding.FragmentHomeBinding;
import com.example.android.helpers.AlertDialogHelper;
import com.example.android.helpers.ChartHelper;
import com.example.android.models.DataHT;
import com.example.android.models.DataModel;
import com.example.android.models.DataPM;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.common.SignInButton;


public class HomeFragment extends Fragment {

    private DataHT dataHT;
    private DataPM dataPM;
    private LineChart mChart1, mChart2, mChart3, mChart4, mChartDialog;
    private CardView mChartViewDialog;
    private View mCoverView;
    private TextView mDataView;

    private static final String TAG = HomeFragment.class.toString();

    public View.OnClickListener createPopupListener(String column, DataModel data,
                                                    ChartHelper chartHelper, int lineColor) {
        return view -> {
            Log.d(getTag(), "Created listener for graph " + column);
            mChartDialog.clearValues();
            DataModel.currentColumnName = column;
            for (Float[] entries : data.getEntries(column).getValue()) {
                chartHelper.addEntry(mChartDialog, entries, lineColor, true);
            }
            mChartViewDialog.setVisibility(View.VISIBLE);
            mCoverView.setVisibility(View.VISIBLE);

            data.getEntries(column).observe(this, entries -> {
                mChartDialog.clearValues();
                    DataModel.currentColumnName = column;
                    for (Float[] pmEntry : entries) {
                        chartHelper.addEntry(mChartDialog, pmEntry, lineColor, true);
                    }
            });
        };
    }

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

        DataModel.setScale("AVG_HOUR", "24");

        mChart1 = rootView.findViewById(R.id.lineChart1);
        mChart2 = rootView.findViewById(R.id.lineChart2);
        mChart3 = rootView.findViewById(R.id.lineChart3);
        mChart4 = rootView.findViewById(R.id.lineChart4);
        mChartDialog = rootView.findViewById(R.id.lineChartDialog);

        mChartViewDialog = rootView.findViewById(R.id.graphDialog);
        mCoverView = rootView.findViewById(R.id.cover);
        mDataView = rootView.findViewById(R.id.data);

        ChartHelper chartHelper = new ChartHelper();
        chartHelper.initChart(mChart1, backgroundColor, textColor);
        chartHelper.initChart(mChart2, backgroundColor, textColor);
        chartHelper.initChart(mChart3, backgroundColor, textColor);
        chartHelper.initChart(mChart4, backgroundColor, textColor);
        chartHelper.initChartDialog(mChartDialog, backgroundColor, textColor);



        dataPM.pm10Entries.observe(this, pm10Entries -> {
            if(DataModel.currentTableName == "AVG_HOUR") {
                mChart1.clearValues();
                DataModel.currentColumnName = DataPM.col_pm10;
                for (Float[] pmEntry : pm10Entries) {
                    chartHelper.addEntry(mChart1, pmEntry, 0xff00ffff, false);
                }
            }
        });
        dataHT.fillGraph(DataModel.currentTableName, DataHT.col_temperature);

        dataPM.pm25Entries.observe(this, pm25Entries -> {
            if(DataModel.currentTableName == "AVG_HOUR") {
                mChart2.clearValues();
                DataModel.currentColumnName = DataPM.col_pm25;
                for (Float[] pmEntry : pm25Entries) {
                    chartHelper.addEntry(mChart2, pmEntry, 0xff00ff00, false);
                }
            }
        });
        dataHT.fillGraph(DataModel.currentTableName, DataHT.col_humidity );

        dataHT.humEntries.observe(this, humEntries -> {
            if(DataModel.currentTableName == "AVG_HOUR") {

                mChart3.clearValues();
                DataModel.currentColumnName = DataHT.col_humidity;
                for (Float[] pmEntry : humEntries) {
                    chartHelper.addEntry(mChart3, pmEntry, 0xffff00ff, false);
                }
            }
        });
        dataPM.fillGraph(DataModel.currentTableName, DataPM.col_pm25 );

        dataHT.tempEntries.observe(this, tempEntries -> {
            if(DataModel.currentTableName == "AVG_HOUR") {
                mChart4.clearValues();
                DataModel.currentColumnName = DataHT.col_temperature;
                for (Float[] pmEntry : tempEntries) {
                    chartHelper.addEntry(mChart4, pmEntry, 0xFFFF4081, false);
                }
            }
        });
        dataPM.fillGraph(DataModel.currentTableName, DataPM.col_pm10 );

        chartHelper.getSelected().observe(this, selected -> {
            mDataView.setText(selected.toString());
        });

        chartHelper.initChartDialog(mChartDialog, backgroundColor, textColor);

        mChart1.setOnClickListener(createPopupListener(DataPM.col_pm10, dataPM, chartHelper, 0xff00ffff));
        mChart2.setOnClickListener(createPopupListener(DataPM.col_pm25, dataPM, chartHelper, 0xff00ff00));
        mChart3.setOnClickListener(createPopupListener(DataHT.col_humidity, dataHT, chartHelper, 0xffff00ff));
        mChart4.setOnClickListener(createPopupListener(DataHT.col_temperature, dataHT, chartHelper, 0xFFFF4081));

        mCoverView.setClickable(true);
        mCoverView.setOnClickListener(view -> {
            mChartViewDialog.setVisibility(View.GONE);
            mCoverView.setVisibility(View.GONE);
            DataModel.currentTableName = "AVG_HOUR";
        });


        Button mButtonDay = rootView.findViewById(R.id.day_bt);
        mButtonDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeGraphScale("AVG_HOUR", "24", DataModel.currentColumnName);
            }
        });

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

