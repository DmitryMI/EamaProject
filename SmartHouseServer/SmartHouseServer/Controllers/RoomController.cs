using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using SmartHouseServer.SmartHouse;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace SmartHouseServer.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class RoomController : ControllerBase
    {
        private readonly ILogger<RoomController> _logger;
        private readonly IRepository repository;
        private readonly Apartment apartment;

        public RoomController(ILogger<RoomController> logger, IRepositoryFactory repositoryFactory)
        {
            _logger = logger;
            repository = repositoryFactory.CreateRepository();
            apartment = repository.GetApartment();
        }        

        // GET api/<RoomController>/5
        [HttpGet("{id}")]
        public Room Get(int id)
        {
            if (apartment.Count > id && id >= 0)
            {
                return apartment[id];
            }

            return null;
        }        
    }
}
