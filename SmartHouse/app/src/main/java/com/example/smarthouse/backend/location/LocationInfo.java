package com.example.smarthouse.backend.location;

public class LocationInfo {

    private final boolean isValid;

    private final int roomId;


    public LocationInfo(boolean isValid, int roomId) {
        this.isValid = isValid;
        this.roomId = roomId;
    }

    public int getRoomId() {
        return roomId;
    }

    public boolean isValid() {
        return isValid;
    }
}
