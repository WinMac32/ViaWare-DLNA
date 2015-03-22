package ca.viaware.dlna.upnp.service.base;

import ca.viaware.api.logging.Log;
import ca.viaware.dlna.util.UrlParser;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.Socket;
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

                out.println("NOTIFY " + url.getPath() + " HTTP/1.1");

                out.println("CONTENT-TYPE: text/xml; charset=\"utf-8\"");
                out.println("NT: upnp:event");
                out.println("NTS: upnp:propchange");
                out.println("SID: uuid:" + getId());
                out.println("SEQ: " + getEventKey());
                out.println();

                out.write(xml.getBytes("UTF-8"));
                out.close();

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
