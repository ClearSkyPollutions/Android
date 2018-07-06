package com.example.android.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.activities.R;
import com.example.android.helpers.AlertDialogHelper;
import com.example.android.helpers.JsonReaderHelper;
import com.example.android.models.Sensor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListSensorsFragment extends Fragment {

    ArrayList<Sensor> listSensors = new ArrayList<>();

    static ListSensorsFragment newInstance(int num) {
        return new ListSensorsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_sensors, container, false);

        getSensorsData();

        // Create an adapter class extending ArrayAdapter :
        ArrayAdapter<Sensor> adapter = new ArrayAdapter<Sensor>(getContext(), R.layout.item_adapter_sensor, listSensors) {

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                Sensor sensor = getItem(position);

                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_adapter_sensor, parent, false);
                }

                ImageView sensor_image = convertView.findViewById(R.id.sensor_img);
                TextView sensor_name = convertView.findViewById(R.id.sensor_name);
                TextView sensor_desc = convertView.findViewById(R.id.sensor_smalldesc);

                sensor_image.setImageResource(sensor.getImage());
                sensor_name.setText(sensor.getName());
                sensor_desc.setText(sensor.getSmalldesc());

                return convertView;
            }
        };

        // Attach the adapter to the ListView
        ListView listView = rootView.findViewById(R.id.listSensorsView);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener((parent, view, position, id) -> AlertDialogHelper.createOkAlertDialog(
                listSensors.get(position).getName(),
                listSensors.get(position).getDesc(),
                getActivity()).show());

        return rootView;
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
