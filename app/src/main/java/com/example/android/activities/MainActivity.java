package com.example.android.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.android.models.Data;
import com.example.android.models.DataModel;
import com.example.android.network.FetchDataClient;


public class MainActivity extends AppCompatActivity {

    private TextView mTextView;
    private DataModel mdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the objects id from XML layout
        mTextView = findViewById(R.id.textView);
        Button mButtonRefresh = findViewById(R.id.button_refresh);

        // Get the ViewModel.
        mdata = ViewModelProviders.of(this).get(DataModel.class);

        // Create the observer which updates the UI.
        final Observer<Data> dataPmObserver = new Observer<Data>() {
            @Override
            public void onChanged(@Nullable final Data newMeasurement) {
                // Update the UI, in this case, a TextView.
                if(newMeasurement != null) {
                    mTextView.setText(newMeasurement.toString());
                }
                else {
                    mTextView.setText("Data Error");
                }
            }
        };

        //init new RequestQueue
        final RequestQueue queue = Volley.newRequestQueue(this);
        FetchDataClient.getLastData(mdata, queue);

        mdata.getmeasurementLive().observe(this, dataPmObserver);

        mButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchDataClient.getLastData(mdata, queue);
            }
        });

    }
}
