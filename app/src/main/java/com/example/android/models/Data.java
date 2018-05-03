package com.example.android.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public abstract class Data {

    private int id;
    private Date date;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

    Data(int id, String date_mesure) {
        this.id = id;
        try {
            setDate(date_mesure);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    String getDate() {
        return date.toString();
    }

    private void setDate(String date_mesure) throws ParseException {
        date = format.parse(date_mesure);
    }
}


