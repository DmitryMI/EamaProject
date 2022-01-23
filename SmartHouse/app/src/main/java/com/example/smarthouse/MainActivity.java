package com.example.smarthouse;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.example.smarthouse.backend.discovery.Discovery;
import com.example.smarthouse.backend.discovery.DiscoveryService;
import com.example.smarthouse.backend.restAPI.SynchronisationWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity{

    private DiscoveryService discoveryService;
    private boolean isBoundToDiscoveryService;

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            if(service instanceof DiscoveryService.LocalBinder) {
                DiscoveryService.LocalBinder binder = (DiscoveryService.LocalBinder) service;
                discoveryService = binder.getService();
                isBoundToDiscoveryService = true;

                discoveryService.StartDiscovery(new DiscoveryService.DiscoveryReceivedCallback() {
                    @Override
                    public void OnDiscoveryReceived(Discovery discovery) {
                        OnDiscoveryReceivedCallback(discovery);
                    }
                });
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            if(arg0.getClassName().equals(discoveryService.getClass().getName())) {
                isBoundToDiscoveryService = false;
            }
        }
    };

    private void OnDiscoveryReceivedCallback(Discovery discovery)
    {
        Toast toast = Toast.makeText(getApplicationContext(), String.format("Discovery: %b, %s", discovery.isLan(), discovery.getUrl()), Toast.LENGTH_SHORT);
        toast.show();
    }

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


        PeriodicWorkRequest periodic = new PeriodicWorkRequest
                .Builder(SynchronisationWorker.class, 16, TimeUnit.MINUTES).build();
        WorkManager
                .getInstance(getApplicationContext())
                .enqueueUniquePeriodicWork("smartHouseSync", ExistingPeriodicWorkPolicy.KEEP, periodic);

        Intent intent = new Intent(this, DiscoveryService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }


}
