package com.example.android.fragments;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.android.activities.R;
import com.example.android.activities.databinding.FragmentHomeBinding;
import com.example.android.helpers.ChartHelper;
import com.example.android.models.Data;
import com.example.android.models.Graph;
import com.example.android.viewModels.DataModel;
import com.github.mikephil.charting.charts.LineChart;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment {

    private DataModel mDataModel;
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
    private List<LineChart> mCharts = new ArrayList<LineChart>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate using DataBinding library and set the lifeCycleOwner
        mFragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        mFragmentHomeBinding.setLifecycleOwner(this);
        // Init Charts and views
        initViews();
        // Create or get the ViewModel for our charts, and bind the xml variable lastData to it (DataBinding library)
        mDataModel = ViewModelProviders.of(getActivity()).get(DataModel.class);
        mFragmentHomeBinding.setLastData(mDataModel);
        // Bind each UI chart with one MutableLiveData<Graph> (pm10, pm25...) and set an onClickListener for displaying the chart in a popup view
        for (int i = 0; i < DataModel.GRAPH_NAMES.length; i++) {
            LineChart chartView = mCharts.get(i);
            int lineColor = DataModel.LINE_COLORS[i];
            MutableLiveData<Graph> liveChart = mDataModel.graphList.get(i);
            liveChart.getValue().setScale(DataModel.AVG_HOUR);
            liveChart.observe(this, graph -> {
                // The home charts should only show graph by hour
                if (!graph.getScale().equals("AVG_HOUR")) {
                    return;
                }
                chartView.clearValues();
                for (Data data : graph.getData()) {
                    mChartHelper.addEntry(chartView, data, lineColor , false);
                }
            });
            // Set listener to display the popup when chart is clicked
            View.OnClickListener onClickListener = createPopupListener(liveChart, mChartHelper, lineColor);
            chartView.setOnClickListener(onClickListener);

            mDataModel.loadGraphData(liveChart);
        }

        // Bind the UI to the model to display last hour's data
        mDataModel.lastDataReceived.observe(this, measures -> {
            mDataModel.lastTempValueReceived.postValue(measures.get(3).toString());
            SimpleDateFormat ft = new SimpleDateFormat("EEE hh'h'", Locale.FRANCE);
            mDataModel.lastDatetimeReceived.postValue(ft.format(measures.get(0)));
        });
        mDataModel.loadLastData();

        //Click event listener for hiding popup views
        mCoverView.setClickable(true);
        mCoverView.setOnClickListener(view -> {
            mDialogView.setVisibility(View.GONE);
            mCoverView.setVisibility(View.GONE);
        });

        return mRootView;
    }

    private View.OnClickListener createPopupListener(MutableLiveData<Graph> graph, ChartHelper chartHelper, int lineColor) {
        return view -> {
            // Bind the popup dialog graph to the data from the clicked chart
            graph.observe(this, chart -> {
                mChartDialog.clearValues();
                for (Data data : chart.getData()) {
                    chartHelper.addEntry(mChartDialog, data, lineColor, true);
                }
            });
            //Set buttons listeners to change the graph scale
            mButtonDay.setOnClickListener(v -> {
                graph.getValue().setScale(DataModel.AVG_HOUR);
                mDataModel.loadGraphData(graph);
            });
            mButtonMonth.setOnClickListener(v -> {
                graph.getValue().setScale(DataModel.AVG_DAY);
                mDataModel.loadGraphData(graph);
            });
            mButtonYear.setOnClickListener(v -> {
                graph.getValue().setScale(DataModel.AVG_MONTH);
                mDataModel.loadGraphData(graph);
            });
            // Display value selected with the graph cursor
            chartHelper.getSelected().observe(this, selected -> {
                if (selected.length == 0) {
                    mSelectedValueView.setText("");
                    mLabelView.setText("");
                } else {
                    mSelectedValueView.setText(selected[1].toString());
                    mLabelView.setText(ChartHelper.getStringDate(selected[0], graph.getValue().getScale()));
                }
            });
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
        }
        mChartDialog = mRootView.findViewById(R.id.lineChartDialog);
        mChartHelper.initChartDialog(mChartDialog, chartBackgroundColor, chartTextColor);
        //Init buttons
        mButtonDay = mRootView.findViewById(R.id.day_bt);
        mButtonMonth = mRootView.findViewById(R.id.month_bt);
        mButtonYear = mRootView.findViewById(R.id.year_bt);
        // Init popup views
        mCoverView = mRootView.findViewById(R.id.cover);
        mSelectedValueView = mRootView.findViewById(R.id.data);
        mLabelView = mRootView.findViewById(R.id.labelData);
        mDialogView = mRootView.findViewById(R.id.viewDialog);
    }

}