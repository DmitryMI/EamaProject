package com.example.smarthouse.backend.location;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class LocationService extends Service {
    public final static String LocationUpdatedAction = "LOCATION_INFO_ACTION";

    private LocationClient locationClient;
    private WiFiApInfoProvider wiFiApInfoProvider;
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
            wiFiApInfoProvider = new WiFiApInfoProvider();
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

    public void requestLocationInfo()
    {
        WiFiApInfo[] wiFiApInfos = wiFiApInfoProvider.getApInfos(getApplicationContext());
        int roomId = locationClient.getRoomIndex(wiFiApInfos);

        locationInfo = new LocationInfo(roomId);

        Intent intent = new Intent();
        intent.setAction(LocationUpdatedAction);
        getApplicationContext().sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        initializeClients();

        return binder;
    }
}