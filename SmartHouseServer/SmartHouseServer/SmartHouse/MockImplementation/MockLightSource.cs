using SmartHouseServer.SmartHouse.Helpers;
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
        private float brightness;

        [RestVariable(RestAccess.ReadWrite, "brightness")]
        public float Brightness
        {
            get => brightness;
            set
            {
                brightness = value;
                OnBrightnessChanged();
            }
        }

        [RestVariable(RestAccess.ReadWrite, "isOn")]
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

        public void OnBrightnessChanged()
        {
            OnValueChanged(nameof(Brightness));
        }

        public override void SimulationUpdate(float deltaTime)
        {
            // Randomly toggle light
            if (RandomUtils.GetBoolWithChange(ChangeChancePerSecond * deltaTime))
            {                
                IsOn = !IsOn;
            }
        }
    }
}
