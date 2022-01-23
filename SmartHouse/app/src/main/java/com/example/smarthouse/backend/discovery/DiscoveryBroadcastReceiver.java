package com.example.smarthouse.backend.discovery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class DiscoveryBroadcastReceiver extends BroadcastReceiver {

    public interface DiscoveryReceiver
    {
        void OnDiscoveryReceived(Discovery discovery);
    }

    private final ArrayList<DiscoveryReceiver> receivers = new ArrayList<>();

    public void AddReceiver(DiscoveryReceiver receiver)
    {
        receivers.add(receiver);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Discovery discovery = intent.getParcelableExtra(Discovery.IntentPayloadName);

        for (DiscoveryReceiver receiver : receivers)
        {
            receiver.OnDiscoveryReceived(discovery);
        }
    }

    public DiscoveryBroadcastReceiver(DiscoveryReceiver receiver)
    {
        receivers.add(receiver);
    }
}
