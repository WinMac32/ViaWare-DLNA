package ca.viaware.dlna.upnp.device;

import ca.viaware.dlna.upnp.service.Service;

import java.util.ArrayList;

public abstract class Device {

    private ArrayList<Service<? extends Device>> services;

    public Device() {
        services = new ArrayList<Service<? extends Device>>();
    }

    protected void addService(Service<? extends Device> s) {
        services.add(s);
    }

    public ArrayList<Service<? extends Device>> getServices() {
        return services;
    }

    public abstract String getType();
    public abstract String getUid();
    public abstract int getVersion();
    public abstract String getName();
    public abstract String getManufacturer();
    public abstract String getWebsite();
    public abstract String getModelNumber();

}
