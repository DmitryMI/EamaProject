package com.example.smarthouse.backend.deviceTree.types;

public abstract class Appliance implements JsonReadable {

    protected int id;
    protected String name;
    protected float relativeX;
    protected float relativeY;
    protected boolean isOn;
    protected String applianceType;

    public String getName() {
        return name;
    }

    public float getRelativeX() {
        return relativeX;
    }

    public float getRelativeY() {
        return relativeY;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setIsOn(boolean on) {
        isOn = on;
    }

    public boolean getIsOn()
    {
        return isOn;
    }

    public String getApplianceType() {
        return applianceType;
    }

    public int getId() {
        return id;
    }

    public Appliance(int id, float x, float y, String name, String applianceType)
    {
        this.id = id;
        relativeX = x;
        relativeY = y;
        this.name = name;
        this.applianceType = applianceType;
    }
}
