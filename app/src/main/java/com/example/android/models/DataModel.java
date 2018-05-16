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
import java.util.ArrayList;
import java.util.List;

public abstract class DataModel extends ViewModel {

    private static final String ip_address = "192.168.2.69";

    public abstract void setMeasurementLive(JSONObject measure);

    public abstract void LoadData(Context mCtx);

    protected static URL buildUrl(String table, String query) {
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
