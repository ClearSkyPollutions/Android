package com.example.android.models;

import java.util.ArrayList;
import java.util.Date;

public class Chart {
    private String name;
    private String unit;
    private String scale;
    private ArrayList<Date> xAxis;
    private ArrayList<Float> yAxis;

    public Chart(String name, String unit, String scale) {
        this.name = name;
        this.unit = unit;
        this.scale = scale;
        this.xAxis = new ArrayList<>();
        this.yAxis = new ArrayList<>();
    }

    public Chart(Chart copy, ArrayList<Date> xAxis, ArrayList<Float> yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.name = copy.name;
        this.unit = copy.unit;
        this.scale = copy.scale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public ArrayList<Float> getYAxis() {
        return yAxis;
    }

    public void setYAxis(ArrayList<Float> yAxis) {
        this.yAxis = yAxis;
    }

    public ArrayList<Date> getXAxis() {
        return xAxis;
    }

    public void setXAxis(ArrayList<Date> xAxis) {
        this.xAxis = xAxis;
    }
}

