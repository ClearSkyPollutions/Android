package com.example.android.helpers;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public final class ChartHelper {

    private static final String PM25_lABEL = "pm25";

    private static final String PM10_LABEL = "pm10";

    private static final String DESCRIPTION = "";

    public static void initChart(LineChart mChart) {

        // customize line chart
        Description des = new Description();
        des.setText(DESCRIPTION);
        mChart.setDescription(des);

        // enable value highlighting
        // mChart.setDefaultFocusHighlightEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling & dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // enable pinch zoom to avoid scaling x and y axis separately
        mChart.setPinchZoom(true);

        // alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        // DATA
        LineData data = new LineData();
        data.setValueTextColor(Color.RED);

        // add data to line chart
        mChart.setData(data);

        // get legend object
        Legend l = mChart.getLegend();

        // customize legend
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);


        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        XAxis x1 = mChart.getXAxis();
        x1.setTextColor(Color.BLACK);
        x1.setDrawGridLines(true);
        x1.setAvoidFirstLastClipping(true);
        x1.setEnabled(false);

        YAxis y1 = mChart.getAxisLeft();
        y1.setTextColor(Color.BLACK);
        // auto set y axis min max with 5% spacing:
        float percent = 5;
        y1.setSpaceTop(percent);
        y1.setSpaceBottom(percent);
        y1.setDrawGridLines(true);

        YAxis y12 = mChart.getAxisRight();
        y12.setEnabled(false);

    }

    public static void addEntry(LineChart mChart, Float[] concentration_pm) {
        LineData data = mChart.getData();
        Float pm25 = concentration_pm[0];
        Float pm10 = concentration_pm[1];

        if (data != null) {

            // APhsA
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet(PM25_lABEL);
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), pm25), 0);

            // APhsB
            set = data.getDataSetByIndex(1);

            if (set == null) {
                set = createSet(PM10_LABEL);
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), pm10), 1);

            // let the chart know it's data has changed
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
//            mChart.setVisibleXRangeMaximum(15);
            float minXRange = 10;
            float maxXRange = 10;
            mChart.setVisibleXRange(minXRange, maxXRange);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());
        }
    }


    private static LineDataSet createSet(String label) {
        LineDataSet set = new LineDataSet(null, label);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        Integer color;
        switch (label){
            case PM25_lABEL:
                color = Color.RED;
                break;
            case PM10_LABEL:
                color = Color.GREEN;
                break;
            default:
                color = Color.BLACK;
        }
        set.setColors(color);
        set.setCircleColor(color);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setValueTextColor(color);
        set.setValueTextSize(10f);
        // To show values of each point
        set.setDrawValues(true);

        return set;
    }

}
