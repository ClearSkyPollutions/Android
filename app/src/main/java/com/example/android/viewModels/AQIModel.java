package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Color;
import android.util.Log;

import com.example.android.activities.BuildConfig;
import com.example.android.network.NetworkHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class AQIModel extends ViewModel {

    public MutableLiveData<String> label;
    public MutableLiveData<Integer> aqi;
    public MutableLiveData<Integer> color;

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
    public MutableLiveData<Integer> getColor() {
        if (color == null) {
            color = new MutableLiveData<>();
        }
        return color;
    }

    public void loadAQI() {
        NetworkHelper netHelper = new NetworkHelper();
        netHelper.sendRequest(BuildConfig.IPADDR_RPI, BuildConfig.PortHTTP_RPI, "aqi.php", null, NetworkHelper.GET, parseAQI, null);
    }

    private JSONParser<JSONObject> parseAQI = (JSONObject response) -> {
        try {
            Integer aqiRcv = response.getInt("index");
            String levelRcv = response.getString("level");
            String colorRcv = response.getString("color");
            label.postValue(levelRcv);
            color.postValue(Color.parseColor(colorRcv));
            aqi.postValue(aqiRcv);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

}
