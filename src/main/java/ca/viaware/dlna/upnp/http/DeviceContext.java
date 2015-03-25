package ca.viaware.dlna.upnp.http;

import ca.viaware.api.logging.Log;
import ca.viaware.dlna.upnp.device.Device;
import ca.viaware.dlna.upnp.service.Service;
import ca.viaware.dlna.util.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;

public class DeviceContext {

    private HttpServer server;
    private Device device;

    private String descXml;

    public DeviceContext(HttpServer server, Device device) {
        this.server = server;
        this.device = device;

        this.descXml = "<?xml version=\"1.0\"?>" +
            "<root xmlns=\"urn:schemas-upnp-org:device-1-0\">" +
            "<specVersion><major>" + device.getVersion() + "</major><minor>0</minor></specVersion>" +
            "<device>" +
            "<UDN>uuid:" + device.getUid() + "</UDN>" +
            "<friendlyName>" + device.getName() + "</friendlyName>" +
            "<deviceType>urn:schemas-upnp-org:device:" + device.getType() + ":" + device.getVersion() + "</deviceType>" +
            "<manufacturer>" + device.getManufacturer() + "</manufacturer>" +
            "<manufacturerURL>" + device.getWebsite() + "</manufacturerURL>" +
            "<modelName>" + device.getName() + "</modelName>" +
            "<modelNumber>" + device.getModelNumber() + "</modelNumber>" +
            "<modelURL>" + device.getWebsite() + "</modelURL>" +
            "<dlna:X_DLNADOC xmlns:dlna=\"urn:schemas-dlna-org:device-1-0\">DMS-1.50</dlna:X_DLNADOC>" +
            "<iconList>" +
            "<icon>" +
            "<mimetype>image/png</mimetype>" +
            "<width>48</width>" +
            "<height>48</height>" +
            "<depth>24</depth>" +
            "<url>/" + device.getUid() + "/icon/png48</url>" +
            "</icon>" +
            "</iconList>";
    }

    public void start() {
        server.createContext("/" + device.getUid() + "/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                Log.info("Got root description request for device %0", device.getType());
                if (exchange.getRequestMethod().equals("GET")) {
                    HttpUtils.sendXML(descXml, exchange);
                    Log.info("Sent XML");
                }
            }
        });

        server.createContext("/" + device.getUid() + "/icon/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                Log.info("Got icon request for device %0", device.getType());
                if (exchange.getRequestMethod().equals("GET")) {
                    HttpUtils.sendImage(new File("data/icon.png"), "image/png", exchange);
                    Log.info("Sent image");
                }
            }
        });

        descXml += "<serviceList>";

        for (Service<? extends Device> service : device.getServices()) {
            String context = "/" + device.getUid() + "/" + service.getUid() + "/";

            descXml += "<service>";
            descXml += "<serviceType>urn:schemas-upnp-org:service:" + service.getType() + ":" + service.getVersion() + "</serviceType>";
            descXml += "<serviceId>urn:upnp-org:serviceId:" + service.getType() + "</serviceId>";
            descXml += "<controlURL>" + context + "control</controlURL>";
            descXml += "<eventSubURL>" + context + "event</eventSubURL>";
            descXml += "<SCPDURL>" + context + "desc</SCPDURL>";
            descXml += "</service>";

            server.createContext(context, new ServiceContext(service));
            Log.info("HTTP: Created context %0 for service %1 in device %2", context, service.getType(), device.getType());
        }

        descXml += "</serviceList></device></root>";
    }

}
