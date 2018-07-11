package com.example.android.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Color;

import com.example.android.activities.BuildConfig;
import com.example.android.helpers.ChartHelper;
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
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class DataModel extends ViewModel {

    private Realm realm;
    private NetworkHelper network = new NetworkHelper();

    public static final String AVG_HOUR = "AVG_HOUR";
    public static final String AVG_DAY = "AVG_DAY";
    public static final String AVG_MONTH = "AVG_MONTH";

    public List<MutableLiveData<Chart>> chartList = new ArrayList<>();
    public MutableLiveData<String> lastTempValueReceived = new MutableLiveData<>();
    public MutableLiveData<String> lastDatetimeReceived = new MutableLiveData<>();
    public MutableLiveData<Boolean> refresh = new MutableLiveData<>();
    public MutableLiveData<Boolean> updateChartList = new MutableLiveData<>();
    private String lastDateUrlParam;
    private static final String POLLUTANT = "POLLUTANT";
    private static final String DATE = "date";
    private static final String VALUE = "value";
    private static final String POLLUTANT_NAME = "name";

    public DataModel() {
        realm = Realm.getDefaultInstance();
        loadDataTypeUnits();
        /*
        charts.add(new Chart("pm10", getUnit("pm10"), getColor("pm10"), AVG_HOUR));
        charts.add(new Chart("pm25", getUnit("pm25"), getColor("pm25"), AVG_HOUR));
        charts.add(new Chart("temperature", getUnit("temperature"), getColor("temperature"), AVG_HOUR));
        charts.add(new Chart("humidity", getUnit("humidity"), getColor("humidity"), AVG_HOUR));
        */
    }

    public void loadDataTypeUnits() {
        String query = "transform=1&order=id";
        network.sendRequest(BuildConfig.IPADDR_RPI, BuildConfig.PortHTTP_RPI, POLLUTANT, query, NetworkHelper.GET, parseDataTypes, null);
    }

    private JSONParser<JSONObject> parseDataTypes = (JSONObject response) -> {
        ArrayList<Chart> charts = new ArrayList<>();
        Random rnd = new Random();
        //TODO ne pas ajoute deux fois le même élement
        try {
            JSONArray jsonArray = response.getJSONArray(POLLUTANT);
            for (int i = 0; i <= jsonArray.length() - 1; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Chart chartReceive = new Chart(jsonObject.getString("name"),
                        jsonObject.getString("unit"),
                        Color.argb(255,
                                rnd.nextInt(256),
                                rnd.nextInt(256),
                                rnd.nextInt(256)), AVG_HOUR);
                charts.add(chartReceive);
            }
            for(Chart chart : charts) {
                MutableLiveData<Chart> liveChart = new MutableLiveData<>();

                liveChart.setValue(chart);
                chartList.add(liveChart);
            }
            updateChartList.postValue(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    public void syncAll() {
        syncChartData(AVG_HOUR);
        syncChartData(AVG_DAY);
        syncChartData(AVG_MONTH);
    }

    private void syncChartData(String scale) {
        realm.executeTransaction(realmDb -> {
            Data lastData = realmDb
                    .where(Data.class)
                    .equalTo("scale", scale)
                    .sort("date", Sort.DESCENDING)
                    .findFirst();
            if (lastData != null) {
                lastDateUrlParam = ChartHelper.getStringDate(lastData.getDate(),"");
            }
            String query = "filter="+DATE+",gt," + lastDateUrlParam + "&order="+DATE+",desc&include="+POLLUTANT+"&transform=1";
            network.sendRequest(BuildConfig.IPADDR_RPI, BuildConfig.PortHTTP_RPI, scale, query, NetworkHelper.GET, parseChartData, null);
            //Log.d(DataModel.class.toString(), "sync " + scale + " data");
        });
    }

    // Both parse the JSON received from server and store this data into DB
    private JSONParser<JSONObject> parseChartData = (JSONObject response) -> {
        Realm realm = Realm.getDefaultInstance();
        Data data = new Data();
        realm.executeTransactionAsync(realmDb -> {
            try {
                String scale = response.keys().next();
                JSONArray jsonArray = response.getJSONArray(scale);
                for (int i = jsonArray.length() - 1; i >= 0; i--) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String dateString = jsonObject.getString(DATE);
                    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date date = ft.parse(dateString);
                    JSONArray pollutant = jsonObject.getJSONArray(POLLUTANT);
                    String type = pollutant.getJSONObject(0).getString(POLLUTANT_NAME);
                    Float val = (float) jsonObject.getDouble(VALUE);
                    data.setId(HashHelper.generateMD5(dateString + type + scale));
                    data.setDate(date);
                    data.setValue(val);
                    data.setDataType(type);
                    data.setScale(scale);
                    realmDb.copyToRealmOrUpdate(data);
                    /*Log.d("parseChartData", data.getDataType() + ", "
                            + data.getScale() + ", " + data.getValue());*/
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }, () -> {
            for (int position = 0; position < chartList.size(); position++){
                loadChartData(position, AVG_HOUR);
                loadLastData();
            }
        });
        realm.close();
    };

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
                lastDatetimeReceived.postValue(ChartHelper.getStringDate(lastData.getDate(),"CardCities"));
                /*Log.d("loadLastData", lastData.getDataType() + ", "
                        + lastData.getScale() + ", " + lastData.getValue());*/
            }
        });
    }

    public void loadChartData(int position, String scale) {
        MutableLiveData<Chart> liveChart = chartList.get(position);
        ArrayList<Date> xAxis = new ArrayList<>();
        ArrayList<Float> yAxis = new ArrayList<>();
        //Log.d(DataModel.class.toString(), "loadChartData");
        realm.executeTransactionAsync(realmDb -> {
            RealmResults<Data> dataList = realmDb
                    .where(Data.class)
                    .equalTo("dataType", liveChart.getValue().getType())
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
                    /*Log.d("loadChartData", data.getDataType() + ", "
                            + data.getScale() + ", " + data.getValue());*/
                    i--;
                }
                liveChart.getValue().setScale(scale);
                liveChart.postValue(new Chart(liveChart.getValue(), xAxis, yAxis));
            }
        }, () -> {
            refresh.postValue(false);
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

    @Override
    protected void onCleared() {
        super.onCleared();
        realm.close();
    }

}

