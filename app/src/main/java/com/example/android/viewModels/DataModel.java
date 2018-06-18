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

    private Realm realm;
    private NetworkHelper network = new NetworkHelper();

    public static final String[] CHART_NAMES = {"pm10", "pm25", "humidity", "temperature"};
    private static final String[] DATA_UNITS = {"µg/m^3", "µg/m^3", "°C", "%"};
    public static final String AVG_HOUR = "AVG_HOUR";
    public static final String AVG_DAY = "AVG_DAY";
    public static final String AVG_MONTH = "AVG_MONTH";
    public static final int[] LINE_COLORS = {0xff00ffff, 0xff00ff00, 0xffff00ff, 0xFFFF4081};

    public List<MutableLiveData<Chart>> chartList = new ArrayList<>();
    public MutableLiveData<String> lastTempValueReceived = new MutableLiveData<>();
    public MutableLiveData<String> lastDatetimeReceived = new MutableLiveData<>();
    private String lastDateUrlParam;

    private SimpleDateFormat ft =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

    public DataModel() {
        realm = Realm.getDefaultInstance();
        syncChartData(AVG_HOUR);
        syncChartData(AVG_DAY);
        syncChartData(AVG_MONTH);
        //Create MutableLiveData<Chart> for each UI chart
        for (int i = 0; i < CHART_NAMES.length; i++) {
            MutableLiveData<Chart> chart = new MutableLiveData<>();
            chart.setValue(new Chart(
                    CHART_NAMES[i],
                    DATA_UNITS[i],
                    AVG_HOUR,
                    new ArrayList<>(),
                    new ArrayList<>())
            );
            chartList.add(chart);
            /*Log.d(DataModel.class.toString(), "add to chartList MutableLiveData<Chart> "
                    + "of type = " + chart.getValue().getName()
                    + " and scale = " + chart.getValue().getScale()
            );*/
        }
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
            network.sendRequest(scale, query, NetworkHelper.GET, storeChartData);
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

    public void loadChartData(MutableLiveData<Chart> chart) {
        ArrayList<Date> xAxis = new ArrayList<>();
        ArrayList<Float> yAxis = new ArrayList<>();
        Chart temp = chart.getValue();
        String scale = temp != null ? temp.getScale() : "";
        String type = temp != null ? temp.getName() : "";
        Log.d("loadChartData", type + ", " + scale);
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
                chart.postValue(new Chart(chart.getValue(), xAxis, yAxis));
            }
        });
    }

    private int getNbOfData(String scale) {
        switch (scale == null ? "" : scale) {
            case "AVG_HOUR":
                return 24;
            case "AVG_DAY":
                return 30;
            case "AVG_MONTH":
                return 12;
        }
        return 0;
    }

    private JSONParser<JSONObject> storeChartData = (JSONObject response) -> {
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
                    for (String type : DataModel.CHART_NAMES) {
                        Float val = (float) jsonObject.getDouble(type);
                        data.setId(HashHelper.generateMD5(dateString + type + scale));
                        data.setDate(date);
                        data.setValue(val);
                        data.setDataType(type);
                        data.setScale(scale);
                        realmDb.copyToRealmOrUpdate(data);
                        Log.d("storeChartData", data.getDataType() + ", "
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

