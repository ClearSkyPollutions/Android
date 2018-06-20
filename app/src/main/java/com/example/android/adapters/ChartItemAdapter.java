package com.example.android.adapters;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

public class ChartItemAdapter extends BaseAdapter {

    private final Context mContext;
    private DataModel mDataModel;
    private ChartHelper mChartHelper;

    public ArrayList<FrameLayout> mChartCardFront = new ArrayList<>();
    public ArrayList<FrameLayout> mChartCardBack = new ArrayList<>();
    private ImageButton mButtonDelete;
    private ImageButton mButtonFavorite;

    public ArrayList<String> favorite  = new ArrayList<>();


    public ChartItemAdapter(Context mContext,DataModel dataModel, ChartHelper chartHelper) {
        this.mContext = mContext;
        this.mDataModel = dataModel;
        mChartHelper = chartHelper;
    }

    @Override
    public int getCount() {
        return mDataModel.data_types.size();
    }

    @Override
    public MutableLiveData<Chart> getItem(int position) {
        return mDataModel.getChart(mDataModel.data_types.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int backgroundColor =  Color.WHITE;
        int textColor = R.color.primaryTextColor;
        String type = mDataModel.data_types.get(position);
        int color = mDataModel.line_colors.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.activity_chart_item_adapter, null);

            LineChart lineChart = convertView.findViewById(R.id.lineChart);
            mChartHelper.initChart(lineChart, backgroundColor, textColor);

            lineChart.setTouchEnabled(false);

            // Init FrameLayout
            mChartCardFront.add(convertView.findViewById(R.id.chart_card_front));
            mChartCardBack.add(convertView.findViewById(R.id.chart_card_back));
            mButtonDelete = convertView.findViewById(R.id.buttonDelete);
            mButtonFavorite = convertView.findViewById(R.id.buttonFavori);
            mChartCardFront.get(position).setVisibility(View.VISIBLE);
            mChartCardBack.get(position).setVisibility(View.GONE);

            getItem(position).observe((LifecycleOwner) mContext, chart -> {
                // The home charts should only show data by hour
                if (!chart.getScale().equals(DataModel.AVG_HOUR))
                    return;
                lineChart.clearValues();

                for (int index = 0; index < chart.getXAxis().size(); index++) {
                    Float ts_f = (float) chart.getXAxis().get(index).getTime();
                    Float value = chart.getYAxis().get(index);
                    Float[] entry = new Float[]{ts_f, value};
                    mChartHelper.addEntry(lineChart, entry, color, false);
                }
            });

            mDataModel.loadChartData(type, DataModel.AVG_HOUR);

            mButtonFavorite.setOnClickListener(v -> {
                if (favorite.contains(mDataModel.data_types.get(position))) {
                    favorite.remove(mDataModel.data_types.get(position));
                    mButtonFavorite.setImageResource(R.drawable.ic_star_border_black_24dp);
                } else {
                    favorite.add(mDataModel.data_types.get(position));
                    mButtonFavorite.setImageResource(R.drawable.ic_star_black_24dp);
                }
                Log.d("Favorite",Arrays.toString(new ArrayList[]{favorite}));
            });

            mButtonDelete.setOnClickListener(v -> {
                Log.d("Position dans delete", "" + position);
                getItem(position).removeObservers((LifecycleOwner) mContext);

                mDataModel.data_types.remove(mDataModel.data_types.get(position));
                mDataModel.data_units.remove(mDataModel.data_units.get(position));
                mDataModel.line_colors.remove(mDataModel.line_colors.get(position));

                notifyDataSetChanged();
            });

        }


        TextView chartTitleFront = convertView.findViewById(R.id.chartTitleFront);
        TextView chartTitleBack = convertView.findViewById(R.id.chartTitleBack);

        chartTitleFront.setText(mDataModel.data_types.get(position));
        chartTitleBack.setText(mDataModel.data_types.get(position));





        return convertView;
    }
}
