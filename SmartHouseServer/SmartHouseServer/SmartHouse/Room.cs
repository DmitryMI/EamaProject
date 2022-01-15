using SmartHouseServer.SmartHouse.Helpers;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse
{
    public class Room : IRestSerializable
    {
        [RestVariable(RestAccess.ReadOnly)]
        public int Id { get; }
        [RestVariable(RestAccess.ReadOnly)]
        public string Name { get; }
        [RestVariable(RestAccess.ReadOnly)]
        public float Width { get; }
        [RestVariable(RestAccess.ReadOnly)]
        public float Height { get; }
        [RestVariable(RestAccess.ReadOnly)]
        public float RelativeX { get; }
        [RestVariable(RestAccess.ReadOnly)]
        public float RelativeY { get; }
        [RestVariable(RestAccess.ReadOnly)]
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
            Id = id;
            Name = name;
            Width = width;
            RelativeX = x;
            RelativeY = y;
            Appliances = appliances;
        }
    }
}
