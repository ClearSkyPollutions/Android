package com.example.android.models;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
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

    private static final String ip_address = "192.168.2.118";
    public static String currentTableName;
    public static String currentColumnName;
    public static String currentNumberOfValues;

    public void loadLastData(Context mCtx, String tableName){

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
        RequestQueueSingleton.getInstance(mCtx).addToRequestQueue(jsonObjectRequest);
        Log.d(DataModel.class.toString(), "loadLastData: network request");
    }

    public void fillGraph(Context mCtx, String tableName, String columnName){
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
        RequestQueueSingleton.getInstance(mCtx).addToRequestQueue(jsonObjectRequest);
        Log.d(DataModel.class.toString(), "fillGraph: network request");
    }

    public static void setScale(String tableName, String numberOfValues) {
        currentTableName = tableName;
        currentNumberOfValues = numberOfValues;
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
