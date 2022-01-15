using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse.Helpers
{
    public static class RestVariableHelper
    {
        private static MethodInfo GetParser(Type type)
        {
            MethodInfo[] parseMethods = type.GetMethods(BindingFlags.Public | BindingFlags.Static);
            var parser = parseMethods.FirstOrDefault(m => m.Name == "Parse" && m.GetParameters().Length == 1);
            return parser;
        }

        public static bool CanDeserialize(Type type)
        {
            if(type == typeof(string))
            {
                return true;
            }

            return GetParser(type) != null;
        }

        public static object Deserialize(Type type, string value)
        {
            if(type == typeof(string))
            {
                return value;
            }
            MethodInfo parser = GetParser(type);
            if(parser == null)
            {
                return null;
            }
            object result = parser.Invoke(null, new[] { value });
            return result;
        }

        public static IReadOnlyList<IRestInvokable> GetRestVariables(object restObject)
        {
            Type type = restObject.GetType();
            List<IRestInvokable> restInvokables = new List<IRestInvokable>();
            MemberInfo[] members = type.GetMembers(BindingFlags.Public);
            foreach(var member in members)
            {
                RestVariableAttribute restVariableAttribute = member.GetCustomAttribute<RestVariableAttribute>();
                if(restVariableAttribute == null)
                {
                    continue;
                }

                if(member is PropertyInfo propertyInfo)
                {
                    RestProperty restProperty = new RestProperty(restObject, propertyInfo);
                    restInvokables.Add(restProperty);
                }
            }

            return restInvokables;
        }

        public static IRestInvokable GetRestVariable(object restObject, string name)
        {
            Type type = restObject.GetType();
            MemberInfo[] members = type.GetMembers(BindingFlags.Public | BindingFlags.Instance | BindingFlags.IgnoreCase);
            foreach (var member in members)
            {
                RestVariableAttribute restVariableAttribute = member.GetCustomAttribute<RestVariableAttribute>();
                if (restVariableAttribute == null)
                {
                    continue;
                }

                if(restVariableAttribute.RestApiName == name || member.Name == name)
                {
                    if(member is PropertyInfo propertyInfo)
                    {
                        RestProperty restProperty = new RestProperty(restObject, propertyInfo);
                        return restProperty;
                    }
                }
            }

            return null;
        }
    }
}
