package com.example.android.helpers;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class JsonReaderHelper {

    public static String loadJSONFromAsset(String jsonFileName, Context context){
        String json ;
        try {
            InputStream is = context.getAssets().open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
