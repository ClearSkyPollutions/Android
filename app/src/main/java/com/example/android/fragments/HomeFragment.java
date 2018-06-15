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
import com.example.android.models.Graph;
import com.example.android.viewModels.AQIModel;
import com.example.android.viewModels.DataModel;
import com.github.mikephil.charting.charts.LineChart;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment {

    private DataModel mDataModel;
    private AQIModel aqiModel;
    private ChartHelper mChartHelper = new ChartHelper();
    private FragmentHomeBinding mFragmentHomeBinding;

    //View objects
    private View mRootView;
    private LineChart mChartDialog;
    private CardView mDialogView;
    private View mCoverView;
    private Button mButtonDay;
    private Button mButtonMonth;
    private Button mButtonYear;
    private TextView mSelectedValueView;
    private TextView mLabelView;

    // List of charts in the home fragment
    private List<LineChart> mCharts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate using DataBinding library and set the lifeCycleOwner
        mFragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        mFragmentHomeBinding.setLifecycleOwner(this);
        // Init Charts and views
        initViews();
        Log.d(HomeFragment.class.toString(), "initViews()");
        // Create or get the ViewModel for our charts, and bind the xml variable lastData to it (DataBinding library)
        mDataModel = ViewModelProviders.of(getActivity()).get(DataModel.class);
        Log.d(HomeFragment.class.toString(), "get DataModel");
        mFragmentHomeBinding.setLastData(mDataModel);
        // Display last hour's data
        mDataModel.loadLastData();
        Log.d(HomeFragment.class.toString(), "loadLastData()");

        aqiModel = ViewModelProviders.of(getActivity()).get(AQIModel.class);
        mFragmentHomeBinding.setAqiUI(aqiModel);
        aqiModel.loadAQI();

        // Bind each UI chart with one MutableLiveData<Graph> (pm10, pm25...) and set an onClickListener for displaying the chart in a popup view
        for (int i = 0; i < DataModel.GRAPH_NAMES.length; i++) {
            LineChart chartView = mCharts.get(i);
            int lineColor = DataModel.LINE_COLORS[i];
            MutableLiveData<Graph> liveChart = mDataModel.graphList.get(i);
            if (liveChart.getValue() != null) {
                liveChart.getValue().setScale(DataModel.AVG_HOUR);
            }
            liveChart.observe(this, chart -> {
                // The home charts should only show graph by hour
                if ((chart == null || !chart.getScale().equals("AVG_HOUR"))) {
                    return;
                }
                mChartHelper.reset(chartView);
                for (int index = 0; index < chart.getXAxis().size(); index++ ) {
                    String dateString = getStringDate(chart.getXAxis().get(index), "");
                    Float ts_f = (float) Timestamp.valueOf(dateString).getTime();
                    Float value = chart.getYAxis().get(index);
                    Float[] entry = new Float[] {ts_f, value};
                    Log.d(HomeFragment.class.toString(),  "new entry for "+chart.getName()+" : "+ts_f+", "+value);
                    mChartHelper.addEntry(chartView, entry, lineColor , false);
                }
            });
            // Set listener to display the popup when chart is clicked
            View.OnClickListener onClickListener = createPopupListener(liveChart, mChartHelper, lineColor);
            chartView.setOnClickListener(onClickListener);
            Log.d(HomeFragment.class.toString(), "Set popup and live data observer to chart " + liveChart.getValue().getName());
            //Display chart data
            mDataModel.loadGraphData(liveChart);
            Log.d(HomeFragment.class.toString(), "loadGraphData() " + liveChart.getValue().getName());
        }

        // Sync graph data in the realm database with remote server

        //Click event listener for hiding popup views
        mCoverView.setClickable(true);
        mCoverView.setOnClickListener(view -> {
            mChartHelper.selected.removeObservers(this);
            mDialogView.setVisibility(View.GONE);
            mCoverView.setVisibility(View.GONE);
        });
        return mRootView;
    }

    private View.OnClickListener createPopupListener(MutableLiveData<Graph> graph, ChartHelper chartHelper, int lineColor) {
        return (View view) -> {
            // Bind the popup dialog graph to the data from the clicked chart
            graph.observe(this,  chart -> {
                chartHelper.reset(mChartDialog);
                if (chart != null) {
                    for (int index = 0; index < chart.getXAxis().size(); index++ ) {
                        String dateString = getStringDate(chart.getXAxis().get(index), "");
                        Float ts_f = (float) Timestamp.valueOf(dateString).getTime();
                        Float value = chart.getYAxis().get(index);
                        Float[] entry = new Float[] {ts_f, value};
                        mChartHelper.addEntry(mChartDialog, entry, lineColor , false);
                        Log.d(HomeFragment.class.toString(),  "new entry for "+chart.getName()+" : "+ts_f+", "+value);
                    }
                }
            });
            //Set buttons listeners to change the graph scale
            mButtonDay.setOnClickListener(v -> {
                graph.getValue().setScale(DataModel.AVG_HOUR);
                mDataModel.loadGraphData(graph);
                Log.d(HomeFragment.class.toString(), "loadGraphData() " + graph.getValue().getName());
            });
            Log.d(HomeFragment.class.toString(), "Set AVG_HOUR button listener");
            mButtonMonth.setOnClickListener(v -> {
                graph.getValue().setScale(DataModel.AVG_DAY);
                mDataModel.loadGraphData(graph);
                Log.d(HomeFragment.class.toString(), "loadGraphData() " + graph.getValue().getName());
            });
            Log.d(HomeFragment.class.toString(), "Set AVG_DAY button listener");
            mButtonYear.setOnClickListener(v -> {
                graph.getValue().setScale(DataModel.AVG_MONTH);
                mDataModel.loadGraphData(graph);
                Log.d(HomeFragment.class.toString(), "loadGraphData() " + graph.getValue().getName());
            });
            Log.d(HomeFragment.class.toString(), "Set AVG_MONTH button listener");
            // Display value selected with the graph cursor
            chartHelper.getSelected().observe(this, (Integer selected) -> {
                if (selected != null) {
                    if (selected == -1) {
                        mSelectedValueView.setText("");
                        mLabelView.setText("");
                    } else {
                        Log.d(HomeFragment.class.toString(), "graph size : "+graph.getValue().getYAxis().size());
                        mSelectedValueView.setText(graph.getValue().getYAxis().get(selected).toString());
                        Log.d(HomeFragment.class.toString(),selected.toString() + ", " + graph.getValue().getYAxis().get(selected) );
                        mLabelView.setText(getStringDate(graph.getValue().getXAxis().get(selected), graph.getValue().getScale()));
                        Log.d(HomeFragment.class.toString(),selected.toString() + ", " +
                                getStringDate(graph.getValue().getXAxis().get(selected), graph.getValue().getScale()) );

                    }
                }
            });
            Log.d(HomeFragment.class.toString(), "Set chart cursor listener");

            //Popup effect
            mDialogView.setVisibility(View.VISIBLE);
            mCoverView.setVisibility(View.VISIBLE);
        };
    }

    private void initViews() {
        mRootView = mFragmentHomeBinding.getRoot();
        // Init charts
        int chartTextColor = getResources().getColor(R.color.primaryTextColor);
        int chartBackgroundColor = Color.WHITE;
        for (String name : DataModel.GRAPH_NAMES) {
            LineChart chart = mRootView.findViewById(
                    getResources()
                    .getIdentifier(
                            "lineChart" + name,
                            "id",
                            getContext().getPackageName()
                    ));
            mChartHelper.initChart(chart, chartBackgroundColor, chartTextColor);
            mCharts.add(chart);
            Log.d(HomeFragment.class.toString(), "init chart " + name);
        }
        mChartDialog = mRootView.findViewById(R.id.lineChartDialog);
        mChartHelper.initChartDialog(mChartDialog, chartBackgroundColor, chartTextColor);
        Log.d(HomeFragment.class.toString(), "init dialog chart");

        //Init buttons
        mButtonDay = mRootView.findViewById(R.id.day_bt);
        mButtonMonth = mRootView.findViewById(R.id.month_bt);
        mButtonYear = mRootView.findViewById(R.id.year_bt);
        Log.d(HomeFragment.class.toString(), "init buttons");
        // Init popup views
        mCoverView = mRootView.findViewById(R.id.cover);
        mSelectedValueView = mRootView.findViewById(R.id.data);
        mLabelView = mRootView.findViewById(R.id.labelData);
        mDialogView = mRootView.findViewById(R.id.viewDialog);
        Log.d(HomeFragment.class.toString(), "init popup views");
    }

    private static String getStringDate(Date date, String scale) {
        SimpleDateFormat ft;
        switch(scale){
            case "AVG_HOUR": ft = new SimpleDateFormat("EEE HH'h'", Locale.FRANCE);
                break;
            case "AVG_DAY": ft = new SimpleDateFormat("EEE dd", Locale.FRANCE);
                break;
            case "AVG_MONTH": ft = new SimpleDateFormat("MMM", Locale.FRANCE);
                break;
            default: ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
                break;
        }
        return ft.format(date);
    }
}