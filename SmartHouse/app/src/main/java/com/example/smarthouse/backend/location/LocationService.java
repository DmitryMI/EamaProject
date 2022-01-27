package com.example.smarthouse.backend.location;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.security.Permission;

public class LocationService extends Service {
    public final static String LocationUpdatedAction = "LOCATION_INFO_ACTION";

    private LocationClient locationClient;
    private NetworkInfoProvider wiFiApInfoProvider;
    private final LocalBinder binder = new LocalBinder();
    private LocationInfo locationInfo;

    public LocationService() {
    }

    private void initializeClients()
    {
        if(locationClient == null)
        {
            locationClient = new MockLocationClient();
        }
        if(wiFiApInfoProvider == null)
        {
            wiFiApInfoProvider = new NetworkInfoProvider();
        }
    }

    public class LocalBinder extends Binder
    {
        public LocationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocationService.this;
        }
    }

    public LocationInfo getLocation()
    {
        return locationInfo;
    }

    private void sendLocationInfo()
    {
        WiFiApInfo[] wiFiApInfos = wiFiApInfoProvider.getApInfos(getApplicationContext());
        for(WiFiApInfo apInfo : wiFiApInfos)
        {
            Log.i("SmartHouse LocationService", String.format("WiFi AP found: %s %3.2f", apInfo.getSsid(), apInfo.getSignalPower()));
        }

        int roomId = locationClient.getRoomIndex(wiFiApInfos);

        locationInfo = new LocationInfo(roomId);

        Intent intent = new Intent();
        intent.setAction(LocationUpdatedAction);
        getApplicationContext().sendBroadcast(intent);
    }



    public void requestLocationInfo()
    {
        Context context = getApplicationContext();
        if(context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Log.e("SmartHouse LocationService", "ACCESS_FINE_LOCATION not granted");
        }
        else
        {
            sendLocationInfo();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        initializeClients();

        return binder;
    }
}