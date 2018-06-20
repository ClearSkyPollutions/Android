package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
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
    private MutableLiveData<String> lastHour;
    private MutableLiveData<ArrayList<SharedData>> liveSharedDataArrayList;
    private static final String AVG_HOUR = "AVG_HOUR";
    private static final String SYSTEM = "System";
    private static final String TYPE = "Type";




    public MapModel() {
        networkHelper = new NetworkHelper();
        liveSharedDataArrayList = new MutableLiveData<>();
        getLastHour();
    }

    private void getLastHour() {
        String query = "order=Date,desc&page=1,1&columns=Date&transform=1";
        networkHelper.sendRequest(AVG_HOUR, query, NetworkHelper.GET, parseLastHour, null);
    }


    public void syncMapData() {
        String query = "filter=Date,eq,"+lastHour+"&order=SystemID,asc&include=System,Type&columns=Date,Value,System.Latitude,System.Longitude,Type.Name&transform=1";
        networkHelper.sendRequest(AVG_HOUR, query, NetworkHelper.GET, parseMapData, null);
    }

    JSONParser<JSONObject> parseMapData = (JSONObject response) -> {
        ArrayList<SharedData> sharedDataArrayList = new ArrayList<>();
        try {
            String tableName = response.keys().next();
            JSONArray jsonArray = response.getJSONArray(tableName);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String date = jsonObject.getString("Date");
                Double value = jsonObject.getDouble("Value");
                JSONObject system = jsonObject.getJSONArray(SYSTEM).getJSONObject(0);
                String latitude = system.getString("Latitude");
                String longitude = system.getString("Longitude");
                JSONObject type = jsonObject.getJSONArray(TYPE).getJSONObject(0);
                String typeName = type.getString("Name");
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
            String hour = jsonArray.getJSONObject(0).getString("date");
            lastHour.postValue(hour);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
