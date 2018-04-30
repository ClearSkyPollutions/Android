package com.example.android.network;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.activities.BuildConfig;
import com.example.android.models.DataModel;
import com.example.android.models.Data_PM;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class FetchDataClient {

    @SuppressWarnings("FieldCanBeLocal")
    private static final String ip_address = "192.168.2.108";

    private static URL buildUrl(String table, String query) {
        URI uri;
        URL url = null;

        try {
            uri = new URI("http",null, ip_address, BuildConfig.PortHTTP, table, query, null);
            url = uri.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            System.out.println("Wrong URL");
            e.printStackTrace();
        }

        return url;
    }

    public static void getLastData(final DataModel mdata, RequestQueue queue) {

        final String ip_file = "/Concentration_pm";
        String query = "order=id,desc&page=1,1&transform=1";
        String urlLastData;

        URL tmp_url = buildUrl(ip_file, query);
        if(tmp_url == null) {
            return;
        }
        urlLastData = tmp_url.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                urlLastData,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // Process the JSON
                        try{
                            // Get the date and time of the measure
                            JSONArray array = response.getJSONArray("Concentration_pm");

                            // Loop through the array elements
                            for(int i=0;i<array.length();i++){
                                // Get current json object
                                JSONObject measure = array.getJSONObject(i);

                                // Get the current (json object) data
                                mdata.getmeasurementLive().setValue(new Data_PM(measure.getInt("id"),
                                        measure.getString("date_mesure"),
                                        measure.getDouble("pm2_5"),
                                        measure.getDouble("pm10")));

                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        e.printStackTrace();
                    }
                }
        );

        queue.add(jsonObjectRequest);
    }
}
