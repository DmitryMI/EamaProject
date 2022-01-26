package com.example.smarthouse.backend.deviceTree.types;

public class SimulatedLightSource extends LightSource{
    public SimulatedLightSource(int id, float x, float y, String name) {
        super(id, x, y, name);
    }

    @Override
    public void setIsOn(boolean isOn)
    {
        this.isOn = isOn;
    }
}
