package com.example.android.network;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.activities.BuildConfig;
import com.example.android.viewModels.DataModel;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class NetworkHelper {
    
    public void sendRequest(URL requestURL, Response.Listener<JSONObject> listener){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                requestURL.toString(),
                null,
                listener,
                e -> e.printStackTrace()
        );
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
        Log.d(DataModel.class.toString(), "fillGraph: network request");
    }

    public URL buildUrl(String path, String params) {
        URI uri;
        URL url = null;

        try {
            uri = new URI("http", null, BuildConfig.IPADDR, BuildConfig.PortHTTP,
                    path, params , null);
            url = uri.toURL();
            Log.d(DataModel.class.toString(), url.toString());
        } catch (URISyntaxException | MalformedURLException e) {
            System.out.println("Wrong URL");
            e.printStackTrace();
        }
        return url;
    }

}
