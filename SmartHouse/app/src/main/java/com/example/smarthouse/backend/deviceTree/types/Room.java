package com.example.smarthouse.backend.deviceTree.types;

import org.json.JSONObject;

public class Room implements JsonReadable {
    private String name;
    private float width;
    private float height;
    private float relativeX;
    private float relativeY;
    private Appliance[] appliances;

    @Override
    public void FromJson(JSONObject jsonObject) {

    }
}
