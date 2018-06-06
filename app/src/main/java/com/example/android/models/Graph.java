package com.example.android.models;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Graph extends RealmObject {

    @Required
    @PrimaryKey
    private String name;
    @Required
    private String unit;
    private String scale;
    private List<Measure> measures;

    public Graph(String name, String unit, String scale) {
        this.name = name;
        this.unit = unit;
        this.scale = scale;
        this.measures = new ArrayList<>();
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

    public List<Measure> getMeasures() {
        return measures;
    }

    public void setMeasures(List<Measure> measures) {
        this.measures = measures;
    }
}

