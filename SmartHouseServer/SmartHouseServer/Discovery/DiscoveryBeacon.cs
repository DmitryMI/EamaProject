using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Threading.Tasks;

namespace SmartHouseServer.Discovery
{
    public class DiscoveryBeacon : IDisposable
    {
        public const int DefaultPort = 36363;

        private int port;
        private string ipAddress;

        private Task task;
        private UdpClient udpClient;
        private bool shouldStop = false;

        public string WanUrl { get; set; }

        public static string GetLocalIPAddress()
        {
            var host = Dns.GetHostEntry(Dns.GetHostName());
            foreach (var ip in host.AddressList)
            {
                if (ip.AddressFamily == AddressFamily.InterNetwork)
                {
                    return ip.ToString();
                }
            }

            return null;
        }

        public DiscoveryBeacon(string wanUrl)
        {
            WanUrl = wanUrl;
            ipAddress = GetLocalIPAddress();
            port = DefaultPort;
            if(ipAddress == null)
            {
                throw new NoBindingAddressException();
            }
        }

        public DiscoveryBeacon(string wanUrl, string bindIp, int port = DefaultPort)
        {
            WanUrl = wanUrl;
            ipAddress = bindIp;
            this.port = port;
        }

        private void BindUdpClient()
        {
            udpClient = new UdpClient(ipAddress, port);

            try
            {
                IPAddress multicastAddress = IPAddress.Parse("228.5.6.7");
                
                udpClient.JoinMulticastGroup(multicastAddress);
            }
            catch(SocketException ex)
            {
                Console.WriteLine("Cannot setup multicast group: " + ex.Message);
            }
        }

        public void StopBeacon()
        {
            shouldStop = true;
            if(task == null)
            {
                return;
            }

            Console.WriteLine("Stopping beacon...");
            udpClient.Close();
            task.Wait();
            task = null;
            Console.WriteLine("Beacon stopped");
        }

        public void StartBeacon()
        {
            if(task != null)
            {
                Console.WriteLine($"Beacon is already running");
                return;
            }

            BindUdpClient();

            Console.WriteLine($"Starting beacon on {ipAddress}:{port}...");

            task = new Task(BeaconLoop);
            task.Start();

            Console.WriteLine("Beacon started");
        }

        private void BeaconLoop()
        {
            IPEndPoint sender = new IPEndPoint(0, 0);
            string lanUrl = $"{ipAddress}:{port}";
            while (!shouldStop)
            {
                try
                {
                    byte[] datagram = udpClient.Receive(ref sender);
                    DiscoveryRequest discoveryRequest = new DiscoveryRequest(datagram);
                    Console.WriteLine($"Discovery request: {discoveryRequest.ClientName} (v{discoveryRequest.ClientVersion}) from {sender}");
                    DiscoveryResponse discoveryResponse = new DiscoveryResponse(WanUrl, lanUrl, 1);
                    byte[] responseDatagram = discoveryResponse.ToDatagram();
                    udpClient.Send(responseDatagram, responseDatagram.Length, sender);
                    Console.WriteLine($"Discovery response: WAN = {WanUrl}, LAN = {lanUrl} to {sender}");
                }
                catch(ObjectDisposedException ex)
                {
#if DEBUG
                    Console.WriteLine(ex.Message);
                    throw;
#endif
                }
                catch(SocketException ex)
                {
#if DEBUG
                    Console.WriteLine(ex.Message);
                    throw;
#endif
                }
                catch(DiscoveryBadRequestException ex)
                {
                    Console.WriteLine($"Bad discovery request received: {ex.Message} from {sender}");
                }

            }
        }

        public void Dispose()
        {
            shouldStop = true;
            task.Wait();
            task.Dispose();
        }
    }
}
