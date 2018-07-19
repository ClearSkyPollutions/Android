package com.example.android.adapters;


import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.activities.R;
import com.example.android.models.Settings;

public class SensorsItemAdapter extends BaseAdapter {

    private final Context mContext;
    public MutableLiveData<Settings> mSettings;

    public SensorsItemAdapter(Context context, MutableLiveData<Settings> settings) {
        this.mContext = context;
        this.mSettings = settings;
    }

    @Override
    public int getCount() {
        return mSettings.getValue().getSensors().size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(R.layout.item_adapter_sensors_choose, parent, false);

        ImageButton mButtonRemove = convertView.findViewById(R.id.button_remove);
        TextView sensorName = convertView.findViewById(R.id.name_sensor);

        sensorName.setText(mSettings.getValue().getSensors().get(position));

        mButtonRemove.setOnClickListener(v -> {
            mSettings.getValue().getSensors().remove(position);
            notifyDataSetChanged();
        });

        return convertView;
    }

}
