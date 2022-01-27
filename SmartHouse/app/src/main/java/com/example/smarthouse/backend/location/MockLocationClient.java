package com.example.smarthouse.backend.location;

import java.util.Calendar;
import java.util.Date;

public class MockLocationClient implements LocationClient{
    @Override
    public int getRoomIndex(WiFiApInfo[] wifiState) {
        int minute = Calendar.getInstance().get(Calendar.SECOND);
        return minute % 4;
    }
}
