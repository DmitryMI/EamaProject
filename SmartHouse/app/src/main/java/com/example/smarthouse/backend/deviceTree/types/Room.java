package com.example.smarthouse.backend.deviceTree.types;

import org.json.JSONObject;

public class Room implements JsonReadable {

    private int id;
    private String name;
    private float width;
    private float height;
    private float relativeX;
    private float relativeY;
    private Appliance[] appliances;

    @Override
    public void FromJson(JSONObject jsonObject) {

    }

    public Room(int id, String name, float width, float height, float x, float y, Appliance[] appliances)
    {
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
        this.relativeX = x;
        this.relativeY = y;
        this.appliances = appliances;
    }

    public String getName() {
        return name;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getRelativeX() {
        return relativeX;
    }

    public float getRelativeY() {
        return relativeY;
    }

    public Appliance[] getAppliances() {
        return appliances;
    }

}
