package com.example.android.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.android.activities.R;
import com.example.android.activities.databinding.FragmentLastDataBinding;
import com.example.android.models.DataModel;


public class LastDataFragment extends Fragment {

    DataModel datamodel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // Inflate using Dabtabinding library
        FragmentLastDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_last_data, container, false);
        binding.setLifecycleOwner(this);

        // Get the view
        View rootView= binding.getRoot();

        // Create or get the ViewModel for our date, load the data from server
        datamodel = ViewModelProviders.of(this).get(DataModel.class);
        datamodel.LoadData(getContext());

        // Bind the UI elements to the viewmodel
        binding.setLastData(datamodel);

        Button mButtonRefresh = rootView.findViewById(R.id.buttonRefresh);
        mButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datamodel.LoadData(getContext());
            }
        });

        return rootView;
    }

}
