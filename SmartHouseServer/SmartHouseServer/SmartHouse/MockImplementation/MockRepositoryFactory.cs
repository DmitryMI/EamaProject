using SmartHouseServer.SmartHouse.MockImplementation;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse.MockImplementation
{
    public class MockRepositoryFactory : IRepositoryFactory
    {
        private Simulator instance;

        public IRepository CreateRepository()
        {
            if(instance == null)
            {
                instance = new Simulator();
                instance.StartSimulation();
            }
            return instance;
        }
    }
}
