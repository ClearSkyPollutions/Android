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
import com.example.android.activities.R;
import com.example.android.activities.databinding.FragmentLastDataBinding;
import com.example.android.models.DataHT;
import com.example.android.models.DataPM;


public class LastDataFragment extends Fragment {

    DataHT dataHT;
    DataPM dataPM;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // Inflate using Databinding library
        FragmentLastDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_last_data, container, false);
        binding.setLifecycleOwner(this);

        // Get the view
        View rootView= binding.getRoot();

        // Create or get the ViewModel for our date, load the data from server
        dataPM = ViewModelProviders.of(this).get(DataPM.class);
        dataPM.LoadData(getContext());

        // Bind the UI elements to the viewmodel
        binding.setLastData(dataPM);

        Button mButtonRefresh = rootView.findViewById(R.id.buttonRefresh);
        mButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataPM.LoadData(getContext());
            }
        });

        return rootView;
    }

}
