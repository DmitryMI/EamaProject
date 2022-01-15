using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse.Helpers
{
    public interface IRestInvokable
    {
        string VariableName { get; }
        bool IsArray { get; }
        RestVariableAttribute VariableAttribute { get; }
        void Set(object value);
        void Set(string value);
        object Get();
    }
}
