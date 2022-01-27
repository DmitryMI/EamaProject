package com.example.smarthouse.backend.location;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

public class WiFiApInfoProvider {

    public WiFiApInfo[] getApInfos(Context appContext)
    {
        WifiManager wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> apList = wifiManager.getScanResults();

        WiFiApInfo[] result = new WiFiApInfo[apList.size()];

        int index = 0;
        for (ScanResult scanResult : apList)
        {
            WiFiApInfo apInfo = new WiFiApInfo(scanResult.SSID, scanResult.level);
            result[index] = apInfo;
            index++;
        }

        return result;
    }
}
