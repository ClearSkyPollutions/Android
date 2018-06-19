package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.android.helpers.HashHelper;
import com.example.android.models.Chart;
import com.example.android.models.Data;
import com.example.android.network.NetworkHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


public class DataModel extends ViewModel {

    // @TODO : faire un objet Type qui contient ces trois variables
    public ArrayList<String> data_types; // = new ArrayList<>(Arrays.asList("pm10","pm25", ""));
    public ArrayList<String> data_units;     //{"µg/m^3", "µg/m^3", "°C", "%"};
    public ArrayList<Integer> line_colors;   //{0xff00ffff, 0xff00ff00, 0xffff00ff, 0xFFFF4081}

    private Realm realm;
    private NetworkHelper network = new NetworkHelper();

    public static final String AVG_HOUR = "AVG_HOUR";
    public static final String AVG_DAY = "AVG_DAY";
    public static final String AVG_MONTH = "AVG_MONTH";

    public List<MutableLiveData<Chart>> chartList = new ArrayList<>();
    public MutableLiveData<String> lastTempValueReceived = new MutableLiveData<>();
    public MutableLiveData<String> lastDatetimeReceived = new MutableLiveData<>();
    private String lastDateUrlParam;

    private SimpleDateFormat ft =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);


    public DataModel() {
        realm = Realm.getDefaultInstance();
        data_types = new ArrayList<>();
        data_units = new ArrayList<>();
        line_colors = new ArrayList<>();
    }

    public void syncAll() {
        syncChartData(AVG_HOUR);
        syncChartData(AVG_DAY);
        syncChartData(AVG_MONTH);
    }

    public MutableLiveData<Chart> getChart(String type) {

        for (MutableLiveData<Chart> i : chartList) {
            if (i.getValue() != null && i.getValue().getName().equals(type)) {
                return i;
            }
        }
        int index = data_types.indexOf(type);
        if (index != -1) {
            MutableLiveData<Chart> newLiveChart = new MutableLiveData<>();
            Chart newChart = new Chart(data_types.get(index), data_units.get(index), AVG_HOUR);
            newLiveChart.setValue(newChart);
            this.chartList.add(newLiveChart);
            return this.getChart(type);
        }
        return null;
    }

    private void syncChartData(String scale) {
        realm.executeTransactionAsync(realmDb -> {
            Data lastData = realmDb
                    .where(Data.class)
                    .equalTo("scale", scale)
                    .sort("date", Sort.DESCENDING)
                    .findFirst();
            if (lastData != null) {
                lastDateUrlParam = ft.format(lastData.getDate());
            }
        }, () -> {
            String query = "filter=date,gt," + lastDateUrlParam + "&order=date,desc&transform=1";
            network.sendRequest(scale, query, NetworkHelper.GET, parseChartData, null);
            Log.d(DataModel.class.toString(), "sync " + scale + " data");
        });
    }

    public void loadLastData() {
        realm.executeTransactionAsync(realmDb -> {
            Data lastData = realmDb
                    .where(Data.class)
                    .equalTo("scale", AVG_HOUR)
                    .equalTo("dataType", "temperature")
                    .sort("date", Sort.DESCENDING)
                    .findFirst();
            if (lastData != null) {
                lastTempValueReceived.postValue(lastData.getValue().toString());
                lastDatetimeReceived.postValue(ft.format(lastData.getDate()));
                Log.d("loadLastData", lastData.getDataType() + ", "
                        + lastData.getScale() + ", " + lastData.getValue());
            }
        });
    }

    public void loadChartData(String type, String scale) {
        MutableLiveData<Chart> chart = getChart(type);

        ArrayList<Date> xAxis = new ArrayList<>();
        ArrayList<Float> yAxis = new ArrayList<>();

        realm.executeTransactionAsync(realmDb -> {
            RealmResults<Data> dataList = realmDb
                    .where(Data.class)
                    .equalTo("dataType", type)
                    .equalTo("scale", scale)
                    .sort("date", Sort.DESCENDING)
                    .findAll();
            if (!dataList.isEmpty()) {
                int maxSize = getNbOfData(scale);
                int i = dataList.size() > maxSize ? maxSize - 1 : dataList.size() - 1;
                while (i >= 0) {
                    Data data = dataList.get(i);
                    xAxis.add(data.getDate());
                    yAxis.add(data.getValue());
                    i--;
                }
                chart.getValue().setScale(scale);
                chart.postValue(new Chart(chart.getValue(), xAxis, yAxis));
            }
        });
    }

    private int getNbOfData(String scale) {
        switch (scale) {
            case "AVG_HOUR":
                return 24;
            case "AVG_DAY":
                return 30;
            case "AVG_MONTH":
                return 12;
            default:
                return 0;
        }
    }

    public void loadDataTypeUnits() {
        String query = "?columns&filter=date,eq,0";

        network.sendRequest(AVG_HOUR, query, NetworkHelper.GET, parseDataTypes, null);
    }

    private JSONParser<JSONObject> parseDataTypes = (JSONObject response) -> {
        try {
            JSONObject jsonObject = response.getJSONObject(AVG_HOUR);
            JSONArray dataType = jsonObject.getJSONArray("columns");
            for (int i = 0; i <= dataType.length() - 1; i++) {
                if (!(dataType.getString(i).equals("date"))) {
                    data_types.add(dataType.getString(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    // Both parse the JSON received from server and store this data into DB
    private JSONParser<JSONObject> parseChartData = (JSONObject response) -> {
        Realm realm = Realm.getDefaultInstance();
        Data data = new Data();
        realm.executeTransaction(realmDb -> {
            try {
                String scale = response.keys().next();
                JSONArray jsonArray = response.getJSONArray(scale);
                for (int i = jsonArray.length() - 1; i >= 0; i--) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String dateString = jsonObject.getString("date");
                    Date date = ft.parse(dateString);
                    for (String type : data_types) {
                        Float val = (float) jsonObject.getDouble(type);
                        data.setId(HashHelper.generateMD5(dateString + type + scale));
                        data.setDate(date);
                        data.setValue(val);
                        data.setDataType(type);
                        data.setScale(scale);
                        realmDb.copyToRealmOrUpdate(data);
                        Log.d("parseChartData", data.getDataType() + ", "
                                + data.getScale() + ", " + data.getValue());
                    }
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        });
        realm.close();
    };

    @Override
    protected void onCleared() {
        super.onCleared();
        realm.close();
    }

}

