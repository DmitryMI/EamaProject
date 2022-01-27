package com.example.smarthouse.backend.location;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NetworkInfoProvider {

    public NetworkState getNetworkState(Context appContext)
    {
        boolean hasWiFiConnection = false;
        boolean hasCellConnection = false;

        ConnectivityManager connManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connManager.getActiveNetwork();
        NetworkInfo networkInfo = connManager.getNetworkInfo(network);
        if(networkInfo == null)
        {
            return new NetworkState(false, false);
        }

        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            hasWiFiConnection = connectionInfo.getSupplicantState() == SupplicantState.COMPLETED;
            hasCellConnection = true;
        }

        return new NetworkState(hasWiFiConnection, hasCellConnection);
    }

    public WiFiApInfo[] getApInfos(Context appContext)
    {
        String ssid = null;
        float power = 0;
        ConnectivityManager connManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connManager.getActiveNetwork();
        NetworkInfo networkInfo = connManager.getNetworkInfo(network);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null) {
                boolean hasWiFiConnection = connectionInfo.getSupplicantState() == SupplicantState.COMPLETED;
                if(!hasWiFiConnection)
                {
                    return new WiFiApInfo[0];
                }
                ssid = connectionInfo.getSSID();
                if(ssid.equals("<unknown ssid>"))
                {
                    return new WiFiApInfo[0];
                }
                power = connectionInfo.getLinkSpeed();
            }
        }

        return new WiFiApInfo[]{new WiFiApInfo(ssid, power)};
    }
}
