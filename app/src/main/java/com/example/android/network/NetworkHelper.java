package com.example.android.network;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.activities.R;
import com.example.android.models.Address;
import com.example.android.helpers.JSONParser;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
;


public class NetworkHelper implements Request.Method {

    private static final String RPI_ADDRESS_KEY = "raspberryPiAddressIp";
    private static final String RPI_PORT_KEY    = "raspberryPiAddressPort";
    private static final String SERVER_ADDRESS_KEY = "serverAddressIp";
    private static final String SERVER_PORT_KEY    = "serverAddressPort";
    private static final String RPI_ADDRESS_DEFAULT = "192.168.0.";
    private static final int RPI_PORT_DEFAULT    = 80;
    private static final String SERVER_ADDRESS_DEFAULT = "";
    private static final int SERVER_PORT_DEFAULT    = 0;

    public Address getNetworkAddress(Context context, int sharedPreferencesId,
                                     String addressIpKey, String addressIpDefault, String addressPortKey, int addressPortDefault) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(sharedPreferencesId),Context.MODE_PRIVATE);
        Address address = new Address(
                sharedPref.getString(addressIpKey,addressIpDefault),
                sharedPref.getInt(addressPortKey, addressPortDefault));
        return address;
    }

    public Address getNetworkAddressRPI(Context context) {
        return getNetworkAddress(context, R.string.settings_rpi_file_key,
                RPI_ADDRESS_KEY, RPI_ADDRESS_DEFAULT, RPI_PORT_KEY, RPI_PORT_DEFAULT);
    }

    public Address getNetworkAddressServer(Context context) {
        return getNetworkAddress(context, R.string.settings_rpi_file_key,
                SERVER_ADDRESS_KEY, SERVER_ADDRESS_DEFAULT, SERVER_PORT_KEY, SERVER_PORT_DEFAULT);
    }

    public void sendRequest(Address address, String path, String query,
                            int method, JSONParser<JSONObject> f, Response.ErrorListener e, JSONObject dataToSend) {
        String url = buildUrl(address.getIp(), address.getPort(), path, query).toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                method,
                url,
                dataToSend,
                f::apply,
                (e != null) ? e : Throwable::printStackTrace
        );
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
        Log.d(NetworkHelper.class.toString(), url);
    }

    public void sendRequestRPI(Context context, String path, String query,
                               int method, JSONParser<JSONObject> f, JSONObject dataToSend) {
        Address raspberryPiAddress = getNetworkAddressRPI(context);
        sendRequest(raspberryPiAddress, path, query, method, f, null, dataToSend);
    }

    public void sendRequestServer(Context context, String path, String query,
                                  int method, JSONParser<JSONObject> f, JSONObject dataToSend) {
        Address serverAddress = getNetworkAddressServer(context);
        sendRequest(serverAddress, path, query, method, f , null, dataToSend);
    }

    public MutableLiveData<Boolean> checkConnection(Address address) {
        MutableLiveData<Boolean> connection = new MutableLiveData<>();
        String path = "api.php";
        String query = "";
        URL url = buildUrl(address.getIp(), address.getPort(), path, query);
        if(url == null) {
            connection.postValue(false);
        }
        else {
            sendRequest(address, path, query, Request.Method.GET,
                    success -> connection.postValue(true),
                    error -> connection.postValue(false), null);
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
