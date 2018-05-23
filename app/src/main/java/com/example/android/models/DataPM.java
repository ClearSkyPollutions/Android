package com.example.android.models;


import android.arch.lifecycle.MutableLiveData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DataPM extends DataModel{

    public MutableLiveData<String> dateMeasurement = new MutableLiveData<>();
    public MutableLiveData<Double> pm25 = new MutableLiveData<>();
    public MutableLiveData<Double> pm10 = new MutableLiveData<>();
    public static final String col_pm25 = "pm25";
    public static final String col_pm10 = "pm10";
    public static final String col_date = "date";

    @Override
    protected void setLastData(JSONObject response) {
        try {
            JSONArray array = response.getJSONArray(DataModel.currentTableName);
            JSONObject measure =  array.getJSONObject(0);
            pm25.postValue(measure.getDouble(col_pm25));
            pm10.postValue(measure.getDouble(col_pm10));
            dateMeasurement.postValue(measure.getString(col_date));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getColumnDateStr() { return col_date; }
}
