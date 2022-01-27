package com.example.smarthouse.backend.location;

public class NetworkState {
    private final boolean hasWiFiConnection;
    private final boolean hasCellConnection;

    public NetworkState(boolean hasWiFiConnection, boolean hasCellConnection) {
        this.hasWiFiConnection = hasWiFiConnection;
        this.hasCellConnection = hasCellConnection;
    }

    public boolean hasWiFiConnection() {
        return hasWiFiConnection;
    }

    public boolean hasCellConnection() {
        return hasCellConnection;
    }
}
