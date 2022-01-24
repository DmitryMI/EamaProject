using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using SmartHouseServer.Discovery;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Threading.Tasks;

namespace SmartHouseServer
{
    public class Program
    {
        private static DiscoveryBeacon discoveryBeacon;

        private class CmdArguments
        {
            public const string Prefix = "SH_";

            public string[] AspNetArgs { get; private set; }
            public string DiscoveryBeaconEnabled { get; private set; }
            public string DiscoveryBeaconIp { get; private set; }
            public string DiscoveryBeaconPort { get; private set; }
            public string DiscoveryBeaconWanUrl { get; private set; }


            public CmdArguments(string[] args)
            {
                List<string> aspNetArgs = new List<string>();
                PropertyInfo[] properties = GetType().GetProperties();
                for(int i = 0; i < args.Length; i++)
                {
                    if(!args[i].StartsWith(Prefix))
                    {
                        aspNetArgs.Add(args[i]);
                        continue;
                    }
                    string propertyName = args[i].Remove(0, Prefix.Length);
                    PropertyInfo propertyInfo = properties.FirstOrDefault(p => p.Name == propertyName);
                    if(propertyInfo == null)
                    {
                        Console.WriteLine($"Argument {propertyName} not recognized");
                        i++;
                        continue;
                    }

                    string value = args[i + 1];
                    i++;
                    propertyInfo.SetValue(this, value);
#if DEBUG
                    Console.WriteLine($"{propertyInfo.Name} : {value}");
#endif
                }
                AspNetArgs = aspNetArgs.ToArray();
            }

        }

        public static void Main(string[] args)
        {
            CmdArguments arguments = new CmdArguments(args);

            if(arguments.DiscoveryBeaconEnabled == "True")
            {
                string wanUrl = "127.0.0.1:80";
                string beaconIp = DiscoveryBeacon.GetLocalIPAddress();
                int beaconPort = DiscoveryBeacon.DefaultPort;
                if(arguments.DiscoveryBeaconPort != null)
                {
                    beaconPort = int.Parse(arguments.DiscoveryBeaconPort);
                }
                if(!String.IsNullOrWhiteSpace(arguments.DiscoveryBeaconWanUrl))
                {
                    wanUrl = arguments.DiscoveryBeaconWanUrl;
                }
                if (!String.IsNullOrWhiteSpace(arguments.DiscoveryBeaconIp))
                {
                    beaconIp = arguments.DiscoveryBeaconIp;
                }
                discoveryBeacon = new DiscoveryBeacon(wanUrl, beaconIp, beaconPort);
                discoveryBeacon.StartBeacon();
            }

            CreateHostBuilder(arguments.AspNetArgs).Build().Run();
        }

        public static IHostBuilder CreateHostBuilder(string[] args) =>
            Host.CreateDefaultBuilder(args)
                .ConfigureWebHostDefaults(webBuilder =>
                {
                    webBuilder.UseStartup<Startup>();
                });
    }
}
