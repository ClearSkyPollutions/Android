package com.example.android.network;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.activities.R;
import com.example.android.models.Address;
import com.example.android.helpers.JSONParser;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class NetworkHelper implements Request.Method {

    private final int timeoutRequest = 2000;

    public void sendRequestRPI(Context context, String path, String query,
                               int method, JSONParser<JSONObject> f, JSONObject dataToSend) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.settings_rpi_file_key),Context.MODE_PRIVATE);

        Address raspberryPiAddress = new Address(
                sharedPref.getString("raspberryPiAddressIp", "192.168.0."),
                sharedPref.getInt("raspberryPiAddressPort", 80));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                method,
                buildUrl(raspberryPiAddress.getIp(), raspberryPiAddress.getPort(), path, query).toString(),
                dataToSend,
                f::apply,
                Throwable::printStackTrace
        );
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
        Log.d(NetworkHelper.class.toString(), buildUrl(raspberryPiAddress.getIp(),
                raspberryPiAddress.getPort(), path, query).toString());
    }

    public void sendRequestServer(Context context, String path, String query,
                                  int method, JSONParser<JSONObject> f, JSONObject dataToSend) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.settings_rpi_file_key),Context.MODE_PRIVATE);

        Address serverAddress = new Address(
                sharedPref.getString("serverAddressIp",""),
                sharedPref.getInt("serverAddressPort", 0));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                method,
                buildUrl(serverAddress.getIp(), serverAddress.getPort(), path, query).toString(),
                dataToSend,
                f::apply,
                Throwable::printStackTrace
        );
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
        Log.d(NetworkHelper.class.toString(), buildUrl(serverAddress.getIp(), serverAddress.getPort(), path, query).toString());
    }

    public MutableLiveData<Boolean> checkConnection(Address address) {
        MutableLiveData<Boolean> connection = new MutableLiveData<>();
        URL url = buildUrl(address.getIp(), address.getPort(), "api.php", "");
        if(url == null) {
            connection.postValue(false);
        }
        else {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url.toString(),
                     null,
                    success -> connection.postValue(true),
                    error -> connection.postValue(false)
            );
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    timeoutRequest,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
            Log.d(NetworkHelper.class.toString(), buildUrl(address.getIp(), address.getPort(), "api.php", null).toString());
        }
        return connection;
    }

    private URL buildUrl(String ipAddress, int portHTTP, String path, String query) {

        URI uri;
        URL url = null;
        try {
            uri = new URI("http", null, ipAddress, portHTTP,
                    "/" + path, query, null);
            url = uri.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            System.out.println("Wrong URL");
            e.printStackTrace();
        }
        return url;
    }

}
