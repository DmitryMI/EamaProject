package com.example.smarthouse.backend.discovery;

public class DiscoveryClient {

    private final Thread discoveryThread;
    private final DiscoveryCallback callback;
    private boolean isStarted = false;
    private boolean isDone = false;
    private boolean isCanceled = false;

    public interface DiscoveryCallback
    {
        void OnServerDiscovered(Discovery discovery);
    }

    private class DiscoveryRunnable implements Runnable
    {
        @Override
        public void run() {
            Discovery discovery = new Discovery(true, "localhost:5001");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isDone = true;
            isStarted = false;
            callback.OnServerDiscovered(discovery);
        }
    }

    public DiscoveryClient(DiscoveryCallback callback)
    {
        this.callback = callback;
        discoveryThread = new Thread(new DiscoveryRunnable());
    }

    public void cancel()
    {
        isCanceled = true;
    }

    public void discoverServer()
    {
        if(isStarted && !isDone)
        {
            return;
        }
        isCanceled = false;
        discoveryThread.start();
        isStarted = true;
    }

    public boolean isDone()
    {
        return isDone;
    }
}
