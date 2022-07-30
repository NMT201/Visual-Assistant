package com.example.myapplication.model;

import java.util.List;

public class ResultDescribe {

    private List<Integer> orientation;
    private List<String> object_name;
    private List<Float> distance;

    public ResultDescribe(List<Integer> orientation, List<String> object_name, List<Float> distance) {
        this.orientation = orientation;
        this.object_name = object_name;
        this.distance = distance;
    }

    public List<Integer> getOrientation() {
        return orientation;
    }

    public void setOrientation(List<Integer> orientation) {
        this.orientation = orientation;
    }

    public List<String> getObject_name() {
        return object_name;
    }

    public void setObject_name(List<String> object_name) {
        this.object_name = object_name;
    }

    public List<Float> getDistance() {
        return distance;
    }

    public void setDistance(List<Float> distance) {
        this.distance = distance;
    }
}
