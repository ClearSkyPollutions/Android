package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.android.models.Data;
import com.example.android.network.NetworkHelper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class DataModelNew extends ViewModel {

    public static final String[] DATA_TYPES = {"pm10", "pm25", "temperature", "humidity"};
    public static final String[] DATA_UNITS = {"µg/m^3", "µg/m^3", "°C", "%"};
    public static final int[] LINE_COLORS = {0xff00ffff, 0xff00ff00, 0xffff00ff, 0xFFFF4081};

    private List<MutableLiveData<Data>> measurements = new ArrayList<>();

    private NetworkHelper network = new NetworkHelper();

    public DataModelNew() {
        for (int i = 0; i<DATA_TYPES.length; i++) {
            this.measurements.add(new MutableLiveData<>());
            this.measurements.get(i).setValue(new Data(DATA_TYPES[i], DATA_UNITS[i]));
        }
    }

    public MutableLiveData<Data> getMeasurements(String type){
        for (MutableLiveData<Data> i : measurements) {
            if (i.getValue() != null && i.getValue().name.equals(type)){
                return i;
            }
        }
        return null;
    }

    public void loadData(String type, String scale) {
        MutableLiveData<Data> data = getMeasurements(type);
        if (data.getValue() == null){
            Log.d("Download Data", "Wrong data type : " + type);
            return;
        }

        data.getValue().scale = scale;

        network.downloadData(data);
    }


}

