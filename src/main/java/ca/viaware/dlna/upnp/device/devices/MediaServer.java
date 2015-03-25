package ca.viaware.dlna.upnp.device.devices;

import ca.viaware.dlna.upnp.device.Device;
import ca.viaware.dlna.upnp.service.Service;
import ca.viaware.dlna.upnp.service.services.ConnectionManager;
import ca.viaware.dlna.upnp.service.services.ContentDirectory;
import ca.viaware.dlna.upnp.service.services.av.AVTransport;

import javax.print.attribute.standard.Media;
import java.util.ArrayList;

public class MediaServer extends Device {

    private ConnectionManager connectionManager;
    private ContentDirectory contentDirectory;
    private AVTransport avTransport;

    public MediaServer() {
        this.connectionManager = new ConnectionManager(this);
        this.contentDirectory = new ContentDirectory(this);
        this.avTransport = new AVTransport(this);

        addService(connectionManager);
        addService(contentDirectory);
        addService(avTransport);
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public ContentDirectory getContentDirectory() {
        return contentDirectory;
    }

    public AVTransport getAvTransport() {
        return avTransport;
    }

    @Override
    public String getType() {
        return "MediaServer";
    }

    @Override
    public String getUid() {
        return "52411920-AE67-11E4-AB27-0800200C9A66";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public String getName() {
        return "ViaWare DLNA";
    }

    @Override
    public String getManufacturer() {
        return "ViaWare - Seth Traverse";
    }

    @Override
    public String getWebsite() {
        return "http://www.viaware.ca";
    }

    @Override
    public String getModelNumber() {
        return "0.0.1";
    }

}
