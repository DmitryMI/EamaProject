using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.SmartHouse
{
    public interface IRepositoryFactory
    {
        IRepository CreateRepository();
    }
}
