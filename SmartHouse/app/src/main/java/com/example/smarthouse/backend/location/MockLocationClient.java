package com.example.smarthouse.backend.location;

import java.util.Calendar;
import java.util.Date;

public class MockLocationClient implements LocationClient{
    @Override
    public int getRoomIndex(WiFiApInfo[] wifiState) {
        int seconds = Calendar.getInstance().get(Calendar.SECOND);
        return seconds / 15;
    }
}
