package com.example.smarthouse.backend.deviceTree.types;

import org.json.JSONObject;

public class WashingMachine extends Machine {
    protected float workTimeLeft;
    protected float washingTemperature;

    @Override
    public void FromJson(JSONObject jsonObject) {

    }

    public WashingMachine(int id, int x, int y, String name)
    {
        this.id = id;
        this.relativeX = x;
        this.relativeY = y;
        this.name = name;
    }

    public float getWorkTimeLeft() {
        return workTimeLeft;
    }

    public void setWorkTimeLeft(float workTimeLeft) {
        this.workTimeLeft = workTimeLeft;
    }

    public float getWashingTemperature() {
        return washingTemperature;
    }

    public void setNextWashingProgram(String programName)
    {

    }
}
