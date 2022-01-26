package com.example.smarthouse.backend.discovery;

public class MockDiscoveryClient implements DiscoveryClient{
    @Override
    public void discoverServer(DiscoveryCallback callback) {
        callback.onServerDiscovered(new Discovery(true, "localhost", "localhost"));
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isDone() {
        return true;
    }
}
