package com.example.android.activities;

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
import com.example.android.network.FetchDataClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private Button mButtonRefresh;
    private TextView mTextView;
    private String mURLString = " http://192.168.2.108:4000/Concentration_pm?order=id,desc&page=1,1&transform=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the objects id from XML layout
        mTextView = (TextView) findViewById(R.id.textView);

        //init new RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        Data data = FetchDataClient.getLastData(queue);

        while(true) {
            if (data != null) {
                mTextView.setText(data.toString());
            }
        }
    }
}
