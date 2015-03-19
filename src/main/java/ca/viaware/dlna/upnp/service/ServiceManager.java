package ca.viaware.dlna.upnp.service;

public class ServiceManager {

    private static int serviceUID = 0;

    public static int getServiceUID() {
        return serviceUID++;
    }

}
