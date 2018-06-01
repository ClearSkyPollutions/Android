package com.example.android.network;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.activities.BuildConfig;
import com.example.android.models.Data;
import com.example.android.viewModels.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class NetworkHelper {

    private String colDate = "date";

    private int getNbOfData(String scale){
        switch(scale){
            case "AVG_HOUR":
                return 24;
            case "AVG_DAY":
                return 30;
            case "AVG_YEAR":
                return 12;
        }
        return 0;
    }

    private URL buildUrl(String type, String scale, int nb) {
        String query = "order=date,desc&page=1," + nb + "&columns=date," + type + "&transform=1";
        URI uri;
        URL url = null;

        try {
            uri = new URI("http", null, BuildConfig.IPADDR, BuildConfig.PortHTTP,
                    "/"+scale, query, null);
            url = uri.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            System.out.println("Wrong URL");
            e.printStackTrace();
        }
        Log.d(DataModel.class.toString(), url.toString());
        return url;
    }

    private void parseJSONResponse(JSONObject response, MutableLiveData<Data> data) {
        try {
            List<Float[]> vals = new ArrayList<>();
            JSONArray array = response.getJSONArray(data.getValue().scale);

            for (int i = array.length() - 1; i >= 0; i--) {

                JSONObject measure =  array.getJSONObject(i);
                Float val = (float) measure.getDouble(data.getValue().scale);
                String date = measure.getString(colDate);

                // Change the date String in a float representing ms since 01/01/1970
                Float ts_f = (float) Timestamp.valueOf(date).getTime();

                vals.add(new Float[]{ts_f, val});
            }
            data.postValue(new Data(data.getValue(), vals));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void downloadData(MutableLiveData<Data> data){
        String type = data.getValue().name;
        String scale = data.getValue().scale;

        URL requestURL = buildUrl(type, scale, getNbOfData(scale));

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
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
        Log.d(DataModel.class.toString(), "fillGraph: network request");
    }



}
