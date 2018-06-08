package com.example.android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.activities.R;
import com.example.android.models.Pollutant;

import java.util.ArrayList;

public class SensorsItemAdapter extends ArrayAdapter<String> {
    public SensorsItemAdapter(@NonNull Context context, ArrayList<String> sensors) {
        super(context, R.layout.activity_sensors_item_adapter ,sensors);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String sensors = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_sensors_item_adapter, parent, false);
        }

        TextView sensors_name = convertView.findViewById(R.id.switchSensorsView);

        sensors_name.setText(sensors);

        return convertView;
    }

}
