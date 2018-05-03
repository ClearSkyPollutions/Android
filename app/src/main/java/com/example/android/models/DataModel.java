package com.example.android.models;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class DataModel extends ViewModel {
    private MutableLiveData<DataPM> measurementLive;

    public MutableLiveData<DataPM> getmeasurementLive() {
        if (measurementLive == null) {
            measurementLive = new MutableLiveData<DataPM>();
        }
        return measurementLive;
    }
}
