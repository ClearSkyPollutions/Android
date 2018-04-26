package com.example.android.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public abstract class Data {
    int id;
    private Date date;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

    public Data (int id, String date_mesure) {
        this.id = id;
        try {
            setDate(date_mesure);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public String getDate() {
        return date.toString();
    }

    public void setDate(String date_mesure) throws ParseException {
        date = format.parse(date_mesure);
    }
}


