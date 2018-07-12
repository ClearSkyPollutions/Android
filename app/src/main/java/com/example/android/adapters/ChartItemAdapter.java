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
import com.example.android.helpers.ChartHelper;
import com.example.android.models.Chart;
import com.example.android.viewModels.DataModel;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChartItemAdapter extends BaseAdapter {

    public final Context mContext;
    private List<MutableLiveData<Chart>> mChartList;
    private ChartHelper mChartHelper;

    private boolean isBackCardVisible = false;

    public ArrayList<String> favorite = new ArrayList<>();

    public void setIsBackCardVisible(boolean isBackCardVisible) {
        this.isBackCardVisible = isBackCardVisible;
    }
    public boolean isBackCardVisible() {
        return isBackCardVisible;
    }

    public ChartItemAdapter(Context context, List<MutableLiveData<Chart>> chartList, ChartHelper chartHelper) {
        this.mContext = context;
        this.mChartList = chartList;
        this.mChartHelper = chartHelper;
    }

    @Override
    public int getCount() {
        return mChartList.size();
    }

    @Override
    public MutableLiveData<Chart> getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //@TODO: refactor to remove the warning
        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View itemView = layoutInflater.inflate(R.layout.item_adapter_chart, parent, false);

        initCards(position, itemView);
        initButtons(position, itemView);
        initChart(position, itemView);

        return itemView;
    }

    private void initCards(int position, View itemView) {

        FrameLayout frontCard = itemView.findViewById(R.id.chart_card_front);
        FrameLayout backCard = itemView.findViewById(R.id.chart_card_back);

        if (isBackCardVisible) {
            frontCard.setVisibility(View.INVISIBLE);
            backCard.setVisibility(View.VISIBLE);
        } else {
            frontCard.setVisibility(View.VISIBLE);
            backCard.setVisibility(View.INVISIBLE);
        }

        TextView chartTitleFront = itemView.findViewById(R.id.chartTitleFront);
        TextView chartTitleBack = itemView.findViewById(R.id.chartTitleBack);

        String type = mChartList.get(position).getValue().getType();
        chartTitleFront.setText(type);
        chartTitleBack.setText(type);
    }

    private void initButtons(int position, View itemView){
        ImageButton mButtonDelete = itemView.findViewById(R.id.buttonDelete);
        ImageButton mButtonFavorite = itemView.findViewById(R.id.buttonFavori);
        String type = mChartList.get(position).getValue().getType();

        if (favorite.contains(type)) {
            mButtonFavorite.setImageResource(R.drawable.ic_star_black_24dp);
        } else {
            mButtonFavorite.setImageResource(R.drawable.ic_star_border_black_24dp);
        }

        mButtonFavorite.setOnClickListener(v -> {
            if (favorite.contains(type)) {
                favorite.remove(type);
                mButtonFavorite.setImageResource(R.drawable.ic_star_border_black_24dp);
            } else {
                favorite.add(type);
                mButtonFavorite.setImageResource(R.drawable.ic_star_black_24dp);
            }
        });

        mButtonDelete.setOnClickListener(v -> {
            mChartList.get(position).removeObservers((LifecycleOwner) mContext);
            mChartList.remove(position);
            notifyDataSetChanged();
        });
    }

    private void initChart(int position, View itemView) {
        LineChart lineChart = itemView.findViewById(R.id.lineChart);
        mChartHelper.initChart(lineChart, Color.WHITE, R.color.primaryTextColor);

        lineChart.setTouchEnabled(false);

        mChartList.get(position).observe((LifecycleOwner) mContext, newChart -> {
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
    }
}

