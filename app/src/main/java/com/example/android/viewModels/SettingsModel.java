package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
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


public class SettingsModel extends ViewModel {

    private MutableLiveData<Settings> setting = new MutableLiveData<>();
    private NetworkHelper network = new NetworkHelper();

    public MutableLiveData<Boolean> refreshSettings = new MutableLiveData<>();
    public MutableLiveData<Boolean> connectionStat = new MutableLiveData<>();

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

    public void communication(String path, int method, JSONObject configToSend) {
        network.sendRequest(BuildConfig.IPADDR_RPI, BuildConfig.PortHTTP_RPI, path, null, method, parseSettings, configToSend);
    }

    public void connectionRaspberryPiTest(Address raspberryPiAddress) {

        String path = "api.php";

        JSONParser<JSONObject> parserConnectionRaspberryPiTest = (JSONObject response) -> {
            try {
                JSONObject objectInfo = response.getJSONObject("info");
                String dataBaseRaspberryPiName = objectInfo.getString("title");
                Log.d("connection", dataBaseRaspberryPiName);
                if (dataBaseRaspberryPiName.equals(BuildConfig.DataBaseRPI_Name)) {
                    connectionStat.postValue(Boolean.TRUE);
                }
            } catch (JSONException e) {
                Log.d("connection", "error");
                connectionStat.postValue(Boolean.FALSE);
                e.printStackTrace();
            }
        };

        network.sendRequest(raspberryPiAddress.getIp(), raspberryPiAddress.getPort(), path,
                null, Request.Method.GET, parserConnectionRaspberryPiTest, null);


    }
}