package com.example.smarthouse.backend.deviceTree.types;

public abstract class Sensor extends Appliance{
    protected float value;

    public Sensor(int id, float x, float y, String name, String applianceType) {
        super(id, x, y, name, applianceType);
    }

    public float getValue() {
        return value;
    }
}
