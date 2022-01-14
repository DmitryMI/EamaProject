using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse.MockImplementation
{
    public class MockWashingMachine : MockAppliance, IMachine
    {
        private const float ChangeChancePerSecond = 0.05f;

        private float workTimeLeft;
        private bool isWorking;
        private float washingTemperature;

        public MockWashingMachine(int id, float x, float y, string name) : base(id, x, y, name)
        {
        }

        public override bool IsOn { 
            get => isWorking; 
            set
            {
                bool oldIsWorking = isWorking;
                isWorking = value;
                if (oldIsWorking && !value)
                {
                    OnTurnedOff();
                }
                if(!oldIsWorking && value)
                {
                    OnTurnedOn();
                }                
            }
        }

        public float WashingTemperature => washingTemperature;
        public float WashingDuration => workTimeLeft;

        public string NextWashingProgram
        {
            get => "N/A";
            set
            {
                if(value == "Default")
                {
                    washingTemperature = 60.0f;
                    workTimeLeft = 2 * 60 * 60;
                    OnProgramReceived();
                }
                else if(value == "Short")
                {
                    washingTemperature = 60.0f;
                    workTimeLeft = 60 * 60;
                    OnProgramReceived();
                }
                else if(value == "Hot")
                {
                    washingTemperature = 80.0f;
                    workTimeLeft = 60 * 60;
                    OnProgramReceived();
                }
            }
        }

        private void OnProgramReceived()
        {
            IsOn = true;
            OnValueChanged(nameof(WashingDuration));
            OnValueChanged(nameof(WashingTemperature));
        }

        public event Action<IMachine, string> OnNotificationEvent;

        public void OnTurnedOn()
        {
            OnValueChanged(nameof(IsOn));
        }

        public void OnTurnedOff()
        {
            OnValueChanged(nameof(IsOn));
        }

        public override string GetApplianceType()
        {
            return "WashingMachine";
        }

        public override void SimulationUpdate(float deltaTime)
        {
            if (!isWorking)
            {
                // Start machine with a random program
                if(RandomUtils.GetBoolWithChange(ChangeChancePerSecond * deltaTime))
                {
                    string mode = RandomUtils.GetRandomValue(new string[] { "Default", "Short", "Hot" });
                    NextWashingProgram = mode;
                }
            }
            else
            {
                workTimeLeft -= deltaTime;
                if (workTimeLeft <= 0)
                {
                    workTimeLeft = 0;
                    isWorking = false;
                    OnValueChanged(nameof(IsOn));
                    OnNotificationEvent?.Invoke(this, "Washing finished");
                }
            }
        }
    }
}
