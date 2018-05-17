package com.example.android.models;


import android.arch.lifecycle.MutableLiveData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DataPM extends DataModel{

    public MutableLiveData<String> dateMeasurement = new MutableLiveData<>();
    public MutableLiveData<Double> pm2_5 = new MutableLiveData<>();
    public MutableLiveData<Double> pm10 = new MutableLiveData<>();
    public MutableLiveData<List<Float[]>> pmEntries = new MutableLiveData<>();

    private static final String TABLE_NAME = "Concentration_pm";

    @Override
    protected void setMeasurementLive(JSONObject response) {
        try {
            // Get the date and time of the measure
            ArrayList<Float[]> pmArray = new ArrayList<>();
            JSONArray array = response.getJSONArray(TABLE_NAME);
            for (int i = 0; i < array.length(); i++) {
                JSONObject measure =  array.getJSONObject(i);
                Double pm25_d = measure.getDouble("pm25");
                Double pm10_d = measure.getDouble("pm10");
                pmArray.add(new Float[]{pm25_d.floatValue(), pm10_d.floatValue()});
                if(i == array.length() - 1) {
                    pm2_5.postValue(pm25_d);
                    pm10.postValue(pm10_d);
                    dateMeasurement.postValue(measure.getString("date"));
                }
            }
            pmEntries.postValue(pmArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
