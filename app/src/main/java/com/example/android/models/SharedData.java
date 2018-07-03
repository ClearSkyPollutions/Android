package com.example.android.models;

import java.util.Date;

public class SharedData {

    private String type;
    private Date date;
    private Double  value;

    public SharedData(String type, Date date, Double value) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
