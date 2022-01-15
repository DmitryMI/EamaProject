using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse.Helpers
{
    public class ReadOnlyVariableException : Exception
    {
        public string Variable { get; }
        public Type RestType { get; }

        public ReadOnlyVariableException(Type type, string variable) : base($"Read-only variable: {type}->{variable}")
        {
            RestType = type;
            Variable = variable;
        }
    }
}
