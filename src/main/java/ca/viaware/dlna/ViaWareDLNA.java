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

package ca.viaware.dlna;

import ca.viaware.api.logging.Log;
import ca.viaware.dlna.database.threadsafe.DatabaseQueueManager;
import ca.viaware.dlna.library.Library;
import ca.viaware.dlna.library.filesystem.Watcher;
import ca.viaware.dlna.library.model.LibraryFactory;
import ca.viaware.dlna.library.model.LibraryInstanceRunner;
import ca.viaware.dlna.settings.SettingsManager;
import ca.viaware.dlna.ssdp.SSDPService;
import ca.viaware.dlna.streamserver.StreamServer;
import ca.viaware.dlna.streamserver.rtsp.RTSPServer;
import ca.viaware.dlna.upnp.device.DeviceManager;
import ca.viaware.dlna.upnp.device.devices.MediaServer;
import ca.viaware.dlna.upnp.http.UpnpHttpServer;
import ca.viaware.dlna.webinterface.InterfaceServer;

import java.io.IOException;

public class ViaWareDLNA {

    public static final String VERSION = "0.0.1";

    public static void main(String[] args) throws IOException {
        Log.info("Starting ViaWare UPnP Server v" + VERSION);

        SettingsManager.loadSettings();
        DatabaseQueueManager.init();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Library.runInstance(new LibraryInstanceRunner() {
                    @Override
                    public Object run(LibraryFactory factory) {
                        factory.init();
                        factory.verifyFilesystemIntegrity();

                        //factory.deleteAll();
                        //factory.addRootFolder(new File("testfiles/testlib/music"), "Music");
                        //factory.addRootFolder(new File("testfiles/testlib/tv"), "TV");
                        //factory.addRootFolder(new File("G:/UserData/Music/YoutubePlaylists"), "Youtube Playlists");
                        //factory.addRootFolder(new File("G:/UserData/Videos"), "Videos");
                        //factory.addRootFolder(new File("/home/viaware/Music/YoutubePlaylists"), "Youtube Playlists");
                        //factory.addRootFolder(new File("/home/viaware/Videos"), "Videos");

                        return null;
                    }
                });
            }
        }).start();

        DeviceManager.registerDevice(new MediaServer());

        UpnpHttpServer http = new UpnpHttpServer();
        http.start();

        SSDPService ssdp = new SSDPService();
        ssdp.start();

        StreamServer streamServer = new StreamServer();
        streamServer.start();

        RTSPServer rtspServer = new RTSPServer();
        new Thread(rtspServer).start();

        InterfaceServer interfaceServer = new InterfaceServer();
        interfaceServer.start();

        Watcher watcher = new Watcher();
        watcher.run();
    }

}
