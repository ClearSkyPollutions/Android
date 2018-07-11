package com.example.android.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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
import com.example.android.activities.BuildConfig;
import com.example.android.activities.R;
import com.example.android.activities.databinding.FragmentSettingsBinding;
import com.example.android.adapters.SensorsItemAdapter;
import com.example.android.listview.SensorsListView;
import com.example.android.models.Address;
import com.example.android.models.Settings;
import com.example.android.viewModels.SettingsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class SettingsFragment extends Fragment {

    private SettingsModel mSettingsModel;

    //
    private static final String MY_PREFS_NAME = "MySettingsPref";

    private SensorsItemAdapter sensorsItemAdapter;

    private TextView mFrequencyLabel;
    private EditText mEditTextSensorAdd;
    private EditText mRaspberryPiAddressIp;
    private EditText mServerAddressIp;
    private EditText mRaspberryPiAddressPort;
    private EditText mServerAddressPort;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private CardView mCardConfirmation;
    private CardView mCoverSettingsFragment;;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate using DataBinding library
        FragmentSettingsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        binding.setLifecycleOwner(this);
        View rootView = binding.getRoot();

        // Create or get the ViewModel for our date, and bind the xml variable lastData to it (Databinding library)
        mSettingsModel = ViewModelProviders.of(getActivity()).get(SettingsModel.class);

        binding.setSettings(mSettingsModel);


        // Set Data in storage
        SharedPreferences sharedPref = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        ArrayList<String> sensors = new ArrayList<>(sharedPref.getStringSet("sensors", new HashSet<>()));

        int frequency = sharedPref.getInt("frequency", 15);
        Address raspberryPiAddress = new Address(
                sharedPref.getString("raspberryPiAddressIp", "192.168.0."),
                sharedPref.getString("raspberryPiAddressPort", "80"));
        Address serverAddress = new Address(
                sharedPref.getString("serverAddressIp", BuildConfig.IPADDR_SERVER),
                sharedPref.getString("serverAddressPort", ""+BuildConfig.PortHTTP_SERVER));
        boolean isDataShared = sharedPref.getBoolean("isDataShared", false);


        mSettingsModel.getSetting().setValue(new Settings(sensors, frequency,
                raspberryPiAddress, serverAddress, isDataShared));

        // Init views
        initViews(rootView);

        /*
        // Recover data to file config.json in Raspberry Pi
        mSettingsModel.communication("config.json", Request.Method.GET,null);
        */

        /*
        mSettingsModel.refreshSettings.observe(this,updateSensorsValue -> {
            if (updateSensorsValue){
                // Init ItemAdapter
                sensorsItemAdapter = new SensorsItemAdapter(getContext(), mSettingsModel.getSetting().getValue().getSensors());

                // Init ListView
                mSensorList = rootView.findViewById(R.id.list_sensors);
                mSensorList.setAdapter(sensorsItemAdapter);
                mSettingsModel.refreshSettings.postValue(false);
            }
        });*/
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSettingsModel != null) {
            Settings settings = mSettingsModel.getSetting().getValue();
            Set<String> sensorsSet = new HashSet<>(settings.getSensors());

            SharedPreferences sharedPref = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putStringSet("sensors", sensorsSet);
            editor.putInt("frequency", settings.getFrequency());
            editor.putString("raspberryPiAddressIp", settings.getRaspberryPiAddress().getIp());
            editor.putString("raspberryPiAddressPort", settings.getRaspberryPiAddress().getPort());
            editor.putString("serverAddressIp", settings.getServerAddress().getIp());
            editor.putString("serverAddressPort", settings.getServerAddress().getPort());
            editor.putBoolean("isDataShared", settings.isDataShared());
            editor.apply();
        }
    }

    private void initCardView(View rootView) {
        // Init CardView
        mCardConfirmation = rootView.findViewById(R.id.confirmation_send_config);
        mCoverSettingsFragment = rootView.findViewById(R.id.cover_settings_fragment);

        mCoverSettingsFragment.setOnClickListener(v -> {
            mCardConfirmation.setVisibility(View.GONE);
            mCoverSettingsFragment.setVisibility(View.GONE);
            Toast.makeText(getActivity(), R.string.toast_cancel_configuration,
                    Toast.LENGTH_LONG).show();
        });
    }

    private void initSeekBar(View rootView) {
        // Init SeekBar View in XML
        SeekBar mSeekBar = rootView.findViewById(R.id.seekBar);

        // Setup SeekBar
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        SensorsListView mSensorList = rootView.findViewById(R.id.list_sensors);
        mSensorList.setAdapter(sensorsItemAdapter);
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
                // Code to execute when EditText loses focus
                mSettingsModel.getSetting().getValue().setRaspberryPiAddress(new Address(
                        mRaspberryPiAddressIp.getText().toString(),
                        mRaspberryPiAddressPort.getText().toString()));
            }
        });
        mRaspberryPiAddressPort.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // Code to execute when EditText loses focus
                mSettingsModel.getSetting().getValue().setRaspberryPiAddress(new Address(
                        mRaspberryPiAddressIp.getText().toString(),
                        mRaspberryPiAddressPort.getText().toString()));
            }
        });

        // Server Address
        mServerAddressIp.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // Code to execute when EditText loses focus
                mSettingsModel.getSetting().getValue().setServerAddress(new Address(
                        mServerAddressIp.getText().toString(),
                        mServerAddressPort.getText().toString()));
            }
        });
        mServerAddressPort.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // Code to execute when EditText loses focus
                mSettingsModel.getSetting().getValue().setServerAddress(new Address(
                        mServerAddressIp.getText().toString(),
                        mServerAddressPort.getText().toString()));
            }
        });
    }

    private void initButton(View rootView) {
        // Init Button
        Button mValidate = rootView.findViewById(R.id.validate);
        Button mAccept = rootView.findViewById(R.id.accept_confirmation);
        Button mCancel = rootView.findViewById(R.id.cancel_confirmation);

        // Init ImageButton
        ImageButton mButtonAddSensor = rootView.findViewById(R.id.button_add_sensor);

        // Setup Button
        mValidate.setOnClickListener(v -> {
            mCoverSettingsFragment.setVisibility(View.VISIBLE);
            mCardConfirmation.setVisibility(View.VISIBLE);
        });

        mAccept.setOnClickListener(v -> {
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
        });

        mCancel.setOnClickListener(v -> {
            mCoverSettingsFragment.setVisibility(View.GONE);
            mCardConfirmation.setVisibility(View.GONE);
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
        Switch mSwitchShareData = rootView.findViewById(R.id.switch_data_shared);

        // Setup Switch
        mSwitchShareData.setOnCheckedChangeListener((buttonView, isChecked) ->
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

