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
    public class ApartmentController : ControllerBase
    {
        private readonly ILogger<ApartmentController> _logger;
        private readonly IRepository repository;
        private readonly Apartment apartment;

        public ApartmentController(ILogger<ApartmentController> logger, IRepositoryFactory repositoryFactory)
        {
            _logger = logger;
            repository = repositoryFactory.CreateRepository();
            apartment = repository.GetApartment();
        }

        [HttpGet]
        public Apartment Get()
        {
            return apartment;
        }
    }
}
