using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse.Helpers
{
    public class RestProperty : IRestInvokable
    {
        private PropertyInfo propertyInfo;
        private object obj;

        public RestVariableAttribute VariableAttribute => propertyInfo.GetCustomAttribute<RestVariableAttribute>();

        public string VariableName => GetVariableName();

        public bool IsArray => propertyInfo.PropertyType.IsArray;

        private string GetVariableName()
        {
            if(VariableAttribute.RestApiName != null)
            {
                return VariableAttribute.RestApiName;
            }
            return propertyInfo.Name;
        }

        public RestProperty(object owner, PropertyInfo propertyInfo)
        {
            obj = owner;
            this.propertyInfo = propertyInfo;
        }

        public object Get()
        {
            return propertyInfo.GetValue(obj);
        }

        public void Set(object value)
        {
            if(VariableAttribute.AccessMode == RestAccess.ReadOnly)
            {
                throw new ReadOnlyVariableException(obj.GetType(), propertyInfo.Name); 
            }
            propertyInfo.SetValue(obj, value);
        }

        public void Set(string value)
        {
            if (VariableAttribute.AccessMode == RestAccess.ReadOnly)
            {
                throw new ReadOnlyVariableException(obj.GetType(), propertyInfo.Name);
            }
            if(RestVariableHelper.CanDeserialize(propertyInfo.PropertyType))
            {
                object deserialized = RestVariableHelper.Deserialize(propertyInfo.PropertyType, value);
                propertyInfo.SetValue(obj, deserialized);
            }
            
        }
    }
}
