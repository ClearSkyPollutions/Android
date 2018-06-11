package com.example.android.network;

import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.activities.BuildConfig;
import com.example.android.viewModels.DataModel;
import com.example.android.viewModels.SettingsModel;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class NetworkHelper {

    public void sendRequest(JsonObjectRequest jsonObjectRequest) {
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
        //Log.d(SettingsModel.class.toString(), "fillGraph: network request");
    }

    public URL buildUrl(String path, String params) {
        URI uri;
        URL url = null;
        try {
            uri = new URI("http", null, BuildConfig.IPADDR, BuildConfig.PortHTTP,
                    "/"+path, params, null);
            url = uri.toURL();
            Log.d(DataModel.class.toString(), url.toString());
        } catch (URISyntaxException | MalformedURLException e) {
            System.out.println("Wrong URL");
            e.printStackTrace();
        }
        return url;
    }

}
