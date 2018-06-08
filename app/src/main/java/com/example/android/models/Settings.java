package com.example.android.models;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Settings {

    public List<String> Sensors;
    public int Frequency;
    public String Ssid;
    public String SecutityType;
    public String Password;

    public Settings(List<String> sensors, int frequency, String ssid, String secutityType, String password) {
        Sensors = sensors;
        Frequency = frequency;
        Ssid = ssid;
        SecutityType = secutityType;
        Password = password;
    }
}
