package com.example.android.models;


import android.arch.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DataPM extends DataModel{

    public MutableLiveData<String> dateMeasurement = new MutableLiveData<>();
    public MutableLiveData<Double> pm2_5 = new MutableLiveData<>();
    public MutableLiveData<Double> pm10 = new MutableLiveData<>();

    private static final String TABLE_NAME = "Concentration_pm";

    @Override
    protected void setMeasurementLive(JSONObject response) {
        try {
            // Get the date and time of the measure
            JSONArray array = response.getJSONArray(TABLE_NAME);
            JSONObject measure =  array.getJSONObject(array.length() - 1);
            dateMeasurement.postValue(measure.getString("date"));
            pm2_5.postValue(measure.getDouble("pm25"));
            pm10.postValue(measure.getDouble("pm10"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
