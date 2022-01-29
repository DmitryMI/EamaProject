package com.example.smarthouse;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthouse.backend.deviceTree.DeviceTreeBroadcastReceiver;
import com.example.smarthouse.backend.deviceTree.DeviceTreeService;
import com.example.smarthouse.backend.deviceTree.MockDeviceTreeClient;
import com.example.smarthouse.backend.deviceTree.types.Apartment;
import com.example.smarthouse.backend.deviceTree.types.Appliance;
import com.example.smarthouse.backend.deviceTree.types.LightSource;
import com.example.smarthouse.backend.deviceTree.types.Room;
import com.example.smarthouse.backend.deviceTree.types.TemperatureSensor;
import com.example.smarthouse.backend.deviceTree.types.WashingMachine;
import com.example.smarthouse.backend.location.LocationInfo;
import com.example.smarthouse.backend.location.LocationService;
import com.example.smarthouse.backend.location.LocationUpdatedBroadcastReceiver;
import com.example.smarthouse.ui.DrawApartment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements DeviceTreeBroadcastReceiver.DeviceTreeReceiver, LocationUpdatedBroadcastReceiver.LocationUpdateReceiver {
    public static final int FineLocationPermissionRequestCode = 100;
    private static final int DeviceTreeRefreshRateMs = 5000;
    private static final int LocationRefreshRateMs = 10000;

    private DeviceTreeService deviceTreeService;
    private LocationService locationService;

    private DeviceTreeBroadcastReceiver deviceTreeBroadcastReceiver;
    private LocationUpdatedBroadcastReceiver locationUpdatedBroadcastReceiver;

    private Apartment apartment;
    private DrawApartment drawApartment;
    private LocationInfo previousLocation;
    private LocationInfo currentLocation;

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            if (service instanceof DeviceTreeService.LocalBinder)
            {
                DeviceTreeService.LocalBinder binder = (DeviceTreeService.LocalBinder) service;
                deviceTreeService = binder.getService();
                deviceTreeService.requestDeviceTreeUpdate();

                drawApartment.setDeviceTreeService(deviceTreeService);

                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        deviceTreeService.requestDeviceTreeUpdate();
                        handler.postDelayed(this,DeviceTreeRefreshRateMs);
                    }
                },DeviceTreeRefreshRateMs);
            }
            if(service instanceof LocationService.LocalBinder)
            {
                LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
                locationService = binder.getService();
                locationService.requestLocationInfo();

                drawApartment.setLocationService(locationService);

                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        locationService.requestLocationInfo();
                        handler.postDelayed(this,LocationRefreshRateMs);
                    }
                },LocationRefreshRateMs);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // TODO Check component name
            deviceTreeService = null;
            locationService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        if(navView != null) {
            navView.setSelectedItemId(R.id.navigation_home);
            navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_devices:
                            startActivity(new Intent(getApplicationContext(), DevicesActivity.class));
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.navigation_notifications:
                            startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.navigation_home:
                            return true;
                    }
                    return false;
                }
            });
        }

        //drawApartment = new DrawApartment(this);
        //addContentView(drawApartment, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        drawApartment = findViewById(R.id.drawApartment);

        deviceTreeBroadcastReceiver = new DeviceTreeBroadcastReceiver(this);
        IntentFilter deviceTreeFilter = new IntentFilter(DeviceTreeService.SyncFinishedAction);
        registerReceiver(deviceTreeBroadcastReceiver, deviceTreeFilter);

        locationUpdatedBroadcastReceiver = new LocationUpdatedBroadcastReceiver(this);
        IntentFilter locationUpdateFilter = new IntentFilter(LocationService.LocationUpdatedAction);
        registerReceiver(locationUpdatedBroadcastReceiver, locationUpdateFilter);

        Intent deviceTreeServiceBind = new Intent(this, DeviceTreeService.class);
        bindService(deviceTreeServiceBind, serviceConnection, Context.BIND_AUTO_CREATE);

        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            Intent locationServiceBind = new Intent(this, LocationService.class);
            bindService(locationServiceBind, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FineLocationPermissionRequestCode);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        unregisterReceiver(deviceTreeBroadcastReceiver);
        unregisterReceiver(locationUpdatedBroadcastReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FineLocationPermissionRequestCode) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Toast.makeText(MainActivity.this, "ACCESS_FINE_LOCATION Permission Granted", Toast.LENGTH_SHORT).show();

                Intent locationServiceBind = new Intent(this, LocationService.class);
                bindService(locationServiceBind, serviceConnection, Context.BIND_AUTO_CREATE);
            }
            else {
                Toast.makeText(MainActivity.this, "ACCESS_FINE_LOCATION Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDeviceTreeReceived() {
        if(deviceTreeService == null)
        {
            return;
        }
        apartment = deviceTreeService.getDeviceTree();
        drawApartment.setApartment(apartment);
    }

    private void setAllLights(Room room, boolean isOn)
    {
        for(Appliance appliance : room.getAppliances())
        {
            if(appliance instanceof LightSource)
            {
                LightSource lightSource = (LightSource) appliance;
                lightSource.setIsOn(isOn);
            }
        }
    }

    @Override
    public void onLocationReceived() {
        if(locationService == null)
        {
            return;
        }
        if(apartment == null)
        {
            return;
        }
        LocationInfo locationInfo = locationService.getLocation();
        if(locationInfo.isValid()) {
            if(previousLocation != null && previousLocation.isValid() && previousLocation.getRoomId() != locationInfo.getRoomId())
            {
                Room previousRoom = apartment.getRooms()[previousLocation.getRoomId()];
                setAllLights(previousRoom, false);
            }
            Room currentRoom = apartment.getRooms()[locationInfo.getRoomId()];
            setAllLights(currentRoom, true);
            deviceTreeService.sendDeviceTree(apartment);
            drawApartment.setApartment(apartment);
        }
        else
        {
            /*
            Room previousRoom = apartment.getRooms()[previousLocation.getRoomId()];
            setAllLights(previousRoom, false);
            deviceTreeService.sendDeviceTree(apartment);
            drawApartment.setApartment(apartment);
            */
        }
        previousLocation = currentLocation;
        currentLocation = locationInfo;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
