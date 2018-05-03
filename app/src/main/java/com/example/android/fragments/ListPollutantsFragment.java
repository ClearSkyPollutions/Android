package com.example.android.fragments;

import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.activities.R;
import com.example.android.adapters.PollutantItemAdapter;
import com.example.android.models.Pollutant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ListPollutantsFragment extends Fragment {

    ArrayList<Pollutant> listPollutant= new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView= inflater.inflate(R.layout.fragment_list_pollutants, container, false);
        // Construct the data source
        listPollutant = loadJSONFromAsset();

        // Create the adapter to convert the array to views
        PollutantItemAdapter pollutantAdapter = new PollutantItemAdapter(getActivity(), listPollutant);

        // Attach the adapter to the ListView
        ListView listView = rootView.findViewById(R.id.listPollutantView);
        listView.setAdapter(pollutantAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());


                alertDialogBuilder.setTitle(listPollutant.get(position).getName());
                alertDialogBuilder
                        .setMessage(listPollutant.get(position).getDesc())
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
            }
        });

        return rootView;
    }



    private ArrayList<Pollutant> loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("pollutants.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        try {
            JSONObject obj = new JSONObject(json);
            // Get JSON Array node
            JSONArray pollutantsArray = obj.getJSONArray("pollutants");

            for (int i = 0; i < pollutantsArray.length(); i++) {
                JSONObject jObj = pollutantsArray.getJSONObject(i);
                Pollutant p = new Pollutant();
                p.setName(jObj.getString("name"));
                p.setDesc(jObj.getString("desc"));

                // Get resource id from image name
                p.setImage(getResources().getIdentifier(
                        jObj.getString("image"),
                        "drawable",
                        getActivity().getPackageName()));

                //Add object to ArrayList
                listPollutant.add(p);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listPollutant;
    }
}
