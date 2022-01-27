package com.example.smarthouse.backend.deviceTree;

import com.example.smarthouse.backend.deviceTree.types.Apartment;
import com.example.smarthouse.backend.deviceTree.types.Appliance;
import com.example.smarthouse.backend.deviceTree.types.LightSource;
import com.example.smarthouse.backend.deviceTree.types.Room;
import com.example.smarthouse.backend.deviceTree.types.SimulatedLightSource;
import com.example.smarthouse.backend.deviceTree.types.SimulatedTemperatureSensor;
import com.example.smarthouse.backend.deviceTree.types.SimulatedWashingMachine;
import com.example.smarthouse.backend.deviceTree.types.TemperatureSensor;
import com.example.smarthouse.backend.deviceTree.types.WashingMachine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

public class MockDeviceTreeClient extends DeviceTreeClient{

    private final Apartment apartment;

    private final ArrayList<SimulatedLightSource> simulatedLights = new ArrayList<>();
    private final ArrayList<SimulatedWashingMachine> simulatedWashingMachines = new ArrayList<>();
    private final ArrayList<SimulatedTemperatureSensor> simulatedTemperatureSensors = new ArrayList<>();

    private Thread simulationThread;
    private SimulationRunnable simulationRunnable;

    public MockDeviceTreeClient()
    {
        SimulatedLightSource livingRoomPrimaryLight = new SimulatedLightSource(0, 0, 0, "Living Room Light");
        simulatedLights.add(livingRoomPrimaryLight);
        SimulatedLightSource livingRoomWallLight = new SimulatedLightSource(1, 2.4f, 0, "Living Room Wall Light");
        simulatedLights.add(livingRoomWallLight);
        SimulatedTemperatureSensor temperatureSensor = new SimulatedTemperatureSensor(2, -2.4f, 2.4f, "Living Room Temperature Sensor");
        simulatedTemperatureSensors.add(temperatureSensor);

        Room livingRoom = new Room(0, "Living Room", 5, 5, 0, 0, new Appliance[] { livingRoomPrimaryLight, livingRoomWallLight, temperatureSensor });

        SimulatedLightSource bedroomLight = new SimulatedLightSource(0, 0, 0, "Bedroom Light");
        simulatedLights.add(bedroomLight);

        Room bedroom = new Room(1, "Bedroom", 4, 5, -4.5f, 0, new Appliance[] { bedroomLight });

        SimulatedLightSource bathroomLight = new SimulatedLightSource(0, 0, 0, "Bathroom Light");
        simulatedLights.add(bathroomLight);

        SimulatedWashingMachine washingMachine = new SimulatedWashingMachine(1, 2, 0, "Washing Machine");
        simulatedWashingMachines.add(washingMachine);

        Room bathroom = new Room(2, "Bathroom", 4, 2.5f, 4.5f, -1.25f, new Appliance[] { bathroomLight, washingMachine });

        SimulatedLightSource wcLight = new SimulatedLightSource(0, 0, 0, "WC Light");
        simulatedLights.add(wcLight);

        Room wcRoom = new Room(3, "WC", 4, 2.5f, 4.5f, 1.25f, new Appliance[] { wcLight });

        apartment = new Apartment("Home", 10, 10, new Room[] { livingRoom, bedroom, bathroom, wcRoom });

        simulationRunnable = new SimulationRunnable();
        simulationThread = new Thread(simulationRunnable);
        simulationThread.start();
    }

    private class SimulationRunnable implements Runnable {
        private boolean stopSimulation;
        private final Random random = new Random();

        private void simulationLoop(float deltaTime) {
            for(SimulatedLightSource lightSource : simulatedLights)
            {
                lightSource.setIsOn(random.nextBoolean());
            }
            for(SimulatedWashingMachine washingMachine : simulatedWashingMachines)
            {
                washingMachine.simulationUpdate(deltaTime);
            }
            for(SimulatedTemperatureSensor temperatureSensor : simulatedTemperatureSensors)
            {
                temperatureSensor.setValue(random.nextFloat() * 30.0f);
            }
        }

        @Override
        public void run() {
            while (!stopSimulation)
            {
                simulationLoop(1000);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setVariable(int roomId, int applianceId, String variable, Object value) {
        Appliance appliance = apartment.getRooms()[roomId].getAppliances()[applianceId];
        try {
            Field field = appliance.getClass().getField(variable);
            field.set(appliance, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getVariable(ObjectReceivedCallback<Object> callback, int roomId, int applianceId, String variable) {
        Appliance appliance = apartment.getRooms()[roomId].getAppliances()[applianceId];
        try {
            Field field = appliance.getClass().getField(variable);
            Object value = field.get(appliance);
            if(value == null)
            {
                return;
            }
            callback.onObjectReceived(value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getAppliance(ObjectReceivedCallback<Appliance> callback, int roomId, int applianceId) {
        callback.onObjectReceived(apartment.getRooms()[roomId].getAppliances()[applianceId]);
    }

    @Override
    public void getRoom(ObjectReceivedCallback<Room> callback, int roomId) {
        callback.onObjectReceived(apartment.getRooms()[roomId]);
    }

    @Override
    public void getApartment(ObjectReceivedCallback<Apartment> callback) {
        // TODO Failure simulation
        callback.onObjectReceived(apartment);
    }

}
