package com.example.android.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.models.Data;
import com.example.android.models.DataModel;
import com.example.android.models.Data_PM;
import com.example.android.network.FetchDataClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private Button mButtonRefresh;
    private TextView mTextView;
    private DataModel mdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the objects id from XML layout
        mTextView = (TextView) findViewById(R.id.textView);

        // Get the ViewModel.
        mdata = ViewModelProviders.of(this).get(DataModel.class);

        // Create the observer which updates the UI.
        final Observer<Data_PM> dataPmObserver = new Observer<Data_PM>() {
            @Override
            public void onChanged(@Nullable final Data_PM newMeasurement) {
                // Update the UI, in this case, a TextView.
                mTextView.setText(newMeasurement.toString());
            }
        };

        //init new RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        FetchDataClient.getLastData(mdata, queue);

        mdata.getmeasurementLive().observe(this, dataPmObserver);
    }
}
