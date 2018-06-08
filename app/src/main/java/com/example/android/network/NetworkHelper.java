package com.example.android.network;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.activities.BuildConfig;
import com.example.android.models.Data;
import com.example.android.models.DataType;
import com.example.android.models.Graph;
import com.example.android.models.Scale;
import com.example.android.viewModels.DataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;

import io.realm.Realm;

public class NetworkHelper {

    private String colDate = "date";

    public void downloadGraphData(Graph graph){

        String type = graph.getName();
        String scale = graph.getScale();
        String query = "order=date,desc&page=1," + getNbOfData(scale) + "&columns=date," + type + "&transform=1";

        URL requestURL = buildUrl(scale, query);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                requestURL.toString(),
                null,
                response -> storeGraphData(response, type, scale),
                e -> e.printStackTrace()
        );
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
        Log.d(DataModel.class.toString(), "fillGraph: network request");
    }

    private URL buildUrl(String scale, String query) {
        URI uri;
        URL url = null;

        try {
            uri = new URI("http", null, BuildConfig.IPADDR, BuildConfig.PortHTTP,
                    "/"+scale, query, null);
            url = uri.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            System.out.println("Wrong URL");
            e.printStackTrace();
        }
        Log.d(DataModel.class.toString(), url.toString());
        return url;
    }


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

    private void storeGraphData(JSONObject response, String type, String scale) {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<Float[]> graphData = new ArrayList<>();
        try {
            JSONArray array = response.getJSONArray(scale);
            for (int i = array.length() - 1; i >= 0; i--) {
                JSONObject measure =  array.getJSONObject(i);
                Float val = (float) measure.getDouble(type);
                String date = measure.getString(colDate);
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
                            .equalTo("name",scale)
                            .findFirst()
                    );
                    realmDb.copyToRealmOrUpdate(data);
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        realm.close();
    }

}
