package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.example.android.activities.R;
import com.example.android.helpers.JSONParser;
import com.example.android.network.NetworkHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class AQIModel extends ViewModel {

    private MutableLiveData<String> label;
    private MutableLiveData<Integer> aqi;
    private MutableLiveData<Integer> color;

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

    public void loadAQIRPI(Context context) {
        NetworkHelper netHelper = new NetworkHelper();
        netHelper.sendRequestRPI(context, "aqi.php", null, NetworkHelper.GET, parseAQI, null);
    }
    public void loadAQIServer(Context context) {
        NetworkHelper netHelper = new NetworkHelper();

        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.settings_rpi_file_key),Context.MODE_PRIVATE);
        String systemID = sharedPref.getString("systemID", "-1");

        if (!systemID.equals("-1")) {
            String query = "id=" + systemID;
            netHelper.sendRequestServer(context, "aqi.php", query, NetworkHelper.GET, parseAQI, null);
        }
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
