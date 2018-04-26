package com.example.android.androidapp;

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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                mURLString,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //Set empty field
                        mTextView.setText("");

                        // Process the JSON
                        try{
                            // Get the date and time of the measure
                            JSONArray array = response.getJSONArray("Concentration_pm");

                            // Loop through the array elements
                            for(int i=0;i<array.length();i++){
                                // Get current json object
                                JSONObject measure = array.getJSONObject(i);

                                // Get the current student (json object) data
                                String date = measure.getString("date_mesure");
                                Double pm2_5 = measure.getDouble("pm2_5");
                                Double pm10 = measure.getDouble("pm10");

                                // Display the formatted json data in text view
                                mTextView.append(date + "\n" + pm2_5 + "\n" + pm10);
                            }
                        }catch (JSONException e){
                            mTextView.append("\nError parsing the JSON Data");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mTextView.setText("Error connecting to the server");
                    }
                }
        );

        queue.add(jsonObjectRequest);
    }

}