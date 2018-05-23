package com.example.android.models;


import android.arch.lifecycle.MutableLiveData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DataHT extends DataModel{

    public MutableLiveData<Double> temperature = new MutableLiveData<>();
    public MutableLiveData<Double> humidity = new MutableLiveData<>();
    public static final String col_humidity = "humidity";
    public static final String col_temperature = "temperature";
    public static final String col_date = "date";


    @Override
    protected void setLastData(JSONObject response) {
        JSONArray array;
        try {
            array = response.getJSONArray(DataModel.currentTableName);
            JSONObject measure = array.getJSONObject(array.length() - 1);
            temperature.postValue(measure.getDouble(col_temperature));
            humidity.postValue(measure.getDouble(col_humidity));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getColumnDateStr() {
        return col_date;
    }
}
