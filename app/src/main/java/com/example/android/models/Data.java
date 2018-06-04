package com.example.android.models;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Data {
    public final String name;
    public final String unit;
    public String scale = "AVG_HOUR";
    public List<Float[]> values;
    public Float lastValRcved;
    public String lastDateRcved;

    public Data(String name, String unit) {
        this.name = name;
        this.unit = unit;
        this.values = new ArrayList<>();
    }

    public Data(Data copy, List<Float[]> newValues) {
        this.name = copy.name;
        this.unit = copy.unit;
        this.scale = copy.scale;
        this.values = newValues;
        this.lastValRcved = newValues.get(newValues.size()-1)[1];

        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh--mm--ss", Locale.FRANCE);
        this.lastDateRcved = ft.format(new Timestamp(newValues.get(newValues.size()-1)[0].longValue()));
    }
}

