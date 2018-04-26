package com.example.android.models;

public class Data_PM extends Data{
    Double pm2_5;
    Double pm10;

    public Data_PM(int id, String date_mesure, Double pm2_5, Double pm10) {
        super(id, date_mesure);
        this.pm2_5 = pm2_5;
        this.pm10 = pm10;
    }

    @Override
    public String toString() {
        String str = getDate() + " : "  +  pm2_5 + " : " + pm10;
        return str;
    }
}
