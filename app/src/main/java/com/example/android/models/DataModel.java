package com.example.android.models;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.activities.BuildConfig;
import com.example.android.network.RequestQueueSingleton;

import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class DataModel extends ViewModel {

    private static final String ip_address = BuildConfig.IPADDR;
    public static String currentTableName = "AVG_HOUR";
    public static String currentColumnName = "pm25";
    public static String currentNumberOfValues = "24";


    public void loadLastData(String tableName){

        String query = "order=date,desc&page=1,1&transform=1";
        URL url = buildUrl(query);
        String urlLastData = null;
        if ( url != null) {
            urlLastData = url.toString();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                 urlLastData,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setLastData(response, tableName);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        e.printStackTrace();
                    }
                }
        );
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
        Log.d(DataModel.class.toString(), "loadLastData: network request");
    }

    public void fillGraph(String tableName, String columnName){
        currentColumnName = columnName;
        String query = "order=date,desc&page=1,"+currentNumberOfValues+"&columns=date,"+columnName+"&transform=1";
        URL url = buildUrl(query);
        String urlLastData = null;
        if ( url != null) {
            urlLastData = url.toString();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                urlLastData,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) { setChartData(response,tableName, columnName);}
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) { e.printStackTrace(); }
                }
        );
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
        Log.d(DataModel.class.toString(), "fillGraph: network request");
    }

    public static void setScale(String tableName, String numberOfValues) {
        currentTableName = tableName;
        currentNumberOfValues = numberOfValues;
    }

    public static void setPollutant(String columnName) {
        currentColumnName = columnName;
    }

    protected abstract void setChartData(JSONObject response, String tableName, String columnName);

    private URL buildUrl(String query) {
        URI uri;
        URL url = null;

        try {
            uri = new URI("http", null, ip_address, BuildConfig.PortHTTP, "/"+currentTableName, query, null);
            url = uri.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            System.out.println("Wrong URL");
            e.printStackTrace();
        }
        Log.d(DataModel.class.toString(), url.toString());
        return url;
    }

    protected abstract String getColumnDateStr();
    protected abstract void setLastData(JSONObject response, String tableName);
}
