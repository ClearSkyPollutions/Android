package com.example.android.models;

import android.arch.lifecycle.MutableLiveData;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by nrutemby on 06/06/2018.
 */

public class Measure extends RealmObject {

    @Required
    @PrimaryKey
    private UUID id;
    @Required
    private MutableLiveData<Graph> graph;
    @Required
    private Float datetime;
    @Required
    private Float value;

    public Measure(MutableLiveData<Graph> graph, Float datetime, Float value) {
        this.id = UUID.randomUUID();
        this.graph = graph;
        this.datetime = datetime;
        this.value = value;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Float getDatetime() {
        return datetime;
    }

    public void setDatetime(Float datetime) {
        this.datetime = datetime;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public MutableLiveData<Graph> getGraph() {
        return graph;
    }

    public void setGraph(MutableLiveData<Graph> graph) {
        this.graph = graph;
    }
}
