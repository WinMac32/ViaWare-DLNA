package ca.viaware.dlna.upnp.service.base;

import ca.viaware.api.logging.Log;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
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
                HttpURLConnection connection = (HttpURLConnection) new URL(s).openConnection();
                connection.setRequestMethod("NOTIFY");

                connection.setRequestProperty("CONTENT-TYPE", "text/xml; charset=\"utf-8\"");
                connection.setRequestProperty("NT", "upnp:event");
                connection.setRequestProperty("NTS", "upnp:propchange");
                connection.setRequestProperty("SID", "uuid:" + getId());
                connection.setRequestProperty("SEQ", Long.toString(getEventKey()));

                connection.getOutputStream().write(xml.getBytes("UTF-8"));
                connection.getOutputStream().close();

                if (connection.getResponseCode() == 200) {
                    Log.info("Sent event to %0 successfully", s);
                } else {
                    Log.error("Unable to send event to %0, reason %1", s, connection.getResponseMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        incEventKey();
    }
}
