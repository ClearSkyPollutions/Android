package com.example.android.helpers;

import android.arch.lifecycle.MutableLiveData;
import android.os.Build;
import android.util.Log;

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
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChartHelper implements IAxisValueFormatter, OnChartValueSelectedListener {

    public MutableLiveData<Integer> selected;
    private ArrayList<Float> entries = new ArrayList<>();

    public void initChart(LineChart mChart, int BackgroundColor, int TextColor) {

        initStandard(mChart, BackgroundColor, TextColor);

        mChart.setDrawGridBackground(false);

        // limit the number of visible entries
        //mChart.setVisibleXRange(8*3600000,8*3600000);

        XAxis x1 = mChart.getXAxis();
        x1.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        x1.setValueFormatter(this);
        x1.setTextColor(TextColor);
        x1.setDrawGridLines(false);
        x1.setDrawAxisLine(false);
        x1.setAvoidFirstLastClipping(false);
        x1.setLabelCount(5);
        x1.setEnabled(true);

        YAxis y1 = mChart.getAxisLeft();
        y1.setTextColor(TextColor);
        // auto set y axis min max with 5% spacing:
        float percent = 25;
        y1.setSpaceTop(percent);
        y1.setSpaceBottom(percent);
        y1.setDrawGridLines(false);
        YAxis y2 = mChart.getAxisRight();
        y2.setEnabled(false);
    }

    public void initChartDialog(LineChart mChart, int BackgroundColor, int TextColor) {

        initStandard(mChart, BackgroundColor, TextColor);

        mChart.setDrawGridBackground(false);

        mChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);
        XAxis x1 = mChart.getXAxis();
        x1.setValueFormatter(this);
        x1.setTextColor(TextColor);
        x1.setDrawGridLines(false);
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
        mChart.setOnChartValueSelectedListener(this);
    }

    private void initStandard(LineChart mChart, int BackgroundColor, int TextColor){

        Description des = new Description();
        des.setText("");
        mChart.setDescription(des);

        // enable value highlighting
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChart.setDefaultFocusHighlightEnabled(true);
        }

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling & dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

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
        l.setEnabled(false);

        /*
        // customize legend
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(TextColor);
        */
    }

    @Override
    public void onValueSelected(Entry e, Highlight h){
        selected.postValue(entries.indexOf(e.getX()));
    }

    @Override
    public void onNothingSelected() {
        selected.postValue(-1);
    }

    public MutableLiveData<Integer> getSelected() {
        if(selected == null){
            selected = new MutableLiveData<>();
        }
        return selected;
    }

    public void addEntry(LineChart mChart, Float[] entry, int lineColor, boolean draw) {
        LineData data = mChart.getData();
        Float ts_f = entry[0];
        Float dataValue = entry[1];

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet(lineColor, draw);
                data.addDataSet(set);
            }

            set.addEntry(new Entry(ts_f, dataValue));
            entries.add(ts_f);

            // let the chart know it's data has changed
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();

            // move to the latest entry
            mChart.moveViewToX(ts_f);
        }

        YAxis y1 = mChart.getAxisLeft();
        y1.setEnabled(draw);
    }


    private LineDataSet createSet(int lineColor, boolean draw) {
        LineDataSet set = new LineDataSet(null, "");

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColors(lineColor);
        set.setCircleColor(lineColor);
        set.setLineWidth(1.5f);
        set.setCircleRadius(2.5f);
        set.setValueTextColor(lineColor);
        set.setValueTextSize(10f);
        set.setDrawCircleHole(false);
        set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        set.setCubicIntensity(1f);
        set.setHighlightLineWidth(1f);
        set.setDrawValues(false);
        set.setDrawHighlightIndicators(draw);
        set.setDrawHorizontalHighlightIndicator(false);

        return set;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int nb = axis.mEntryCount;
        Timestamp ts = new Timestamp((long) (value));
        String formattedValue = "";
        if (nb == 24) {
            SimpleDateFormat ft = new SimpleDateFormat("HH", Locale.FRANCE);
            formattedValue = " " + ft.format(ts) + "h ";
        } else if (nb == 30) {
            SimpleDateFormat ft = new SimpleDateFormat(" dd ", Locale.FRANCE);
            formattedValue = ft.format(ts);
        } else if (nb == 12) {
            SimpleDateFormat ft = new SimpleDateFormat("MM", Locale.FRANCE);
            formattedValue = ft.format(ts);
        }
        return formattedValue;
    }

    public void reset(LineChart chart) {
        chart.clearValues();
        getEntries().clear();
        getSelected().setValue(-1);
    }

    public static String getStringDate(Date date, String scale) {
        SimpleDateFormat ft;
        switch (scale) {
            case "AVG_HOUR":
                ft = new SimpleDateFormat("EEEE, d MMM, yyyy HH'h'", Locale.getDefault());
                break;
            case "AVG_DAY":
                ft = new SimpleDateFormat("EEEE, d MMM, yyyy", Locale.getDefault());
                break;
            case "AVG_MONTH":
                ft = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                break;
            case "AVG_YEAR":
                ft = new SimpleDateFormat("yyyy", Locale.getDefault());
                break;
            case "CardCities":
                ft = new SimpleDateFormat("EEEE, d MMM, yyyy HH:mm", Locale.getDefault());
                break;
            default:
                ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                break;
        }
        return ft.format(date);
    }

    public ArrayList<Float> getEntries() {
        if(entries == null){
            entries = new ArrayList<>();
        }
        return entries;
    }
}
