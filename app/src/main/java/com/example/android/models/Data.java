package com.example.android.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by nrutemby on 06/06/2018.
 */

public class Data extends RealmObject {

    @Required
    @PrimaryKey
    private String datetime;
    @Required
    private Float timestamp;
    @Required
    private Float value;
    private DataType dataType;
    private Scale scale;

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Float getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Float timestamp) {
        this.timestamp = timestamp;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }
}
