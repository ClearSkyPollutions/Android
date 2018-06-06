package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.models.Graph;
import com.example.android.models.Measure;
import com.example.android.network.NetworkHelper;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;


public class DataModel extends ViewModel {

    private Realm mDb;
    private NetworkHelper network = new NetworkHelper();


    public static final String[] GRAPH_NAMES = {"pm10", "pm25", "temperature", "humidity"};
    public static final String[] DATA_UNITS = {"µg/m^3", "µg/m^3", "°C", "%"};
    public static final String AVG_HOUR = "AVG_HOUR";
    public static final String AVG_DAY = "AVG_DAY";
    public static final String AVG_MONTH = "AVG_MONTH";
    public static final int[] LINE_COLORS = {0xff00ffff, 0xff00ff00, 0xffff00ff, 0xFFFF4081};

    public List<MutableLiveData<Graph>> graphList = new ArrayList<>();
    public MutableLiveData<List<Float>> lastMeasuresReceived;

    public DataModel() {
        mDb = Realm.getDefaultInstance();
        for (int i = 0; i< GRAPH_NAMES.length; i++) {
            MutableLiveData<Graph> graph = new MutableLiveData<>();
            graph.setValue(new Graph(GRAPH_NAMES[i], DATA_UNITS[i], AVG_HOUR));
            this.graphList.add(graph);
        }
    }

    public void loadGraphData(MutableLiveData<Graph> graph) {
        network.downloadGraphData(graph);
    }

    public void loadLastData(MutableLiveData<List<Float>> lastMeasureReceived) {
        network.downloadLastData(lastMeasureReceived);
    }

    public MutableLiveData<Graph> getGraphByName(String name) {

        if (!graphList.isEmpty()) {
            for (MutableLiveData<Graph> graph : graphList) {
                if(graph.getValue().getName() == name) {
                    return graph;
                }
            }
        }
        return null;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        mDb.close();
    }
}

