package com.example.android.network;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.activities.BuildConfig;
import com.example.android.helpers.HashHelper;
import com.example.android.models.Data;
import com.example.android.viewModels.DataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import io.realm.Realm;


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
        Response.Listener<JSONObject> listener;
        switch (id) {
            case 0:
                String scale = url.getPath().replace("/","");
                listener = storeGraphData(scale);
                return listener;
             default:
                 break;
        }
        return null;
    }

    private Response.Listener<JSONObject> storeGraphData(String scale) {
        return (JSONObject response) -> {
            Realm realm = Realm.getDefaultInstance();
            Data data = new Data();
            realm.executeTransaction(realmDb -> {
                try {
                    JSONArray jsonArray = response.getJSONArray(scale);
                    for (int i = jsonArray.length() - 1; i >= 0; i--) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String dateString = jsonObject.getString("date");
                        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = ft.parse(dateString);
                        for(String type : DataModel.GRAPH_NAMES) {
                            Float val = (float) jsonObject.getDouble(type);
                            data.setId(HashHelper.generateMD5(dateString+type+scale));
                            data.setDate(date);
                            data.setValue(val);
                            data.setDataType(type);
                            data.setScale(scale);
                            realmDb.copyToRealmOrUpdate(data);
                            Log.d("storeGraphData", data.getDataType() + ", " + data.getScale() +", " + data.getValue());
                        }
                    }
                } catch (JSONException | ParseException e) {e.printStackTrace();}
            });
            realm.close();
        };
    }
}
