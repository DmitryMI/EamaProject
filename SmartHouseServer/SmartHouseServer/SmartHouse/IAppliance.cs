using SmartHouseServer.SmartHouse.Helpers;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse
{
    public interface IAppliance : IRestSerializable
    {
        int Id { get; }
        float RelativeX { get; }
        float RelativeY { get; }
        string Name { get; }
        bool IsOn { get; set; }

        string GetApplianceType();
        event Action<IAppliance, string> OnValueChangedEvent;

        void RestDeserialize(string variable, string value);
    }
}
