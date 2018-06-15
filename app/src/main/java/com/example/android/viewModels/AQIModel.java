package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.network.NetworkHelper;

public class AQIModel extends ViewModel {

    public MutableLiveData<String> label;
    public MutableLiveData<Integer> aqi;

    public MutableLiveData<Integer> getAqi() {
        if(aqi == null) {
            aqi = new MutableLiveData<>();
        }
        return aqi;
    }

    public MutableLiveData<String> getLabel(){
        if(label == null) {
            label = new MutableLiveData<>();
        }
        return label;
    }

    public void loadAQI(){
        NetworkHelper a = new NetworkHelper();
        a.getAQI("aqi.php", getAqi(), getLabel());
    }
}
