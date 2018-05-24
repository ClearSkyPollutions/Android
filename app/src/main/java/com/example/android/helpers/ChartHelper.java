package com.example.android.helpers;

import android.os.Build;

import com.example.android.models.DataModel;
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

public class ChartHelper implements IAxisValueFormatter {

    private String DESCRIPTION;

    private int scalingCoefficient;

    public void initChart(LineChart mChart, int BackgroundColor, int TextColor) {

        if (DataModel.currentTableName == "AVG_HOUR") {
            scalingCoefficient = 3600000;
        } else if (DataModel.currentTableName == "AVG_DAY") {
            scalingCoefficient = 3600000*24;
        } else if (DataModel.currentTableName == "AVG_MONTH") {
            scalingCoefficient = 3600000*24*30;
        }

        Description des = mChart.getDescription();
        des.setText("");

        // enable value highlighting
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChart.setDefaultFocusHighlightEnabled(true);
        }

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


        mChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);
        XAxis x1 = mChart.getXAxis();
        x1.setValueFormatter(this);
        x1.setTextColor(TextColor);
        x1.setDrawGridLines(true);
        x1.setAvoidFirstLastClipping(false);
        x1.setEnabled(true);

        YAxis y1 = mChart.getAxisLeft();
        y1.setTextColor(TextColor);
        // auto set y axis min max with 5% spacing:
        float percent = 25;
        y1.setSpaceTop(percent);
        y1.setSpaceBottom(percent);
        y1.setDrawGridLines(false);
        y1.setEnabled(false);

        YAxis y2 = mChart.getAxisRight();
        y2.setEnabled(false);

    }

    public void addEntry(LineChart mChart, Float[] concentration_pm, int lineColor) {
        LineData data = mChart.getData();
        Float ts_f = concentration_pm[0];
        Float dataValue = concentration_pm[1];

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet(DataModel.currentColumnName, lineColor);
                data.addDataSet(set);
            }

            data.addEntry(new Entry(ts_f/ scalingCoefficient, dataValue), 0);

            // let the chart know it's data has changed
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
          // mChart.setVisibleXRange(8,8);

            // move to the latest entry
            mChart.moveViewToX(ts_f);
        }
    }


    private LineDataSet createSet(String label, int lineColor) {
        LineDataSet set = new LineDataSet(null, label);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        Integer color = lineColor;

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
        Timestamp ts = new Timestamp((long) value* scalingCoefficient);
        String formattedValue = "";
        if (DataModel.currentTableName == "AVG_HOUR") {
            formattedValue = ts.toString().substring(11,13)+"h";
        } else if (DataModel.currentTableName == "AVG_DAY") {
            formattedValue = ts.toString().substring(8,10);
        } else if (DataModel.currentTableName == "AVG_MONTH") {
            formattedValue = ts.toString().substring(5,7);
        }
        return formattedValue; // returns the corresponding time in the string format "HH:mm"
    }

}
