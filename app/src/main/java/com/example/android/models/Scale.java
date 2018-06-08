package com.example.android.models;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by nrutemby on 07/06/2018.
 */

public class Scale extends RealmObject {
    @Required
    @PrimaryKey
    private String name;
    private RealmList<Data> values;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
