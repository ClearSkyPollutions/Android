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

public class PollutantItemAdapter extends ArrayAdapter<Pollutant> {
    public PollutantItemAdapter(@NonNull Context context, ArrayList<Pollutant> pollutants) {
        super(context, R.layout.item_adapter_list_pollutant,pollutants);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Pollutant pollutant = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_adapter_list_pollutant, parent, false);
        }

        ImageView pollutant_image = convertView.findViewById(R.id.pollutant_img);
        TextView pollutant_name = convertView.findViewById(R.id.pollutant_name);
        TextView pollutant_source = convertView.findViewById(R.id.pollutant_source);

        pollutant_image.setImageResource(pollutant.getImage());
        pollutant_name.setText(pollutant.getName());
        pollutant_source.setText(pollutant.getSource());


        return convertView;
    }

}
