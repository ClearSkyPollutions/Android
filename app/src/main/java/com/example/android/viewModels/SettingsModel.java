package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;

import com.android.volley.Request;
import com.example.android.activities.BuildConfig;
import com.example.android.activities.R;
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
            Location positionSensor = new Location(LocationManager.NETWORK_PROVIDER);
            positionSensor.setLatitude(response.getDouble("latitude"));
            positionSensor.setLongitude(response.getDouble("longitude"));

            getSetting().postValue(new Settings(sensors, frequency,
                    getSetting().getValue().getRaspberryPiAddress(),
                    serverAddress, isDataShared, positionSensor));

            refreshSettings.postValue(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    public void getLocalSettings(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.settings_rpi_file_key),Context.MODE_PRIVATE);

        ArrayList<String> sensors = new ArrayList<>(sharedPref.getStringSet(
                context.getString(R.string.key_sensors), new HashSet<>()));

        int frequency = sharedPref.getInt(context.getString(R.string.key_frequency),
                15);
        Address raspberryPiAddress = new Address(
                sharedPref.getString(context.getString(R.string.key_raspberryPiAddressIp),
                        "192.168.0."),
                sharedPref.getInt(context.getString(R.string.key_raspberryPiAddressPort),
                        80));
        Address serverAddress = new Address(
                sharedPref.getString(context.getString(R.string.key_serverAddressIp),
                        BuildConfig.IPADDR_SERVER),
                sharedPref.getInt(context.getString(R.string.key_serverAddressPort),
                        BuildConfig.PortHTTP_SERVER));
        boolean isDataShared = sharedPref.getBoolean(context.getString(R.string.key_isDataShared),
                false);
        Location positionSensor = new Location(LocationManager.NETWORK_PROVIDER);
        positionSensor.setLatitude(sharedPref.getFloat(
                context.getString(R.string.key_positionSensorLatitude), -1));
        positionSensor.setLongitude(sharedPref.getFloat(
                context.getString(R.string.key_positionSensorLongitude), -1));

        getSetting().setValue(new Settings(sensors, frequency,
                raspberryPiAddress, serverAddress, isDataShared, positionSensor));
    }

    public void setLocalSettings(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.settings_rpi_file_key), Context.MODE_PRIVATE);
        Settings settings = setting.getValue();
        Set<String> sensorsSet = new HashSet<>(settings.getSensors());

        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putStringSet(context.getString(R.string.key_sensors),
                sensorsSet);
        editor.putInt(context.getString(R.string.key_frequency),
                settings.getFrequency());
        editor.putString(context.getString(R.string.key_raspberryPiAddressIp),
                settings.getRaspberryPiAddress().getIp());
        editor.putInt(context.getString(R.string.key_raspberryPiAddressPort),
                settings.getRaspberryPiAddress().getPort());
        editor.putString(context.getString(R.string.key_serverAddressIp),
                settings.getServerAddress().getIp());
        editor.putInt(context.getString(R.string.key_serverAddressPort),
                settings.getServerAddress().getPort());
        editor.putBoolean(context.getString(R.string.key_isDataShared),
                settings.isDataShared());
        editor.putFloat(context.getString(R.string.key_positionSensorLatitude),
                (float) settings.getPositionSensor().getLatitude());
        editor.putFloat(context.getString(R.string.key_positionSensorLongitude),
                (float) settings.getPositionSensor().getLongitude());
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

            jsonSend.put(context.getString(R.string.key_sensors),
                    sensorsJson);
            jsonSend.put(context.getString(R.string.key_frequency),
                    settings.getFrequency());
            jsonSend.put(context.getString(R.string.json_serverAddress),
                    serverAddressJson);
            jsonSend.put(context.getString(R.string.key_isDataShared),
                    settings.isDataShared());
            jsonSend.put(context.getString(R.string.key_positionSensorLatitude),
                    settings.getPositionSensor().getLatitude());
            jsonSend.put(context.getString(R.string.key_positionSensorLongitude),
                    settings.getPositionSensor().getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        communication(context, "config.php", Request.Method.PUT, jsonSend);
    }

    //@TODO : Check validity of Add IP (XXX.XXX.XXX.XXX) and port (<~36k)
    public boolean checkRPiAddr(){
        Address addressRPI = getSetting().getValue().getRaspberryPiAddress();
        Address addressServer = getSetting().getValue().getServerAddress();

        return (!(addressRPI.getIp().equals("")) &&
                addressRPI.getPort() != null &&
                !(addressServer.getIp().equals("")) &&
                addressServer.getPort() != null);

    }
}