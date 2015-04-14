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

package ca.viaware.dlna.streamserver.rtsp;

import ca.viaware.api.logging.Log;
import ca.viaware.api.utils.StringUtils;
import ca.viaware.dlna.util.HttpParser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class RTSPServer implements Runnable {

    private static RTSPServer instance;

    private HashMap<Integer, RTSPStream> streams;
    private DatagramSocket socket;

    public RTSPServer() throws SocketException {
        this.streams = new HashMap<Integer, RTSPStream>();
        this.socket = new DatagramSocket(554);

        instance = this;
    }

    public void run() {
        Log.info("RTSP server started.");

        while (true) {
            byte[] buffer = new byte[512];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
                String rawRequest = new String(packet.getData(), "UTF-8");
                HttpParser parser = new HttpParser();
                parser.parseRequest(rawRequest);

                String path = parser.getResource();
                path = path.replace("rtsp://", "");
                String[] pathParts = path.split("[/]");
                if (pathParts.length < 2) continue;
                int id = Integer.parseInt(StringUtils.cleanNumber(pathParts[1]));
                if (!streams.containsKey(id)) continue;

                String action = parser.getMethod();
                HashMap<String, String> variables = parser.getHeaders();

                Log.info("RTSP: Request %0 on ID %1", action, id);

                RTSPResult result = streams.get(id).runAction(action, variables);
                String response = "";

                if (result != null) {
                    response += "RTSP/1.0 200 OK\r\n";
                    for (Map.Entry<String, String> entry : result.getResponseHeaders().entrySet()) {
                        response += entry.getKey() + ": " + entry.getValue() + "\r\n";
                    }
                    response += "\r\n";
                    response += result.getContent();

                    byte[] responseData = response.getBytes("UTF-8");
                    DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length);
                    socket.send(responsePacket);
                }

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    private HashMap<Integer, RTSPStream> getStreams() {
        return streams;
    }

    public void addStream(int id, RTSPStream stream) {
        getStreams().put(id, stream);
    }

    public void removeStream(int id) {
        getStreams().remove(id);
    }

    public static RTSPServer getInstance() {
        return instance;
    }

}
