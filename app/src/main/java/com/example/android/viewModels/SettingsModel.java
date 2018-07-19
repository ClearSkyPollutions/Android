package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.example.android.activities.BuildConfig;
import com.example.android.models.Address;
import com.example.android.models.Settings;
import com.example.android.network.NetworkHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class SettingsModel extends ViewModel {

    private MutableLiveData<Settings> setting = new MutableLiveData<>();
    private NetworkHelper network = new NetworkHelper();

    public MutableLiveData<Boolean> refreshSettings = new MutableLiveData<>();

    public MutableLiveData<Settings> getSetting() {
        return setting;
    }

    private JSONParser<JSONObject> parseSettings = (JSONObject response) -> {
        try {
            ArrayList<String> sensors = new ArrayList<>();

            JSONArray arraySensors = response.getJSONArray("sensors");
            JSONObject objectServerAddress = response.getJSONObject("serverAddress");

            for (int i = 0; i < arraySensors.length(); i++) {
                sensors.add(arraySensors.getString(i));
            }
            int frequency = response.getInt("frequency");
            Address serverAddress = new Address(objectServerAddress.getString("ip"),
                    objectServerAddress.getInt("port"));
            boolean isDataShared = response.getBoolean("isDataShared");
            getSetting().postValue(new Settings(sensors, frequency, getSetting().getValue().getRaspberryPiAddress(), serverAddress, isDataShared));

            refreshSettings.postValue(false);
        } catch (JSONException e) {
            refreshSettings.postValue(false);
            e.printStackTrace();
        }
    };

    public void fetchPrefsSettings(SharedPreferences sharedPref) {
        ArrayList<String> sensors = new ArrayList<>(sharedPref.getStringSet("sensors", new HashSet<>()));

        int frequency = sharedPref.getInt("frequency", 15);
        Address raspberryPiAddress = new Address(
                sharedPref.getString("raspberryPiAddressIp", "192.168.0."),
                sharedPref.getInt("raspberryPiAddressPort", 80));
        Address serverAddress = new Address(
                sharedPref.getString("serverAddressIp", BuildConfig.IPADDR_SERVER),
                sharedPref.getInt("serverAddressPort", BuildConfig.PortHTTP_SERVER));
        boolean isDataShared = sharedPref.getBoolean("isDataShared", false);

        setting.setValue(new Settings(sensors, frequency,
                raspberryPiAddress, serverAddress, isDataShared));
    }

    public void storeSettings(SharedPreferences sharedPref) {
        Settings settings = setting.getValue();
        Set<String> sensorsSet = new HashSet<>(settings.getSensors());

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet("sensors", sensorsSet);
        editor.putInt("frequency", settings.getFrequency());
        editor.putString("raspberryPiAddressIp", settings.getRaspberryPiAddress().getIp());
        editor.putInt("raspberryPiAddressPort", settings.getRaspberryPiAddress().getPort());
        editor.putString("serverAddressIp", settings.getServerAddress().getIp());
        editor.putInt("serverAddressPort", settings.getServerAddress().getPort());
        editor.putBoolean("isDataShared", settings.isDataShared());
        editor.apply();
    }

    public void communication(String path, int method, JSONObject configToSend) {
        network.sendRequest(BuildConfig.IPADDR_RPI, BuildConfig.PortHTTP_RPI, path, null, method, parseSettings, configToSend);
    }
}