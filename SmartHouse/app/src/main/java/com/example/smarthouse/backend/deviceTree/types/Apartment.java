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
}
