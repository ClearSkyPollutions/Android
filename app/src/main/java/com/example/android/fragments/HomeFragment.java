package com.example.android.fragments;


import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.activities.R;
import com.example.android.activities.databinding.FragmentHomeBinding;
import com.example.android.adapters.ChartItemAdapter;
import com.example.android.helpers.ChartHelper;
import com.example.android.models.Chart;
import com.example.android.viewModels.AQIModel;
import com.example.android.viewModels.DataModel;
import com.github.mikephil.charting.charts.LineChart;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;


public class HomeFragment extends Fragment {

    private DataModel mDataModel;
    private AQIModel aqiModel;
    private ChartHelper mChartHelper = new ChartHelper();

    //View objects
    private View mRootView;
    private LineChart mChartDialog;
    private CardView mCardCitiesFront;
    private CardView mCardCitiesBack;
    private CardView mChartViewDialog;
    private CardView mCardAddChart;
    private View mCoverView;
    private Button mButtonDay;
    private Button mButtonMonth;
    private Button mButtonYear;
    private Button mButtonAddChart;
    private TextView mSelectedValueView;
    private TextView mLabelView;
    private GridView mGridView;
    private ImageButton mImageButtonFront;
    private ImageButton mImageButtonBack;
    private ImageButton mImageButtonAddChart;
    private Spinner mSpinnerNameSensors;
    private Spinner mSpinnerDataType;
    private Spinner mSpinnerDataUnits;

    // Rotate View
    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean mIsBackCardVisible = false;

    private ChartItemAdapter chartItemAdapter;

    public HomeFragment() {
    }

    // @TODO : Remplacer par un download des types depuis la RPI
    private void initDefaultTypes() {
        if(mDataModel.data_types.isEmpty()) {
            mDataModel.data_types.add("pm10");
            mDataModel.data_units.add("µg/m^3");
            mDataModel.line_colors.add(0xff00ffff);

            mDataModel.data_types.add("pm25");
            mDataModel.data_units.add("µg/m^3");
            mDataModel.line_colors.add(0xff00ff00);

            mDataModel.data_types.add("humidity");
            mDataModel.data_units.add("%");
            mDataModel.line_colors.add(0xffff00ff);

            mDataModel.data_types.add("temperature");
            mDataModel.data_units.add("°C");
            mDataModel.line_colors.add(0xFFFF4081);

            mDataModel.syncAll();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate using DataBinding library
        FragmentHomeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        binding.setLifecycleOwner(this);
        mRootView = binding.getRoot();

        // Create or get the ViewModel for our charts. Bind the xml variable lastData
        mDataModel = ViewModelProviders.of(getActivity()).get(DataModel.class);

        // Load data types
        initDefaultTypes();

        binding.setLastData(mDataModel);
        mDataModel.loadLastData();

        // Create or get the ViewModel for the Air Quality Index. Bind the xml variable aqiUI
        aqiModel = ViewModelProviders.of(getActivity()).get(AQIModel.class);
        binding.setAqiUI(aqiModel);
        aqiModel.loadAQI();

        // Init Charts and views
        initViews();

        //Click event listener for hiding popup views
        mCoverView.setClickable(true);
        mCoverView.setOnClickListener(view -> {
            mChartHelper.getSelected().removeObservers(this);
            mChartViewDialog.setVisibility(View.GONE);
            mCoverView.setVisibility(View.GONE);
            mCardAddChart.setVisibility(View.GONE);
            mButtonAddChart.setClickable(false);
        });

        // Functions called when a chart is clicked on
        mGridView.setOnItemClickListener((parent, view, position, id) -> {

            mChartViewDialog.setVisibility(View.VISIBLE);
            mCoverView.setVisibility(View.VISIBLE);

            // Get the correct LiveData(pm10, pm25...) and bind the graph to it
            chartItemAdapter.getItem(position).observe((LifecycleOwner) getContext(), entries -> {
                mChartHelper.reset(mChartDialog);
                for (int index = 0; index < entries.getXAxis().size(); index++) {
                    String dateString = ChartHelper.getStringDate(entries.getXAxis().get(index), "");
                    Float ts_f = (float) Timestamp.valueOf(dateString).getTime();
                    Float value = entries.getYAxis().get(index);
                    Float[] entry = new Float[]{ts_f, value};
                    mChartHelper.addEntry(mChartDialog, entry, mDataModel.line_colors.get(position), true);
                }
            });

            //Change the buttons event according to dataType
            mButtonDay.setOnClickListener(v ->
                    mDataModel.loadChartData(mDataModel.data_types.get(position),
                            DataModel.AVG_HOUR));
            mButtonMonth.setOnClickListener(v ->
                    mDataModel.loadChartData(mDataModel.data_types.get(position),
                            DataModel.AVG_DAY));
            mButtonYear.setOnClickListener(v ->
                    mDataModel.loadChartData(mDataModel.data_types.get(position),
                            DataModel.AVG_MONTH));

            // Display the values selected with the chart cursor
            mChartHelper.getSelected().observe(this, (Integer selected) -> {
                // -1 for error in selection, null if mutableLiveData not set
                if (selected == null || selected == -1) {
                    mSelectedValueView.setText("");
                    mLabelView.setText("");
                } else {
                    Chart ch = mDataModel.getChart(mDataModel.data_types.get(position)).getValue();

                    String val = ch.getYAxis().get(selected).toString();
                    Date date = ch.getXAxis().get(selected);

                    // Formatting of the date depends on the scale of the chart
                    String dateString = ChartHelper.getStringDate(date, ch.getScale());

                    mSelectedValueView.setText(val);
                    mLabelView.setText(dateString);
                }
            });
            if (mIsBackCardVisible) {
                chartItemAdapter.notifyDataSetChanged();
            }
        });

        // Functions Onclick on ImageButton
        mImageButtonFront.setOnClickListener(this::flipCard);
        mImageButtonBack.setOnClickListener(this::flipCard);

        mImageButtonAddChart.setOnClickListener(v -> {
            mCardAddChart.setVisibility(View.VISIBLE);
            mCoverView.setVisibility(View.VISIBLE);
            mButtonAddChart.setClickable(true);
        });

        mButtonAddChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random rnd = new Random();
                if (!mDataModel.data_types.contains(mSpinnerDataType.getSelectedItem())) {
                    mDataModel.data_types.add(mSpinnerDataType.getSelectedItem().toString());
                    mDataModel.data_units.add(mSpinnerDataUnits.getSelectedItem().toString());
                    mDataModel.line_colors.add(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
                }

                //Change item in GridView
                chartItemAdapter.notifyDataSetChanged();

                //Leave Card Add Chart
                mCardAddChart.setVisibility(View.GONE);
                mCoverView.setVisibility(View.GONE);

            }
        });
        return mRootView;
    }

    private void initViews() {

        // Init charts
        int chartTextColor = getResources().getColor(R.color.primaryTextColor);
        int chartBackgroundColor = Color.WHITE;

        // Create the adapter to convert the array to views
        chartItemAdapter = new ChartItemAdapter(getActivity(), mDataModel, mChartHelper);

        // Attach the adapter to the GridView
        mGridView = mRootView.findViewById(R.id.chartlist);
        mGridView.setAdapter(chartItemAdapter);

        mChartDialog = mRootView.findViewById(R.id.lineChartDialog);
        mChartHelper.initChartDialog(mChartDialog, chartBackgroundColor, chartTextColor);
        mChartViewDialog = mRootView.findViewById(R.id.viewDialog);

        // Init CardView
        mCardCitiesFront = mRootView.findViewById(R.id.CardFront);
        mCardCitiesBack = mRootView.findViewById(R.id.CardBack);
        mCardAddChart = mRootView.findViewById(R.id.addElement);

        //For animation change the distance
        changeCameraDistance();

        //Init buttons
        mButtonDay = mRootView.findViewById(R.id.day_bt);
        mButtonMonth = mRootView.findViewById(R.id.month_bt);
        mButtonYear = mRootView.findViewById(R.id.year_bt);
        mButtonAddChart = mRootView.findViewById(R.id.buttonAddElement);

        // Init popup
        mCoverView = mRootView.findViewById(R.id.cover);
        mLabelView = mRootView.findViewById(R.id.labelData);
        mSelectedValueView = mRootView.findViewById(R.id.data);

        // Init ImageButton
        mImageButtonFront = mRootView.findViewById(R.id.buttonEditFront);
        mImageButtonBack = mRootView.findViewById(R.id.buttonEditBack);
        mImageButtonAddChart = mRootView.findViewById(R.id.buttonAddBack);

        // Init Spinner
        mSpinnerNameSensors = mRootView.findViewById(R.id.SPnameSensors);
        mSpinnerDataType = mRootView.findViewById(R.id.SPdataType);
        mSpinnerDataUnits = mRootView.findViewById(R.id.SPdataUnits);

        // Init Animation
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.in_animation);
    }

    public void flipCard(View view) {
        if (!mIsBackCardVisible) {
            mSetRightOut.setTarget(mCardCitiesFront);
            mSetLeftIn.setTarget(mCardCitiesBack);

            for (int i = 0; i < chartItemAdapter.mChartCardFront.size(); i++) {
                chartItemAdapter.mChartCardFront.get(i).setVisibility(View.GONE);
                chartItemAdapter.mChartCardBack.get(i).setVisibility(View.VISIBLE);
            }

            mSetRightOut.start();
            mSetLeftIn.start();
            mImageButtonFront.setClickable(false);
            mImageButtonBack.setClickable(true);
            mButtonAddChart.setClickable(true);
            mImageButtonAddChart.setClickable(true);
            mIsBackCardVisible = true;
        } else {
            mSetRightOut.setTarget(mCardCitiesBack);
            mSetLeftIn.setTarget(mCardCitiesFront);

            for (int i = 0; i < chartItemAdapter.mChartCardFront.size(); i++) {
                chartItemAdapter.mChartCardFront.get(i).setVisibility(View.VISIBLE);
                chartItemAdapter.mChartCardBack.get(i).setVisibility(View.GONE);
            }

            mSetRightOut.start();
            mSetLeftIn.start();
            mImageButtonFront.setClickable(true);
            mImageButtonBack.setClickable(false);
            mButtonAddChart.setClickable(false);
            mImageButtonAddChart.setClickable(false);
            mCardAddChart.setVisibility(View.GONE);
            mIsBackCardVisible = false;
        }
    }

    private void changeCameraDistance() {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        mCardCitiesFront.setCameraDistance(scale);
        mCardCitiesBack.setCameraDistance(scale);
    }


}