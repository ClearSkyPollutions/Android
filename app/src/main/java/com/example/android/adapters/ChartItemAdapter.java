package com.example.android.adapters;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.activities.R;
import com.example.android.fragments.HomeFragment;
import com.example.android.helpers.ChartHelper;
import com.example.android.models.Data;
import com.example.android.viewModels.DataModel;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.Arrays;

public class ChartItemAdapter extends BaseAdapter {

    private final Context mContext;
    private DataModel mDataModel;

    public ArrayList<FrameLayout> mChartCardFront = new ArrayList<>();
    public ArrayList<FrameLayout> mChartCardBack = new ArrayList<>();
    private ImageButton mButtonDeletes;
    private ImageButton mButtonFavors;

    public ArrayList<String> Favor  = new ArrayList<>();


    public ChartItemAdapter(Context mContext,DataModel mdataModel) {
        this.mContext = mContext;
        this.mDataModel = mdataModel;
    }


    @Override
    public int getCount() {
        return mDataModel.DATA_TYPES.size();
    }

    @Override
    public MutableLiveData<Data> getItem(int position) {
        return mDataModel.getMeasurements(mDataModel.DATA_TYPES.get(position));
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ResourceType")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("deb","position "+mDataModel.DATA_TYPES.get(position));
        int backgroundColor =  Color.WHITE;
        int textColor = R.color.primaryTextColor;
        String type = mDataModel.DATA_TYPES.get(position);
        int color = mDataModel.LINE_COLORS.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.activity_chart_item_adapter, null);
        }

        TextView chartTitleFront = convertView.findViewById(R.id.chartTitleFront);
        TextView chartTitleBack = convertView.findViewById(R.id.chartTitleBack);

        // Init FrameLayout
        mChartCardFront.add(convertView.findViewById(R.id.chart_card_front));
        mChartCardBack.add(convertView.findViewById(R.id.chart_card_back));
        mButtonDeletes = convertView.findViewById(R.id.buttonDelete);
        mButtonFavors = convertView.findViewById(R.id.buttonFavori);

        ChartHelper chartHelper = new ChartHelper();
        LineChart lineChart = convertView.findViewById(R.id.lineChart);

        chartTitleFront.setText(mDataModel.DATA_TYPES.get(position));
        chartTitleBack.setText(mDataModel.DATA_TYPES.get(position));

        chartHelper.initChart(lineChart, backgroundColor, textColor);

        lineChart.setTouchEnabled(false);

        mDataModel.getMeasurements(type).observe((LifecycleOwner) mContext, data -> {
            // The home charts should only show data by hour
            if (!data.scale.equals("AVG_HOUR"))
                return;
            lineChart.clearValues();
            for (Float[] pmEntry : data.values) {
                chartHelper.addEntry(lineChart, pmEntry, color, false);
            }
        });
        mDataModel.loadData(type, "AVG_HOUR");


        mButtonFavors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Favor.contains(mDataModel.DATA_TYPES.get(position))){
                    Favor.remove(mDataModel.DATA_TYPES.get(position));
                    mButtonFavors.setImageResource(R.drawable.ic_star_border_black_24dp);
                }else{
                    Favor.add(mDataModel.DATA_TYPES.get(position));
                    mButtonFavors.setImageResource(R.drawable.ic_star_black_24dp);
                }
                Log.d("Favor", Arrays.toString(new ArrayList[]{Favor}));
            }
        });
        mButtonDeletes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click on","Delete " + position);

                if(mDataModel.DATA_TYPES.contains(mDataModel.DATA_TYPES.get(position))) {
                    Log.d("Delete","Existe "+mDataModel.DATA_TYPES.get(position));
                    mDataModel.DATA_TYPES.remove(mDataModel.DATA_TYPES.get(position));
                    mDataModel.DATA_UNITS.remove(mDataModel.DATA_UNITS.get(position));
                    mDataModel.LINE_COLORS.remove(mDataModel.LINE_COLORS.get(position));
                }

                Log.d("DataModel", Arrays.toString(mDataModel.DATA_TYPES.toArray()));
                Log.d("DataModel", Arrays.toString(mDataModel.DATA_UNITS.toArray()));
                Log.d("DataModel", Arrays.toString(mDataModel.LINE_COLORS.toArray()));

                notifyDataSetChanged();

            }
        });

        return convertView;
    }
}
