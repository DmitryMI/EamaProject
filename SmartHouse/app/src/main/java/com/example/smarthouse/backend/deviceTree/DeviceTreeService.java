package com.example.smarthouse.backend.deviceTree;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.smarthouse.backend.deviceTree.types.Apartment;
import com.example.smarthouse.backend.discovery.Discovery;
import com.example.smarthouse.backend.discovery.DiscoveryService;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DeviceTreeService extends Service {
    public static final String SyncFinishedAction = "SYNCHRONIZATION_FINISHED_ACTION";

    private static DeviceTreeClient deviceTreeClientInstance;
    public static DeviceTreeClient getDeviceTreeClient()
    {
        if(deviceTreeClientInstance == null)
        {
            deviceTreeClientInstance = new MockDeviceTreeClient();
        }
        return deviceTreeClientInstance;
    }

    LocalBinder binder = new LocalBinder();      // interface for clients that bind
    private DiscoveryService discoveryService;
    private Apartment apartment;

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            if (service instanceof DiscoveryService.LocalBinder) {
                DiscoveryService.LocalBinder binder = (DiscoveryService.LocalBinder) service;
                discoveryService = binder.getService();

                requestServerDiscovery();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            discoveryService = null;
        }
    };

    private void requestServerDiscovery()
    {
        discoveryService.startDiscovery(new DiscoveryService.DiscoveryReceivedCallback() {
            @Override
            public void OnDiscoveryReceived(Discovery discovery) {
                onDiscoveryReceivedCallback(discovery);
            }
        });
    }


    private void onDiscoveryReceivedCallback(Discovery discovery)
    {
        PeriodicWorkRequest periodic = new PeriodicWorkRequest
                .Builder(SynchronisationWorker.class, 15, TimeUnit.MINUTES).build();
        WorkManager
                .getInstance(getApplicationContext())
                .enqueueUniquePeriodicWork(SynchronisationWorker.PeriodicWorkName, ExistingPeriodicWorkPolicy.REPLACE, periodic);
    }

    private void requestSynchronization()
    {
        OneTimeWorkRequest oneTimeWorkRequest = OneTimeWorkRequest.from(SynchronisationWorker.class);
        WorkManager.getInstance(getApplicationContext()).enqueueUniqueWork(SynchronisationWorker.OneTimeWorkName, ExistingWorkPolicy.KEEP, oneTimeWorkRequest);
    }

    public DeviceTreeService() {
    }

    @Override
    public void onCreate() {
        Intent intent = new Intent(this, DiscoveryService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        unbindService(serviceConnection);
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

        Intent intent = new Intent();
        intent.setAction(SyncFinishedAction);
        getApplicationContext().sendBroadcast(intent);
    }

    public void requestDeviceTreeUpdate()
    {
        if(discoveryService == null)
        {
            Intent intent = new Intent(this, DiscoveryService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        else
        {
            requestSynchronization();
        }
    }

}