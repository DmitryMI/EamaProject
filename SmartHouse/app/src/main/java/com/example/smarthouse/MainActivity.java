package com.example.smarthouse;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.example.smarthouse.backend.deviceTree.DeviceTreeBroadcastReceiver;
import com.example.smarthouse.backend.deviceTree.DeviceTreeService;
import com.example.smarthouse.backend.deviceTree.types.Apartment;
import com.example.smarthouse.backend.discovery.Discovery;
import com.example.smarthouse.backend.discovery.DiscoveryService;
import com.example.smarthouse.backend.deviceTree.SynchronisationWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements DeviceTreeBroadcastReceiver.DeviceTreeReceiver {

    private DeviceTreeService deviceTreeService;
    private DeviceTreeBroadcastReceiver deviceTreeBroadcastReceiver;

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            if(service instanceof DeviceTreeService.LocalBinder) {
                DeviceTreeService.LocalBinder binder = (DeviceTreeService.LocalBinder) service;
                deviceTreeService = binder.getService();

                deviceTreeService.requestDeviceTreeUpdate();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            deviceTreeService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_devices, R.id.navigation_home, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        deviceTreeBroadcastReceiver = new DeviceTreeBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter(DeviceTreeService.SyncFinishedAction);
        registerReceiver(deviceTreeBroadcastReceiver, filter);

        Intent intent = new Intent(this, DeviceTreeService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onDeviceTreeReceived() {
        Apartment apartment = deviceTreeService.getDeviceTree();
        Toast toast = Toast.makeText(this, String.format("Apartment has %d rooms", apartment.getRooms().length), Toast.LENGTH_SHORT);
        toast.show();
    }
}
