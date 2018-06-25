package com.example.android.models;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

/**
 * Created by nrutemby on 21/06/2018.
 */

public class RPI {

    private String name;
    private GeoPoint position;
    private ArrayList<SharedData> sharedDataArrayList;

    public RPI(String name, GeoPoint position) {
        this.name = name;
        this.position = position;
        sharedDataArrayList = new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    public GeoPoint getPosition() {
        return position;
    }

    public ArrayList<SharedData> getSharedDataArrayList() {
        return sharedDataArrayList;
    }

    public void setSharedDataArrayList(ArrayList<SharedData> sharedDataArrayList) {
        this.sharedDataArrayList = sharedDataArrayList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(GeoPoint position) {
        this.position = position;
    }
}
