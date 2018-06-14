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
import android.widget.TextView;

import com.example.android.activities.R;
import com.example.android.activities.databinding.FragmentHomeBinding;
import com.example.android.adapters.ChartItemAdapter;
import com.example.android.helpers.ChartHelper;
import com.example.android.viewModels.DataModel;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private DataModel mDataModel;

    //XML view objects
    private LineChart mChartDialog;
    private CardView mCardCitiesFront;
    private CardView mCardCitiesBack;
    private CardView mChartViewDialog;
    private View mCoverView;
    private Button mButtonDay;
    private Button mButtonMonth;
    private Button mButtonYear;
    private TextView mDataView;
    private TextView mLabelView;
    private GridView mGridView;
    private ImageButton mImageButtonFront;
    private ImageButton mImageButtonBack;

    // Rotate View
    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean mIsBackVisible = false;

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
        Data_types.add("pm25");
        Data_types.add("temperature");
        Data_types.add("humidity");
        Data_units.add("µg/m^3");
        Data_units.add("µg/m^3");
        Data_units.add("°C");
        Data_units.add("%");
        Data_colors.add(0xff00ffff);
        Data_colors.add(0xff00ff00);
        Data_colors.add(0xffff00ff);
        Data_colors.add(0xFFFF4081);

        mDataModel = new DataModel(Data_types,Data_units,Data_colors);
        //mDataModel = ViewModelProviders.of(getActivity()).get(DataModel.class);
        binding.setLastData(mDataModel);

        // Init Charts and views
        initViews(rootView);

        //
        mCoverView.setClickable(true);
        mCoverView.setOnClickListener(view -> {
        mChartViewDialog.setVisibility(View.GONE);
        mCoverView.setVisibility(View.GONE);
        });
        /*
        // Functions called when a chart is clicked on
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("db","clique");
                if (!mIsBackVisible){
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
                }else {
                    Log.d("autre","back");
                }


            }
        });*/

        // Fonctions Onclick on ImageButton
        mImageButtonFront.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                flipCard(v);
                /*mDataModel.DATA_TYPES.remove("temperature");
                mDataModel.DATA_UNITS.remove("°C");

                mDataModel.DATA_TYPES.add("humidity");
                mDataModel.DATA_UNITS.add("%");
                mDataModel.LINE_COLORS.add(0xffff00ff);
                mDataModel.LINE_COLORS.add(0xFFFF4081);

                Log.d("dataT", Arrays.toString(mDataModel.DATA_TYPES.toArray()));
                Log.d("dataU", Arrays.toString(mDataModel.DATA_UNITS.toArray()));


                chartItemAdapter.notifyDataSetChanged();*/

                //chartItemAdapter = new ChartItemAdapter(getActivity(),mDataModel);
                //mGridView.setAdapter(chartItemAdapter);
                //mGridView.setAdapter(null);
            }
        });
        mImageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        changeCameraDistance();

        //Init buttons
        mButtonDay = rootView.findViewById(R.id.day_bt);
        mButtonMonth = rootView.findViewById(R.id.month_bt);
        mButtonYear = rootView.findViewById(R.id.year_bt);

        // Init popup
        mCoverView = rootView.findViewById(R.id.cover);
        mDataView = rootView.findViewById(R.id.data);
        mLabelView = rootView.findViewById(R.id.labelData);

        // Init ImageButton
        mImageButtonFront = rootView.findViewById(R.id.buttonEditFront);
        mImageButtonBack = rootView.findViewById(R.id.buttonEditBack);

        // Init Animation
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.anim.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.anim.in_animation);
    }

    public void flipCard(View view) {
        if (!mIsBackVisible) {
            mSetRightOut.setTarget(mCardCitiesFront);
            mSetLeftIn.setTarget(mCardCitiesBack);
            for (int i = 0; i < chartItemAdapter.mChartSetLeftIn.size(); i++){
                chartItemAdapter.mChartSetLeftIn.get(i).setTarget(chartItemAdapter.viewBack.get(i));
                chartItemAdapter.mChartSetRightOut.get(i).setTarget(chartItemAdapter.viewFront.get(i));
                chartItemAdapter.mChartSetLeftIn.get(i).start();
                chartItemAdapter.mChartSetRightOut.get(i).start();
            }
            mSetRightOut.start();
            mSetLeftIn.start();


            mIsBackVisible = true;
        } else {
            mSetRightOut.setTarget(mCardCitiesBack);
            mSetLeftIn.setTarget(mCardCitiesFront);
            mSetRightOut.start();
            mSetLeftIn.start();
            for (int i = 0; i < chartItemAdapter.mChartSetLeftIn.size(); i++){
                chartItemAdapter.mChartSetLeftIn.get(i).setTarget(chartItemAdapter.viewFront.get(i));
                chartItemAdapter.mChartSetRightOut.get(i).setTarget(chartItemAdapter.viewBack.get(i));
                chartItemAdapter.mChartSetLeftIn.get(i).start();
                chartItemAdapter.mChartSetRightOut.get(i).start();
            }
            mIsBackVisible = false;
        }
    }
    private void changeCameraDistance() {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        mCardCitiesFront.setCameraDistance(scale);
        mCardCitiesBack.setCameraDistance(scale);
    }

    public void CreateChartDialog(int position){
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

    }

}