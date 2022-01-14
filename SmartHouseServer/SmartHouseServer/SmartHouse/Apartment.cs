using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse
{
    public class Apartment
    {
        public String Name { get; }
        public float Width { get; }
        public float Height { get; }
        public float Longtitude { get; }
        public float Latitude { get; }
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
