package com.example.android.adapters;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.activities.R;
import com.example.android.helpers.ChartHelper;
import com.example.android.models.Data;
import com.example.android.viewModels.DataModel;
import com.github.mikephil.charting.charts.LineChart;

public class ChartItemAdapter extends BaseAdapter {

    private final Context mContext;
    private DataModel mDataModel;

    public ChartItemAdapter(Context mContext,DataModel mdataModel) {
        this.mContext = mContext;
        this.mDataModel = mdataModel;
    }


    @Override
    public int getCount() {
        return mDataModel.DATA_TYPES.length;
    }

    @Override
    public MutableLiveData<Data> getItem(int position) {
        return mDataModel.getMeasurements(mDataModel.DATA_TYPES[position]);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int backgroundColor =  Color.WHITE;
        int textColor = R.color.primaryTextColor;
        String type = mDataModel.DATA_TYPES[position];
        int color = mDataModel.LINE_COLORS[position];

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.activity_chart_item_adapter, null);

        }

        TextView chartTitle = convertView.findViewById(R.id.chartTitle);
        ChartHelper chartHelper = new ChartHelper();
        LineChart lineChart = convertView.findViewById(R.id.lineChart);

        chartTitle.setText(mDataModel.DATA_TYPES[position]);

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

        return convertView;
    }


}
