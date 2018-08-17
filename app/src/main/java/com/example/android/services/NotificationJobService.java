package com.example.android.services;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.android.activities.R;
import com.example.android.helpers.JSONParser;
import com.example.android.helpers.NotificationHelper;
import com.example.android.models.Address;
import com.example.android.network.NetworkHelper;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import org.json.JSONException;
import org.json.JSONObject;



public class NotificationJobService extends JobService {

    private static final String AQI_NOTIFICATION_TAG = "aqi-notification";

    private static final int NOTIFICATION_PERIOD_SECONDS = 10;
    private static final int NOTIFICATION_WINDOW_SECONDS = 5;

    public static boolean isInitialized = false;
    
    private NetworkHelper networkHelper = new NetworkHelper();

    private String label;
    private int aqi;

    private JSONParser<JSONObject> parseAQI = (JSONObject response) -> {
        try {
            Integer aqiRcv = response.getInt("index");
            String levelRcv = response.getString("level");
            label = levelRcv;
            aqi   = aqiRcv;
            NotificationHelper.createNotificationAQI(this, label, aqi);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    public void getAQi() {
        Address rpiAddress = networkHelper.getNetworkAddressRPI(this);
        SharedPreferences sharedPref = this.getSharedPreferences(
                this.getString(R.string.settings_rpi_file_key),Context.MODE_PRIVATE);
        String systemId = sharedPref.getString("systemID", "-1");
        String path = "aqi.php";
        String query = "id=" + systemId;
        networkHelper.sendRequest(rpiAddress, path, query, NetworkHelper.GET, parseAQI,
                error -> networkHelper.sendRequestRPI(this, path, query,
                        NetworkHelper.GET, parseAQI, null),
                null);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(JobParameters job) {
        getAQi();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    public static void scheduleFirebaseJobDispatcherNotification(Context context) {
        if(!isInitialized) {
            isInitialized = true;
            Driver driver = new GooglePlayDriver(context);
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
            Job NotificationAQIJob = dispatcher.newJobBuilder()
                    .setService(NotificationJobService.class)
                    .setTag(AQI_NOTIFICATION_TAG)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(true)
                    .setTrigger(Trigger.executionWindow(
                            NOTIFICATION_PERIOD_SECONDS,
                            NOTIFICATION_PERIOD_SECONDS + NOTIFICATION_WINDOW_SECONDS))
                    .setReplaceCurrent(true)
                    .build();
            dispatcher.schedule(NotificationAQIJob);
        }
    }


}
