using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse
{
    public class Room
    {
        public int Id { get; }
        public string Name { get; }
        public float Width { get; }
        public float Height { get; }
        public float RelativeX { get; }
        public float RelativeY { get; }

        public IAppliance[] Appliances { get; }

        public int Count => Appliances.Length;

        public IAppliance this[string applianceName]
        {
            get
            {
                return Appliances.FirstOrDefault(r => r.Name == applianceName);
            }
        }

        public IAppliance this[int applianceIndex] => Appliances[applianceIndex];

        public Room(int id, string name, float width, float height, float x, float y, IAppliance[] appliances)
        {
            Name = name;
            Width = width;
            RelativeX = x;
            RelativeY = y;
            Appliances = appliances;
        }
    }
}
