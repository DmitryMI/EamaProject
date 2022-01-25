package com.example.smarthouse.backend.deviceTree;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.example.smarthouse.backend.deviceTree.types.Apartment;
import com.example.smarthouse.backend.discovery.Discovery;
import com.example.smarthouse.backend.discovery.DiscoveryService;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SynchronisationWorker extends ListenableWorker {

    public static final String PeriodicWorkName = "SYNCHRONIZATION_PERIODIC";
    public static final String OneTimeWorkName = "SYNCHRONIZATION_ONE_TIME";

    private final DeviceTreeClient client;
    private Apartment apartment;
    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            if(service instanceof DeviceTreeService.LocalBinder) {
                DeviceTreeService.LocalBinder binder = (DeviceTreeService.LocalBinder) service;
                DeviceTreeService deviceTreeService = binder.getService();
                deviceTreeService.onDeviceTreeReceived(apartment);

                getApplicationContext().unbindService(this);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public SynchronisationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        client = DeviceTreeService.getDeviceTreeClient();
    }

    private class SyncFuture implements ListenableFuture<Result>, DeviceTreeClient.ObjectReceivedCallback<Apartment>
    {
        ArrayList<Pair<Runnable, Executor>> listeners = new ArrayList<>();
        boolean isDone;

        private void invokeListeners()
        {
            for (Pair<Runnable, Executor> runnableExecutorPair : listeners)
            {
                Executor executor = runnableExecutorPair.second;
                Runnable runnable = runnableExecutorPair.first;
                executor.execute(runnable);
            }
        }

        private void sendApartmentToService()
        {
            Context context = getApplicationContext();
            Intent intent = new Intent(context, DeviceTreeService.class);
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }


        public SyncFuture()
        {
            client.getApartment(this);
        }

        @Override
        public void addListener(Runnable listener, Executor executor) {
            listeners.add(new Pair<>(listener, executor));
        }

        @Override
        public boolean cancel(boolean b) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return isDone;
        }

        @Override
        public Result get() throws ExecutionException, InterruptedException {
            if(apartment == null)
            {
                return Result.failure();
            }
            return Result.success();
        }

        @Override
        public Result get(long l, TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
            if(apartment == null)
            {
                return Result.failure();
            }
            return Result.success();
        }

        @Override
        public void onObjectReceived(Apartment obj) {
            apartment = obj;
            isDone = true;
            invokeListeners();
            sendApartmentToService();
        }

        @Override
        public void onFail(String errorMessage) {
            isDone = true;
            invokeListeners();
        }
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        return new SyncFuture();
    }
}
