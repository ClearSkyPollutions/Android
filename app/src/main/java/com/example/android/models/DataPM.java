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
import java.util.List;

public class DataPM extends DataModel{

    private MutableLiveData<List> columnsList;


    @Override
    public void setMeasurementLive(JSONObject measure) {
        List columns = this.columnsList.getValue();
        columns.clear();
        try {
            columns.add(measure.getString("date_mesure"));
            columns.add(measure.getDouble("pm2_5"));
            columns.add(measure.getDouble("pm10"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.columnsList.postValue(columns);
    }

    @Override
    public void LoadData(Context mCtx) {
        final String ip_file = "/Concentration_pm";
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
                            JSONArray array = response.getJSONArray("Concentration_pm");

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
