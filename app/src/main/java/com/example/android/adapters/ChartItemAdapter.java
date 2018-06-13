package com.example.android.adapters;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Arrays;

public class ChartItemAdapter extends BaseAdapter {

    private final Context mContext;
    private String[] chartType;
    private ChartHelper chartHelper;
    private LineChart lineChart;

    public ChartItemAdapter(Context mContext, String[] chartType, ChartHelper chartHelper) {

        this.mContext = mContext;
        this.chartType = chartType;
        this.chartHelper = chartHelper;

    }

    public LineChart getLineChart() {
        return lineChart;
    }

    @Override
    public int getCount() {
        return chartType.length;
    }

    @Override
    public String getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("position",""+position);
        Log.d("Tableau", Arrays.toString(chartType));

        int textColor = R.color.primaryTextColor;
        int backgroundColor =  Color.WHITE;

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.activity_chart_item_adapter, null);
        }

        TextView chartTitle = convertView.findViewById(R.id.graphtitle);
        lineChart = convertView.findViewById(R.id.lineChart);

        chartTitle.setText(chartType[position]);
        Log.d("debb"," Type "+chartType[position]);
        Log.d("debbb","Avant init");
        chartHelper.initChart(lineChart, backgroundColor, textColor);

        return convertView;
    }


}
