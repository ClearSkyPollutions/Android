package com.example.android.adapters;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.activities.R;
import com.example.android.customOnClickListener.ButtonDeleteOnClickListener;
import com.example.android.customOnClickListener.ButtonFavoriteOnClickListener;
import com.example.android.helpers.ChartHelper;
import com.example.android.models.Chart;
import com.example.android.viewModels.DataModel;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;

public class ChartItemAdapter extends BaseAdapter {

    public final Context mContext;
    private List<MutableLiveData<Chart>> mchartList = new ArrayList<>();
    private ChartHelper mChartHelper;

    public ArrayList<FrameLayout> mChartCardFront = new ArrayList<>();
    public ArrayList<FrameLayout> mChartCardBack = new ArrayList<>();
    private ImageButton mButtonDelete;
    private ImageButton mButtonFavorite;
    private boolean isBackCardVisible;

    public ArrayList<String> favorite = new ArrayList<>();


    public void setIsBackCardVisible(boolean isBackCardVisible) {
        this.isBackCardVisible = isBackCardVisible;
    }

    public ChartItemAdapter(Context mContext, List<MutableLiveData<Chart>> chartList, ChartHelper ChartHelper) {
        this.mContext = mContext;
        this.mchartList = chartList;
        this.mChartHelper = ChartHelper;
    }

    @Override
    public int getCount() {
        return mchartList.size();
    }

    @Override
    public MutableLiveData<Chart> getItem(int position) {
        return mchartList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int backgroundColor = Color.WHITE;
        int textColor = R.color.primaryTextColor;
        MutableLiveData<Chart> chart = mchartList.get(position);
        String type = chart.getValue().getType();

        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(R.layout.item_adapter_chart, parent, false);

        // Init FrameLayout

        if (position < mChartCardFront.size()) {
            mChartCardFront.set(position, convertView.findViewById(R.id.chart_card_front));
            mChartCardBack.set(position, convertView.findViewById(R.id.chart_card_back));
        } else {
            mChartCardFront.add(position, convertView.findViewById(R.id.chart_card_front));
            mChartCardBack.add(position, convertView.findViewById(R.id.chart_card_back));
        }

        mButtonDelete = convertView.findViewById(R.id.buttonDelete);
        mButtonFavorite = convertView.findViewById(R.id.buttonFavori);
        if (isBackCardVisible) {
            mChartCardFront.get(position).setVisibility(View.INVISIBLE);
            mChartCardBack.get(position).setVisibility(View.VISIBLE);
        } else {
            mChartCardFront.get(position).setVisibility(View.VISIBLE);
            mChartCardBack.get(position).setVisibility(View.INVISIBLE);
        }


        if (favorite.contains(type)) {
            mButtonFavorite.setImageResource(R.drawable.ic_star_black_24dp);
        } else {
            mButtonFavorite.setImageResource(R.drawable.ic_star_border_black_24dp);
        }

        mButtonFavorite.setOnClickListener(new ButtonFavoriteOnClickListener(position, this, mButtonFavorite, type));
        mButtonDelete.setOnClickListener(new ButtonDeleteOnClickListener(position, this, mchartList));

        TextView chartTitleFront = convertView.findViewById(R.id.chartTitleFront);
        TextView chartTitleBack = convertView.findViewById(R.id.chartTitleBack);

        chartTitleFront.setText(type);
        chartTitleBack.setText(type);

        LineChart lineChart = convertView.findViewById(R.id.lineChart);
        mChartHelper.initChart(lineChart, backgroundColor, textColor);

        lineChart.setTouchEnabled(false);

        getItem(position).observe((LifecycleOwner) mContext, newChart -> {
            notifyDataSetChanged();
            // The home charts should only show data by hour
            if (!newChart.getScale().equals(DataModel.AVG_HOUR))
                return;
            lineChart.clearValues();
            for (int index = 0; index < newChart.getXAxis().size(); index++) {
                Float ts_f = (float) newChart.getXAxis().get(index).getTime();
                Float value = newChart.getYAxis().get(index);
                Float[] entry = new Float[]{ts_f, value};
                mChartHelper.addEntry(lineChart, entry, newChart.getColor(), false);
            }
        });
        return convertView;
    }

}

