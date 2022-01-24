using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SmartHouseServer.Discovery
{
    public class DiscoveryResponse
    {
        public const uint MagicNumber = 0x1313FFE0;

        public string WanUrl { get; set; }
        public string LanUrl { get; set; }
        public int Version { get; set; }

        public DiscoveryResponse(string wanUrl, string lanUrl, int version)
        {
            WanUrl = wanUrl;
            LanUrl = lanUrl;
            Version = version;
        }

        public byte[] ToDatagram()
        {
            List<byte> datagram = new List<byte>();

            byte[] wanUrlBytes = Encoding.ASCII.GetBytes(WanUrl);
            byte[] lanUrlBytes = Encoding.ASCII.GetBytes(LanUrl);

            datagram.AddRange(BitConverter.GetBytes(MagicNumber));
            datagram.AddRange(BitConverter.GetBytes(wanUrlBytes.Length));
            datagram.AddRange(wanUrlBytes);
            datagram.AddRange(BitConverter.GetBytes(lanUrlBytes.Length));
            datagram.AddRange(lanUrlBytes);
            datagram.AddRange(BitConverter.GetBytes(Version));

            return datagram.ToArray();
        }
    }
}
