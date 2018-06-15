package com.example.android.network;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.activities.BuildConfig;
import com.example.android.viewModels.DataModel;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;



public class NetworkHelper implements Request.Method {

    public static final int STORE_GRAPH_DATA = 0;

    public void sendRequest(String path, String query, int method, int listenerId){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                method,
                buildUrl(path, query).toString(),
                null,
                getListener(listenerId, buildUrl(path, query)),
                Throwable::printStackTrace
        );
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
        Log.d(NetworkHelper.class.toString(), buildUrl(path, query).toString());
    }

    private URL buildUrl(String path, String query) {

        URI uri;
        URL url = null;

        try {
            uri = new URI("http", null, BuildConfig.IPADDR, BuildConfig.PortHTTP,
                    "/" + path, query , null);
            url = uri.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            System.out.println("Wrong URL");
            e.printStackTrace();
        }
        return url;
    }

    private Response.Listener<JSONObject> getListener(int id, URL url) {
        return (JSONObject response) -> {
            switch (id) {
                case 0:
                    String scale = url.getPath().replace("/","");
                    DataModel.storeGraphData(response, scale);
                default:
                    break;
            }
        };
    }

    public void getAQI(String path, MutableLiveData<Integer> aqi, MutableLiveData<String> level ){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                buildUrl(path, null).toString(),
                null,
                (JSONObject response) -> {
                    try {
                        Integer aqiRcv = response.getInt("index");
                        String levelRcv = response.getString("level");
                        aqi.postValue(aqiRcv);
                        level.postValue(levelRcv);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace
        );
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
        Log.d(NetworkHelper.class.toString(), buildUrl(path, null).toString());
    }

}
