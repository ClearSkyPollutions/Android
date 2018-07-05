package com.example.android.customOnClickListener;


import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.example.android.adapters.ChartItemAdapter;
import com.example.android.models.Chart;

import java.util.ArrayList;
import java.util.List;

public class ButtonDeleteOnClickListener implements View.OnClickListener {

    private final int position;
    private ChartItemAdapter mchartItemAdapter;
    private List<MutableLiveData<Chart>> mchartList = new ArrayList<>();

    public ButtonDeleteOnClickListener(int position,ChartItemAdapter chartItemAdapter, List<MutableLiveData<Chart>> chartList) {
        this.position = position;
        this.mchartItemAdapter = chartItemAdapter;
        this.mchartList = chartList;
    }

    @Override
    public void onClick(View v) {
        Log.d("Position dans delete", "" + position + " type: " +
                mchartList.get(position).getValue().getType());
        mchartList.get(position).removeObservers((LifecycleOwner) mchartItemAdapter.mContext);
        mchartList.remove(position);
        mchartItemAdapter.mChartCardFront.remove(position);
        mchartItemAdapter.mChartCardBack.remove(position);
        mchartItemAdapter.notifyDataSetChanged();
    }
}
