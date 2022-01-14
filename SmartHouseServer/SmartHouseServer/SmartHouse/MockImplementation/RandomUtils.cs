using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse.MockImplementation
{
    public static class RandomUtils
    {
        private static Random random = new Random();

        public static bool GetBoolWithChange(double chance)
        {
            double rnd = random.NextDouble();
            return rnd < chance;
        }

        public static float FloatInRange(float value, float min, float max, float maxSpeed)
        {
            int sign = random.Next(-1, 2);
            if(sign == 0)
            {
                return value;
            }

            float delta = (float)(maxSpeed * sign * random.NextDouble());
            double nextValue = value + delta;
            if(nextValue < min)
            {
                return min;
            }
            else if(nextValue > max)
            {
                return max;
            }
            return (float)nextValue;
        }

        public static T GetRandomValue<T>(IEnumerable<T> enumerable)
        {
            T[] collection = enumerable.ToArray();
            if(collection == null || collection.Length == 0)
            {
                return default(T);
            }
            int rnd = random.Next(0, collection.Length);

            return collection[rnd];
        }
    }
}
