package com.example.android.network;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.activities.BuildConfig;
import com.example.android.models.Data;
import com.example.android.models.DataType;
import com.example.android.models.Scale;
import com.example.android.viewModels.DataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import io.realm.Realm;


public class NetworkHelper implements Request.Method {

    public static final int STORE_GRAPH_DATA = 0;


    public void sendRequest(String path, String query, int method, int listenerId){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                method,
                buildUrl(path, query).toString(),
                null,
                getListener(listenerId, buildUrl(path, query)),
                e -> e.printStackTrace()
        );
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
        Log.d(DataModel.class.toString(), "network request");
    }


    public URL buildUrl(String path, String query) {

        URI uri;
        URL url = null;

        try {
            uri = new URI("http", null, BuildConfig.IPADDR, BuildConfig.PortHTTP,
                    "/" + path, query , null);
            url = uri.toURL();
            Log.d(DataModel.class.toString(), url.toString());
        } catch (URISyntaxException | MalformedURLException e) {
            System.out.println("Wrong URL");
            e.printStackTrace();
        }
        return url;
    }

    private Response.Listener<JSONObject> getListener(int id, URL url) {
        Response.Listener<JSONObject> listener;
        switch (id) {
            case 0:
                String scale = url.getPath();
                String type = splitQuery(url).get("column");
                listener = storeGraphData(scale, type);
                return listener;
             default:
                 break;
        }
        return null;
    }

    private Response.Listener<JSONObject> storeGraphData(String scale, String type) {
        return response -> {
            Realm realm = Realm.getDefaultInstance();
            ArrayList<Float[]> graphData = new ArrayList<>();
            try {
                JSONArray array = response.getJSONArray(scale);
                for (int i = array.length() - 1; i >= 0; i--) {
                    JSONObject measure = array.getJSONObject(i);
                    Float val = (float) measure.getDouble(type);
                    String date = measure.getString("date");
                    // Change the date String to a float representing ms since 01/01/1970
                    Float ts_f = (float) Timestamp.valueOf(date).getTime();
                    graphData.add(new Float[]{ts_f, val});
                    //Store data in database
                    realm.executeTransactionAsync(realmDb -> {
                        Data data = new Data();
                        data.setDatetime(date);
                        data.setTimestamp(ts_f);
                        data.setValue(val);
                        data.setDataType(realmDb.where(DataType.class)
                                .equalTo("name", type)
                                .findFirst()
                        );
                        data.setScale(realmDb.where(Scale.class)
                                .equalTo("name", scale)
                                .findFirst()
                        );
                        realmDb.copyToRealmOrUpdate(data);
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            realm.close();
        };
    }

    private Map<String, String> splitQuery(URL url) {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return query_pairs;
    }
}
