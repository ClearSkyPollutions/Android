package com.example.android.models;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

/**
 * Created by nrutemby on 21/06/2018.
 */

public class System {

    private String id;
    private String name;
    private GeoPoint position;
    private ArrayList<SharedData> sharedDataArrayList;

    public System(String id, String name, GeoPoint position) {
        this.id = id;
        this.name = name;
        this.position = position;
        sharedDataArrayList = new ArrayList<>();
    }

    public String getId() {
        return id;
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
}
