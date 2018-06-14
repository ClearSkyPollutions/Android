package com.example.android.adapters;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.activities.R;
import com.example.android.helpers.ChartHelper;
import com.example.android.models.Data;
import com.example.android.viewModels.DataModel;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;

public class ChartItemAdapter extends BaseAdapter {

    private final Context mContext;
    private DataModel mDataModel;
    private View mSmallChartCardFront;
    private View mSmallChartCardBack;

    public ArrayList<View> viewFront = new ArrayList<>();
    public ArrayList<View> viewBack = new ArrayList<>();
    public ArrayList<AnimatorSet> mChartSetRightOut = new ArrayList<>();
    public ArrayList<AnimatorSet> mChartSetLeftIn = new ArrayList<>();

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

        int backgroundColor =  Color.WHITE;
        int textColor = R.color.primaryTextColor;
        String type = mDataModel.DATA_TYPES.get(position);
        int color = mDataModel.LINE_COLORS.get(position);
        ImageButton mButtonDelete;
        ImageButton mButtonFavori;
        CardView mSmallCardFront;
        ViewHolder holder;

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.activity_chart_item_adapter, null);
        }

        TextView chartTitleFront = convertView.findViewById(R.id.chartTitleFront);
        TextView chartTitleBack = convertView.findViewById(R.id.chartTitleBack);
        mButtonDelete = convertView.findViewById(R.id.buttonDelete);
        mButtonFavori = convertView.findViewById(R.id.buttonFavori);
        mSmallCardFront = convertView.findViewById(R.id.smallCardFront);

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

        mSmallChartCardFront = convertView.findViewById(R.id.chart_card_front);
        mSmallChartCardBack = convertView.findViewById(R.id.chart_card_back);

        changeCameraDistance();

        Log.d("chartIt","new add "+getItem(position));
        viewFront.add(mSmallChartCardFront);
        viewBack.add(mSmallChartCardBack);
        mChartSetRightOut.add((AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.anim.out_animation));
        mChartSetLeftIn.add((AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.anim.in_animation));

        holder = new ViewHolder(mButtonDelete,mButtonFavori, mSmallCardFront);
        return convertView;
    }

    private static class ViewHolder {
        public ImageButton mButDelete;
        public ImageButton mButFavor;
        public CardView mCardFront;


        public ViewHolder(ImageButton delete, ImageButton Favor, CardView Card) {
            mButDelete = delete;
            mButFavor = Favor;
            mCardFront = Card;
            mButDelete.setOnClickListener(mButtonDeleteClickListener);
            mButFavor.setOnClickListener(mButtonFavorClickListener);
            mCardFront.setOnClickListener(mButtonCardFrontClickListener);
        }

        private View.OnClickListener mButtonDeleteClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Click","Delete");
            }
        };
        private View.OnClickListener mButtonFavorClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Click","Favor");
            }
        };
        private View.OnClickListener mButtonCardFrontClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Click","Front");
            }
        };
    }

    private void changeCameraDistance() {
        int distance = 8000;
        float scale = mContext.getResources().getDisplayMetrics().density * distance;
        mSmallChartCardFront.setCameraDistance(scale);
        mSmallChartCardBack.setCameraDistance(scale);
    }


}
