package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.models.Data;
import com.example.android.models.Settings;
import com.example.android.network.NetworkHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class DataModel extends ViewModel {

    public String[] DATA_TYPES = {"pm10", "pm25", "temperature", "humidity"};
    public String[] DATA_UNITS = {"µg/m^3", "µg/m^3", "°C", "%"};
    public int[] LINE_COLORS = {0xff00ffff, 0xff00ff00, 0xffff00ff, 0xFFFF4081};

    private List<MutableLiveData<Data>> measurements = new ArrayList<>();

    private NetworkHelper network = new NetworkHelper();

    public DataModel() {
        for (int i = 0; i<DATA_TYPES.length; i++) {
            this.measurements.add(new MutableLiveData<>());
            this.measurements.get(i).setValue(new Data(DATA_TYPES[i], DATA_UNITS[i]));
        }
    }

    public MutableLiveData<Data> getMeasurements(String type){
        for (MutableLiveData<Data> i : measurements) {
            if (i.getValue() != null && i.getValue().name.equals(type)){
                return i;
            }
        }
        return null;
    }

    public void loadData(String type, String scale) {
        MutableLiveData<Data> data = getMeasurements(type);
        if (data.getValue() == null){
            Log.d("Download Data", "Wrong data type : " + type);
            return;
        }
        data.getValue().scale = scale;

        String query = "order=date,desc&page=1," + getNbOfData(scale) + "&columns=date," + type + "&transform=1";

        //TODO TO make
        URL requestURL = network.buildUrl(scale, query);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                requestURL.toString(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) { parseJSONResponse(response, data);}
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) { e.printStackTrace(); }
                }
        );

        network.sendRequest(jsonObjectRequest);
    }

    private int getNbOfData(String scale){
        switch(scale){
            case "AVG_HOUR":
                return 24;
            case "AVG_DAY":
                return 30;
            case "AVG_MONTH":
                return 12;
        }
        return 0;
    }

    private void parseJSONResponse(JSONObject response, MutableLiveData<Data> data) {
        try {
            List<Float[]> vals = new ArrayList<>();
            JSONArray array = response.getJSONArray(data.getValue().scale);

            for (int i = array.length() - 1; i >= 0; i--) {

                JSONObject measure =  array.getJSONObject(i);
                Float val = (float) measure.getDouble(data.getValue().name);
                String date = measure.getString("date");

                // Change the date String to a float representing ms since 01/01/1970
                Float ts_f = (float) Timestamp.valueOf(date).getTime();

                vals.add(new Float[]{ts_f, val});
            }
            data.postValue(new Data(data.getValue(), vals));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

