using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.Discovery
{
    public class DiscoveryBadRequestException : Exception
    {
        public DiscoveryBadRequestException(string message) : base(message)
        {

        }
    }
}
