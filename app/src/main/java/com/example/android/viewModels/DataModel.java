package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import com.example.android.models.Data;
import com.example.android.models.Graph;
import com.example.android.network.NetworkHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


public class DataModel extends ViewModel{

    private static Realm realm;
    private NetworkHelper network = new NetworkHelper();

    public static final String[] GRAPH_NAMES = {"pm10", "pm25", "humidity", "temperature"};
    private static final String[] DATA_UNITS = {"µg/m^3", "µg/m^3", "°C", "%"};
    public static final String AVG_HOUR = "AVG_HOUR";
    public static final String AVG_DAY = "AVG_DAY";
    public static final String AVG_MONTH = "AVG_MONTH";
    public static final int[] LINE_COLORS = {0xff00ffff, 0xff00ff00, 0xffff00ff, 0xFFFF4081};

    public List<MutableLiveData<Graph>> graphList = new ArrayList<>();
    public MutableLiveData<String> lastTempValueReceived = new MutableLiveData<>();
    public MutableLiveData<String> lastDatetimeReceived = new MutableLiveData<>();
    private String lastDateUrlParam;

    public DataModel() {
        realm = Realm.getDefaultInstance();
        syncGraphData(AVG_HOUR);
        syncGraphData(AVG_DAY);
        syncGraphData(AVG_MONTH);
        //Create MutableLiveData<Graph> for each UI chart
        for (int i = 0; i< GRAPH_NAMES.length; i++) {
            MutableLiveData<Graph> graph = new MutableLiveData<>();
            graph.setValue(new Graph(
                    GRAPH_NAMES[i],
                    DATA_UNITS[i],
                    AVG_HOUR,
                    new ArrayList<>(),
                    new ArrayList<>())
            );
            graphList.add(graph);
            Log.d(DataModel.class.toString(), "add to graphList MutableLiveData<Graph> "
                    + "of type = " + graph.getValue().getName()
                    + " and scale = " + graph.getValue().getScale()
            );
        }
    }

    public void syncGraphData(String scale) {
        realm.executeTransactionAsync( realmDb -> {
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Data lastData = realmDb
                    .where(Data.class)
                    .equalTo("scale", scale)
                    .sort("date",Sort.DESCENDING)
                    .findFirst();
            if(lastData != null) {
                lastDateUrlParam = ft.format(lastData.getDate());
            }
        }, () -> {
            String query = "filter=date,gt,"+lastDateUrlParam+"&order=date,desc&transform=1";
            network.sendRequest(scale, query, NetworkHelper.GET, NetworkHelper.STORE_GRAPH_DATA);
            Log.d(DataModel.class.toString(), "sync "+scale+" data");
        });
    }

    public void loadLastData() {
        realm.executeTransactionAsync( realmDb -> {
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Data lastData = realmDb
                    .where(Data.class)
                    .equalTo("scale", AVG_HOUR)
                    .equalTo("dataType", "temperature")
                    .sort("date",Sort.DESCENDING)
                    .findFirst();
            if(lastData != null) {
                lastTempValueReceived.postValue(lastData.getValue().toString());
                lastDatetimeReceived.postValue(ft.format(lastData.getDate()));
                Log.d("loadLastData", lastData.getDataType()+", " + lastData.getScale() + ", "+ lastData.getValue());
            }
        });
    }

    public void loadGraphData(MutableLiveData<Graph> graph) {
        ArrayList<Date> xAxis = new ArrayList<>();
        ArrayList<Float> yAxis = new ArrayList<>();
        Graph  temp = graph.getValue();
        String scale = temp != null ? temp.getScale() : "";
        String type = temp != null ? temp.getName() : "";
        Log.d("loadGraphData", type + ", " + scale);
        realm.executeTransactionAsync( realmDb -> {
                RealmResults<Data> dataList = realmDb
                        .where(Data.class)
                        .equalTo("dataType",type)
                        .equalTo("scale",scale)
                        .sort("date",Sort.DESCENDING)
                        .findAll();
                Log.d(DataModel.class.toString(), "loadGraphData : read all "+ type+ " data for "+scale+" table, size = " + dataList.size());
                if(!dataList.isEmpty()) {
                    int maxSize = getNbOfData(scale);
                    int i =  dataList.size() > maxSize ? maxSize - 1 : dataList.size() - 1;
                    while (i >= 0) {
                        Data data = dataList.get(i);
                        xAxis.add(data.getDate());
                        yAxis.add(data.getValue());
                        Log.d("loadGraphData", data.getDataType() + ", " + data.getScale() +", "+ data.getDate().toString() + ", " + data.getValue());
                        i--;
                    }
                    graph.postValue(new Graph(graph.getValue(), xAxis, yAxis));
                }
        });
    }


    private int getNbOfData(String scale){
        switch(scale == null ? "" : scale){
            case "AVG_HOUR":
                return 24;
            case "AVG_DAY":
                return 30;
            case "AVG_MONTH":
                return 12;
        }
        return 0;
    }



    @Override
    protected void onCleared() {
        super.onCleared();
        realm.close();
    }

}

