package com.example.android.helpers;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.sql.Timestamp;

public final class ChartHelper implements IAxisValueFormatter {

    private static final String PM25_lABEL = "pm25";

    private static final String PM10_LABEL = "pm10";

    private static final String DESCRIPTION = "";

    public void initChart(LineChart mChart, int BackgroundColor, int TextColor) {

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
        mChart.setPinchZoom(false);

        // alternative background color
        mChart.setBackgroundColor(BackgroundColor);

        // DATA
        LineData data = new LineData();
        data.setValueTextColor(TextColor);

        // add data to line chart
        mChart.setData(data);

        // get legend object
        Legend l = mChart.getLegend();

        // customize legend
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(TextColor);


        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        XAxis x1 = mChart.getXAxis();
        x1.setValueFormatter(this);
        x1.setTextColor(TextColor);
        x1.setDrawGridLines(false);
        x1.setAvoidFirstLastClipping(true);
        x1.setEnabled(true);

        YAxis y1 = mChart.getAxisLeft();
        y1.setTextColor(TextColor);
        // auto set y axis min max with 5% spacing:
        float percent = 5;
        y1.setSpaceTop(percent);
        y1.setSpaceBottom(percent);
        y1.setDrawGridLines(false);

        YAxis y12 = mChart.getAxisRight();
        y12.setEnabled(false);

    }

    public void addEntry(LineChart mChart, Float[] concentration_pm, int pm25LineColor, int pm10LineColor) {
        LineData data = mChart.getData();
        Float ts_f = concentration_pm[0];
        Float pm25 = concentration_pm[1];
        Float pm10 = concentration_pm[2];

        if (data != null) {

            // APhsA
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet(PM25_lABEL, pm25LineColor, pm10LineColor);
                data.addDataSet(set);
            }

            data.addEntry(new Entry(ts_f, pm25), 0);

            // APhsB
            set = data.getDataSetByIndex(1);

            if (set == null) {
                set = createSet(PM10_LABEL, pm25LineColor, pm10LineColor);
                data.addDataSet(set);
            }

            data.addEntry(new Entry(ts_f, pm10), 1);

            // let the chart know it's data has changed
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
           mChart.setVisibleXRange(8, 8);

            // move to the latest entry
            mChart.moveViewToX(ts_f);
        }
    }


    private LineDataSet createSet(String label, int pm25LineColor, int pm10LineColor) {
        LineDataSet set = new LineDataSet(null, label);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        Integer color;
        switch (label){
            case PM25_lABEL:
                color = pm25LineColor;
                break;
            case PM10_LABEL:
                color = pm10LineColor;
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

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Timestamp ts = new Timestamp((long) value*3600000);
        return ts.toString().substring(11,16); // returns the corresponding time in the string format "HH:mm"
    }
}
