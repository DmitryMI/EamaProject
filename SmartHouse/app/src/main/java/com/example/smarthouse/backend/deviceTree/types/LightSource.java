package com.example.smarthouse.backend.deviceTree.types;

import org.json.JSONObject;

public class LightSource extends Appliance{
    private float brightness;

    @Override
    public void FromJson(JSONObject jsonObject) {

    }

    public LightSource(int id, float x, float y, String name)
    {
        this.id = id;
        this.relativeX = x;
        this.relativeY = y;
        this.name = name;
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }
}
