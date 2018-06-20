package com.example.android.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.android.activities.R;
import com.example.android.activities.databinding.FragmentSettingsBinding;
import com.example.android.models.Settings;
import com.example.android.viewModels.SettingsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


public class SettingsFragment extends Fragment {

    private SettingsModel mSettingsModel;

    private List<String> mSecurityList = null;

    //XML view objects
    private SeekBar mSeekBar;
    private TextView mFrequency;
    private EditText mSSID, mPassword;
    private Button mValidate;
    private Spinner mlistSecurityNet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate using DataBinding library
        FragmentSettingsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        binding.setLifecycleOwner(this);
        View rootView = binding.getRoot();

        // Create or get the ViewModel for our date, and bind the xml variable lastData to it (Databinding library)
        mSettingsModel = ViewModelProviders.of(getActivity()).get(SettingsModel.class);
        mSettingsModel.communication("config.json", Request.Method.GET,null);
        binding.setSettings(mSettingsModel);

        // Init views
        initViews(rootView);

        //SeekBar
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int progressValues = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mFrequency.setText(progress + " " + getResources().getText(R.string.unitfrequency));
                progressValues = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSettingsModel.getSetting().getValue().Frequency = progressValues;
            }

        });

        mSSID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    // code to execute when EditText loses focus
                    mSettingsModel.getSetting().getValue().Ssid = mSSID.getText().toString();
                }
            }
        });

        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    // code to execute when EditText loses focus
                    mSettingsModel.getSetting().getValue().Password = mPassword.getText().toString();
                }
            }
        });

        mValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings settings = mSettingsModel.getSetting().getValue();

                settings.Ssid = mSSID.getText().toString();
                settings.SecurityType = mlistSecurityNet.getSelectedItem().toString();
                settings.Password = mPassword.getText().toString();
                settings.Sensors.add("SDS011");
                settings.Sensors.add("DHT22");
                settings.Sensors.add("MQ2");

                JSONArray sensorsJson = new JSONArray(settings.Sensors);

                JSONObject jsonSend = new JSONObject();
                try {
                    jsonSend.put("Sensors", sensorsJson);
                    jsonSend.put("Frequency", settings.Frequency);
                    jsonSend.put("SSID", settings.Ssid);
                    jsonSend.put("SecurityType", settings.SecurityType);
                    jsonSend.put("Password", settings.Password);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("debug",jsonSend.toString());
                mSettingsModel.communication("config.php",Request.Method.PUT,jsonSend);

            }
        });

        //Spinner for change position of list
        mSettingsModel.getSetting().observe(this, entries -> {
            int position = mSecurityList.indexOf(mSettingsModel.getSetting().getValue().SecurityType);
            mlistSecurityNet.setSelection(position);
        });

        return rootView;
    }

    private void initViews(View rootView) {
        // Init SeekBar
        mSeekBar = rootView.findViewById(R.id.seekBar);

        // Init Text
        mFrequency = rootView.findViewById(R.id.labelFrequency);

        // Init EditText
        mSSID = rootView.findViewById(R.id.writeSSID);
        mPassword = rootView.findViewById(R.id.writePassword);

        // Init Button
        mValidate = rootView.findViewById(R.id.validate);

        // Init Spinner
        mlistSecurityNet = rootView.findViewById(R.id.listSecurityNetwork);

        if (mSecurityList == null) {
            mSecurityList = Arrays.asList(getResources().getStringArray(R.array.networkSecurity));
        }
    }

}

