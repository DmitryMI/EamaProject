package com.example.smarthouse.backend.deviceTree.types;

import org.json.JSONObject;

public class Apartment implements JsonReadable{
    private String name;
    private float width;
    private float height;
    private float longitude;
    private float latitude;
    private Room[] rooms;

    @Override
    public void FromJson(JSONObject jsonObject) {

    }

    public Apartment(String name, float width, float height, Room[] rooms)
    {
        this.name = name;
        this.width = width;
        this.height = height;
        this.rooms = rooms;
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

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public Room[] getRooms() {
        return rooms;
    }

}
