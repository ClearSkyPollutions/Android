package com.example.android.helpers;

import android.arch.lifecycle.MutableLiveData;
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
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChartHelper implements IAxisValueFormatter, OnChartValueSelectedListener {

    public MutableLiveData<Float> selectedValue;
    public MutableLiveData<String> selectedLabel;

    public MutableLiveData<Float> getSelectedValue() {
        if(selectedValue == null){
            selectedValue = new MutableLiveData<>();
        }
        return selectedValue;
    }

    public MutableLiveData<String> getSelectedLabel() {
        if(selectedLabel == null){
            selectedLabel = new MutableLiveData<>();
        }
        return selectedLabel;
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
        y1.setEnabled(false);
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

    public void onValueSelected(Entry e, Highlight h){
        selectedValue.postValue(h.getY());
        selectedLabel.postValue(getStringDate(h.getX()));
    }
    public void onNothingSelected(){

    }


    public void addEntry(LineChart mChart, Float[] entry, int lineColor, boolean draw) {
        LineData data = mChart.getData();
        Float ts_f = entry[0];
        Float dataValue = entry[1];

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet(DataModel.currentColumnName, lineColor);
                data.addDataSet(set);
            }
            if(draw){
                set.setDrawValues(true);
            }

            set.addEntry(new Entry(ts_f, dataValue));

            // let the chart know it's data has changed
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();



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
            SimpleDateFormat ft = new SimpleDateFormat("HH", Locale.FRANCE);
            formattedValue = " " + ft.format(ts) + "h ";
        } else if (DataModel.currentTableName == "AVG_DAY") {
            SimpleDateFormat ft = new SimpleDateFormat(" dd ", Locale.FRANCE);
            formattedValue = ft.format(ts);
        } else if (DataModel.currentTableName == "AVG_MONTH") {
            SimpleDateFormat ft = new SimpleDateFormat("MM", Locale.FRANCE);
            formattedValue = ft.format(ts);
        }
        return formattedValue;
    }

    private String getStringDate(float value) {
        Timestamp ts = new Timestamp((long) (value));
        String stringValue = "";
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE);
        stringValue = ft.format(ts);
        return stringValue;
    }

}
