package com.example.android.models;


import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.network.RequestQueueSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class DataHT extends DataModel{

    private MutableLiveData<String> dateMeasurement;
    private MutableLiveData<Double> pm2_5;
    private MutableLiveData<Double> pm10;

    private static final String TABLE_NAME = "Concentration_pm";


    @Override
    public void setMeasurementLive(JSONObject measure) {
        try {
            dateMeasurement.postValue(measure.getString("date_mesure"));
            pm2_5.postValue(measure.getDouble("pm2_5"));
            pm10.postValue(measure.getDouble("pm10"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void LoadData(Context mCtx) {
        final String ip_file = "/" + TABLE_NAME;
        String query = "order=id,desc&page=1,1&transform=1";
        String urlLastData;

        URL tmp_url = buildUrl(ip_file, query);
        urlLastData = tmp_url.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                urlLastData,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // Process the JSON
                        try {
                            // Get the date and time of the measure
                        JSONArray array = response.getJSONArray(TABLE_NAME);

                            // Loop through the array elements
                            for (int i = 0; i < array.length(); i++) {
                                // Get current json object
                                JSONObject measure = array.getJSONObject(i);

                                // Get the current (json object) data
                                setMeasurementLive(measure);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        e.printStackTrace();
                    }
                }
        );
        RequestQueueSingleton.getInstance(mCtx).addToRequestQueue(jsonObjectRequest);
    }
}
