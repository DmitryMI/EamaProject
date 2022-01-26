package com.example.smarthouse;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthouse.backend.deviceTree.DeviceTreeBroadcastReceiver;
import com.example.smarthouse.backend.deviceTree.DeviceTreeService;
import com.example.smarthouse.backend.deviceTree.types.Apartment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements DeviceTreeBroadcastReceiver.DeviceTreeReceiver {

    private DeviceTreeService deviceTreeService;
    private DeviceTreeBroadcastReceiver deviceTreeBroadcastReceiver;

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            if (service instanceof DeviceTreeService.LocalBinder) {
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

        navView.setSelectedItemId(R.id.navigation_home);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_devices:
                        startActivity(new Intent(getApplicationContext(), DevicesActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.navigation_home:
                        return true;
                }
                return false;
            }
        });


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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_settings:
                onClickSettings(item);
                break;
            case R.id.app_bar_search:

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onClickSettings(MenuItem item) {
        setContentView(R.layout.fragment_settings);
    }


}
