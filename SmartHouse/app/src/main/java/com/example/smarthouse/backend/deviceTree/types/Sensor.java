package com.example.smarthouse.backend.deviceTree.types;

public abstract class Sensor extends Appliance{
    protected float value;

    public float getValue() {
        return value;
    }
}
