package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import com.example.android.activities.BuildConfig;
import com.example.android.models.RPI;
import com.example.android.models.SharedData;
import com.example.android.network.NetworkHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MapModel extends ViewModel {

    private NetworkHelper networkHelper;
    public MutableLiveData<String> lastHour;
    public MutableLiveData<ArrayList<RPI>> liveRpiArrayList;
    private static final String MAP = "MAP";
    private static final String POLLUTANT = "pollutant";
    private static final String SYSTEM = "system";
    private static final String DATE = "date";
    private static final String VALUE = "value";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";


    public MapModel() {
        networkHelper = new NetworkHelper();
        lastHour = new MutableLiveData<>();
        liveRpiArrayList = new MutableLiveData<>();
    }

    public void syncMapData(Context context) {
        String query = "order=system,asc&transform=1";
        networkHelper.sendRequestServer(context, MAP, query, NetworkHelper.GET, parseMapData, null);
    }

    private JSONParser<JSONObject> parseMapData = (JSONObject response) -> {
        ArrayList<RPI> rpiArrayList = new ArrayList<>();
        try {
            String tableName = response.keys().next();
            JSONArray jsonArray = response.getJSONArray(tableName);
            String systemName = "";
            RPI rpi = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (!systemName.equals(jsonObject.getString(SYSTEM))) {
                    systemName = jsonObject.getString(SYSTEM);
                    String latitude = jsonObject.getString(LATITUDE);
                    Double lat_d = Double.parseDouble(latitude);
                    String longitude = jsonObject.getString(LONGITUDE);
                    Double long_d = Double.parseDouble(longitude);
                    rpi = new RPI(systemName, new GeoPoint(lat_d, long_d));
                    rpiArrayList.add(rpi);
                }
                String dateString = jsonObject.getString(DATE);
                Double value = jsonObject.getDouble(VALUE);
                String pollutantName = jsonObject.getString(POLLUTANT);
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = ft.parse(dateString);
                SharedData sharedData = new SharedData(pollutantName, date, value);
                if (rpi != null)
                    rpi.getSharedDataArrayList().add(sharedData);
            }
            liveRpiArrayList.postValue(rpiArrayList);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    };

    public void getLastHour(Context context) {
        String query = "order=" + DATE + ",desc&page=1,1&columns=" + DATE + "&transform=1";
        networkHelper.sendRequestServer(context, MAP, query, NetworkHelper.GET, parseLastHour, null);
    }

    JSONParser<JSONObject> parseLastHour = (JSONObject response) -> {
        try {
            String tableName = response.keys().next();
            JSONArray jsonArray = response.getJSONArray(tableName);
            String hour = jsonArray.getJSONObject(0).getString(DATE);
            //Log.d(MapModel.class.toString(), "Last Hour: " + hour);
            if (lastHour.getValue() == null) lastHour.postValue(hour);
            else if (!hour.equals(lastHour.getValue())) lastHour.postValue(hour);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
