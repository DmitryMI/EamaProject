package com.example.smarthouse.backend.discovery;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class DiscoveryWorker extends ListenableWorker {
    public static final String PeriodicWorkName = "DISCOVERY_PERIODIC";
    public static final String OneTimeWorkName = "DISCOVERY_ONE_TIME";

    private final DiscoveryClient client;

    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public DiscoveryWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);

        client = DiscoveryService.getDiscoveryClient();
    }

    private class DiscoveryFuture implements ListenableFuture<Result>
    {
        boolean isCanceled;
        Context context;
        ArrayList<Pair<Runnable, Executor>> listeners = new ArrayList<>();

        public DiscoveryFuture(Context context)
        {
            this.context = context;
            client.discoverServer(this::SendDiscoveryInfo, getApplicationContext());
        }

        private void SendDiscoveryInfo(Discovery discovery)
        {
            Intent intent = new Intent();
            intent.setAction(Discovery.DiscoveryAction);
            intent.putExtra(Discovery.IntentPayloadName, discovery);
            context.sendBroadcast(intent);

            for (Pair<Runnable, Executor> runnableExecutorPair : listeners)
            {
                Executor executor = runnableExecutorPair.second;
                Runnable runnable = runnableExecutorPair.first;
                executor.execute(runnable);
            }
        }

        @Override
        public void addListener(Runnable listener, Executor executor) {
            listeners.add(new Pair<>(listener, executor));
        }

        @Override
        public boolean cancel(boolean b) {
            if(b)
            {
                isCanceled = true;
                client.cancel();
            }
            return b;
        }

        @Override
        public boolean isCancelled() {
            return isCanceled;
        }

        @Override
        public boolean isDone() {
            return client.isDone();
        }

        @Override
        public Result get() throws ExecutionException, InterruptedException {
            if(client.isDone())
            {
                return Result.success();
            }

            return Result.failure();
        }

        @Override
        public Result get(long l, TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
            if(client.isDone())
            {
                return Result.success();
            }
            return Result.failure();
        }
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        Log.i("SmartHouse DiscoveryWorker", "startWork()");
        return new DiscoveryFuture(getApplicationContext());
    }
}
