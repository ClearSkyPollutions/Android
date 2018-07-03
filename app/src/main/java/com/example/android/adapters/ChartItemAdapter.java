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

public class ChartItemAdapter extends BaseAdapter{

    private final Context mContext;
    private DataModel mDataModel;
    private ChartHelper mChartHelper;

    public ArrayList<FrameLayout> mChartCardFront = new ArrayList<>();
    public ArrayList<FrameLayout> mChartCardBack = new ArrayList<>();
    private ImageButton mButtonDelete;
    private ImageButton mButtonFavorite;

    public ArrayList<String> favorite  = new ArrayList<>();


    public ChartItemAdapter(Context mContext, DataModel dataModel, ChartHelper ChartHelper) {
        this.mContext = mContext;
        this.mDataModel = dataModel;
        this.mChartHelper = ChartHelper;
    }

    @Override
    public int getCount() {
        return mDataModel.charts.size();
    }

    @Override
    public MutableLiveData<Chart> getItem(int position) {
        return mDataModel.getLiveChart(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int backgroundColor =  Color.WHITE;
        int textColor = R.color.primaryTextColor;
        Chart chart = mDataModel.charts.get(position);
        String type = chart.getType();
        Log.d("mData", mDataModel.charts.get(position).getType());
        if (convertView == null) {
            Log.d("mData2", mDataModel.charts.get(position).getType() + " p " + position);
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.activity_chart_item_adapter, null);

            // Init FrameLayout
            /*if(position < mChartCardFront.size()) {
                mChartCardFront.remove(position);
                mChartCardBack.remove(position);
            }*/
            mChartCardFront.add(position, convertView.findViewById(R.id.chart_card_front));
            mChartCardBack.add(position, convertView.findViewById(R.id.chart_card_back));
            mButtonDelete = convertView.findViewById(R.id.buttonDelete);
            mButtonFavorite = convertView.findViewById(R.id.buttonFavori);
            mChartCardFront.get(position).setVisibility(View.VISIBLE);
            mChartCardBack.get(position).setVisibility(View.GONE);

            mButtonFavorite.setOnClickListener(v -> {
                if (favorite.contains(type)) {
                    favorite.remove(type);
                    mButtonFavorite.setImageResource(R.drawable.ic_star_border_black_24dp);
                } else {
                    favorite.add(type);
                    mButtonFavorite.setImageResource(R.drawable.ic_star_black_24dp);
                }
                Log.d("Favorite",Arrays.toString(new ArrayList[]{favorite}));
            });

            mButtonDelete.setOnClickListener(v -> {
                Log.d("Position dans delete", "" + position);
                getItem(position).removeObservers((LifecycleOwner) mContext);
                mDataModel.charts.remove(position);
                notifyDataSetChanged();
            });

        }

        TextView chartTitleFront = convertView.findViewById(R.id.chartTitleFront);
        TextView chartTitleBack = convertView.findViewById(R.id.chartTitleBack);

        chartTitleFront.setText(type);
        chartTitleBack.setText(type);

        LineChart lineChart = convertView.findViewById(R.id.lineChart);
        mChartHelper.initChart(lineChart, backgroundColor, textColor);

        lineChart.setTouchEnabled(false);

        getItem(position).observe((LifecycleOwner) mContext, newChart -> {
            // The home charts should only show data by hour
            if (!newChart.getScale().equals(DataModel.AVG_HOUR))
                return;
            lineChart.clearValues();
            for (int index = 0; index < newChart.getXAxis().size(); index++) {
                Float ts_f = (float) newChart.getXAxis().get(index).getTime();
                Float value = newChart.getYAxis().get(index);
                Float[] entry = new Float[]{ts_f, value};
                mChartHelper.addEntry(lineChart, entry, mDataModel.charts.get(position).getColor(), false);
            }
        });
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
