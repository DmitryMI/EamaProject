package com.example.smarthouse.backend.deviceTree.types;

public abstract class Machine extends Appliance{

    public Machine(int id, float x, float y, String name, String applianceType) {
        super(id, x, y, name, applianceType);
    }
}
