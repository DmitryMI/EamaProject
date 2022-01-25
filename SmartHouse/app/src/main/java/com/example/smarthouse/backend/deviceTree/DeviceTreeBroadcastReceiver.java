package com.example.smarthouse.backend.deviceTree;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.smarthouse.backend.deviceTree.types.Apartment;
import com.example.smarthouse.backend.discovery.Discovery;

import java.util.ArrayList;

public class DeviceTreeBroadcastReceiver extends BroadcastReceiver {

    public interface DeviceTreeReceiver
    {
        void onDeviceTreeReceived();
    }

    private final ArrayList<DeviceTreeReceiver> receivers = new ArrayList<>();

    public void AddReceiver(DeviceTreeReceiver receiver)
    {
        receivers.add(receiver);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        for (DeviceTreeReceiver receiver : receivers)
        {
            receiver.onDeviceTreeReceived();
        }
    }

    public DeviceTreeBroadcastReceiver(DeviceTreeReceiver receiver)
    {
        receivers.add(receiver);
    }
}
