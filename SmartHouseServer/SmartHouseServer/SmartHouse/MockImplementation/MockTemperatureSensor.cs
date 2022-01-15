using SmartHouseServer.SmartHouse.Helpers;
using SmartHouseServer.SmartHouse.Sensors;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse.MockImplementation
{
    public class MockTemperatureSensor : MockAppliance, ITemperatureSensor
    {
        private float value;
        private float beforeNextMeasurement;
        private bool isOn;

        [RestVariable(RestAccess.ReadWrite, "isOn")]
        public override bool IsOn
        {
            get => isOn;
            set
            {
                if(isOn != value)
                {
                    isOn = value;
                    OnValueChanged(nameof(IsOn));
                }
                else
                {
                    isOn = value;
                }
            }
        }

        [RestVariable(RestAccess.ReadWrite, "measurementInterval")]
        public float MeasurementInterval { get; set; }

        public float Value => value;

        public override string GetApplianceType()
        {
            return "TemperatureSensor";
        }

        public string GetSensorType()
        {
            throw new NotImplementedException();
        }

        public override void SimulationUpdate(float deltaTime)
        {
            if(!IsOn)
            {
                return;
            }
            beforeNextMeasurement -= deltaTime;
            value = RandomUtils.FloatInRange(value, 10.0f, 30.0f, 1.0f);
            if (beforeNextMeasurement <= 0)
            {
                OnValueChanged(nameof(Value));
                beforeNextMeasurement = MeasurementInterval;
            }
        }

        public MockTemperatureSensor(int id, float x, float y, string name) : base(id, x, y, name)
        {

        }
    }
}
