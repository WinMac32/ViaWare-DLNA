/*
 * Copyright 2015 Seth Traverse
 *
 * This file is part of ViaWareDLNAServer.
 *
 * ViaWareDLNAServer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ViaWareDLNAServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ViaWareDLNAServer. If not, see <http://www.gnu.org/licenses/>.
 */

package ca.viaware.dlna.ssdp;

import ca.viaware.api.logging.Log;
import ca.viaware.dlna.Globals;
import ca.viaware.dlna.settings.SettingsManager;
import ca.viaware.dlna.upnp.device.Device;
import ca.viaware.dlna.upnp.device.DeviceManager;
import ca.viaware.dlna.upnp.service.Service;
import ca.viaware.dlna.util.DateUtils;
import ca.viaware.dlna.util.HttpParser;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class SSDPService extends Thread {

    private String baseAnnounce;
    private MulticastSocket outSock;
    private MulticastListener listener;
    private JSONObject ssdpConfig;
    private JSONObject httpConfig;

    public SSDPService() {
        ssdpConfig = SettingsManager.getServerConfig().getJSONObject("ssdp");
        httpConfig = SettingsManager.getServerConfig().getJSONObject("http");

        try {
            outSock = new MulticastSocket(ssdpConfig.getInt("port"));
            outSock.setNetworkInterface(NetworkInterface.getByInetAddress(InetAddress.getByName(ssdpConfig.getString("host"))));
            Log.info("SSDP: Outbound UDP bound to " + this.outSock.getLocalAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.baseAnnounce = "";
        this.baseAnnounce += "HOST: 239.255.255.250:1900\r\n";
        this.baseAnnounce += "CACHE-CONTROL: max-age=1800\r\n";
        this.baseAnnounce += "NTS: ssdp:alive\r\n";
        this.baseAnnounce += "SERVER:" + Globals.SERVER + "\r\n";
    }

    public void run() {
        try {
            this.listener = new MulticastListener(this);
            this.listener.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread advertiser = new Thread(new Runnable() {
            public void send(String data) {
                try {
                    byte[] bytes = data.getBytes("UTF-8");
                    DatagramPacket packet = new DatagramPacket(bytes, 0, bytes.length, InetAddress.getByName("239.255.255.250"), 1900);
                    outSock.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void sendNotify(String usn, String nt, String uid) {
                String http = "NOTIFY * HTTP/1.1\n";
                http += baseAnnounce;
                http += "NT: " + nt + "\r\n";
                http += "USN: " + usn + "\r\n";
                http += "LOCATION: http://" + httpConfig.getString("host") + ":" + httpConfig.getInt("port") + "/" + uid + "/\r\n";
                http += "\r\n";
                send(http);
            }

            @Override
            public void run() {
                while (true) {
                    Log.info("SSDP: Advertising...");
                    for (int i = 0; i < 3; i++) {
                        for (Device d : DeviceManager.getDevices()) {
                            sendNotify("uuid: " + d.getUid() + "::upnp:rootdevice", "upnp:rootdevice", d.getUid());
                            sendNotify("uuid:" + d.getUid(), "uuid:" + d.getUid(), d.getUid());
                            sendNotify("uuid:" + d.getUid() + "::urn:schemas-upnp-org:device:" + d.getType() + ":" + d.getVersion(), "urn:schemas-upnp-org:device:" + d.getType() + ":" + d.getVersion(), d.getUid());
                            for (Service s : d.getServices()) {
                                sendNotify("uuid:" + d.getUid() + "::urn:schemas-upnp-org:service:" + s.getType() + ":" + s.getVersion(), "urn:schemas-upnp-org:service:" + s.getType() + ":" + s.getVersion(), d.getUid());
                            }
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(30 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        advertiser.start();

        Log.info("Started SSDP Service");
    }

    public void reply(String st, String usn, String uid, SocketAddress source) {
        String http = "HTTP/1.1 200 OK\r\n";
        http += "CACHE-CONTROL: max-age=1800\r\n";
        http += "EXT: \r\n";
        http += "ST: " + st + "\r\n";
        http += "USN: " + usn + "\r\n";
        http += "SERVER: " + Globals.SERVER + "\r\n";
        http += "LOCATION: http://" + httpConfig.getString("host") + ":" + httpConfig.getInt("port") + "/" + uid + "/\r\n";

        http += "DATE: " + DateUtils.getDate() + "\r\n";

        http += "\r\n";

        try {
            byte[] bytes = http.getBytes("UTF-8");
            DatagramPacket p = new DatagramPacket(bytes, 0, bytes.length, source);
            outSock.send(p);
            Log.info("SSDP: M-SEARCH Reply sent to '" + source + "' successfully : ST: " + st + " : USN: " + usn);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleCommand(String data, DatagramPacket packet) {
        HttpParser parser = new HttpParser();
        parser.parseRequest(data);

        if (parser.getMethod().equals("M-SEARCH")) {
            String st = parser.getHeader("st");

            if (st.equals("ssdp:all")) {
                for (Device d : DeviceManager.getDevices()) {
                    reply("upnp:rootdevice", "uuid:" + d.getUid() + "::upnp:rootdevice", d.getUid(), packet.getSocketAddress());
                    reply("uuid:" + d.getUid(), "uuid:" + d.getUid(), d.getUid(), packet.getSocketAddress());
                    reply("urn:schemas-upnp-org:device:" + d.getType() + ":" + d.getVersion(), "uuid:" + d.getUid() + "::urn:schemas-upnp-org:device:" + d.getType() + ":" + d.getVersion(), d.getUid(), packet.getSocketAddress());
                    for (Service s : d.getServices()) {
                        reply("urn:schemas-upnp-org:service:" + s.getType() + ":" + s.getVersion(), "uuid:" + d.getUid() + "::urn:schemas-upnp-org:service:" + s.getType() + ":" + s.getVersion(), d.getUid(), packet.getSocketAddress());
                    }
                }
            } else if (st.equals("upnp:rootdevice")) {

                for (Device d : DeviceManager.getDevices()) {
                    reply(st, "uuid:" + d.getUid() + "::upnp:rootdevice", d.getUid(), packet.getSocketAddress());
                }

            } else if (st.startsWith("uuid")) {

                for (Device d : DeviceManager.getDevices()) {
                    if (parser.getHeader("st").contains(d.getUid())) {
                        String usn = "uuid:" + d.getUid();
                        reply(st, usn, d.getUid(), packet.getSocketAddress());
                        break;
                    }
                }

            } else {
                for (Device d : DeviceManager.getDevices()) {
                    if (st.equals("urn:schemas-upnp-org:device:" + d.getUid() + ":" + d.getVersion())) {
                        String usn = "uuid:" + d.getUid() + "::" + st;
                        reply(st, usn, d.getUid(), packet.getSocketAddress());
                        break;
                    } else {
                        for (Service s : d.getServices()) {
                            if (st.equals("urn:schemas-upnp-org:service:" + s.getUid() + ":" + s.getVersion())) {
                                String usn = "uuid:" + d.getUid() + "::" + st;
                                reply(st, usn, d.getUid(), packet.getSocketAddress());
                                break;
                            }
                        }
                    }
                }
            }
        }

    }

}
