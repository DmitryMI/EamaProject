using SmartHouseServer.SmartHouse.Helpers;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse
{
    public class Apartment : IRestSerializable
    {
        [RestVariable(RestAccess.ReadOnly)]
        public String Name { get; }
        [RestVariable(RestAccess.ReadOnly)]
        public float Width { get; }
        [RestVariable(RestAccess.ReadOnly)]
        public float Height { get; }
        [RestVariable(RestAccess.ReadOnly)]
        public float Longtitude { get; }
        [RestVariable(RestAccess.ReadOnly)]
        public float Latitude { get; }
        [RestVariable(RestAccess.ReadOnly)]
        public Room[] Rooms { get; }

        public int Count => Rooms.Length;

        public Room this[string roomName]
        {
            get
            {
                return Rooms.FirstOrDefault(r => r.Name == roomName);
            }
        }

        public Room this[int roomIndex] => Rooms[roomIndex];

        public Apartment(string name, float width, float height, Room[] rooms)
        {
            Name = name;
            Width = width;
            Height = height;
            Rooms = rooms;
        }
    }
}
