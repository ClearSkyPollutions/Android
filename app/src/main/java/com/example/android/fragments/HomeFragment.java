package com.example.android.fragments;


import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleOwner;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.activities.R;
import com.example.android.activities.databinding.FragmentHomeBinding;
import com.example.android.adapters.ChartItemAdapter;
import com.example.android.helpers.ChartHelper;
import com.example.android.viewModels.DataModel;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class HomeFragment extends Fragment {

    private DataModel mDataModel;

    //XML view objects
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
    private TextView mDataView;
    private TextView mLabelView;
    private GridView mGridView;
    private ImageButton mImageButtonFront;
    private ImageButton mImageButtonBack;
    private ImageButton mImageButtonAdddChart;
    private Spinner mSpinnerNameSensors;
    private Spinner mSpinnerDataType;
    private Spinner mSpinnerDataUnits;

    // Rotate View
    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean mIsBackCardVisible = false;

    private ChartItemAdapter chartItemAdapter;
    private ChartHelper chartHelper = new ChartHelper();

    private ArrayList<String> Data_types = new ArrayList<>();
    private ArrayList<String> Data_units = new ArrayList<>();
    private ArrayList<Integer> Data_colors = new ArrayList<>();

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // Inflate using DataBinding library
        FragmentHomeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        binding.setLifecycleOwner(this);
        View rootView = binding.getRoot();

        // Create or get the ViewModel for our date, and bind the xml variable lastData to it (Databinding library)
        Data_types.add("pm10");
        //Data_types.add("pm25");
        //Data_types.add("temperature");
        Data_types.add("humidity");
        Data_units.add("µg/m^3");
        //Data_units.add("µg/m^3");
        //Data_units.add("°C");
        Data_units.add("%");
        Data_colors.add(0xff00ffff);
        //Data_colors.add(0xff00ff00);
        //Data_colors.add(0xffff00ff);
        Data_colors.add(0xFFFF4081);

        mDataModel = new DataModel(Data_types,Data_units,Data_colors);
        binding.setLastData(mDataModel);

        // Init Charts and views
        initViews(rootView);

        // Cover View
        mCoverView.setClickable(true);
        mCoverView.setOnClickListener(view -> {
            mChartViewDialog.setVisibility(View.GONE);
            mCoverView.setVisibility(View.GONE);
            mCardAddChart.setVisibility(View.GONE);
            mButtonAddChart.setClickable(false);
        });

        // Functions called when a chart is clicked on
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("db","clique");

                mChartViewDialog.setVisibility(View.VISIBLE);
                mCoverView.setVisibility(View.VISIBLE);

                // Get the correct LiveData(pm10, pm25...) and bind the graph to it
                chartItemAdapter.getItem(position).observe((LifecycleOwner) getContext(), entries -> {
                    mChartDialog.clearValues();
                    for (Float[] pmEntry : entries.values) {
                        chartHelper.addEntry(mChartDialog, pmEntry, mDataModel.LINE_COLORS.get(position), true);
                    }
                });
                //Change the buttons event according to dataType
                mButtonDay.setOnClickListener(v ->
                        mDataModel.loadData(mDataModel.DATA_TYPES.get(position), "AVG_HOUR"));
                mButtonMonth.setOnClickListener(v ->
                        mDataModel.loadData(mDataModel.DATA_TYPES.get(position), "AVG_DAY"));
                mButtonYear.setOnClickListener(v ->
                        mDataModel.loadData(mDataModel.DATA_TYPES.get(position), "AVG_MONTH"));

                chartHelper.getSelected().observe((LifecycleOwner) getContext(), selected -> {
                    mDataView.setText(selected[1].toString());
                    mLabelView.setText(ChartHelper.getStringDate(selected[0], mDataModel.getMeasurements(mDataModel.DATA_TYPES.get(position)).getValue().scale));
                });
                if (mIsBackCardVisible){
                    chartItemAdapter.notifyDataSetChanged();
                }
            }
        });

        // Functions Onclick on ImageButton
        mImageButtonFront.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                flipCard(v);
            }
        });
        mImageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCard(v);
            }
        });

        mImageButtonAdddChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardAddChart.setVisibility(View.VISIBLE);
                mCoverView.setVisibility(View.VISIBLE);
                mButtonAddChart.setClickable(true);
            }
        });

        mButtonAddChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random rnd = new Random();
                if(!mDataModel.DATA_TYPES.contains(mSpinnerDataType.getSelectedItem())) {
                    mDataModel.DATA_TYPES.add(mSpinnerDataType.getSelectedItem().toString());
                    mDataModel.DATA_UNITS.add(mSpinnerDataUnits.getSelectedItem().toString());
                    mDataModel.LINE_COLORS.add(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)) );
                }
                Log.d("DataModel", Arrays.toString(mDataModel.DATA_TYPES.toArray()));
                Log.d("DataModel", Arrays.toString(mDataModel.DATA_UNITS.toArray()));
                Log.d("DataModel", Arrays.toString(mDataModel.LINE_COLORS.toArray()));

                //Change item in GridView
                chartItemAdapter.notifyDataSetChanged();
                mGridView.setAdapter(chartItemAdapter);

                //Leave Card Add Chart
                mCardAddChart.setVisibility(View.GONE);
                mCoverView.setVisibility(View.GONE);
                flipCard(v);
                }
        });
        return rootView;
    }


    @SuppressLint("ResourceType")
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

        // Init CardView
        mCardCitiesFront = rootView.findViewById(R.id.CardFront);
        mCardCitiesBack = rootView.findViewById(R.id.CardBack);
        mCardAddChart = rootView.findViewById(R.id.addElement);

        //For animation change the distance
        changeCameraDistance();

        //Init buttons
        mButtonDay = rootView.findViewById(R.id.day_bt);
        mButtonMonth = rootView.findViewById(R.id.month_bt);
        mButtonYear = rootView.findViewById(R.id.year_bt);
        mButtonAddChart = rootView.findViewById(R.id.buttonAddElement);

        // Init popup
        mCoverView = rootView.findViewById(R.id.cover);
        mDataView = rootView.findViewById(R.id.data);
        mLabelView = rootView.findViewById(R.id.labelData);

        // Init ImageButton
        mImageButtonFront = rootView.findViewById(R.id.buttonEditFront);
        mImageButtonBack = rootView.findViewById(R.id.buttonEditBack);
        mImageButtonAdddChart = rootView.findViewById(R.id.buttonAddBack);

        // Init Spinner
        mSpinnerNameSensors = rootView.findViewById(R.id.SPnameSensors);
        mSpinnerDataType = rootView.findViewById(R.id.SPdataType);
        mSpinnerDataUnits = rootView.findViewById(R.id.SPdataUnits);

        // Init Animation
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.anim.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.anim.in_animation);
    }

    public void flipCard(View view) {
        if (!mIsBackCardVisible) {
            mSetRightOut.setTarget(mCardCitiesFront);
            mSetLeftIn.setTarget(mCardCitiesBack);
            Log.d("nb Chart", ""+chartItemAdapter.mChartCardFront.size());
            for (int i =  0; i < chartItemAdapter.mChartCardFront.size(); i++) {
                chartItemAdapter.mChartCardFront.get(i).setAlpha(0);
                chartItemAdapter.mChartCardBack.get(i).setAlpha(1);
                chartItemAdapter.mChartCardFront.get(i).setVisibility(View.GONE);
                chartItemAdapter.mChartCardBack.get(i).setVisibility(View.VISIBLE);
            }
            mSetRightOut.start();
            mSetLeftIn.start();
            mImageButtonFront.setClickable(false);
            mImageButtonBack.setClickable(true);
            mButtonAddChart.setClickable(true);
            mImageButtonAdddChart.setClickable(true);
            mIsBackCardVisible = true;
        } else {
            mSetRightOut.setTarget(mCardCitiesBack);
            mSetLeftIn.setTarget(mCardCitiesFront);
            Log.d("nb Chart", ""+chartItemAdapter.mChartCardFront.size());
            for (int i =  0; i < chartItemAdapter.mChartCardFront.size(); i++) {
                chartItemAdapter.mChartCardFront.get(i).setAlpha(1);
                chartItemAdapter.mChartCardBack.get(i).setAlpha(0);
                chartItemAdapter.mChartCardFront.get(i).setVisibility(View.VISIBLE);
                chartItemAdapter.mChartCardBack.get(i).setVisibility(View.GONE);
            }
            mSetRightOut.start();
            mSetLeftIn.start();
            mImageButtonFront.setClickable(true);
            mImageButtonBack.setClickable(false);
            mButtonAddChart.setClickable(false);
            mImageButtonAdddChart.setClickable(false);
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