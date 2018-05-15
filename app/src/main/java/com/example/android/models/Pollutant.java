package com.example.android.models;



public class Pollutant  {
    private String name;
    private String desc;
    private String source;
    private int image;

    public Pollutant() {
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getSource() {
        return source;
    }

    public int getImage() {
        return image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
