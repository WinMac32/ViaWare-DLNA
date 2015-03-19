package ca.viaware.dlna.upnp.device;

import ca.viaware.api.logging.Log;
import ca.viaware.dlna.upnp.service.Service;

import java.util.ArrayList;

public class DeviceManager {

    private static ArrayList<Device> devices;

    public static void registerDevice(Device device) {
        if (devices == null) devices = new ArrayList<Device>();

        devices.add(device);
        Log.info("Registered device %0", device.getType());
        for (Service s : device.getServices()) {
            Log.info("--> %0", s.getType());
        }
    }

    public static ArrayList<Device> getDevices() {
        return devices;
    }

}
