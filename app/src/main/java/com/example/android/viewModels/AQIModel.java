package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.activities.BuildConfig;
import com.example.android.network.NetworkHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class AQIModel extends ViewModel {

    public MutableLiveData<String> label;
    public MutableLiveData<Integer> aqi;

    public MutableLiveData<Integer> getAqi() {
        if (aqi == null) {
            aqi = new MutableLiveData<>();
        }
        return aqi;
    }

    public MutableLiveData<String> getLabel() {
        if (label == null) {
            label = new MutableLiveData<>();
        }
        return label;
    }

    public void loadAQI() {
        NetworkHelper netHelper = new NetworkHelper();
        netHelper.sendRequest(BuildConfig.IPADDR_RPI, BuildConfig.PortHTTP_RPI, "aqi.php", null, NetworkHelper.GET, parseAQI, null);
    }

    private JSONParser<JSONObject> parseAQI = (JSONObject response) -> {
        try {
            Integer aqiRcv = response.getInt("index");
            String levelRcv = response.getString("level");
            aqi.postValue(aqiRcv);
            label.postValue(levelRcv);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };
}
