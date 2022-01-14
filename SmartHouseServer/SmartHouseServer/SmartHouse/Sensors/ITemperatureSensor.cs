using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse.Sensors
{
    public interface ITemperatureSensor : ISensor
    {
        float MeasurementInterval { get; set; }
    }
}
