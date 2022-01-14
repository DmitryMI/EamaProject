using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse.MockImplementation
{
    public class MockLightSource : MockAppliance, ILightSource
    {
        private const float ChangeChancePerSecond = 0.1f;

        private bool isOn;   

        public override bool IsOn { 
            get => isOn; 
            set 
            {
                if (isOn != value)
                {
                    isOn = value; 
                    OnIsOnChanged();
                }
            } 
        }

        public MockLightSource(int id, float x, float y, string name) : base(id, x, y, name)
        {
            RelativeX = x;
            RelativeY = y;
            Name = name;
        }

        public override string GetApplianceType()
        {
            return "LightSource";
        }

        public void OnIsOnChanged()
        {
            // The real LightSource will send command over CAN network
            // We can simulate errors here (e.g. CAN device is not responding)

            OnValueChanged(nameof(IsOn));
        }

        public override void SimulationUpdate(float deltaTime)
        {
            if(RandomUtils.GetBoolWithChange(ChangeChancePerSecond * deltaTime))
            {
                // Randomly toggle light

                IsOn = !IsOn;
            }
        }
    }
}
