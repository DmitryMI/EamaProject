package com.example.smarthouse.backend.discovery;

import android.content.Context;

import com.example.smarthouse.backend.location.NetworkInfoProvider;
import com.example.smarthouse.backend.location.NetworkState;

import java.util.Calendar;

public class MockDiscoveryClient implements DiscoveryClient{
    @Override
    public void discoverServer(DiscoveryCallback callback, Context context) {
        NetworkInfoProvider networkInfoProvider = new NetworkInfoProvider();
        NetworkState networkState = networkInfoProvider.getNetworkState(context);
        boolean isLan = networkState.hasWiFiConnection();

        callback.onServerDiscovered(new Discovery(isLan, "localhost", "localhost"));
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isDone() {
        return true;
    }
}
