package com.example.android.fragments;


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
import com.example.android.viewModels.DataModel;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private DataModel mDataModel;

    //XML view objects
    private LineChart mChartDialog;
    private CardView mChartViewDialog;
    private View mCoverView;
    private TextView mSelectedView;
    private Button mButtonDay;
    private Button mButtonMonth;
    private Button mButtonYear;

    // List of charts in the home page
    private List<LineChart> mCharts = new ArrayList<LineChart>();

    public View.OnClickListener createPopupListener(String dataType, ChartHelper chartHelper,
                                                    int lineColor) {
        // Creates and return the functions called when a chart is clicked on
        return view -> {
            //Popup effect
            mChartViewDialog.setVisibility(View.VISIBLE);
            mCoverView.setVisibility(View.VISIBLE);

            // Get the correct LiveData(pm10, pm25...) and bind the graph to it
            mDataModel.getMeasurements(dataType).observe(this, entries -> {
                mChartDialog.clearValues();
                for (Float[] pmEntry : entries.values) {
                    chartHelper.addEntry(mChartDialog, pmEntry, lineColor, true);
                }
            });

            //Change the buttons event according to dataType
            mButtonDay.setOnClickListener(v ->
                    mDataModel.loadData(dataType, "AVG_HOUR"));
            mButtonMonth.setOnClickListener(v ->
                    mDataModel.loadData(dataType, "AVG_DAY"));
            mButtonYear.setOnClickListener(v ->
                    mDataModel.loadData(dataType, "AVG_MONTH"));

        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // Inflate using DataBinding library
        FragmentHomeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        binding.setLifecycleOwner(this);
        View rootView = binding.getRoot();

        // Create or get the ViewModel for our date, and bind the xml variable lastData to it (Databinding library)
        mDataModel = ViewModelProviders.of(getActivity()).get(DataModel.class);
        binding.setLastData(mDataModel);

        // Init Charts and views
        ChartHelper chartHelper = new ChartHelper();
        initViews(rootView, chartHelper);

        // Associates each chart with one LiveData (pm10, pm25...)
        for (int i = 0; i < DataModel.DATA_TYPES.length; i++) {
            String type = DataModel.DATA_TYPES[i];
            int color = DataModel.LINE_COLORS[i];
            LineChart ch = mCharts.get(i);

            mDataModel.getMeasurements(type).observe(this, data -> {
                // The home charts should only show data by hour
                if (!data.scale.equals("AVG_HOUR"))
                    return;
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

        mCoverView.setClickable(true);
        mCoverView.setOnClickListener(view -> {
            mChartViewDialog.setVisibility(View.GONE);
            mCoverView.setVisibility(View.GONE);
        });

        return rootView;
    }

    private void initViews(View rootView, ChartHelper chartHelper) {

        int textColor = getResources().getColor(R.color.primaryTextColor);
        int backgroundColor = Color.WHITE;

        // Init charts
        for (String i : DataModel.DATA_TYPES) {
            String idChart = "lineChart" + i;
            mCharts.add(rootView.findViewById(getResources().getIdentifier(idChart, "id", getContext().getPackageName())));
            chartHelper.initChart(mCharts.get(mCharts.size() - 1), backgroundColor, textColor);
        }
        mChartDialog = rootView.findViewById(R.id.lineChartDialog);
        chartHelper.initChartDialog(mChartDialog, backgroundColor, textColor);
        mChartViewDialog = rootView.findViewById(R.id.graphDialog);

        //Init buttons
        mButtonDay = rootView.findViewById(R.id.day_bt);
        mButtonMonth = rootView.findViewById(R.id.month_bt);
        mButtonYear = rootView.findViewById(R.id.year_bt);

        // Init popup
        mCoverView = rootView.findViewById(R.id.cover);
        mSelectedView = rootView.findViewById(R.id.data);

    }

}