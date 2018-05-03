package com.example.android.models;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class DataModel extends ViewModel {

    private MutableLiveData<Data> measurementLive;

    public MutableLiveData<Data> getmeasurementLive() {
        if (measurementLive == null) {
            measurementLive = new MutableLiveData<>();

        }
        return measurementLive;
    }
}
