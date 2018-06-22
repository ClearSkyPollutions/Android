package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.android.activities.BuildConfig;
import com.example.android.models.SharedData;
import com.example.android.network.NetworkHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by nrutemby on 20/06/2018.
 */

public class MapModel extends ViewModel {

    private NetworkHelper networkHelper;
    public MutableLiveData<String> lastHour;
    public MutableLiveData<ArrayList<SharedData>> liveSharedDataArrayList;
    private static final String AVG_HOUR = "AVG_HOUR";
    private static final String TYPE = "Type";
    private static final String SYSTEM = "System";
    private static final String DATE = "Date";
    private static final String VALUE = "Value";
    private static final String LATITUDE = "Latitude";
    private static final String LONGITUDE = "Longitude";
    private static final String NAME = "Name";




    public MapModel() {
        networkHelper = new NetworkHelper();
        lastHour = new MutableLiveData<>();
        liveSharedDataArrayList = new MutableLiveData<>();
    }

    public void getLastHour() {
        String query = "order="+DATE+",desc&page=1,1&columns="+DATE+"&transform=1";
        networkHelper.sendRequest(BuildConfig.IPADDR_SERVER, BuildConfig.PortHTTP_SERVER, AVG_HOUR, query, NetworkHelper.GET, parseLastHour, null);
    }


    public void syncMapData() {
        String query = "filter="+DATE+",eq,"+lastHour.getValue()+"&include="+SYSTEM+","+TYPE+"&columns="+DATE+","+VALUE+","+SYSTEM+
                "."+LATITUDE+","+SYSTEM+"."+LONGITUDE+","+TYPE+"."+NAME+"&transform=1";
        networkHelper.sendRequest(BuildConfig.IPADDR_SERVER, BuildConfig.PortHTTP_SERVER, AVG_HOUR, query, NetworkHelper.GET, parseMapData, null);
    }

    private JSONParser<JSONObject> parseMapData = (JSONObject response) -> {
        ArrayList<SharedData> sharedDataArrayList = new ArrayList<>();
        try {
            String tableName = response.keys().next();
            JSONArray jsonArray = response.getJSONArray(tableName);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String date = jsonObject.getString(DATE);
                Double value = jsonObject.getDouble(VALUE);
                JSONObject system = jsonObject.getJSONArray(SYSTEM).getJSONObject(0);
                String latitude = system.getString(LATITUDE);
                String longitude = system.getString(LONGITUDE);
                JSONObject type = jsonObject.getJSONArray(TYPE).getJSONObject(0);
                String typeName = type.getString(NAME);
                SharedData sharedData = new SharedData();
                sharedData.setType(typeName);
                sharedData.setLatitude(latitude);
                sharedData.setLongitude(longitude);
                sharedData.setDate(date);
                sharedData.setValue(value);
                sharedDataArrayList.add(sharedData);
            }
            liveSharedDataArrayList.postValue(sharedDataArrayList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    JSONParser<JSONObject> parseLastHour = (JSONObject response) -> {
        try {
            String tableName = response.keys().next();
            JSONArray jsonArray = response.getJSONArray(tableName);
            String hour = jsonArray.getJSONObject(0).getString(DATE);
            Log.d(MapModel.class.toString(), "Last Hour: "+hour);
            if (lastHour.getValue() == null) lastHour.postValue(hour);
            else if (!hour.equals(lastHour.getValue())) lastHour.postValue(hour);
            ;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
