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

package ca.viaware.dlna.upnp.http;

import ca.viaware.api.logging.Log;
import ca.viaware.dlna.Globals;
import ca.viaware.dlna.ViaWareDLNA;
import ca.viaware.dlna.settings.SettingsManager;
import ca.viaware.dlna.upnp.device.Device;
import ca.viaware.dlna.upnp.device.DeviceManager;
import ca.viaware.dlna.upnp.service.Service;
import ca.viaware.dlna.util.HttpUtils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

public class UpnpHttpServer {

    private HttpServer server;

    public UpnpHttpServer() throws IOException {
        JSONObject config = SettingsManager.getServerConfig().getJSONObject("http");
        this.server = HttpServer.create(new InetSocketAddress(config.getString("host"), config.getInt("port")), 8);
    }

    public void start() {
        for (Device device : DeviceManager.getDevices()) {
            new DeviceContext(server, device).start();
        }

        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                Log.info("HTTP: Got root context request... Sending generic response.");
                try {
                    HttpUtils.emptyStream(exchange.getRequestBody());

                    String html = "";

                    html += "<!DOCTYPE html><html>";
                    html += "<head><title>ViaWare UPnP</title></head>";
                    html += "<body>";
                    html += "<h1>ViaWare UPnP Server v" + ViaWareDLNA.VERSION + "</h1><hr>";
                    html += "No device/service specified in URL. No data requested.<br>";
                    html += "Copyright 2015 Seth Traverse";
                    html += "</body></html>";

                    byte[] bytes = html.getBytes("UTF-8");

                    Headers headers = exchange.getResponseHeaders();
                    headers.set("CONTENT-TYPE", "text/html");
                    headers.set("CONTENT-LANGUAGE", "en");
                    exchange.sendResponseHeaders(404, bytes.length);

                    exchange.getResponseBody().write(bytes);
                    exchange.getResponseBody().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        server.start();
        Log.info("Started HTTP server");
    }

}
