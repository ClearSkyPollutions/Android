package com.example.android.models;


import android.arch.lifecycle.MutableLiveData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DataHT extends DataModel{

    public MutableLiveData<Double> temperature = new MutableLiveData<>();
    public MutableLiveData<Double> humidity = new MutableLiveData<>();

    private static final String TABLE_NAME = "DHT22";


    @Override
    protected void setMeasurementLive(JSONObject response) {
        JSONArray array = null;
        try {
            array = response.getJSONArray(TABLE_NAME);
            JSONObject measure = array.getJSONObject(array.length() - 1);
            temperature.postValue(measure.getDouble("temperature"));
            humidity.postValue(measure.getDouble("humidity"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
