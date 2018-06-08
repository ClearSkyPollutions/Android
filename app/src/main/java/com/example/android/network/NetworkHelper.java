package com.example.android.network;

import android.app.Fragment;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.activities.BuildConfig;
import com.example.android.models.Data;
import com.example.android.viewModels.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NetworkHelper {

    private String colDate = "date";

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

    //TODO add ID and PORT in createURL
    private URL createURL(String ipaddr, int port, String scale, String query){
        URI uri;
        URL url = null;
        try {
            uri = new URI("http", null, ipaddr, port,
                    "/"+scale, query, null);
            url = uri.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            System.out.println("Wrong URL");
            e.printStackTrace();
        }
        return url;
    }
    private URL buildUrl(String type, String scale,  int nb) {
        String query = "order=date,desc&page=1," + nb + "&columns=date," + type + "&transform=1";

        URL url;

        url = createURL(BuildConfig.IPADDR,BuildConfig.PortHTTP,scale,query);

        Log.d(DataModel.class.toString(), url.toString());
        return url;
    }



    public void downloadData(MutableLiveData<Data> data){
        String type = data.getValue().name;
        String scale = data.getValue().scale;

        URL requestURL = buildUrl(type, scale, getNbOfData(scale));

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

    public static String dateStrFromTimeStamp(float date) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh--mm--ss", Locale.FRANCE);
        return ft.format(new Timestamp((long) date));
    }
}
