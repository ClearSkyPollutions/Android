package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.activities.BuildConfig;
import com.example.android.models.Settings;
import com.example.android.network.NetworkHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


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

    private JSONParser<JSONObject> parseSettings = (JSONObject response) -> {
        try {
            ArrayList<String> sensors = new ArrayList<>();

            JSONArray arraySensors = response.getJSONArray("Sensors");

            for (int i = 0; i < arraySensors.length(); i++) {
                sensors.add(arraySensors.getString(i));
            }
            Log.d("Sens", Arrays.toString(new ArrayList[]{sensors}));

            int frequency = response.getInt("Frequency");
            String ssid = response.getString("SSID");
            String securityType = response.getString("SecurityType");
            String password = response.getString("Password");

            getSetting().postValue(new Settings(sensors, frequency, ssid, securityType, password));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    public void communication(String path, int method, JSONObject configToSend) {
        network.sendRequest(BuildConfig.IPADDR_RPI, BuildConfig.PortHTTP_RPI, path, null, method, parseSettings, configToSend);
    }
}