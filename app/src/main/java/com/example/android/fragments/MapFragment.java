package com.example.android.fragments;

import android.arch.lifecycle.MutableLiveData;
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
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.activities.R;
import com.example.android.activities.databinding.FragmentMapBinding;
import com.example.android.adapters.SensorsItemAdapter;
import com.example.android.models.Data;
import com.example.android.models.Settings;
import com.example.android.viewModels.SettingsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment {

    private SettingsModel mSettingsModel;

    //XML view objects
    private SeekBar mSeekBar;
    private TextView mFrequency;
    private EditText mSSID,  mPassword;
    private Button mValidate;
    private Spinner mlistSecurityNet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate using DataBinding library
        FragmentMapBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        binding.setLifecycleOwner(this);
        View rootView = binding.getRoot();

        // Create or get the ViewModel for our date, and bind the xml variable lastData to it (Databinding library)
        mSettingsModel = ViewModelProviders.of(getActivity()).get(SettingsModel.class);
        mSettingsModel.loadData();

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

        //
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
                settings.SecutityType = mlistSecurityNet.getSelectedItem().toString();
                settings.Password = mPassword.getText().toString();


            }
        });

        return rootView;
    }

    private void parseJSONResponse(JSONObject response, MutableLiveData<Settings> settings) {
        try {
            List<Float[]> vals = new ArrayList<>();
            JSONArray array = response.getJSONArray(data.getValue().scale);

            for (int i = array.length() - 1; i >= 0; i--) {

                JSONObject measure =  array.getJSONObject(i);
                Float val = (float) measure.getDouble(data.getValue().name);
                String date = measure.getString(colDate);

                // Change the date String to a float representing ms since 01/01/1970
                Float ts_f = (float) Timestamp.valueOf(date).getTime();

                vals.add(new Float[]{ts_f, val});
            }
            data.postValue(new Data(data.getValue(), vals));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
