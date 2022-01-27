package com.example.smarthouse.backend.deviceTree.types;

import org.json.JSONObject;

public class TemperatureSensor extends Sensor {
    protected float measurementInterval;

    @Override
    public void FromJson(JSONObject jsonObject) {

    }

    public TemperatureSensor(int id, float x, float y, String name)
    {
        super(id, x, y, name, TemperatureSensor.class.getName());
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
