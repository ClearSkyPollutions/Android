package com.example.android.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.android.activities.R;
import com.example.android.models.DataModel;
import com.example.android.models.DataPM;
import com.example.android.network.FetchDataClient;



public class LastDataFragment extends Fragment {

    private Button mButtonRefresh;
    private TextView mTextView;
    private DataModel mdata;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView= inflater.inflate(R.layout.fragment_last_data, container, false);
        // Get the objects id from XML layout
        mTextView = rootView.findViewById(R.id.textView);

        // Get the ViewModel.
        mdata = ViewModelProviders.of(this).get(DataModel.class);

        // Create the observer which updates the UI.
        final Observer<DataPM> dataPmObserver = new Observer<DataPM>() {
            @Override
            public void onChanged(@Nullable final DataPM newMeasurement) {
                // Update the UI, in this case, a TextView.
                mTextView.setText(newMeasurement.toString());
            }
        };

        //init new RequestQueue
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        FetchDataClient.getLastData(mdata, queue);

        mdata.getmeasurementLive().observe(this, dataPmObserver);

        mButtonRefresh= rootView.findViewById(R.id.button_refresh);
        mButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rootView;
    }

}
