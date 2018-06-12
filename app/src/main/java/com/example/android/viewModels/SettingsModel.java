package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.models.Settings;
import com.example.android.network.NetworkHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;


public class SettingsModel extends ViewModel {

    private MutableLiveData<Settings> setting;

    private NetworkHelper network = new NetworkHelper();

    public MutableLiveData<Settings> getSetting() {
        if (setting == null) {
            setting = new MutableLiveData<>();
            setting.postValue(new Settings(new ArrayList<>(), 15, "", "WEP", ""));
        }
        return setting;
    }

    public void communication(String path, int method, JSONObject jsonObject) {

        URL requestURL = network.buildUrl(path, "");
        Log.d("URL",requestURL.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                method,
                requestURL.toString(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) { parseJSONResponse(response);}
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) { e.printStackTrace(); }
                }
        );
        network.sendRequest(jsonObjectRequest);


        //network.downloadData(settings);
    }

    private void parseJSONResponse(JSONObject response) {
        try {

            ArrayList<String> sensors = new ArrayList<>();

            JSONArray arraySensors = response.getJSONArray("Sensors");

            for (int i = 0; i >= arraySensors.length() - 1; i++) {
                sensors.add(arraySensors.getJSONObject(i).toString());
            }

            int frequency = response.getInt("Frequency");
            String ssid = response.getString("SSID");
            String securitytype = response.getString("SecurityType");
            String password = response.getString("Password");
            Settings newsettings = new Settings(sensors,frequency,ssid,securitytype,password);
            getSetting().postValue(newsettings);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}