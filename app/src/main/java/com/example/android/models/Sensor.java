package com.example.android.models;

public class Sensor {
    private String name;
    private String desc;
    private String smalldesc;
    private int image;

    public Sensor(String name, String desc, String smalldesc, int image) {
        this.name = name;
        this.desc = desc;
        this.smalldesc = smalldesc;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getSmalldesc() { return smalldesc; }

    public int getImage() {
        return image;
    }
}
