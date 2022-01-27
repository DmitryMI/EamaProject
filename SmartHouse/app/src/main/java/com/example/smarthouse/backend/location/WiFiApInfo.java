package com.example.smarthouse.backend.location;

public class WiFiApInfo {
    private final String ssid;
    private final float signalPower;

    public WiFiApInfo(String ssid, float signalPower) {
        this.ssid = ssid;
        this.signalPower = signalPower;
    }


    public String getSsid() {
        return ssid;
    }

    public float getSignalPower() {
        return signalPower;
    }
}
