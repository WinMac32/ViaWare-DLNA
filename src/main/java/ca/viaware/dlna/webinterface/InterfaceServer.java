package ca.viaware.dlna.webinterface;

import ca.viaware.api.logging.Log;
import ca.viaware.dlna.library.EntryType;
import ca.viaware.dlna.library.Library;
import ca.viaware.dlna.library.model.LibraryEntry;
import ca.viaware.dlna.library.model.LibraryFactory;
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
                JSONArray root = new JSONArray();
                LibraryFactory factory = Library.getFactory();
                ArrayList<LibraryEntry> entries = factory.getAll();

                exploreEntries(root, entries, -1);
                String json = new JSONObject().put("entries", root).toString(4);

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
