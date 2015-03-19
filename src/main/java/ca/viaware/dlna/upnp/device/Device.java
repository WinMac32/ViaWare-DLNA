package ca.viaware.dlna.upnp.device;

import ca.viaware.dlna.upnp.service.Service;

public abstract class Device {

    public abstract String getType();
    public abstract String getUid();
    public abstract int getVersion();
    public abstract String getName();
    public abstract String getManufacturer();
    public abstract String getWebsite();
    public abstract String getModelNumber();
    public abstract Service[] getServices();

}
