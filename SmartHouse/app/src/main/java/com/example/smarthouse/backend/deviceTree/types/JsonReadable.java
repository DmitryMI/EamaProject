package com.example.smarthouse.backend.deviceTree.types;

import org.json.JSONObject;

public interface JsonReadable {
    void FromJson(JSONObject jsonObject);
}
