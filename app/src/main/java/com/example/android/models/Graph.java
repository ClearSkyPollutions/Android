package com.example.android.models;

import java.util.List;

public class Graph {
    private String name;
    private String unit;
    private String scale;
    private List<Data> data;

    public Graph(String name, String unit, String scale) {
        this.name = name;
        this.unit = unit;
        this.scale = scale;
    }

    public Graph(Graph copy, List<Data> values) {
        this.data = values;
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

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}

