package com.example.android.models;


import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DataPM extends DataModel{

    private static final String TAG =  DataHT.class.toString();

    public MutableLiveData<String> dateMeasurement = new MutableLiveData<>();
    public MutableLiveData<Double> pm25 = new MutableLiveData<>();
    public MutableLiveData<Double> pm10 = new MutableLiveData<>();
    public MutableLiveData<List<Float[]>> pm10Entries = new MutableLiveData<>();
    public MutableLiveData<List<Float[]>> pm25Entries = new MutableLiveData<>();
    public static final String col_pm25 = "pm25";
    public static final String col_pm10 = "pm10";
    public static final String col_date = "date";

    public DataPM() {
        this.loadLastData(DataModel.currentTableName);
    }

    public MutableLiveData<List<Float[]>> getEntries(String columnName){
        switch (columnName){
            case col_pm10:
                return pm10Entries;
            case col_pm25:
                return pm25Entries;
        }
        return null;
    }

    @Override
    protected void setLastData(JSONObject response, String tableName) {
        try {
            JSONArray array = response.getJSONArray(tableName);
            Log.d(TAG, array.toString());
            JSONObject measure =  array.getJSONObject(0);
            pm25.postValue(measure.getDouble(col_pm25));
            pm10.postValue(measure.getDouble(col_pm10));
            dateMeasurement.postValue(measure.getString(col_date));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setChartData(JSONObject response, String tableName, String columnName) {
        try {
            ArrayList<Float[]> pmArray = new ArrayList<>();
            JSONArray array = response.getJSONArray(tableName);
            for (int i = array.length() - 1; i >= 0; i--) {
                JSONObject measure =  array.getJSONObject(i);
                Double dataValue = measure.getDouble(columnName);
                String date = measure.getString(this.getColumnDateStr());
                Timestamp ts = Timestamp.valueOf(date);
                Float ts_f = (float) ts.getTime();
                pmArray.add(new Float[]{ts_f, dataValue.floatValue()});
            }
            if (columnName == DataPM.col_pm10) {
                this.pm10Entries.postValue(pmArray);
            } else if (columnName == DataPM.col_pm25) {
                this.pm25Entries.postValue(pmArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getColumnDateStr() { return col_date; }
}
