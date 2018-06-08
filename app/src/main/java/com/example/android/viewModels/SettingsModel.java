package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.android.models.Settings;
import com.example.android.network.NetworkHelper;

import java.util.ArrayList;
import java.util.List;


public class SettingsModel extends ViewModel {

    private MutableLiveData<Settings> setting;

    private NetworkHelper network = new NetworkHelper();

    public MutableLiveData<Settings> getSetting() {
        if (setting == null){
            setting = new MutableLiveData<>();
        }
        return setting;
    }

    public void loadData() {
        MutableLiveData<Settings> settings = getSetting();
        //TODO
        ArrayList<String> tab = new ArrayList<>();
        tab.add("SDS");
        tab.add("DHT");
        settings.postValue(new Settings(tab,15,"MSF_AP","WEP","efe548r"));
        if (settings.getValue() == null){
            Log.d("Download Data", "Wrong data type : ");
            return;
        }
        //network.downloadData(settings);
    }
}
