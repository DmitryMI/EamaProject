package com.example.smarthouse.backend.deviceTree.types;

public abstract class Appliance implements JsonReadable {
    private String name;
    private float width;
    private float height;
    private float relativeX;
    private float relativeY;
    private boolean isOn;
    private String applianceType;
}
