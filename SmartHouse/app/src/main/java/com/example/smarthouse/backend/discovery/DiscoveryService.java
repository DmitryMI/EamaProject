package com.example.smarthouse.backend.discovery;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DiscoveryService extends Service implements DiscoveryBroadcastReceiver.DiscoveryReceiver {

    private static DiscoveryClient clientInstance;
    public static DiscoveryClient getDiscoveryClient()
    {
        if(clientInstance == null)
        {
            clientInstance = new MockDiscoveryClient();
        }
        return clientInstance;
    }

    private final LocalBinder localBinder = new LocalBinder();
    private Discovery discovery;
    private boolean areWorkersInitialized = false;
    private final ArrayList<DiscoveryReceivedCallback> oneTimeCallbacks = new ArrayList<>();
    private final ArrayList<DiscoveryReceivedCallback> permanentCallbacks = new ArrayList<>();
    private boolean isBroadcastReceiverRegistered;
    private final DiscoveryBroadcastReceiver discoveryBroadcastReceiver = new DiscoveryBroadcastReceiver(this);

    public DiscoveryService() {

    }

    private void RegisterBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(Discovery.DiscoveryAction);
        this.registerReceiver(discoveryBroadcastReceiver, filter);
        isBroadcastReceiverRegistered = true;
    }

    public void OnDiscoveryReceived(Discovery discovery)
    {
        this.discovery = discovery;
        for(DiscoveryReceivedCallback callback : oneTimeCallbacks)
        {
            callback.OnDiscoveryReceived(discovery);
        }
        oneTimeCallbacks.clear();

        for(DiscoveryReceivedCallback callback : permanentCallbacks)
        {
            callback.OnDiscoveryReceived(discovery);
        }
    }

    public Discovery getDiscovery() {
        return discovery;
    }

    private void initializeDiscoveryWorkers()
    {
        // The service is starting, due to a call to startService()

        WorkManager workManager = WorkManager.getInstance(getApplicationContext());

        //OneTimeWorkRequest discoveryNowRequest = OneTimeWorkRequest.from(DiscoveryWorker.class);
        //workManager.enqueue(discoveryNowRequest);
        Constraints.Builder constraintsBuilder = new Constraints.Builder();
        constraintsBuilder.setRequiresBatteryNotLow(true);
        constraintsBuilder.setRequiredNetworkType(NetworkType.CONNECTED);
        Constraints constraints = constraintsBuilder.build();

        PeriodicWorkRequest.Builder discoveryPeriodicRequestBuilder = new PeriodicWorkRequest.Builder(DiscoveryWorker.class, 15, TimeUnit.MINUTES);
        discoveryPeriodicRequestBuilder.setConstraints(constraints);

        workManager.enqueueUniquePeriodicWork(DiscoveryWorker.PeriodicWorkName, ExistingPeriodicWorkPolicy.REPLACE, discoveryPeriodicRequestBuilder.build());
        areWorkersInitialized = true;
    }

    public void startDiscovery(@Nullable DiscoveryReceivedCallback callback)
    {
        Log.i("SmartHouse DiscoveryService", "startDiscovery()");
        if(callback != null) {
            oneTimeCallbacks.add(callback);
        }
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        OneTimeWorkRequest discoveryNowRequest = OneTimeWorkRequest.from(DiscoveryWorker.class);
        workManager.enqueueUniqueWork(DiscoveryWorker.OneTimeWorkName, ExistingWorkPolicy.KEEP, discoveryNowRequest);
    }

    public void AddPermanentCallback(DiscoveryReceivedCallback callback)
    {
        permanentCallbacks.add(callback);
    }

    public void RemovePermanentCallback(DiscoveryReceivedCallback callback)
    {
        permanentCallbacks.remove(callback);
    }


    public class LocalBinder extends Binder
    {
        public DiscoveryService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DiscoveryService.this;
        }
    }

    public interface DiscoveryReceivedCallback
    {
        void OnDiscoveryReceived(Discovery discovery);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if(!areWorkersInitialized) {
            initializeDiscoveryWorkers();
        }
        if(!isBroadcastReceiverRegistered)
        {
            RegisterBroadcastReceiver();
        }
        return localBinder;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(discoveryBroadcastReceiver);
    }
}