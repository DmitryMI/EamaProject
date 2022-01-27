package com.example.smarthouse.backend.location;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends Service {
    public final static String LocationUpdatedAction = "LOCATION_INFO_ACTION";

    private LocationClient locationClient;
    private NetworkInfoProvider networkInfoProvider;
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
        if(networkInfoProvider == null)
        {
            networkInfoProvider = new NetworkInfoProvider();
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
        boolean isValid;
        int roomId = 0;
        NetworkState networkState = networkInfoProvider.getNetworkState(getApplicationContext());
        isValid = networkState.hasWiFiConnection();
        if(isValid) {
            WiFiApInfo[] wiFiApInfos = networkInfoProvider.getApInfos(getApplicationContext());
            for (WiFiApInfo apInfo : wiFiApInfos) {
                Log.i("SmartHouse LocationService", String.format("WiFi AP found: %s %3.2f", apInfo.getSsid(), apInfo.getSignalPower()));
            }

            if(wiFiApInfos.length == 0)
            {
                isValid = false;
            }
            else {
                roomId = locationClient.getRoomIndex(wiFiApInfos);
            }
        }

        locationInfo = new LocationInfo(isValid, roomId);

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