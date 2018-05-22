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

    private static final String ip_address = "192.168.2.69";


    protected abstract void setMeasurementLive(JSONObject response);

    public abstract String getTableName();

    public void LoadLastData(Context mCtx){
        String query = "order=id,desc&page=1,168&transform=1"; // takes the 168 last entries

        URL url = buildUrl(query);
        String urlLastData = url.toString();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                 urlLastData,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setMeasurementLive(response);
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
        Log.d(DataModel.class.toString(), "LoadLastData: network request");
    }


    public URL buildUrl(String query) {
        URI uri;
        URL url = null;

        try {
            uri = new URI("http", null, ip_address, BuildConfig.PortHTTP, "/"+ getTableName(), query, null);
            url = uri.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            System.out.println("Wrong URL");
            e.printStackTrace();
        }

        return url;
    }
}
