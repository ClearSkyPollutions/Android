package com.example.android.customViews;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.android.activities.R;
import com.example.android.adapters.ChartItemAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class ButtonFavoriteOnClickListener implements View.OnClickListener{

    private final int position;
    private String type;
    private ImageButton ButtonFavorite;
    private ChartItemAdapter chartItemAdapter;

    public ButtonFavoriteOnClickListener(int position, ChartItemAdapter chartItemAdapter, ImageButton imageButton, String type) {
        this.position = position;
        this.chartItemAdapter = chartItemAdapter;
        this.ButtonFavorite = imageButton;
        this.type = type;
    }

    @Override
    public void onClick(View v) {
        if (chartItemAdapter.favorite.contains(type)) {
            chartItemAdapter.favorite.remove(type);
            ButtonFavorite.setImageResource(R.drawable.ic_star_border_black_24dp);
        } else {
            chartItemAdapter.favorite.add(type);
            ButtonFavorite.setImageResource(R.drawable.ic_star_black_24dp);
        }
        Log.d("Favorite", Arrays.toString(new ArrayList[]{chartItemAdapter.favorite}));
    }
}
