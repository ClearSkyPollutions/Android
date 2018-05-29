package com.example.android.helpers;

import android.os.Build;

import android.util.Log;
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
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChartHelper implements IAxisValueFormatter {

    private String DESCRIPTION;


    public void initChart(LineChart mChart, int BackgroundColor, int TextColor) {

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
        x1.setDrawGridLines(false);
        x1.setAvoidFirstLastClipping(false);
        x1.setEnabled(false);

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

    public void addEntry(LineChart mChart, Float[] entry, int lineColor) {
        LineData data = mChart.getData();
        Float ts_f = entry[0];
        Float dataValue = entry[1];

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet(DataModel.currentColumnName, lineColor);
                data.addDataSet(set);
            }

            set.addEntry(new Entry(ts_f, dataValue));

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
        set.setDrawValues(false);

        return set;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Timestamp ts = new Timestamp((long) (value));
        String formattedValue = "";
        if (DataModel.currentTableName == "AVG_HOUR") {
            SimpleDateFormat ft = new SimpleDateFormat("hh", Locale.FRANCE);
            formattedValue = ft.format(ts)+"h";
        } else if (DataModel.currentTableName == "AVG_DAY") {
            SimpleDateFormat ft = new SimpleDateFormat("dd", Locale.FRANCE);
            formattedValue = ft.format(ts);
        } else if (DataModel.currentTableName == "AVG_MONTH") {
            SimpleDateFormat ft = new SimpleDateFormat("MM", Locale.FRANCE);
            formattedValue = ft.format(ts);
        }
        return formattedValue; // returns the corresponding time in the string format "HH:mm"
    }

}
