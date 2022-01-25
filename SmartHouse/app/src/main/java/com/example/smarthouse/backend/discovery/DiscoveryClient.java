package com.example.smarthouse.backend.discovery;

public interface DiscoveryClient {
    interface DiscoveryCallback
    {
        void onServerDiscovered(Discovery discovery);
    }

    void discoverServer(DiscoveryCallback callback);

    void cancel();
    boolean isDone();

}
