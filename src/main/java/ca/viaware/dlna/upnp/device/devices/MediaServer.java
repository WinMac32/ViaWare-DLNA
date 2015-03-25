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
