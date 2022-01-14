using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse.MockImplementation
{
    public abstract class MockAppliance : IAppliance
    {
        public int Id { get; }
        public float RelativeX { get; set; }

        public float RelativeY { get; set; }

        public string Name { get; set; }

        //public Room Parent { get; set; }
        public abstract bool IsOn { get; set; }

        public event Action<IAppliance, string> OnValueChangedEvent;

        protected void OnValueChanged(string variableName)
        {
            if (OnValueChangedEvent != null)
            {
                OnValueChangedEvent(this, variableName);
            }
        }

        public abstract string GetApplianceType();

        public abstract void SimulationUpdate(float deltaTime);

        public MockAppliance(int id, float x, float y, string name)
        {
            Id = id;
            Name = name;
            RelativeX = x;
            RelativeY = y;
        }
    }
}
