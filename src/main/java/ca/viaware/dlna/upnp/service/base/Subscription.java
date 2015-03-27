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

package ca.viaware.dlna.upnp.service.base;

import ca.viaware.api.logging.Log;
import ca.viaware.dlna.util.UrlParser;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public class Subscription {

    private static int idCounter = 0;

    private long id;
    private String[] deliveryUrls;
    private long eventKey;
    private int duration;
    private String httpVersion;

    public Subscription(String[] deliveryUrls, int duration, String httpVersion) {
        this.deliveryUrls = deliveryUrls;
        this.eventKey = 0;
        this.duration = duration;
        this.httpVersion = httpVersion;

        this.id = idCounter++;
    }

    public long getId() {
        return id;
    }

    public String[] getDeliveryUrls() {
        return deliveryUrls;
    }

    public long getEventKey() {
        return eventKey;
    }

    public void incEventKey() {
        eventKey++;
        //Simulate the wrapping of unsigned integers with a signed long...
        //TODO we might be able to utilize the characteristics of a signed integer such that -1 == U_INT_MAX
        if (eventKey > 4294967295L) {
            eventKey = 1;
        }
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void emitEvent(HashMap<String, Object> variables) {
        Log.info("EVENT: Emitting event...");

        String xml = "<?xml version=\"1.0\"?>";
        xml += "<e:propertyset xmlns:e=\"urn:schemas-upnp-org:event-1-0\">";
        for (Entry<String, Object> e : variables.entrySet()) {
            xml += "<e:property>";
            xml += "<" + e.getKey() + ">" + e.getValue() + "</" + e.getKey() + ">";
            xml += "</e:property>";
        }
        xml += "</e:propertyset>";

        for (String s : deliveryUrls) {
            try {
                Log.info("Delivery URL %0", s);

                UrlParser url = new UrlParser(s);

                Log.info("EVENT: Sending event to %0 %1 %2", url.getHost(), url.getPort(), url.getPath());

                Socket socket = new Socket(url.getHost(), url.getPort());
                PrintStream out = new PrintStream(socket.getOutputStream());

                byte[] bytes = xml.getBytes("UTF-8");

                out.println("NOTIFY " + url.getPath() + " HTTP/1.1");

                out.println("HOST: " + url.getHost() + ":" + url.getPort());
                out.println("CONTENT-TYPE: text/xml; charset=\"utf-8\"");
                out.println("CONTENT-LENGTH: " + bytes.length);
                out.println("NT: upnp:event");
                out.println("NTS: upnp:propchange");
                out.println("SID: uuid:" + getId());
                out.println("SEQ: " + getEventKey());
                out.println();

                out.write(bytes);

                Scanner scanner = new Scanner(socket.getInputStream());

                String respMsg = scanner.nextLine();

                if (respMsg.contains("200")) {
                    Log.info("EVENT: Sent event to %0 successfully", s);
                } else {
                    Log.error("EVENT: Unable to send event to %0, reason %1", s, respMsg);
                }

                scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        incEventKey();
    }
}
