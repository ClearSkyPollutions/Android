package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.example.android.activities.BuildConfig;
import com.example.android.helpers.JSONParser;
import com.example.android.models.Address;
import com.example.android.models.Settings;
import com.example.android.network.NetworkHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            e.printStackTrace();
        }
    };

    public void getLocalSettings(SharedPreferences sharedPref) {
        ArrayList<String> sensors = new ArrayList<>(sharedPref.getStringSet("sensors", new HashSet<>()));

        int frequency = sharedPref.getInt("frequency", 15);
        Address raspberryPiAddress = new Address(
                sharedPref.getString("raspberryPiAddressIp", "192.168.0."),
                sharedPref.getInt("raspberryPiAddressPort", 80));
        Address serverAddress = new Address(
                sharedPref.getString("serverAddressIp", "127.0.0.1"),
                sharedPref.getInt("serverAddressPort", 7001));
        boolean isDataShared = sharedPref.getBoolean("isDataShared", false);

        getSetting().setValue(new Settings(sensors, frequency,
                raspberryPiAddress, serverAddress, isDataShared));
    }

    public void setLocalSettings(SharedPreferences sharedPref) {
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

    public void communication(Context context, String path, int method, JSONObject configToSend) {
        network.sendRequestRPI(context, path, null, method, parseSettings, configToSend);
    }

    public void sendNewSettings(Context context) {
        Settings settings = getSetting().getValue();

        JSONArray sensorsJson = new JSONArray(settings.getSensors());
        JSONObject serverAddressJson = new JSONObject();

        JSONObject jsonSend = new JSONObject();
        try {
            serverAddressJson.put("ip", settings.getServerAddress().getIp());
            serverAddressJson.put("port", settings.getServerAddress().getPort());

            jsonSend.put("sensors", sensorsJson);
            jsonSend.put("frequency", settings.getFrequency());
            jsonSend.put("serverAddress", serverAddressJson);
            jsonSend.put("isDataShared", settings.isDataShared());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        communication(context, "config.php", Request.Method.PUT, jsonSend);
    }

    //@TODO : Check validity of Add IP (XXX.XXX.XXX.XXX) and port (<~36k)
    public boolean checkRPiAddr(){
        Address addressRPI = getSetting().getValue().getRaspberryPiAddress();
        Address addressServer = getSetting().getValue().getServerAddress();

        if (!(addressRPI.getIp().equals("")) &&
                addressRPI.getPort() != null &&
                !(addressServer.getIp().equals("")) &&
                addressServer.getPort() != null) {
            return true;
        }else {
            return false;
        }
    }
}