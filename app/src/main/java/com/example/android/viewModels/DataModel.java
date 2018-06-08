package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.models.DataType;
import com.example.android.models.Graph;
import com.example.android.models.Scale;
import com.example.android.network.NetworkHelper;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;


public class DataModel extends ViewModel {

    private Realm realm;
    private NetworkHelper network = new NetworkHelper();


    public static final String[] GRAPH_NAMES = {"pm10", "pm25", "temperature", "humidity"};
    public static final String[] DATA_UNITS = {"µg/m^3", "µg/m^3", "°C", "%"};
    public static final String AVG_HOUR = "AVG_HOUR";
    public static final String AVG_DAY = "AVG_DAY";
    public static final String AVG_MONTH = "AVG_MONTH";
    public static final int[] LINE_COLORS = {0xff00ffff, 0xff00ff00, 0xffff00ff, 0xFFFF4081};

    public List<MutableLiveData<Graph>> graphList = new ArrayList<>();
    public MutableLiveData<List<Float>> lastDataReceived = new MutableLiveData<>();
    public MutableLiveData<String> lastTempValueReceived = new MutableLiveData<>();
    public MutableLiveData<String> lastDatetimeReceived = new MutableLiveData<>();

    public DataModel() {
        realm = Realm.getDefaultInstance();
        //Store each dataType in database and create liveData graph for each dataType
        for (int i = 0; i< GRAPH_NAMES.length; i++) {
            String name = GRAPH_NAMES[i];
            String unit = DATA_UNITS[i];
            realm.executeTransactionAsync(realm -> {
                DataType dataType =  new DataType();
                dataType.setName(name);
                dataType.setUnit(unit);
                realm.copyToRealmOrUpdate(dataType);
            });
            MutableLiveData<Graph> graph = new MutableLiveData<>();
            graph.setValue(new Graph(name, unit, AVG_HOUR));
            this.graphList.add(graph);
        }
        //Store scale object in database
        realm.executeTransactionAsync(realm -> {
            Scale scale = new Scale();
            scale.setName(AVG_HOUR);
            realm.copyToRealmOrUpdate(scale);
        });
        realm.executeTransactionAsync(realm -> {
            Scale scale = new Scale();
            scale.setName(AVG_DAY);
            realm.copyToRealmOrUpdate(scale);
        });
        realm.executeTransactionAsync(realm -> {
            Scale scale = new Scale();
            scale.setName(AVG_MONTH);
            realm.copyToRealmOrUpdate(scale);
        });
    }

    public void loadGraphData(MutableLiveData<Graph> graph) {
    }

    public void loadLastData() {
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        realm.close();
    }
}

