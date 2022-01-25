package com.example.smarthouse.backend.deviceTree.types;

public class SimulatedTemperatureSensor extends TemperatureSensor{
    public SimulatedTemperatureSensor(int id, float x, float y, String name) {
        super(id, x, y, name);
    }

    @Override
    public void setIsOn(boolean isOn)
    {
        this.isOn = isOn;
    }

    @Override
    public void setMeasurementInterval(float interval)
    {
        this.measurementInterval = interval;
    }
}
