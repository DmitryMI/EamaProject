using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse
{
    public interface ILightSource : IAppliance
    {
        float Brightness { get; set; }        
    }
}
