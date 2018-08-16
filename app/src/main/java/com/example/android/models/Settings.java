package com.example.android.models;

import android.location.Location;

import java.util.ArrayList;

public class Settings {

    private ArrayList<String> sensors;
    private int frequency;
    private Address raspberryPiAddress;
    private Address serverAddress;
    private boolean isDataShared;
    private String systemID;
    private String systemName;
    private Location positionSensor;


    public Settings(ArrayList<String> sensors, int frequency,
                    Address raspberryPiAddress, Address serverAddress, boolean isDataShared, Location positionSensor,
                    String systemID, String systemName) {
        this.sensors = sensors;
        this.frequency = frequency;
        this.raspberryPiAddress = raspberryPiAddress;
        this.serverAddress = serverAddress;
        this.isDataShared = isDataShared;
        this.systemID = systemID;
        this.systemName = systemName;
        this.positionSensor = positionSensor;
    }

    public Settings(Settings copy) {
        this.sensors = copy.sensors;
        this.frequency = copy.frequency;
        this.raspberryPiAddress = copy.raspberryPiAddress;
        this.serverAddress = copy.serverAddress;
        this.isDataShared = copy.isDataShared;
        this.systemID = copy.systemID;
        this.systemName = copy.systemName;
        this.positionSensor = copy.positionSensor;
    }

    public ArrayList<String> getSensors() {
        return sensors;
    }

    public void setSensors(ArrayList<String> sensors) {
        this.sensors = sensors;
    }

    public void addSensors(String sensors) {
        this.sensors.add(sensors);
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public Address getRaspberryPiAddress() {
        return raspberryPiAddress;
    }

    public void setRaspberryPiAddress(Address raspberryPiAddress) {
        this.raspberryPiAddress = raspberryPiAddress;
    }

    public Address getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(Address serverAddress) {
        this.serverAddress = serverAddress;
    }

    public boolean isDataShared() {
        return isDataShared;
    }

    public void setDataShared(boolean dataShared) {
        this.isDataShared = dataShared;
    }

    public String getSystemID() {
        return systemID;
    }

    public void setSystemID(String systemID) {
        this.systemID = systemID;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
    public Location getPositionSensor() {
        return positionSensor;
    }

    public void setPositionSensor(Location positionSensor) {
        this.positionSensor = positionSensor;
    }
}
