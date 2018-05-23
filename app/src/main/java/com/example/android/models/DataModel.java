package com.example.android.models;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public abstract class DataModel extends ViewModel {

    private static final String ip_address = "192.168.2.69";
    public static String currentTableName;
    public static String currentColumnName;
    public static String numberOfValues;
    public MutableLiveData<List<Float[]>> pmEntries = new MutableLiveData<>();

    public void loadLastData(Context mCtx){

        String query = "order=id,desc&page=1,1&transform=1";
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
                        setLastData(response);
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
        Log.d(DataModel.class.toString(), "loadLastData: network request");
    }

    public void fillGraph(Context mCtx, String columnName){
        currentColumnName = columnName;
        String query = "order=id,desc&page=1,"+numberOfValues+"&columns=date,"+columnName+"&transform=1";
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
                    public void onResponse(JSONObject response) { setChartData(response, columnName);}
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) { e.printStackTrace(); }
                }
        );
        RequestQueueSingleton.getInstance(mCtx).addToRequestQueue(jsonObjectRequest);
        Log.d(DataModel.class.toString(), "loadLastData: network request");
    }

    private  void setChartData(JSONObject response, String columnName) {
        try {
            ArrayList<Float[]> pmArray = new ArrayList<>();
            JSONArray array = response.getJSONArray(currentTableName);
            for (int i = array.length() - 1; i >= 0; i--) {
                JSONObject measure =  array.getJSONObject(i);
                Double dataValue = measure.getDouble(columnName);
                String date = measure.getString(this.getColumnDateStr());
                Timestamp ts = Timestamp.valueOf(date);
                Float ts_f = Float.parseFloat("" + ts.getTime());
                pmArray.add(new Float[]{ts_f, dataValue.floatValue()});
            }
            this.pmEntries.postValue(pmArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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

        return url;
    }

    protected abstract String getColumnDateStr();
    protected abstract void setLastData(JSONObject response);
}
