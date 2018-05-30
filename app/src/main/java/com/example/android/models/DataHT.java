package com.example.android.models;


import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class DataHT extends DataModel{

    private static final String TAG =  DataHT.class.toString();

    public MutableLiveData<Double> temperature = new MutableLiveData<>();
    public MutableLiveData<Double> humidity = new MutableLiveData<>();
    public MutableLiveData<List<Float[]>> humEntries = new MutableLiveData<>();
    public MutableLiveData<List<Float[]>> tempEntries = new MutableLiveData<>();
    public static final String col_humidity = "humidity";
    public static final String col_temperature = "temperature";
    public static final String col_date = "date";

    public DataHT() {
        this.loadLastData(DataModel.currentTableName);
    }

    public MutableLiveData<List<Float[]>> getEntries(String columnName){
        switch (columnName){
            case col_humidity:
                return humEntries;
            case col_temperature:
                return tempEntries;
        }
        return null;
    }

    @Override
    protected void setLastData(JSONObject response, String tableName) {
        JSONArray array;
        try {
            array = response.getJSONArray(tableName);
            Log.d(TAG, array.toString());
            JSONObject measure = array.getJSONObject(0);
            temperature.postValue(measure.getDouble(col_temperature));
            humidity.postValue(measure.getDouble(col_humidity));
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
                Float ts_f = Float.parseFloat("" + ts.getTime());
                pmArray.add(new Float[]{ts_f, dataValue.floatValue()});
            }
            if (columnName == DataHT.col_humidity) {
                this.humEntries.postValue(pmArray);
            } else if (columnName == DataHT.col_temperature) {
                this.tempEntries.postValue(pmArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getColumnDateStr() {
        return col_date;
    }
}
