package com.example.android.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.example.android.activities.R;
import com.example.android.activities.databinding.FragmentSettingsBinding;
import com.example.android.adapters.SensorsItemAdapter;
import com.example.android.customViews.SensorsListView;
import com.example.android.models.Address;
import com.example.android.models.Settings;
import com.example.android.network.NetworkHelper;
import com.example.android.viewModels.SettingsModel;

import java.util.List;
import java.util.Random;

public class SettingsFragment extends Fragment {

    private SettingsModel mSettingsModel;

    private SensorsItemAdapter sensorsItemAdapter;

    private EditText mEditTextSensorAdd;
    private EditText mRaspberryPiAddressIp;
    private EditText mServerAddressIp;
    private EditText mRaspberryPiAddressPort;
    private EditText mServerAddressPort;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private CardView mCardConfirmation;
    private CardView mCoverSettingsFragment;

    private NetworkHelper mNetworkHelper = new NetworkHelper();

    private LocationManager locationManager;
    private LocationListener locationListener;
    private final static int DISTANCE_UPDATES = 5;
    private final static int TIME_UPDATES = 5000;
    public static final int PERMISSION_REQUEST_LOCATION_CODE = 1;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create or get the ViewModel for our date, and bind the xml variable lastData to it (Databinding library)
        mSettingsModel = ViewModelProviders.of(getActivity()).get(SettingsModel.class);

        // Set Data in storage

        mSettingsModel.getLocalSettings(getContext());

        mSettingsModel.refreshSettings.observe(this, updateSettingsValue -> {
            if (!updateSettingsValue) {
                mSwipeRefreshLayout.setRefreshing(updateSettingsValue);
                sensorsItemAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), R.string.toast_data_updated,
                        Toast.LENGTH_SHORT).show();
            }
        });

        Address addressRPI = mSettingsModel.getSetting().getValue().getRaspberryPiAddress();
        mNetworkHelper.checkConnection(addressRPI.getIp(), addressRPI.getPort()).observe(
                this,
                connectionValue -> {
                    if (connectionValue) {
                        mSettingsModel.communication(getContext(), "config.json", Request.Method.GET, null);
                    } else {
                        Toast.makeText(getActivity(), R.string.toast_data_storage,
                                Toast.LENGTH_SHORT).show();
                    }
                });

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                float accuracy = location.getAccuracy();
                Log.d("GPS", "Lat " + location.getLatitude() + " long " + location.getLongitude());
                if (accuracy < 20) {
                    stopLocationTracking();
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
        // Inflate using DataBinding library
        FragmentSettingsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        binding.setLifecycleOwner(this);

        View rootView = binding.getRoot();
        binding.setSettings(mSettingsModel);

        initViews(rootView);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mSettingsModel.refreshSettings.removeObservers(this);
        Address addressRPI = mSettingsModel.getSetting().getValue().getRaspberryPiAddress();
        mNetworkHelper.checkConnection(addressRPI).removeObservers(this);
    }

    private void initPopup(View rootView) {
        // Init CardView

        mCardConfirmation = rootView.findViewById(R.id.confirmation_send_config);
        mCoverSettingsFragment = rootView.findViewById(R.id.cover_settings_fragment);

        mCoverSettingsFragment.setOnClickListener(v -> {
            mCardConfirmation.setVisibility(View.GONE);
            mCoverSettingsFragment.setVisibility(View.GONE);

            mSwipeRefreshLayout.setEnabled(true);

            Toast.makeText(getActivity(), R.string.toast_cancel_configuration,
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void initFrequencyBar(View rootView) {

        SeekBar seekBar = rootView.findViewById(R.id.seekBar);
        TextView frequencyLabel = rootView.findViewById(R.id.labelFrequency);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int progressValues = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                frequencyLabel.setText(progress + " " + getResources().getText(R.string.unitfrequency));
                progressValues = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSettingsModel.getSetting().getValue().setFrequency(progressValues);
            }
        });
    }

    private void initSensorsList(View rootView) {
        ImageButton mButtonAddSensor = rootView.findViewById(R.id.button_add_sensor);
        SensorsListView sensorList = rootView.findViewById(R.id.list_sensors);

        sensorsItemAdapter = new SensorsItemAdapter(getContext(), mSettingsModel.getSetting());
        sensorList.setAdapter(sensorsItemAdapter);

        mButtonAddSensor.setOnClickListener(v -> {
            String newSensor = mEditTextSensorAdd.getText().toString();
            if (!newSensor.equals("")) {
                mSettingsModel.getSetting().getValue().addSensors(newSensor);
                mEditTextSensorAdd.setText("");
                sensorsItemAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), R.string.toast_name_empty,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initInputs(View rootView) {
        mEditTextSensorAdd = rootView.findViewById(R.id.edit_text_add_sensors);
        mRaspberryPiAddressIp = rootView.findViewById(R.id.ip_raspberry_pi);
        mServerAddressIp = rootView.findViewById(R.id.ip_server);
        mRaspberryPiAddressPort = rootView.findViewById(R.id.port_raspberry_pi);
        mServerAddressPort = rootView.findViewById(R.id.port_server);

        // Raspberry Pi Address
        mRaspberryPiAddressIp.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String tmp = ((EditText) v).getText().toString();
                Settings s = mSettingsModel.getSetting().getValue();

                if (tmp.equals("")) {
                    Toast.makeText(getActivity(), R.string.toast_ip_empty,
                            Toast.LENGTH_SHORT).show();
                }
                s.getRaspberryPiAddress().setIp(tmp);
                mSettingsModel.getSetting().setValue(new Settings(s));
            }
        });

        mRaspberryPiAddressPort.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String tmp = ((EditText) v).getText().toString();
                Settings s = mSettingsModel.getSetting().getValue();

                if (!tmp.equals("")) {
                    s.getRaspberryPiAddress().setPort(Integer.parseInt(tmp));
                    mSettingsModel.getSetting().setValue(new Settings(s));
                } else {
                    s.getRaspberryPiAddress().setPort(null);
                    mSettingsModel.getSetting().setValue(new Settings(s));
                    Toast.makeText(getActivity(), R.string.toast_port_empty,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Server Address
        mServerAddressIp.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String tmp = ((EditText) v).getText().toString();
                Settings s = mSettingsModel.getSetting().getValue();

                if (tmp.equals("")) {
                    Toast.makeText(getActivity(), R.string.toast_ip_empty,
                            Toast.LENGTH_SHORT).show();
                }
                s.getServerAddress().setIp(tmp);
                mSettingsModel.getSetting().setValue(new Settings(s));
            }
        });

        mServerAddressPort.setOnFocusChangeListener((v, hasFocus) -> {
            String tmp = ((EditText) v).getText().toString();
            Settings s = mSettingsModel.getSetting().getValue();

            if (!tmp.equals("")) {
                s.getServerAddress().setPort(Integer.parseInt(tmp));
                mSettingsModel.getSetting().setValue(new Settings(s));
            } else {
                s.getServerAddress().setPort(null);
                mSettingsModel.getSetting().setValue(new Settings(s));
                Toast.makeText(getActivity(), R.string.toast_port_empty,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initConfirmDialog(View rootView) {
        initPopup(rootView);

        Button validate = rootView.findViewById(R.id.validate);
        Button accept = rootView.findViewById(R.id.accept_confirmation);
        Button cancel = rootView.findViewById(R.id.cancel_confirmation);

        // Setup Button
        validate.setOnClickListener(v -> {
            mRaspberryPiAddressIp.clearFocus();
            mRaspberryPiAddressPort.clearFocus();
            mServerAddressIp.clearFocus();
            mServerAddressPort.clearFocus();

            Address addressRPI = mSettingsModel.getSetting().getValue().getRaspberryPiAddress();

            if (mSettingsModel.checkRPiAddr()) {
                mNetworkHelper.checkConnection(addressRPI).observe(
                        this,
                        connectionValue -> {
                            if (connectionValue) {
                                mCoverSettingsFragment.setVisibility(View.VISIBLE);
                                mCardConfirmation.setVisibility(View.VISIBLE);
                                mSwipeRefreshLayout.setEnabled(false);
                            } else {
                                Toast.makeText(getActivity(), R.string.toast_could_not_connect_RPI,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Popup
        accept.setOnClickListener(v -> {
            mSettingsModel.setLocalSettings(getContext());
            mSettingsModel.sendNewSettings(getContext());

            mCoverSettingsFragment.setVisibility(View.GONE);
            mCardConfirmation.setVisibility(View.GONE);
            mSwipeRefreshLayout.setEnabled(true);
            Toast.makeText(getActivity(), R.string.toast_configuration_changed,
                    Toast.LENGTH_SHORT).show();
        });

        cancel.setOnClickListener(v -> {
            mCoverSettingsFragment.setVisibility(View.GONE);
            mCardConfirmation.setVisibility(View.GONE);
            mSwipeRefreshLayout.setEnabled(true);
            Toast.makeText(getActivity(), R.string.toast_cancel_configuration,
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void initSwitch(View rootView) {
        Switch switchShareData = rootView.findViewById(R.id.switch_data_shared);
        ImageButton imageButtonPosition = rootView.findViewById(R.id.button_position_gps);

        switchShareData.setOnCheckedChangeListener((buttonView, isChecked) ->
                mSettingsModel.getSetting().getValue().setDataShared(isChecked));

        imageButtonPosition.setOnClickListener(v -> {
            if (mSettingsModel.getSetting().getValue().isDataShared()) {
                startLocationTracking();
            } else {
                Toast.makeText(getActivity(), R.string.toast_ask_sharing,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initSwipeRefresh(View rootView) {
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshSettingsFragment);

        // Recover data from the file config.json in Raspberry Pi
        mSwipeRefreshLayout.setOnRefreshListener(this::refreshSettings);
    }

    private void initViews(View rootView) {
        initFrequencyBar(rootView);
        initSensorsList(rootView);
        initInputs(rootView);
        initConfirmDialog(rootView);
        initSwitch(rootView);
        initSwipeRefresh(rootView);
    }


    public void startLocationTracking() {
        if (ActivityCompat
                .checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION_CODE);
        } else {
            checkProvider();
        }
    }

    @SuppressLint("MissingPermission")
    private void stopLocationTracking() {
        locationManager.removeUpdates(locationListener);
        Location location = locationManager.getLastKnownLocation(
                LocationManager.NETWORK_PROVIDER);

        Location newLocation = randomizeLocation(location);
        Settings settings = mSettingsModel.getSetting().getValue();
        settings.setPositionSensor(newLocation);
        mSettingsModel.getSetting().setValue(new Settings(settings));

        Toast.makeText(getActivity(), R.string.toast_position_acquired, Toast.LENGTH_LONG).show();
    }

    private Location randomizeLocation(Location location) {
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
                startLocationTracking();
                break;
        }
    }
}