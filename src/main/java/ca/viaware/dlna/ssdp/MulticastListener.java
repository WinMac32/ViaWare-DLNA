/*
 * Copyright 2015 Seth Traverse
 *
 * This file is part of ViaWare DLNA Server.
 *
 * ViaWare DLNA Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ViaWare DLNA Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ViaWare DLNA Server. If not, see <http://www.gnu.org/licenses/>.
 */

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
