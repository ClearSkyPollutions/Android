package com.example.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.activities.R;


public class SliderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LAYOUT = "layout";
    private static final String ARG_POSITION = "position";

    // TODO: Rename and change types of parameters
    private int mLayout;
    private int mPosition;

    public SliderFragment() {
    }

    public static SliderFragment newInstance(int layout, int position) {
        SliderFragment fragment = new SliderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT, layout);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLayout = getArguments().getInt(ARG_LAYOUT);
            mPosition = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(mLayout, container, false);
        TextView t = rootView.findViewById(R.id.title);
        t.setText(Html.fromHtml(getResources().getStringArray(R.array.slide_titles)[mPosition]));
        t = rootView.findViewById(R.id.body);
        t.setText(Html.fromHtml(getResources().getStringArray(R.array.slide_content)[mPosition]));

        return rootView;

    }
}
