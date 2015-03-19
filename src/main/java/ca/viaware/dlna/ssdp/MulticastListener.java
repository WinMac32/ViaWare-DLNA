package ca.viaware.dlna.ssdp;

import ca.viaware.dlna.Globals;

import java.io.IOException;
import java.net.*;

public class MulticastListener extends Thread {

    private SSDPService service;
    private MulticastSocket socket;

    public MulticastListener(SSDPService service) throws IOException {
        this.service = service;

        socket = new MulticastSocket(1900);
        InetAddress group = InetAddress.getByName("239.255.255.250");
        socket.joinGroup(new InetSocketAddress(group, 0), NetworkInterface.getByInetAddress(InetAddress.getByName(Globals.IP)));
    }

    public void run() {
        try {
            while (true) {
                byte[] buffer = new byte[512];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String data = new String(packet.getData(), "UTF-8");
                service.handleCommand(data, packet);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

}
