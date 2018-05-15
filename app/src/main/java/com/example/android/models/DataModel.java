package com.example.android.models;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.activities.BuildConfig;
import com.example.android.network.RequestQueueSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class DataModel extends ViewModel {

    private static final String ip_address = "192.168.2.69";

    private DataPM measurementLive;

    MutableLiveData<String> dateMeasurement;
    MutableLiveData<Double> pm2_5;
    MutableLiveData<Double> pm10;

    public DataPM getMeasurementLive() {
        return measurementLive;
    }

    public void setMeasurementLive(DataPM measurementLive) {
        this.measurementLive = measurementLive;
        dateMeasurement.postValue(measurementLive.getDate());
        pm2_5.postValue(measurementLive.pm2_5);
        pm10.postValue(measurementLive.pm10);
    }

    public MutableLiveData<String> getDateMeasurement() {
        if(dateMeasurement == null) {
            dateMeasurement = new MutableLiveData<>();
        }
        return dateMeasurement;
    }

    public MutableLiveData<Double> getPm2_5() {
        if(pm2_5 == null) {
            pm2_5 = new MutableLiveData<>();
        }
        return pm2_5;
    }

    public MutableLiveData<Double> getPm10() {
        if(pm10 == null) {
            pm10 = new MutableLiveData<>();
        }
        return pm10;
    }

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
                                setMeasurementLive(new DataPM(measure.getInt("id"),
                                        measure.getString("date_mesure"),
                                        measure.getDouble("pm2_5"),
                                        measure.getDouble("pm10")));
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

    private static URL buildUrl(String table, String query) {
        URI uri;
        URL url = null;

        try {
            uri = new URI("http", null, ip_address, BuildConfig.PortHTTP, table, query, null);
            url = uri.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            System.out.println("Wrong URL");
            e.printStackTrace();
        }

        return url;
    }
}
