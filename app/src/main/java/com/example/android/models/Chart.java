package com.example.android.models;

import java.util.ArrayList;
import java.util.Date;

public class Chart {

    private Integer type;
    private String unit;
    private String scale;
    private Integer color;
    private ArrayList<Date> xAxis;
    private ArrayList<Float> yAxis;

    public Chart(Integer dataType, String dataUnit, Integer lineColor, String scale) {
        type = dataType;
        unit = dataUnit;
        color = lineColor;
        this.scale = scale;
        xAxis = new ArrayList<>();
        yAxis = new ArrayList<>();
    }

    public Chart(Chart copy, ArrayList<Date> xAxis, ArrayList<Float> yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.type = copy.type;
        this.unit = copy.unit;
        this.color = copy.color;
        this.scale = copy.scale;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }
}

