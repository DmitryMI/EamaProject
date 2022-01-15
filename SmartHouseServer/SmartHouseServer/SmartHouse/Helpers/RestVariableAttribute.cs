using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse.Helpers
{
    public class RestVariableAttribute : Attribute
    {
        public string RestApiName { get; }
        public RestAccess AccessMode { get; }

        public RestVariableAttribute(RestAccess restAccess, string restApiName = null)
        {
            AccessMode = restAccess;
            RestApiName = restApiName;
        }
    }
}
