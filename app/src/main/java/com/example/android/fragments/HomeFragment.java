package com.example.android.fragments;


import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.android.activities.R;
import com.example.android.activities.databinding.FragmentHomeBinding;
import com.example.android.adapters.ChartItemAdapter;
import com.example.android.helpers.ChartHelper;
import com.example.android.viewModels.DataModel;
import com.github.mikephil.charting.charts.LineChart;


public class HomeFragment extends Fragment {

    private DataModel mDataModel;

    //XML view objects
    private LineChart mChartDialog;
    private CardView mChartViewDialog;
    private View mCoverView;
    private Button mButtonDay;
    private Button mButtonMonth;
    private Button mButtonYear;
    private TextView mDataView;
    private TextView mLabelView;
    private GridView mGridView;

    private ChartItemAdapter chartItemAdapter;
    private ChartHelper chartHelper = new ChartHelper();

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
        initViews(rootView);

        mCoverView.setClickable(true);
        mCoverView.setOnClickListener(view -> {
        mChartViewDialog.setVisibility(View.GONE);
        mCoverView.setVisibility(View.GONE);
        });

        // Functions called when a chart is clicked on
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mChartViewDialog.setVisibility(View.VISIBLE);
                mCoverView.setVisibility(View.VISIBLE);

                // Get the correct LiveData(pm10, pm25...) and bind the graph to it
                chartItemAdapter.getItem(position).observe((LifecycleOwner) getContext(), entries -> {
                    mChartDialog.clearValues();
                    for (Float[] pmEntry : entries.values) {
                        chartHelper.addEntry(mChartDialog, pmEntry, mDataModel.LINE_COLORS[position], true);
                    }
                });

                //Change the buttons event according to dataType
                mButtonDay.setOnClickListener(v ->
                        mDataModel.loadData(mDataModel.DATA_TYPES[position], "AVG_HOUR"));
                mButtonMonth.setOnClickListener(v ->
                        mDataModel.loadData(mDataModel.DATA_TYPES[position], "AVG_DAY"));
                mButtonYear.setOnClickListener(v ->
                        mDataModel.loadData(mDataModel.DATA_TYPES[position], "AVG_MONTH"));

                chartHelper.getSelected().observe((LifecycleOwner) getContext(), selected -> {
                    mDataView.setText(selected[1].toString());
                    mLabelView.setText(ChartHelper.getStringDate(selected[0], mDataModel.getMeasurements(mDataModel.DATA_TYPES[position]).getValue().scale));
                });
            }
        });

        return rootView;
    }

    private void initViews(View rootView) {

        int textColor = getResources().getColor(R.color.primaryTextColor);
        int backgroundColor = Color.WHITE;

        // Create the adapter to convert the array to views
        chartItemAdapter = new ChartItemAdapter(getActivity(),mDataModel);

        // Attach the adapter to the GridView
        mGridView = rootView.findViewById(R.id.chartlist);
        mGridView.setAdapter(chartItemAdapter);

        mChartDialog = rootView.findViewById(R.id.lineChartDialog);
        chartHelper.initChartDialog(mChartDialog, backgroundColor, textColor);
        mChartViewDialog = rootView.findViewById(R.id.graphDialog);

        //Init buttons
        mButtonDay = rootView.findViewById(R.id.day_bt);
        mButtonMonth = rootView.findViewById(R.id.month_bt);
        mButtonYear = rootView.findViewById(R.id.year_bt);

        // Init popup
        mCoverView = rootView.findViewById(R.id.cover);
        mDataView = rootView.findViewById(R.id.data);
        mLabelView = rootView.findViewById(R.id.labelData);
    }

}