package com.example.android.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
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
import com.example.android.listview.SensorsListView;
import com.example.android.models.Address;
import com.example.android.models.Settings;
import com.example.android.network.NetworkHelper;
import com.example.android.viewModels.SettingsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingsFragment extends Fragment {

    private SettingsModel mSettingsModel;

    private SensorsItemAdapter sensorsItemAdapter;

    private TextView mFrequencyLabel;
    private EditText mEditTextSensorAdd;
    private EditText mRaspberryPiAddressIp;
    private EditText mServerAddressIp;
    private EditText mRaspberryPiAddressPort;
    private EditText mServerAddressPort;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private CardView mCardConfirmation;
    private CardView mCoverSettingsFragment;

    private NetworkHelper mNetworkHelper = new NetworkHelper();
    private SharedPreferences mPrefSettings;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create or get the ViewModel for our date, and bind the xml variable lastData to it (Databinding library)
        mSettingsModel = ViewModelProviders.of(getActivity()).get(SettingsModel.class);

        // Set Data in storage
        mPrefSettings = getActivity().getSharedPreferences(getString(R.string.settings_rpi_file_key), Context.MODE_PRIVATE);
        mSettingsModel.fetchPrefsSettings(mPrefSettings);

        Address addressRPI = mSettingsModel.getSetting().getValue().getRaspberryPiAddress();

        mNetworkHelper.checkConnection(addressRPI.getIp(), addressRPI.getPort()).observe(
                this,
                connectionValue -> {
                    if (connectionValue) {
                        mSettingsModel.communication("config.json", Request.Method.GET, null);
                        Toast.makeText(getActivity(), R.string.toast_data_updated,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), R.string.toast_data_storage,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate using DataBinding library
        FragmentSettingsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        binding.setLifecycleOwner(this);
        View rootView = binding.getRoot();

        binding.setSettings(mSettingsModel);

        // Init views
        initViews(rootView);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mSettingsModel.refreshSettings.removeObservers(this);
        Address addressRPI = mSettingsModel.getSetting().getValue().getRaspberryPiAddress();
        mNetworkHelper.checkConnection(addressRPI.getIp(), addressRPI.getPort()).removeObservers(this);
    }

    private void initCardView(View rootView) {
        // Init CardView
        mCardConfirmation = rootView.findViewById(R.id.confirmation_send_config);
        mCoverSettingsFragment = rootView.findViewById(R.id.cover_settings_fragment);

        mCoverSettingsFragment.setOnClickListener(v -> {
            mCardConfirmation.setVisibility(View.GONE);
            mCoverSettingsFragment.setVisibility(View.GONE);

            mSwipeRefreshLayout.setEnabled(true);

            Toast.makeText(getActivity(), R.string.toast_cancel_configuration,
                    Toast.LENGTH_LONG).show();
        });
    }

    private void initSeekBar(View rootView) {
        // Init SeekBar View in XML
        SeekBar seekBar = rootView.findViewById(R.id.seekBar);

        // Setup SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int progressValues = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mFrequencyLabel.setText(progress + " " + getResources().getText(R.string.unitfrequency));
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

    private void initSensorsItemAdapterListView(View rootView) {
        // Init ItemAdapter
        sensorsItemAdapter = new SensorsItemAdapter(getContext(), mSettingsModel.getSetting());

        // Init ListView
        SensorsListView sensorList = rootView.findViewById(R.id.list_sensors);
        sensorList.setAdapter(sensorsItemAdapter);
    }

    private void initTextView(View rootView) {
        // Init Text
        mFrequencyLabel = rootView.findViewById(R.id.labelFrequency);
    }

    private void initEditText(View rootView) {
        // Init EditText
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

                if (tmp.equals("")){
                    Toast.makeText(getActivity(), R.string.toast_ip_empty,
                            Toast.LENGTH_LONG).show();
                }
                s.getRaspberryPiAddress().setIp(tmp);
                mSettingsModel.getSetting().setValue(new Settings(s));
            }
        });

        mRaspberryPiAddressPort.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String tmp = ((EditText) v).getText().toString();
                Settings s = mSettingsModel.getSetting().getValue();

                if (!tmp.equals("")){
                    s.getRaspberryPiAddress().setPort(Integer.parseInt(tmp));
                    mSettingsModel.getSetting().setValue(new Settings(s));
                } else {
                    s.getRaspberryPiAddress().setPort(null);
                    mSettingsModel.getSetting().setValue(new Settings(s));
                    Toast.makeText(getActivity(), R.string.toast_port_empty,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        // Server Address
        mServerAddressIp.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String tmp = ((EditText) v).getText().toString();
                Settings s = mSettingsModel.getSetting().getValue();

                if (tmp.equals("")){
                    Toast.makeText(getActivity(), R.string.toast_ip_empty,
                            Toast.LENGTH_LONG).show();
                }
                s.getServerAddress().setIp(tmp);
                mSettingsModel.getSetting().setValue(new Settings(s));
            }
        });

        mServerAddressPort.setOnFocusChangeListener((v, hasFocus) -> {
            String tmp = ((EditText) v).getText().toString();
            Settings s = mSettingsModel.getSetting().getValue();

            if (!tmp.equals("")){
                s.getServerAddress().setPort(Integer.parseInt(tmp));
                mSettingsModel.getSetting().setValue(new Settings(s));
            } else {
                s.getServerAddress().setPort(null);
                mSettingsModel.getSetting().setValue(new Settings(s));
                Toast.makeText(getActivity(), R.string.toast_port_empty,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initButton(View rootView) {
        // Init Button
        Button validate = rootView.findViewById(R.id.validate);
        Button accept = rootView.findViewById(R.id.accept_confirmation);
        Button cancel = rootView.findViewById(R.id.cancel_confirmation);

        // Init ImageButton
        ImageButton mButtonAddSensor = rootView.findViewById(R.id.button_add_sensor);

        // Setup Button
        validate.setOnClickListener(v -> {
            mRaspberryPiAddressIp.clearFocus();
            mRaspberryPiAddressPort.clearFocus();
            mServerAddressIp.clearFocus();
            mServerAddressPort.clearFocus();

            Address addressRPI = mSettingsModel.getSetting().getValue().getRaspberryPiAddress();

            if (mSettingsModel.checkInput()) {
                mNetworkHelper.checkConnection(addressRPI.getIp(), addressRPI.getPort()).observe(
                        this,
                        connectionValue -> {
                            if (connectionValue) {
                                Toast.makeText(getActivity(), R.string.toast_connection_successful_RPI,
                                        Toast.LENGTH_LONG).show();
                                mCoverSettingsFragment.setVisibility(View.VISIBLE);
                                mCardConfirmation.setVisibility(View.VISIBLE);
                                mSwipeRefreshLayout.setEnabled(false);
                            } else {
                                Toast.makeText(getActivity(), R.string.toast_could_not_connect_RPI,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        accept.setOnClickListener(v -> {
            mSettingsModel.storeSettings(mPrefSettings);

            Settings settings = mSettingsModel.getSetting().getValue();

            JSONArray sensorsJson = new JSONArray(settings.getSensors());
            JSONObject serverAddressJson = new JSONObject();

            JSONObject jsonSend = new JSONObject();
            try {
                serverAddressJson.put("ip", settings.getServerAddress().getIp());
                serverAddressJson.put("port", settings.getServerAddress().getPort());

                jsonSend.put("sensors", sensorsJson);
                jsonSend.put("frequency", settings.getFrequency());
                jsonSend.put("serverAddress", serverAddressJson);
                jsonSend.put("isDataShared", settings.isDataShared());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("debug", jsonSend.toString());
            mSettingsModel.communication("config.php", Request.Method.PUT, jsonSend);
            mCoverSettingsFragment.setVisibility(View.GONE);
            mCardConfirmation.setVisibility(View.GONE);
            mSwipeRefreshLayout.setEnabled(true);
        });

        cancel.setOnClickListener(v -> {
            mCoverSettingsFragment.setVisibility(View.GONE);
            mCardConfirmation.setVisibility(View.GONE);
            mSwipeRefreshLayout.setEnabled(true);
            Toast.makeText(getActivity(), R.string.toast_cancel_configuration,
                    Toast.LENGTH_LONG).show();
        });

        // Setup ImageButton
        mButtonAddSensor.setOnClickListener(v -> {
            String newSensor = mEditTextSensorAdd.getText().toString();
            if (!newSensor.equals("")) {
                mSettingsModel.getSetting().getValue().addSeqnsors(newSensor);
                mEditTextSensorAdd.setText("");
                sensorsItemAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), R.string.toast_name_empty,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initSwitch(View rootView) {
        // Init Switch
        Switch switchShareData = rootView.findViewById(R.id.switch_data_shared);

        // Setup Switch
        switchShareData.setOnCheckedChangeListener((buttonView, isChecked) ->
                mSettingsModel.getSetting().getValue().setDataShared(isChecked));
    }

    private void initSwipeRefresh(View rootView) {
        // Init SwipeRefreshLayout
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshSettingsFragment);

        // Setup
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            // Recover data to file config.json in Raspberry Pi
            mSettingsModel.communication("config.json", Request.Method.GET, null);
            mSettingsModel.refreshSettings.observe(this, updateSettingsValue -> {
                mSwipeRefreshLayout.setRefreshing(updateSettingsValue);
                sensorsItemAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), R.string.toast_data_updated,
                        Toast.LENGTH_LONG).show();
            });
        });
    }

    private void initViews(View rootView) {
        initCardView(rootView);
        initSeekBar(rootView);
        initSensorsItemAdapterListView(rootView);
        initTextView(rootView);
        initEditText(rootView);
        initButton(rootView);
        initSwitch(rootView);
        initSwipeRefresh(rootView);
    }
}