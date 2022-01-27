package com.example.smarthouse.backend.deviceTree.types;

import org.json.JSONObject;

public class WashingMachine extends Machine {
    protected float workTimeLeft;
    protected float washingTemperature;

    @Override
    public void FromJson(JSONObject jsonObject) {

    }

    public WashingMachine(int id, float x, float y, String name)
    {
        super(id, x, y, name, WashingMachine.class.getName());
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
