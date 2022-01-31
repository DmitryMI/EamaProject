package com.example.smarthouse.backend.deviceTree.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SimulatedWashingMachine extends WashingMachine{
    public SimulatedWashingMachine(int id, float x, float y, String name) {
        super(id, x, y, name);
    }

    public String getRandom(List<String> modes) {
        Random rand = new Random();
        return modes.get(rand.nextInt(modes.size()));
    }

    @Override
    public void setNextWashingProgram(String programName) {
        switch (programName) {
            case "Default":
                washingTemperature = 60.0f;
                workTimeLeft = 2 * 60 * 60;
                onProgramReceived();
                break;
            case "Short":
                washingTemperature = 60.0f;
                workTimeLeft = 60 * 60;
                onProgramReceived();
                break;
            case "Hot":
                washingTemperature = 80.0f;
                workTimeLeft = 60 * 60;
                onProgramReceived();
                break;
        }
    }

    private void onProgramReceived()
    {
        isOn = true;
    }

    public void simulationUpdate(float deltaTime)
    {
        if (!isOn) {
            //String mode = getRandom(Arrays.asList("Default", "Short", "Hot"));
            //setNextWashingProgram(mode);
        }
        else
        {
            workTimeLeft -= deltaTime;
            if (workTimeLeft <= 0)
            {
                workTimeLeft = 0;
                isOn = false;
            }
        }
    }

    @Override
    public void setIsOn(boolean isOn)
    {
        this.isOn = isOn;
    }
}
