package com.example.android.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.android.activities.BuildConfig;
import com.example.android.activities.MainActivity;
import com.example.android.activities.R;
import com.example.android.helpers.JsonReaderHelper;
import com.example.android.models.Sensor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class SliderFragment extends Fragment {
    private static final String ARG_LAYOUT = "layout";
    private static final String ARG_POSITION = "position";

    private int mLayout;
    private int mPosition;
    private ArrayList<Sensor> listSensors = new ArrayList<>();
    private HashSet<String> usedSensors = new HashSet<>();

    private SharedPreferences mPrefs;

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
        mPrefs = getContext().getSharedPreferences(getString(R.string.config_file_key), Context.MODE_PRIVATE);

        if (getArguments() != null) {
            mLayout = getArguments().getInt(ARG_LAYOUT);
            mPosition = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(mLayout, container, false);

        String title = "";
        int imgId = 0;
        StringBuilder content = new StringBuilder();
        String json = JsonReaderHelper.loadJSONFromAsset("slides.json", getContext());
        try {
            JSONObject root = new JSONObject(json);
            // Get JSON Array node
            JSONObject slide = root.getJSONArray("slide").getJSONObject(mPosition);

            title = slide.getString("title");

            JSONArray lines = slide.getJSONArray("lines");
            for (int i = 0; i<lines.length(); i++) {
                content.append(lines.getString(i));
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
        t.setText(Html.fromHtml(content.toString()));

        if (imgId != 0){
            ImageView slider_image = rootView.findViewById(R.id.slider_image);
            slider_image.setImageResource(imgId);
        }

        Switch sw = rootView.findViewById(R.id.share);
        if(sw != null) {
            //@TODO: setup event listener
        }

        ListView listView = rootView.findViewById(R.id.listSensors);

        if (listView != null) {
            listView.setAdapter(setup_list_sensors());
        }

        ImageButton confirm = rootView.findViewById(R.id.confirmSensors);

        if (confirm != null) {

            TextInputEditText input = rootView.findViewById(R.id.add_ip_input);
            input.setText(BuildConfig.IPADDR_SERVER);
            input = rootView.findViewById(R.id.add_port_input);
            input.setText(Integer.toString(BuildConfig.PortHTTP_SERVER));

            confirm.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            });
        }

        return rootView;
    }

    private ArrayAdapter<Sensor> setup_list_sensors() {
        if(listSensors.isEmpty()) {
            getSensorsData();
        }
        // Create an adapter class extending ArrayAdapter :
        ArrayAdapter<Sensor> adapter = new ArrayAdapter<Sensor>(getContext(), R.layout.item_adapter_sensor, listSensors) {

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                Sensor sensor = getItem(position);
                Switch check;

                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_adapter_sensor_slider, parent, false);
                }
                TextView name = convertView.findViewById(R.id.nameSensor);
                name.setText(sensor.getName());
                Switch toggle = convertView.findViewById(R.id.switchSensor);
                toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if(isChecked){
                        usedSensors.add(sensor.getName());
                    }
                    else {
                        usedSensors.remove(sensor.getName());
                    }
                });

                return convertView;
            }
        };
        // Attach the adapter to the ListView
        return adapter;
    }

    private void getSensorsData() {
        String json = JsonReaderHelper.loadJSONFromAsset("sensors.json", getContext());

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
}

