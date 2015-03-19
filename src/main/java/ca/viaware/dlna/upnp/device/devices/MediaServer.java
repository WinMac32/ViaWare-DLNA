package ca.viaware.dlna.upnp.device.devices;

import ca.viaware.dlna.upnp.device.Device;
import ca.viaware.dlna.upnp.service.Service;
import ca.viaware.dlna.upnp.service.services.ConnectionManager;
import ca.viaware.dlna.upnp.service.services.ContentDirectory;

public class MediaServer extends Device {

    private Service[] services;

    private ConnectionManager connectionManager;
    private ContentDirectory contentDirectory;

    public MediaServer() {
        this.connectionManager = new ConnectionManager();
        this.contentDirectory = new ContentDirectory();

        this.services = new Service[2];
        this.services[0] = connectionManager;
        this.services[1] = contentDirectory;
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

    @Override
    public Service[] getServices() {
        return services;
    }
}
