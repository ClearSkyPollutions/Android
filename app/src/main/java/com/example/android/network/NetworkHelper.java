package com.example.android.network;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.activities.BuildConfig;
import com.example.android.viewModels.JSONParser;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class NetworkHelper implements Request.Method {

    public void sendRequest(String ipAddress, int portHTTP,  String path, String query, int method, JSONParser<JSONObject> f, JSONObject dataToSend) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                method,
                buildUrl(ipAddress, portHTTP, path, query).toString(),
                dataToSend,
                f::apply,
                Throwable::printStackTrace
        );
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
        Log.d(NetworkHelper.class.toString(), buildUrl(ipAddress, portHTTP, path, query).toString());
    }

    private URL buildUrl(String ipAddress, int portHTTP, String path, String query) {

        URI uri;
        URL url = null;
        try {
            uri = new URI("http", null, ipAddress, portHTTP,
                    "/" + path, query, null);
            url = uri.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            System.out.println("Wrong URL");
            e.printStackTrace();
        }
        return url;
    }

}
