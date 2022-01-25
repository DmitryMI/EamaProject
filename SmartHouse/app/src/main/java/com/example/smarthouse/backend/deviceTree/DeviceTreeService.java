package com.example.smarthouse.backend.deviceTree;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.smarthouse.backend.deviceTree.types.Apartment;

public class DeviceTreeService extends Service {

    LocalBinder binder = new LocalBinder();      // interface for clients that bind
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
    public IBinder onBind(Intent intent) {

        return binder;
    }

    public class LocalBinder extends Binder
    {
        public DeviceTreeService getService() {

            return DeviceTreeService.this;
        }
    }

    public Apartment getDeviceTree()
    {
        return apartment;
    }

    public void onDeviceTreeReceived(Apartment apartment)
    {
        // TODO Update existing object instead of overwriting
        this.apartment = apartment;
    }

    public void requestDeviceTreeUpdate()
    {

    }

    public interface DeviceTreeUpdateListener
    {
        void OnDeviceTreeUpdated(Apartment apartment);
    }


}