package com.example.android.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.activities.R;
import com.example.android.adapters.PollutantItemAdapter;
import com.example.android.helpers.AlertDialogHelper;
import com.example.android.helpers.JsonReaderHelper;
import com.example.android.models.Pollutant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ListPollutantsFragment extends Fragment {

    ArrayList<Pollutant> listPollutant= new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView= inflater.inflate(R.layout.fragment_list_pollutants, container, false);
        // Construct the data source
        listPollutant = getPollutantsData();

        // Create the adapter to convert the array to views
        PollutantItemAdapter pollutantAdapter = new PollutantItemAdapter(getActivity(), listPollutant);

        // Attach the adapter to the ListView
        ListView listView = rootView.findViewById(R.id.listPollutantView);
        listView.setAdapter(pollutantAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialogHelper.createOkAlertDialog(
                        listPollutant.get(position).getName(),
                        listPollutant.get(position).getDesc(),
                         getActivity()).show();
            }
        });

        return rootView;
    }


    private ArrayList<Pollutant> getPollutantsData() {
        String json = JsonReaderHelper.loadJSONFromAsset("pollutants.json", getActivity());

        try {
            JSONObject obj = new JSONObject(json);
            // Get JSON Array node
            JSONArray pollutantsArray = obj.getJSONArray("pollutants");

            for (int i = 0; i < pollutantsArray.length(); i++) {
                JSONObject jObj = pollutantsArray.getJSONObject(i);
                Pollutant p = new Pollutant();
                p.setName(jObj.getString("name"));
                p.setDesc(jObj.getString("desc"));
                p.setSource(jObj.getString("source"));

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
