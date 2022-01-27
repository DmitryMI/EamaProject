package com.example.smarthouse.backend.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.smarthouse.backend.discovery.Discovery;
import com.example.smarthouse.backend.discovery.DiscoveryBroadcastReceiver;

import java.util.ArrayList;

public class LocationUpdatedBroadcastReceiver extends BroadcastReceiver {

    public interface LocationUpdateReceiver
    {
        void onLocationReceived();
    }

    private final ArrayList<LocationUpdateReceiver> receivers = new ArrayList<>();

    public void AddReceiver(LocationUpdateReceiver receiver)
    {
        receivers.add(receiver);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        for (LocationUpdateReceiver receiver : receivers)
        {
            receiver.onLocationReceived();
        }
    }

    public LocationUpdatedBroadcastReceiver(LocationUpdateReceiver receiver)
    {
        receivers.add(receiver);
    }
}
