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

package ca.viaware.dlna.webinterface;

import ca.viaware.api.logging.Log;
import ca.viaware.dlna.library.EntryType;
import ca.viaware.dlna.library.Library;
import ca.viaware.dlna.library.model.LibraryEntry;
import ca.viaware.dlna.library.model.LibraryFactory;
import ca.viaware.dlna.library.model.LibraryInstanceRunner;
import ca.viaware.dlna.settings.SettingsManager;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class InterfaceServer {

    private HttpServer server;

    public InterfaceServer() throws IOException {
        JSONObject config = SettingsManager.getServerConfig().getJSONObject("webInterface");
        this.server = HttpServer.create(new InetSocketAddress(config.getString("host"), config.getInt("port")), 8);
    }

    public void start() {

        server.createContext("/list/", new HttpHandler() {

            private void exploreEntries(JSONArray children, ArrayList<LibraryEntry> entries, int parent) {
                for (LibraryEntry e : entries) {
                    if (e.getParent() == parent) {
                        JSONObject entryJson = new JSONObject();
                        entryJson.put("name", e.getName());
                        entryJson.put("id", e.getId());
                        entryJson.put("type", e.getTypeID());
                        if (e.getTypeID() == EntryType.CONTAINER) {
                            JSONArray sub = new JSONArray();
                            entryJson.put("children", sub);
                            exploreEntries(sub, entries, e.getId());
                        }
                        children.put(entryJson);
                    }
                }
            }

            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String json = (String) Library.runInstance(new LibraryInstanceRunner() {
                    @Override
                    public Object run(LibraryFactory factory) {
                        JSONArray root = new JSONArray();
                        ArrayList<LibraryEntry> entries = factory.getAll();

                        exploreEntries(root, entries, -1);
                        return new JSONObject().put("entries", root).toString(4);
                    }
                });


                while (exchange.getRequestBody().read() != -1) {}

                byte[] bytes = json.getBytes("UTF-8");

                Headers headers = exchange.getResponseHeaders();
                headers.set("CONTENT-TYPE", "application/json");
                headers.set("CONTENT-LANGUAGE", "en");
                exchange.sendResponseHeaders(200, bytes.length);

                exchange.getResponseBody().write(bytes);
                exchange.getResponseBody().close();
            }
        });

        server.start();
        Log.info("Started Web Interface HTTP server");
    }

}
