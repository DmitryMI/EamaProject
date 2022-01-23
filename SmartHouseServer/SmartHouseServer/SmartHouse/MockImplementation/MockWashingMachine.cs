using SmartHouseServer.SmartHouse.Helpers;
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

        [RestVariable(RestAccess.ReadWrite, "isOn")]
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

        [RestVariable(RestAccess.ReadOnly, "applianceType")]
        public override string ApplianceType => "WashingMachine";

        [RestVariable(RestAccess.ReadOnly, "washingTemperature")]
        public float WashingTemperature => washingTemperature;
        [RestVariable(RestAccess.ReadOnly, "washingDuration")]
        public float WashingDuration => workTimeLeft;

        [RestVariable(RestAccess.WriteOnly, "nextWashingProgram")]
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

        private void OnTurnedOn()
        {
            OnValueChanged(nameof(IsOn));
        }

        private void OnTurnedOff()
        {
            OnValueChanged(nameof(IsOn));
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
