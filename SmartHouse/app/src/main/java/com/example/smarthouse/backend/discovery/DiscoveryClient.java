package com.example.smarthouse.backend.discovery;

import android.content.Context;

public interface DiscoveryClient {
    interface DiscoveryCallback
    {
        void onServerDiscovered(Discovery discovery);
    }

    void discoverServer(DiscoveryCallback callback, Context context);

    void cancel();
    boolean isDone();

}
