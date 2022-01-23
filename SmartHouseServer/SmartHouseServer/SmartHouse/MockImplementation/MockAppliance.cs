using SmartHouseServer.SmartHouse.Helpers;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse.MockImplementation
{
    public abstract class MockAppliance : IAppliance
    {

        [RestVariable(RestAccess.ReadOnly, "id")]
        public int Id { get; }
        [RestVariable(RestAccess.ReadOnly, "relativeX")]
        public float RelativeX { get; set; }
        [RestVariable(RestAccess.ReadOnly, "relativeY")]
        public float RelativeY { get; set; }
        [RestVariable(RestAccess.ReadOnly, "name")]
        public string Name { get; set; }

        [RestVariable(RestAccess.ReadWrite, "isOn")]
        public abstract bool IsOn { get; set; }
        public abstract string ApplianceType { get; }

        public event Action<IAppliance, string> OnValueChangedEvent;

        protected void OnValueChanged(string variableName)
        {
            if (OnValueChangedEvent != null)
            {
                OnValueChangedEvent(this, variableName);
            }
        }


        public abstract void SimulationUpdate(float deltaTime);

        public void RestDeserialize(string variable, string value)
        {
            
        }

        public MockAppliance(int id, float x, float y, string name)
        {
            Id = id;
            Name = name;
            RelativeX = x;
            RelativeY = y;
        }
    }
}
