package com.example.android.models;

import java.util.List;

public class Data {
    public final String name;
    public final String unit;
    public String scale = "AVG_HOUR";
    public List<Float[]> values;

    public Data(String name, String unit) {
        this.name = name;
        this.unit = unit;
    }

    public Data(Data copy, List<Float[]> newValues) {
        this.name = copy.name;
        this.unit = copy.unit;
        this.scale = "AVG_HOUR";
        this.values = newValues;

    }
}

