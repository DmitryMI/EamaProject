package com.example.smarthouse.backend.deviceTree.types;

import org.json.JSONObject;

public class TemperatureSensor extends Sensor {
    protected float measurementInterval;

    @Override
    public void FromJson(JSONObject jsonObject) {

    }

    public TemperatureSensor(int id, float x, float y, String name)
    {
        this.id = id;
        this.relativeX = x;
        this.relativeY = y;
        this.name = name;
    }

    public float getMeasurementInterval() {
        return measurementInterval;
    }

    public void setMeasurementInterval(float measurementInterval) {
        this.measurementInterval = measurementInterval;
    }

    public void setValue(float value)
    {
        this.value = value;
    }
}
