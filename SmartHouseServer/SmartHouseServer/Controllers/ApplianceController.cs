using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using SmartHouseServer.SmartHouse;
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

        // GET: api/<RoomController>
        [HttpGet]
        public Apartment Get()
        {
            return apartment;
        }

        // GET api/<RoomController>/5
        [HttpGet("{roomId}/{id}")]
        public IAppliance Get(int roomId, int id)
        {
            Room room = null;
            if (apartment.Count > roomId && roomId >= 0)
            {
                room = apartment[roomId];
            }
            if(room == null)
            {
                return null;
            }

            if(room.Count > id && id >= -0)
            {
                return room[id];
            }

            return null;
        }

        // POST api/<RoomController>
        [HttpPost]
        public void Post([FromBody] string value)
        {
        }

        // PUT api/<RoomController>/5
        [HttpPut("{id}")]
        public void Put(int id, [FromBody] string value)
        {
        }

        // DELETE api/<RoomController>/5
        [HttpDelete("{id}")]
        public void Delete(int id)
        {
        }
    }
}
