package com.example.android.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.activities.MainActivity;
import com.example.android.activities.R;
import com.example.android.helpers.JsonReaderHelper;
import com.example.android.models.Sensor;
import com.example.android.models.Settings;
import com.example.android.network.NetworkHelper;
import com.example.android.viewModels.SettingsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SliderFragment extends Fragment {

    //Slider parameters
    private static final String ARG_LAYOUT = "layout";
    private static final String ARG_POSITION = "position";
    private int mLayout;
    private int mPosition;

    // Settings values
    SettingsModel mSettingsModel;
    MutableLiveData<Settings> mSettings;
    private ArrayList<Sensor> listSensors = new ArrayList<>();

    private LocationManager locationManager;
    private LocationListener locationListener;
    private final static int DISTANCE_UPDATES = 5;
    private final static int TIME_UPDATES = 10000;
    public static final int PERMISSION_REQUEST_LOCATION_CODE = 1;

    public SliderFragment() {
    }

    public static SliderFragment newInstance(int layout, int position) {
        SliderFragment fragment = new SliderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT, layout);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mLayout = getArguments().getInt(ARG_LAYOUT);
            mPosition = getArguments().getInt(ARG_POSITION);
        }

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                float accuracy = location.getAccuracy();
                Log.d("GPS", "Lat " + location.getLatitude() + " long " + location.getLongitude());
                if (accuracy < 20) {
                    stopLocation();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create or get the ViewModel for our date
        mSettingsModel = ViewModelProviders.of(getActivity()).get(SettingsModel.class);
        mSettings = mSettingsModel.getSetting();

        View rootView = inflater.inflate(mLayout, container, false);

        //Init views Title/description/image shared by all slides
        initSlides(rootView);

        if (mLayout == R.layout.fragment_slider_sensor) {
            ListView listView = rootView.findViewById(R.id.listSensors);
            listView.setAdapter(setup_list_sensors());
        }

        if (mLayout == R.layout.fragment_slider_rpi) {
            initRPISlide(rootView);
        }

        if (mLayout == R.layout.fragment_slider_web) {
            initLastSlide(rootView);
        }

        return rootView;
    }

    private void initSlides(View rootView) {
        String title = "";
        int imgId = 0;
        StringBuilder txt = new StringBuilder();

        try {
            String json = JsonReaderHelper.loadJSONFromAsset("slides.json", getActivity());
            JSONObject root = new JSONObject(json);
            JSONObject slide = root.getJSONArray("slide").getJSONObject(mPosition);

            title = slide.getString("title");

            JSONArray lines = slide.getJSONArray("lines");
            for (int i = 0; i < lines.length(); i++) {
                txt.append(lines.getString(i));
            }

            imgId = getResources().getIdentifier(
                    slide.getString("image"),
                    "drawable",
                    getActivity().getPackageName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView t = rootView.findViewById(R.id.title);
        t.setText(Html.fromHtml(title));

        t = rootView.findViewById(R.id.body);
        t.setText(Html.fromHtml(txt.toString()));

        if (imgId != 0) {
            ImageView slider_image = rootView.findViewById(R.id.slider_image);
            slider_image.setImageResource(imgId);
        }
    }

    private void initRPISlide(View rootView) {
        TextInputEditText inputIp = rootView.findViewById(R.id.add_ip_input);
        inputIp.setText(mSettings.getValue().getRaspberryPiAddress().getIp());
        inputIp.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String usrInput = ((TextInputEditText) v).getText().toString();

                Settings s = mSettings.getValue();
                s.getRaspberryPiAddress().setIp(usrInput);
                mSettings.setValue(new Settings(s));
            }
        });

        TextInputEditText inputPort = rootView.findViewById(R.id.add_port_input);
        inputPort.setText(Integer.toString(mSettings.getValue().getRaspberryPiAddress().getPort()));
        inputPort.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String usrInput = ((TextInputEditText) v).getText().toString();

                Settings s = mSettings.getValue();
                s.getRaspberryPiAddress().setPort(Integer.parseInt(usrInput));
                mSettings.setValue(new Settings(s));
            }
        });
    }

    private void initLastSlide(View rootView) {
        {
            Switch sw = rootView.findViewById(R.id.share);
            sw.setOnCheckedChangeListener((v, isChecked) ->
            {
                Settings s = mSettings.getValue();
                s.setDataShared(isChecked);
                mSettings.setValue(new Settings(s));
            });

            initInputs(rootView);

            ImageButton confirm = rootView.findViewById(R.id.confirmSensors);
            confirm.setOnClickListener(v -> {

                NetworkHelper netHelper = new NetworkHelper();

                netHelper.checkConnection(mSettings.getValue().getRaspberryPiAddress().getIp(),
                        mSettings.getValue().getRaspberryPiAddress().getPort())
                        .observe(this, connected ->
                        {
                            if (!connected) {
                                Toast.makeText(getContext(),
                                        getString(R.string.toast_could_not_connect_RPI),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                if (mSettings.getValue().isDataShared()) {
                                    activeLocation();
                                } else {
                                    //Save new sharedPreferences and send the config to the RPI
                                    mSettingsModel.setLocalSettings(getContext());
                                    mSettingsModel.sendNewSettings(getContext());

                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                }

                            }
                        });
            });
        }
    }

    // Only manage inputs from last Slide, maybe refactor to make it more generic and
    // initialise inputs for both RPI and WEB address ? (Non trivial)
    private void initInputs(View rootView) {
        TextInputEditText inputIp = rootView.findViewById(R.id.add_ip_input);
        inputIp.setText(mSettings.getValue().getServerAddress().getIp());
        inputIp.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String tmp = ((TextInputEditText) v).getText().toString();

                Settings s = mSettings.getValue();
                s.getServerAddress().setIp(tmp);
                mSettings.setValue(new Settings(s));
            }
        });

        TextInputEditText inputPort = rootView.findViewById(R.id.add_port_input);
        inputPort.setText(Integer.toString(mSettings.getValue().getServerAddress().getPort()));
        inputPort.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String tmp = ((TextInputEditText) v).getText().toString();

                Settings s = mSettings.getValue();
                if (tmp != null)
                    s.getServerAddress().setPort(Integer.parseInt(tmp));
                mSettings.setValue(new Settings(s));
            }
        });
    }

    private ArrayAdapter<Sensor> setup_list_sensors() {
        if (listSensors.isEmpty()) {
            getSensorsData();
        }
        // Create an adapter class extending ArrayAdapter :
        ArrayAdapter<Sensor> adapter = new ArrayAdapter<Sensor>(getActivity(), R.layout.item_adapter_sensor, listSensors) {

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                Sensor sensor = getItem(position);


                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_adapter_sensor_slider, parent, false);
                TextView name = convertView.findViewById(R.id.nameSensor);
                Switch toggle = convertView.findViewById(R.id.switchSensor);

                name.setText(sensor.getName());
                toggle.setChecked(mSettings.getValue().getSensors().contains(sensor.getName()));

                toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    Settings s = mSettings.getValue();
                    if (isChecked) {
                        s.getSensors().add(sensor.getName());
                    } else {
                        s.getSensors().remove(sensor.getName());
                    }
                    mSettings.setValue(new Settings(s));
                });
                return convertView;
            }
        };
        // Attach the adapter to the ListView
        return adapter;
    }

    private void getSensorsData() {
        String json = JsonReaderHelper.loadJSONFromAsset("sensors.json", getActivity());

        try {
            JSONObject obj = new JSONObject(json);
            // Get JSON Array node
            JSONArray sensorsArray = obj.getJSONArray("sensors");

            for (int i = 0; i < sensorsArray.length(); i++) {
                JSONObject jObj = sensorsArray.getJSONObject(i);
                Sensor s = new Sensor(jObj.getString("name"),
                        jObj.getString("desc"),
                        jObj.getString("smalldesc"),
                        getResources().getIdentifier(
                                jObj.getString("image"),
                                "drawable",
                                getActivity().getPackageName()));

                //Add object to ArrayList
                listSensors.add(s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void activeLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_LOCATION_CODE);
            }else {
                checkProvider();
            }
        } else {
            checkProvider();
        }
    }

    @SuppressLint("MissingPermission")
    private void stopLocation() {
        locationManager.removeUpdates(locationListener);
        Location location = locationManager.getLastKnownLocation(
                LocationManager.NETWORK_PROVIDER);

        Location newLocation = randomLocation(location);
        mSettings.setValue(new Settings(mSettings.getValue().getSensors(),
                mSettings.getValue().getFrequency(), mSettings.getValue().getRaspberryPiAddress(),
                mSettings.getValue().getServerAddress(), mSettings.getValue().isDataShared(),
                newLocation));

        Toast.makeText(getActivity(), "Your position are Latitude = "  +
                newLocation.getLatitude() + " Longitude = " +
                newLocation.getLongitude(), Toast.LENGTH_LONG).show();

        mSettingsModel.setLocalSettings(getActivity());
        mSettingsModel.sendNewSettings(getActivity());

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

    private Location randomLocation(Location location) {
        Random rnd = new Random();
        Location newLocation = new Location(location);

        double rndLatitude = -0.01 + rnd.nextFloat() * 0.01 * 2;
        double rndLongitude = -0.01 + rnd.nextFloat() * 0.01 * 2;
        newLocation.setLatitude(location.getLatitude() + rndLatitude);
        newLocation.setLongitude(location.getLongitude() + rndLongitude);

        return newLocation;
    }

    @SuppressLint("MissingPermission")
    private void checkProvider() {
        List<String> providersNames = locationManager.getProviders(true);

        if (providersNames.contains("network")) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    TIME_UPDATES,
                    DISTANCE_UPDATES,
                    locationListener);
            Toast.makeText(getActivity(), R.string.toast_waiting_location,
                    Toast.LENGTH_LONG).show();
        } else {
            showSettingsAlert();
        }
    }

    private void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setTitle(R.string.alert_GPS_settings);

        alertDialog.setMessage(R.string.alert_turn_on_gps);

        alertDialog.setPositiveButton(R.string.alert_button_settings, (dialog, which) -> {
            Intent intent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            getActivity().startActivity(intent);
        });

        alertDialog.setNegativeButton(R.string.alert_button_cancel,
                (dialog, which) -> dialog.cancel());

        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION_CODE:
                activeLocation();
                break;
        }
    }
}

