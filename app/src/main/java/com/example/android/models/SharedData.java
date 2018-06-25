package com.example.android.models;

/**
 * Created by nrutemby on 20/06/2018.
 */

public class SharedData {

    private String type;
    private String date;
    private Double  value;

    public SharedData(String type, String date, Double value) {
        this.type = type;
        this.date = date;
        this.value = value;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
