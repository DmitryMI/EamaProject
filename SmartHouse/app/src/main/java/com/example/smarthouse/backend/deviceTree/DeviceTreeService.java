package com.example.smarthouse.backend.deviceTree;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.smarthouse.backend.deviceTree.types.Apartment;
import com.example.smarthouse.backend.restAPI.RestClient;

public class DeviceTreeService extends Service {

    IBinder binder;      // interface for clients that bind
    boolean allowRebind; // indicates whether onRebind should be used

    private RestClient restClient;
    private Apartment apartment;

    public DeviceTreeService() {
    }

    @Override
    public void onCreate() {
        //restClient = new RestClient(getApplicationContext());
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return binder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return allowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
    }

}