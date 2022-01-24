using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SmartHouseServer.Discovery
{
    public class DiscoveryRequest
    {
        public uint MagicNumber = 0xABBACCEE;

        public string ClientName { get; set; }
        public int ClientVersion { get; set; }

        public DiscoveryRequest(byte[] datagram)
        {
            int pos = 0;
            uint magic = BitConverter.ToUInt32(datagram, pos);
            pos += sizeof(uint);

            if(magic != MagicNumber)
            {
                throw new DiscoveryBadRequestException($"Magic number {magic} does not match required {MagicNumber}");
            }

            int nameLength = BitConverter.ToInt32(datagram, pos);
            pos += sizeof(int);
            ClientName = Encoding.UTF8.GetString(datagram, pos, (int)nameLength);
            pos += nameLength;
            ClientVersion = BitConverter.ToInt32(datagram, pos);
            pos += sizeof(int);
        }
    }
}
