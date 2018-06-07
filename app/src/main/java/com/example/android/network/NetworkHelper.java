package com.example.android.network;

import android.arch.lifecycle.MutableLiveData;
import android.provider.ContactsContract;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.activities.BuildConfig;
import com.example.android.models.Graph;
import com.example.android.models.Measure;
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

    public void downloadGraphData(MutableLiveData<Graph> data){

        String type = data.getValue().getName();
        String scale = data.getValue().getScale();
        String query = "order=date,desc&page=1," + getNbOfData(scale) + "&columns=date," + type + "&transform=1";

        URL requestURL = buildUrl(scale, query);

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


    public void downloadLastData(MutableLiveData<List<Float>> lastMeasure) {

        String query = "order=date,desc&page=1,1&transform=1";

        URL requestURL = buildUrl(DataModel.AVG_HOUR, query);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                requestURL.toString(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                        List<Float> values = new ArrayList<>();
                        JSONArray array = response.getJSONArray(DataModel.AVG_HOUR);
                        JSONObject measure =  array.getJSONObject(0);
                        String date = measure.getString(colDate);
                        Float ts_f = (float) Timestamp.valueOf(date).getTime();
                        values.add(ts_f);
                        for (String name : DataModel.GRAPH_NAMES) {
                            Float val = (float) measure.getDouble(name);
                            values.add(val);
                        }
                        lastMeasure.postValue(values);
                        } catch (JSONException e) {
                        e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) { e.printStackTrace(); }
                }
        );
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
        Log.d(DataModel.class.toString(), "fillGraph: network request");

    }

    private URL buildUrl(String scale, String query) {
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

    private void parseJSONResponse(JSONObject response, MutableLiveData<Graph> liveGraph) {
        Graph graph = liveGraph.getValue();
        try {
            List<Measure> values = new ArrayList<>();
            JSONArray array = response.getJSONArray(graph.getScale());

            for (int i = array.length() - 1; i >= 0; i--) {

                JSONObject measure =  array.getJSONObject(i);
                Float val = (float) measure.getDouble(graph.getName());
                String date = measure.getString(colDate);

                // Change the date String to a float representing ms since 01/01/1970
                Float ts_f = (float) Timestamp.valueOf(date).getTime();

                values.add(new Measure(liveGraph, ts_f, val));
            }
            liveGraph.postValue(new Graph(graph.getName(), graph.getUnit(), graph.getScale(), values));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
