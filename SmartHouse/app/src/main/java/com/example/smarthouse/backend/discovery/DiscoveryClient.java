package com.example.smarthouse.backend.discovery;

import static android.content.Context.WIFI_SERVICE;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class DiscoveryClient {
    private final int RequestRepeats = 1;
    private final int BeaconPort = 36363;

    private final Thread discoveryThread;
    private final DiscoveryCallback callback;
    private boolean isStarted = false;
    private boolean isDone = false;
    private boolean isCanceled = false;
    private final Context context;

    public interface DiscoveryCallback
    {
        void OnServerDiscovered(Discovery discovery);
    }

    protected String wifiIpAddress() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("SmartHouse Discovery", "Unable to get host address.");
            ipAddressString = null;
        }

        return ipAddressString;

    }

    private static class DiscoveryResponse
    {
        String wanUrl;
        String lanUrl;
        int version;

        public DiscoveryResponse(DatagramPacket datagramPacket)
        {
            InetAddress sender = datagramPacket.getAddress();
            lanUrl = sender.getHostName() + ":" + datagramPacket.getPort();

            byte[] data = datagramPacket.getData();
            int magicCorrect = 0x1313FFE0;

            int pos = 0;
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
            buffer.put(data, pos, Integer.BYTES);
            pos += Integer.BYTES;
            buffer.rewind();
            int magic = buffer.getInt();
            if(magic != magicCorrect)
            {
                Log.e("SmartHouse DiscoveryClient", String.format("Bad DiscoveryResponse recieved: magic value is %d. Expected %d", magic, magicCorrect));
                throw new RuntimeException("Bad DiscoveryResponse recieved: magic value is %d. Expected %d");
            }

            int wanUrlLength;
            buffer = ByteBuffer.allocate(Integer.BYTES);
            buffer.put(data, pos, Integer.BYTES);
            pos += Integer.BYTES;
            buffer.rewind();
            wanUrlLength = buffer.getInt();

            wanUrl = new String(data, pos, wanUrlLength);
            pos += wanUrlLength;

            int lanUrlLength;
            buffer = ByteBuffer.allocate(Integer.BYTES);
            buffer.put(data, pos, Integer.BYTES);
            pos += Integer.BYTES;
            buffer.rewind();
            lanUrlLength = buffer.getInt();

            lanUrl = new String(data, pos, lanUrlLength);
            pos += lanUrlLength;

            buffer = ByteBuffer.allocate(Integer.BYTES);
            buffer.put(data, pos, Integer.BYTES);
            pos += Integer.BYTES;
            buffer.rewind();
            version = buffer.getInt();
        }
    }

    private class DiscoveryRunnable implements Runnable
    {
        private DatagramSocket createMulticastSocket() throws IOException {
            MulticastSocket s = new MulticastSocket();
            InetAddress group = InetAddress.getByName("228.5.6.7");
            s.setSoTimeout(1000);
            s.joinGroup(group);
            return s;
        }

        private DatagramSocket createSimpleSocket() throws SocketException {
            DatagramSocket s = new DatagramSocket();
            s.setSoTimeout(1000);
            return s;
        }

        private void broadcast(DatagramSocket datagramSocket,byte[] packetData) throws IOException {
            if(datagramSocket instanceof MulticastSocket)
            {
                InetAddress group = InetAddress.getByName("228.5.6.7");
                DatagramPacket discoveryRequestPacket = new DatagramPacket(packetData, packetData.length, group, BeaconPort);
                MulticastSocket multicastSocket = (MulticastSocket) datagramSocket;
                multicastSocket.send(discoveryRequestPacket);
            }
            else
            {
                String wifiAddress = wifiIpAddress();
                Log.i("SmartHouse DiscoveryClient", "WiFi ip: " + wifiAddress);
                int lastDot = wifiAddress.lastIndexOf('.');
                String multicastPrefix = wifiAddress.substring(0, lastDot + 1);
                Log.i("SmartHouse DiscoveryClient", "multicast prefix: " + multicastPrefix);
                for(int i = 0; i < 255; i++)
                {
                    String targetIp = multicastPrefix + i;
                    DatagramPacket discoveryRequestPacket = new DatagramPacket(packetData, packetData.length);
                    discoveryRequestPacket.setPort(BeaconPort);
                    discoveryRequestPacket.setAddress(InetAddress.getByName(targetIp));
                    datagramSocket.send(discoveryRequestPacket);
                }
            }
        }

        private DiscoveryResponse sendDiscoveryRequest()
        {
            String clientName = "SmartHouseClient";
            int magic = 0xABBACCEE;

            ArrayList<Byte> discoveryRequest = new ArrayList<Byte>();
            byte[] magicBytes = ByteBuffer.allocate(4).putInt(magic).array();
            byte[] nameLengthBytes = ByteBuffer.allocate(4).putInt(clientName.length()).array();
            byte[] nameBytes = clientName.getBytes(StandardCharsets.UTF_8);
            byte[] versionBytes = ByteBuffer.allocate(4).putInt(1).array();

            ListUtil.addRange(discoveryRequest, magicBytes);
            ListUtil.addRange(discoveryRequest, nameLengthBytes);
            ListUtil.addRange(discoveryRequest, nameBytes);
            ListUtil.addRange(discoveryRequest, versionBytes);

            try {
                DatagramSocket s = createSimpleSocket();

                byte[] bytes = ListUtil.toArray(discoveryRequest);

                //s.send(discoveryRequestPacket);
                broadcast(s, bytes);

                // get their responses!
                byte[] buf = new byte[4096];
                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                s.receive(recv);

                DiscoveryResponse response = new DiscoveryResponse(recv);
                Log.i("SmartHouse DiscoveryClient", String.format("WAN: %s, LAN: %s, Version: %d", response.wanUrl, response.lanUrl, response.version));

                return response;

            } catch (SocketTimeoutException e)
            {
                Log.e("SmartHouse DiscoveryClient", String.format("Beacon listener timeout for %s", "N/A"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void run() {

            DiscoveryResponse response = null;
            for(int i = 0; i < RequestRepeats; i++)
            {
                Log.i("SmartHouse DiscoveryClient", String.format("Sending DiscoveryRequest... Attempt: %s", i));
                response = sendDiscoveryRequest();
                if(response != null)
                {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(response != null)
            {
                Discovery discovery = new Discovery(true, response.lanUrl, response.wanUrl);
                callback.OnServerDiscovered(discovery);
            }
            else
            {
                Log.e("SmartHouse DiscoveryClient", "Server discovery failed");
            }

            isDone = true;
            isStarted = false;
        }
    }

    public DiscoveryClient(Context context, DiscoveryCallback callback)
    {
        this.callback = callback;
        this.context = context;
        discoveryThread = new Thread(new DiscoveryRunnable());
    }

    public void cancel()
    {
        isCanceled = true;
    }

    public void discoverServer()
    {
        if(isStarted && !isDone)
        {
            return;
        }
        isCanceled = false;
        discoveryThread.start();
        isStarted = true;
    }

    public boolean isDone()
    {
        return isDone;
    }
}
