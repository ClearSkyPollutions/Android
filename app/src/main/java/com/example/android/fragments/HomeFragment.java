package com.example.android.fragments;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.activities.R;
import com.example.android.activities.databinding.FragmentHomeBinding;
import com.example.android.helpers.ChartHelper;
import com.example.android.models.Data;
import com.example.android.viewModels.DataHT_ori;
import com.example.android.viewModels.DataModelNew;
import com.example.android.viewModels.DataPM_ori;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    //private DataHT_ori dataHTOri;
    //private DataPM_ori dataPMOri;

    private DataModelNew mDataModel;

    //private LineChart mChart1, mChart2, mChart3, mChart4, mChartDialog;
    private List<LineChart> mCharts = new ArrayList<LineChart>();
    private LineChart mChartDialog;
    private CardView mChartViewDialog;
    private View mCoverView;
    private TextView mSelectedView;
    private Button mButtonDay;
    private Button mButtonMonth;
    private Button mButtonYear;

    private static final String TAG = HomeFragment.class.toString();

    public View.OnClickListener createPopupListener(String dataType, ChartHelper chartHelper,
                                                    int lineColor) {
        return view -> {
            Log.d(getTag(), "Created listener for graph " + dataType);

            mChartDialog.clearValues();

            for (Float[] entries : mDataModel.getMeasurements(dataType).getValue().values) {
                chartHelper.addEntry(mChartDialog, entries, lineColor, true);
            }
            mChartViewDialog.setVisibility(View.VISIBLE);
            mCoverView.setVisibility(View.VISIBLE);

            mDataModel.getMeasurements(dataType).observe(this, entries -> {
                mChartDialog.clearValues();
                    for (Float[] pmEntry : entries.values) {
                        chartHelper.addEntry(mChartDialog, pmEntry, lineColor, true);
                    }
            });
            mButtonDay.setOnClickListener(v ->
                    mDataModel.loadData(dataType, "AVG_DAY"));
            mButtonMonth.setOnClickListener(v ->
                    mDataModel.loadData(dataType, "AVG_MONTH"));
            mButtonYear.setOnClickListener(v ->
                    mDataModel.loadData(dataType, "AVG_YEAR"));

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
        // dataPMOri = ViewModelProviders.of(getActivity()).get(DataPM_ori.class);
        // dataHTOri = ViewModelProviders.of(getActivity()).get(DataHT_ori.class);
        mDataModel = ViewModelProviders.of(getActivity()).get(DataModelNew.class);
        // Bind the UI elements to the viewModel
        //binding.setLastDataPM(dataPMOri);
        //binding.setLastDataHT(dataHTOri);

        // Init charts
        int backgroundColor = Color.WHITE;
        int textColor = getResources().getColor(R.color.primaryTextColor);
        int lineColor = getResources().getColor(R.color.secondaryDarkColor);

        ChartHelper chartHelper = new ChartHelper();

        mChartDialog = rootView.findViewById(R.id.lineChartDialog);
        chartHelper.initChartDialog(mChartDialog, backgroundColor, textColor);

        mChartViewDialog = rootView.findViewById(R.id.graphDialog);

        for(String i : DataModelNew.DATA_TYPES){
            String idChart = "LineChart" + i;
            mCharts.add(rootView.findViewById(getResources().getIdentifier(idChart, "id", getContext().getPackageName())));
            chartHelper.initChart(mCharts.get(mCharts.size() - 1), backgroundColor, textColor);
        }

        mButtonDay = rootView.findViewById(R.id.day_bt);
        mButtonMonth = rootView.findViewById(R.id.month_bt);
        mButtonYear = rootView.findViewById(R.id.year_bt);

        // Init popup
        mCoverView = rootView.findViewById(R.id.cover);
        mSelectedView = rootView.findViewById(R.id.data);

        // Associates each graph with one data type
        for(int i = 0; i < DataModelNew.DATA_TYPES.length; i++){
            String type = DataModelNew.DATA_TYPES[i];
            int color = DataModelNew.LINE_COLORS[i];
            LineChart ch = mCharts.get(i);

            MutableLiveData<Data> dataObs = mDataModel.getMeasurements(type);

            dataObs.observe(this, data -> {
                    ch.clearValues();
                    for (Float[] pmEntry : data.values) {
                        chartHelper.addEntry(ch, pmEntry, color, false);
                    }
            });
            mDataModel.loadData(type, "AVG_HOUR");
            ch.setOnClickListener(createPopupListener(type, chartHelper, color));
        }

        // Associates a textView with the data selected in the graph
        chartHelper.getSelected().observe(this, selected -> {
            mSelectedView.setText(selected.toString());
        });

        chartHelper.initChartDialog(mChartDialog, backgroundColor, textColor);

        mCoverView.setClickable(true);
        mCoverView.setOnClickListener(view -> {
            mChartViewDialog.setVisibility(View.GONE);
            mCoverView.setVisibility(View.GONE);
        });

        return rootView;
    }
}

