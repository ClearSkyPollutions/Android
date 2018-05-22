package com.example.android.models;


import android.arch.lifecycle.MutableLiveData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            Float ts_reference = .0f;
            for (int i = array.length() - 1; i >= 0; i--) {
                JSONObject measure =  array.getJSONObject(i);
                Double pm25_d = measure.getDouble("pm25");
                Double pm10_d = measure.getDouble("pm10");
                String date = measure.getString("date");
                Timestamp ts = Timestamp.valueOf(date);
                Float ts_f = Float.parseFloat("" + ts.getTime());
                if(i == 0) {
                    pm2_5.postValue(pm25_d);
                    pm10.postValue(pm10_d);
                    dateMeasurement.postValue(measure.getString("date"));
                }
                pmArray.add(new Float[]{ts_f/3600000, pm25_d.floatValue(), pm10_d.floatValue()});
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
