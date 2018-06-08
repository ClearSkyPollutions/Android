package com.example.android.models;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by nrutemby on 07/06/2018.
 */

public class DataType extends RealmObject {
    @Required
    @PrimaryKey
    private String name;
    @Required
    private String unit;
    private RealmList<Data> values;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public RealmList<Data> getValues() {
        return values;
    }

    public void setValues(RealmList<Data> values) {
        this.values = values;
    }
}
