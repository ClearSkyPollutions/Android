package com.example.android.fragments;


import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.activities.R;
import com.example.android.activities.databinding.FragmentHomeBinding;
import com.example.android.adapters.ChartItemAdapter;
import com.example.android.helpers.ChartHelper;
import com.example.android.models.Address;
import com.example.android.models.Chart;
import com.example.android.network.NetworkHelper;
import com.example.android.viewModels.AQIModel;
import com.example.android.viewModels.DataModel;
import com.example.android.viewModels.SettingsModel;
import com.github.mikephil.charting.charts.LineChart;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private TextView mSelectedDateView;
    private GridView mGridView;
    private ImageButton mImageButtonFront;
    private ImageButton mImageButtonBack;
    private ImageButton mImageButtonAddChart;
    private Spinner mSpinnerNameSensors;
    private Spinner mSpinnerDataType;
    private Spinner mSpinnerDataUnits;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // Rotate View
    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;

    private ChartItemAdapter chartItemAdapter;

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
        binding.setLastData(mDataModel);

        // Create or get the ViewModel for the Air Quality Index. Bind the xml variable aqiUI
        aqiModel = ViewModelProviders.of(getActivity()).get(AQIModel.class);
        binding.setAqiUI(aqiModel);

        syncAll(getContext());

        initViews();

        // When data in DB has been loaded : init charts & lists with this data
        mDataModel.updateChartList.observe(this, updateChartListValue -> {
            if (updateChartListValue){
                chartItemAdapter.notifyDataSetChanged();
                List<String> listTypes = new ArrayList<>();
                List<String> listUnits = new ArrayList<>();
                for (int i = 0; i < mDataModel.chartList.size(); i++)
                {
                    if (!listUnits.contains(mDataModel.chartList.get(i).getValue().getType())) {
                        listTypes.add(mDataModel.chartList.get(i).getValue().getType());
                    }
                    if (!listUnits.contains(mDataModel.chartList.get(i).getValue().getUnit())) {
                        listUnits.add(mDataModel.chartList.get(i).getValue().getUnit());
                    }
                }
                ArrayAdapter<String> listTypeAdapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, listTypes);
                ArrayAdapter<String> listUnitAdapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, listUnits);
                mSpinnerDataType.setAdapter(listTypeAdapter);
                mSpinnerDataUnits.setAdapter(listUnitAdapter);

                for (int position = 0; position < mDataModel.chartList.size(); position++){
                    mDataModel.loadChartData(position,mDataModel.AVG_HOUR);
                }

            }
        });

        return mRootView;
    }


    private void initAdapter() {
        // Create the adapter to convert the array of pollutant into views, bind it to gridview
        chartItemAdapter = new ChartItemAdapter(getActivity(), mDataModel.chartList, mChartHelper);


        chartItemAdapter.setIsBackCardVisible(false);
        // Attach the adapter to the GridView
        mGridView = mRootView.findViewById(R.id.chartList);
        mGridView.setAdapter(chartItemAdapter);

        // Functions called when a chart is clicked on
        mGridView.setOnItemClickListener((parent, view, position, id) -> {
            mChartViewDialog.setVisibility(View.VISIBLE);
            mCoverView.setVisibility(View.VISIBLE);

            //Reset selected position
            mChartDialog.highlightValues(null);

            // Get the correct LiveData(pm10, pm25...) to fill chartDialog with data
            mDataModel.chartList.get(position).observe((LifecycleOwner) getContext(), newChart -> {
                mChartHelper.reset(mChartDialog);
                for (int index = 0; index < newChart.getXAxis().size(); index++) {
                    String dateString = ChartHelper.getStringDate(newChart.getXAxis().get(index), "");
                    Float ts_f = (float) Timestamp.valueOf(dateString).getTime();
                    Float value = newChart.getYAxis().get(index);
                    Float[] entry = new Float[]{ts_f, value};
                    mChartHelper.addEntry(mChartDialog, entry, newChart.getColor(), true);
                }
            });

            //Change the buttons event according to dataType
            mButtonDay.setOnClickListener(v -> {
                mChartHelper.getSelected().setValue(-1);
                mDataModel.loadChartData(position,
                        DataModel.AVG_HOUR);
            });
            mButtonMonth.setOnClickListener(v -> {
                mChartHelper.getSelected().setValue(-1);
                mDataModel.loadChartData(position,
                        DataModel.AVG_DAY);
            });
            mButtonYear.setOnClickListener(v -> {
                mChartHelper.getSelected().setValue(-1);
                mDataModel.loadChartData(position,
                        DataModel.AVG_MONTH);
            });

            // Display the values selected with the chart cursor
            mChartHelper.getSelected().observe(this, (Integer selected) -> {
                // -1 for error in selection, null if mutableLiveData not set
                if (selected == null || selected == -1) {
                    mSelectedValueView.setText("");
                    mSelectedDateView.setText("");
                } else {
                    Chart ch = mDataModel.chartList.get(position).getValue();

                    String val = ch.getYAxis().get(selected).toString();
                    Date date = ch.getXAxis().get(selected);

                    // Formatting of the date depends on the scale of the chart
                    String dateString = ChartHelper.getStringDate(date, ch.getScale());

                    mSelectedValueView.setText(val + " " + ch.getUnit());
                    mSelectedDateView.setText(dateString);
                }
            });

            mSwipeRefreshLayout.setEnabled(false);
        });
    }

    private void initDialogChart() {
        int chartTextColor = getResources().getColor(R.color.primaryTextColor);
        int chartBackgroundColor = Color.WHITE;

        //Init buttons
        mButtonDay = mRootView.findViewById(R.id.day_bt);
        mButtonMonth = mRootView.findViewById(R.id.month_bt);
        mButtonYear = mRootView.findViewById(R.id.year_bt);

        // Init popup
        mSelectedDateView = mRootView.findViewById(R.id.SelectedDate);
        mSelectedValueView = mRootView.findViewById(R.id.SelectedValue);

        //init chart
        mChartDialog = mRootView.findViewById(R.id.lineChartDialog);
        mChartHelper.initChartDialog(mChartDialog, chartBackgroundColor, chartTextColor);
        mChartViewDialog = mRootView.findViewById(R.id.viewDialog);

        //Click event listener closing popup views
        mCoverView = mRootView.findViewById(R.id.cover);
        mCoverView.setClickable(true);
        mCoverView.setOnClickListener(view -> {
            mChartHelper.getSelected().removeObservers(this);
            mChartViewDialog.setVisibility(View.GONE);
            mCoverView.setVisibility(View.GONE);
            mCardAddChart.setVisibility(View.GONE);
            mButtonAddChart.setClickable(false);
            if (!chartItemAdapter.isBackCardVisible())
                mSwipeRefreshLayout.setEnabled(true);
        });
    }

    private void initBackViews() {
        // Dialog to add new pollutants
        mButtonAddChart = mRootView.findViewById(R.id.buttonAddElement);
        mCardAddChart = mRootView.findViewById(R.id.CardDialogNewChart);

        // Init Spinner
        mSpinnerNameSensors = mRootView.findViewById(R.id.SPnameSensors);
        mSpinnerDataType = mRootView.findViewById(R.id.SPdataType);
        mSpinnerDataUnits = mRootView.findViewById(R.id.SPdataUnits);

        // Init ImageButton
        mImageButtonFront = mRootView.findViewById(R.id.buttonEditFront);
        mImageButtonBack = mRootView.findViewById(R.id.buttonEditBack);
        mImageButtonAddChart = mRootView.findViewById(R.id.buttonAddBack);

        // Functions Onclick on ImageButton
        mImageButtonFront.setOnClickListener(v->flipCard());
        mImageButtonBack.setOnClickListener(v->flipCard());

        mImageButtonAddChart.setOnClickListener(v -> {
            mCardAddChart.setVisibility(View.VISIBLE);
            mCoverView.setVisibility(View.VISIBLE);
            mButtonAddChart.setClickable(true);
        });
        mButtonAddChart.setOnClickListener(v -> {
            Random rnd = new Random();
            boolean existing = false;
            int i = 0;
            while (!existing && i < mDataModel.chartList.size()) {
                existing = mDataModel.chartList.get(i).getValue().getType().equals(mSpinnerDataType.getSelectedItem());
                i++;
            }
            if (!existing) {
                Chart chart = new Chart(mSpinnerDataType.getSelectedItem().toString(),
                        mSpinnerDataUnits.getSelectedItem().toString(),
                        Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)),
                        DataModel.AVG_HOUR);
                MutableLiveData<Chart> liveChart = new MutableLiveData<>();
                liveChart.setValue(chart);
                mDataModel.chartList.add(liveChart);
            }

            //Change item in GridView
            chartItemAdapter.notifyDataSetChanged();

            //Leave Card Add Chart
            mCardAddChart.setVisibility(View.GONE);
            mCoverView.setVisibility(View.GONE);

        });

    }

    private void initViews() {

        initAdapter();
        initDialogChart();
        initBackViews();

        // Init summary cardViews
        mCardCitiesFront = mRootView.findViewById(R.id.CardFront);
        mCardCitiesBack = mRootView.findViewById(R.id.CardBack);

        // Init SwipeRefreshLayout to Refresh Data in DataBase and Chart
        mSwipeRefreshLayout = mRootView.findViewById(R.id.swipeRefreshHomeFragment);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            //mDataModel.loadDataTypeUnits();
            syncAll(getContext());

            mDataModel.refreshChart.observe(this,
                    refreshValue -> mSwipeRefreshLayout.setRefreshing(refreshValue));
        });

        // Init Animation
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.in_animation);
        //For animation change the distance
        changeCameraDistance();
    }

    private void syncAll(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.settings_rpi_file_key), Context.MODE_PRIVATE);

        Address addressRPI = new Address(
                sharedPref.getString("raspberryPiAddressIp",""),
                sharedPref.getInt("raspberryPiAddressPort", 0));
        boolean isDataShared = sharedPref.getBoolean("isDataShared",false);


        SettingsModel settingsModel = ViewModelProviders.of(getActivity()).get(SettingsModel.class);

        NetworkHelper networkHelper = new NetworkHelper();
        networkHelper.checkConnection(addressRPI).observe(
                this,
                connectionValue -> {
                    if (connectionValue) {
                        mDataModel.syncAllRPI(context);

                        settingsModel.getLocalSettings(context);
                        settingsModel.getSystemIDtoRPI(context);
                        aqiModel.loadAQIRPI(context);
                        Toast.makeText(getActivity(), R.string.toast_data_updated_RPI,
                                Toast.LENGTH_SHORT).show();
                    } else if (isDataShared) {
                        Address addressServer = new Address(
                                sharedPref.getString("serverAddressIp",""),
                                sharedPref.getInt("serverAddressPort", 0));

                        networkHelper.checkConnection(addressServer).observe(
                                this,
                                connectionValueServer -> {
                                    if (connectionValueServer) {
                                        mDataModel.syncAllServer(context);
                                        aqiModel.loadAQIServer(context);
                                        Toast.makeText(getActivity(), R.string.toast_data_updated_Server,
                                                Toast.LENGTH_SHORT).show();
                                    }else {
                                        mDataModel.loadDataTypeUnits();
                                        Toast.makeText(getActivity(), R.string.toast_could_not_connect,
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                    }else {
                        mDataModel.loadDataTypeUnits();
                        Toast.makeText(getActivity(), R.string.toast_could_not_connect,
                                Toast.LENGTH_LONG).show();
                    }
                });
        settingsModel.refreshSystemID.observe(this,
                refreshSystemIDValue -> settingsModel.setLocalSettings(context));
    }

    public void flipCard() {
        boolean flipToFrontCard = chartItemAdapter.isBackCardVisible();

        mSetRightOut.setTarget(flipToFrontCard ? mCardCitiesBack:mCardCitiesFront);
        mSetLeftIn.setTarget(flipToFrontCard ? mCardCitiesFront:mCardCitiesBack);
        mSetRightOut.start();
        mSetLeftIn.start();
        mImageButtonFront.setClickable(flipToFrontCard);
        mImageButtonBack.setClickable(!flipToFrontCard);
        mButtonAddChart.setClickable(!flipToFrontCard);
        mImageButtonAddChart.setClickable(!flipToFrontCard);
        mSwipeRefreshLayout.setEnabled(flipToFrontCard);

        chartItemAdapter.setIsBackCardVisible(!flipToFrontCard);

        chartItemAdapter.notifyDataSetChanged();
    }

    private void changeCameraDistance() {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        mCardCitiesFront.setCameraDistance(scale);
        mCardCitiesBack.setCameraDistance(scale);
    }
}
