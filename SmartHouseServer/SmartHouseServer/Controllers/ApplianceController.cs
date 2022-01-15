using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using SmartHouseServer.SmartHouse;
using SmartHouseServer.SmartHouse.Helpers;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartHouseServer.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ApplianceController : ControllerBase
    {
        private readonly ILogger<ApplianceController> _logger;
        private readonly IRepository repository;
        private readonly Apartment apartment;

        public ApplianceController(ILogger<ApplianceController> logger, IRepositoryFactory repositoryFactory)
        {
            _logger = logger;
            repository = repositoryFactory.CreateRepository();
            apartment = repository.GetApartment();
        }

        private IAppliance GetAppliance(int roomId, int applianceId)
        {
            Room room = null;
            if (apartment.Count > roomId && roomId >= 0)
            {
                room = apartment[roomId];
            }
            if (room == null)
            {
                return null;
            }

            if (room.Count > applianceId && applianceId >= -0)
            {
                return room[applianceId];
            }
            return null;
        }

        // GET: api/<RoomController>
        [HttpGet]
        public Apartment Get()
        {
            return apartment;
        }

        // GET api/<RoomController>/5
        [HttpGet("{roomId}/{applianceId}")]
        public IAppliance Get(int roomId, int applianceId)
        {
            return GetAppliance(roomId, applianceId);
        }

        [HttpGet("{roomId}/{applianceId}/{variable}")]
        public string Get(int roomId, int applianceId, string variable)
        {
            IAppliance app = GetAppliance(roomId, applianceId);
            if(app == null)
            {
                return null;
            }

            IRestInvokable restInvokable = RestVariableHelper.GetRestVariable(app, variable);
            if(restInvokable == null)
            {
                return null;
            }
            return restInvokable.Get().ToString();
        }


        // PUT api/<RoomController>/5
        [HttpPut("{roomId}/{applianceId}/{variable}")]
        public void Put(int roomId, int applianceId, string variable, [FromBody] string value)
        {
            IAppliance app = GetAppliance(roomId, applianceId);
            if (app == null)
            {
                return;
            }

            IRestInvokable restInvokable = RestVariableHelper.GetRestVariable(app, variable);
            if (restInvokable == null)
            {
                return;
            }

            if(restInvokable.VariableAttribute.AccessMode == RestAccess.ReadOnly)
            {
                return;
            }

            restInvokable.Set(value);
        }

    }
}
