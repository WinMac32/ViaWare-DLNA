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

import ca.viaware.api.logging.Log;
import ca.viaware.dlna.Globals;
import ca.viaware.dlna.upnp.device.Device;
import ca.viaware.dlna.upnp.device.DeviceManager;
import ca.viaware.dlna.util.HttpParser;
import ca.viaware.dlna.util.UrlParser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Searcher extends Thread {

    private MulticastSocket outSock;

    public Searcher(MulticastSocket outSock) throws IOException {
        this.outSock = outSock;
    }

    @Override
    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String http = "";
                    http += "M-SEARCH * HTTP/1.1\r\n";
                    http += "HOST: 239.255.255.250:1900\r\n";
                    http += "MAN: \"ssdp:discover\"\r\n";
                    http += "MX: 1\r\n";
                    http += "ST: urn:schemas-upnp-org:service:ConnectionManager:1\r\n";
                    http += "USER-AGENT: " + Globals.SERVER + "\r\n";
                    http += "\r\n";

                    try {
                        byte[] bytes = http.getBytes("UTF-8");
                        DatagramPacket packet = new DatagramPacket(bytes, 0, bytes.length, InetAddress.getByName("239.255.255.250"), 1900);
                        outSock.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

        while (true) {
            try {
                byte[] buffer = new byte[512];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                //We should be OK using the same socket as NOTIFY for this, since NOTIFY
                //never listens.
                outSock.receive(packet);
                String data = new String(packet.getData(), "UTF-8");

                HttpParser parser = new HttpParser();
                parser.parseResponse(data);

                if (parser.getErrorCode() == 200) {
                    String location = parser.getHeader("LOCATION");
                    UrlParser urlParser = new UrlParser(location);
                    //This will probably be unique, maybe...
                    String identifier = urlParser.getHost() + ";" + parser.getHeader("SERVER");

                    for (Device d : DeviceManager.getDevices()) {
                        d.addOtherDevice(identifier, location);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
