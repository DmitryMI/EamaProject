package com.example.smarthouse;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthouse.backend.deviceTree.DeviceTreeBroadcastReceiver;
import com.example.smarthouse.backend.deviceTree.DeviceTreeService;
import com.example.smarthouse.backend.deviceTree.types.Apartment;
import com.example.smarthouse.backend.deviceTree.types.Appliance;
import com.example.smarthouse.backend.deviceTree.types.Room;
import com.example.smarthouse.backend.deviceTree.types.WashingMachine;
import com.example.smarthouse.backend.location.LocationService;
import com.example.smarthouse.ui.DrawApartment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class DevicesActivity extends AppCompatActivity implements DeviceTreeBroadcastReceiver.DeviceTreeReceiver {

    private Apartment apartment;
    private DeviceTreeService deviceTreeService;
    private DeviceTreeBroadcastReceiver deviceTreeBroadcastReceiver;

    RecyclerView recyclerView;
    List<String> machineArray = new ArrayList<>();

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            if (service instanceof DeviceTreeService.LocalBinder) {
                DeviceTreeService.LocalBinder binder = (DeviceTreeService.LocalBinder) service;
                deviceTreeService = binder.getService();
                deviceTreeService.requestDeviceTreeUpdate();

                startForegroundSynchronizationLoop();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // TODO Check component name
            deviceTreeService = null;
        }
    };

    private void startForegroundSynchronizationLoop() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                deviceTreeService.requestDeviceTreeUpdate();
                handler.postDelayed(this, MainActivity.DeviceTreeRefreshRateMs);
            }
        }, MainActivity.DeviceTreeRefreshRateMs);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        deviceTreeBroadcastReceiver = new DeviceTreeBroadcastReceiver(this);
        IntentFilter deviceTreeFilter = new IntentFilter(DeviceTreeService.SyncFinishedAction);
        registerReceiver(deviceTreeBroadcastReceiver, deviceTreeFilter);
        Intent deviceTreeServiceBind = new Intent(this, DeviceTreeService.class);
        bindService(deviceTreeServiceBind, serviceConnection, Context.BIND_AUTO_CREATE);

        recyclerView = findViewById(R.id.recyclerView);

        machineArray.add("Washing machine");
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, machineArray);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setSelectedItemId(R.id.navigation_devices);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_notifications:
                        startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_devices:
                        return true;
                }
                return false;
            }
        });


    }


    public void startWashingbutton(View view) {
        ImageButton startWashingButton = findViewById(R.id.startWashingbutton);
        startWashingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(DevicesActivity.this, startWashingButton);
                popup.getMenuInflater().inflate(R.menu.start_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        CharSequence mode = item.getTitle();
                        String modeStr = mode.toString();
                        WashingMachine washingMachine = getWashingMachine();
                        if (washingMachine == null) {
                            return true;
                        }
                        washingMachine.setNextWashingProgram(modeStr);
                        deviceTreeService.sendDeviceTree(apartment);

                        return true;
                    }
                });

                popup.show();
            }
        });
    }

    private WashingMachine getWashingMachine() {
        if (apartment == null) {
            return null;
        }

        for (Room room : apartment.getRooms()) {
            for (Appliance appliance : room.getAppliances()) {
                if (appliance instanceof WashingMachine) {
                    return (WashingMachine) appliance;
                }
            }
        }

        return null;
    }

    public void stopWashingButton(View view) {
        WashingMachine washingMachine = getWashingMachine();
        if (washingMachine == null) {
            return;
        }
        washingMachine.setIsOn(false);
        deviceTreeService.sendDeviceTree(apartment);
    }

    @Override
    public void onDeviceTreeReceived() {
        apartment = deviceTreeService.getDeviceTree();

        WashingMachine washingMachine = getWashingMachine();
        if (washingMachine == null) {
            return;
        }
        boolean isOn = washingMachine.getIsOn();
        float timeLeftSeconds = washingMachine.getWorkTimeLeft();
        float temperature = washingMachine.getWashingTemperature();

        TextView isOnLabel = findViewById(R.id.ison);
        TextView timeLeftSecondsLabel = findViewById(R.id.timeleft);
        TextView temperatureLabel = findViewById(R.id.temperature);

        if (isOn == true) {

            isOnLabel.setText("is On");
            timeLeftSecondsLabel.setText("Time left:" + timeLeftSeconds / 60);
            temperatureLabel.setText("Temperature:" + temperature);
        } else {
            isOnLabel.setText("is Off");
            timeLeftSecondsLabel.setText(" ");
            temperatureLabel.setText(" ");
        }

    }


}
