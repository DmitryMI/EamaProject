using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse.MockImplementation
{
    public class Simulator : IRepository, IDisposable
    {
        private const int SimulationDelayMs = 100;

        private List<MockLightSource> simulatedLights = new List<MockLightSource>();
        private List<MockAppliance> simulatedMachines = new List<MockAppliance>();
        private List<MockAppliance> simulatedSensors = new List<MockAppliance>();

        private List<Room> simulatedRooms = new List<Room>();
        private Apartment simulatedApartment;

        private bool isSimulationRunning;

        private Task simulationThread;

        public void StopSimulation()
        {
            isSimulationRunning = false;
            simulationThread.Wait();
        }

        public Simulator()
        {
            CreateMockLayout();

            simulationThread = new Task(SimulationLoop, TaskCreationOptions.LongRunning);
            isSimulationRunning = true;
            simulationThread.Start();
        }

        private void CreateMockLayout()
        {
            MockLightSource livingRoomPrimaryLight = new MockLightSource(0, 0, 0, "Living Room Light");
            simulatedLights.Add(livingRoomPrimaryLight);
            MockLightSource livingRoomWallLight = new MockLightSource(1, 2.4f, 0, "Living Room Wall Light");
            simulatedLights.Add(livingRoomWallLight);
            MockTemperatureSensor temperatureSensor = new MockTemperatureSensor(2, -2.4f, 2.4f, "Living Room Temperature Sensor");
            simulatedSensors.Add(temperatureSensor);

            Room livingRoom = new Room(0, "Living Room", 5, 5, 0, 0, new IAppliance[] { livingRoomPrimaryLight, livingRoomWallLight, temperatureSensor });
            simulatedRooms.Add(livingRoom);


            MockLightSource bedroomLight = new MockLightSource(0, 0, 0, "Bedroom Light");
            simulatedLights.Add(bedroomLight);

            Room bedroom = new Room(1, "Bedroom", 4, 5, -4.5f, 0, new IAppliance[] { bedroomLight });
            simulatedRooms.Add(bedroom);


            MockLightSource bathroomLight = new MockLightSource(0, 0, 0, "Bathroom Light");
            simulatedLights.Add(bathroomLight);

            MockWashingMachine washingMachine = new MockWashingMachine(1, 2, 0, "Washing Machine");
            simulatedMachines.Add(washingMachine);

            Room bathroom = new Room(2, "Bathroom", 4, 3, 4.5f, -1, new IAppliance[] { bathroomLight, washingMachine });
            simulatedRooms.Add(bathroom);


            MockLightSource wcLight = new MockLightSource(0, 0, 0, "WC Light");
            simulatedLights.Add(wcLight);

            Room wcRoom = new Room(3, "WC", 3, 2, 3.5f, 1.5f, new IAppliance[] { wcLight });
            simulatedRooms.Add(bathroom);


            simulatedApartment = new Apartment("Home", 10, 10, new Room[] { livingRoom, bedroom, bathroom, wcRoom });
        }

        private void SimulationLoop()
        {
            Stopwatch sw = new Stopwatch();
            float deltaTime = 1.0f / SimulationDelayMs;
            while(isSimulationRunning)
            {
                sw.Start();
                foreach (MockLightSource light in simulatedLights)
                {
                    light.SimulationUpdate(deltaTime);
                }
                foreach (MockAppliance machine in simulatedMachines)
                {
                    machine.SimulationUpdate(deltaTime);
                }
                foreach (MockAppliance sensor in simulatedSensors)
                {
                    sensor.SimulationUpdate(deltaTime);
                }

                Thread.Sleep(SimulationDelayMs);

                sw.Stop();
                long ms = sw.ElapsedMilliseconds;
                sw.Reset();
                deltaTime = (float)ms / 1000.0f;
            }
        }

        public Apartment GetApartment()
        {
            return simulatedApartment;
        }

        public void Dispose()
        {
            isSimulationRunning = false;
            simulationThread.Wait();
            simulationThread.Dispose();
        }
    }
}
