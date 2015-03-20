package ca.viaware.dlna.streamserver;

import ca.viaware.api.logging.Log;
import ca.viaware.api.utils.StringUtils;
import ca.viaware.dlna.ViaWareDLNA;
import ca.viaware.dlna.library.Library;
import ca.viaware.dlna.library.model.LibraryEntry;
import ca.viaware.dlna.library.model.LibraryFactory;
import ca.viaware.dlna.settings.SettingsManager;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class StreamServer {

    private HttpServer server;

    public StreamServer() throws IOException {
        JSONObject config = SettingsManager.getServerConfig().getJSONObject("streamServer");
        this.server = HttpServer.create(new InetSocketAddress(config.getString("host"), config.getInt("port")), 8);
    }

    public void start() {

        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                int entryId = Integer.parseInt(StringUtils.cleanNumber(exchange.getRequestURI().getPath()));
                Log.info("Got request for library item %0", entryId);

                while (exchange.getRequestBody().read() != -1) {}

                LibraryFactory factory = Library.getFactory();
                LibraryEntry entry = factory.get(entryId);
                factory.getDatabase().close();
                if (entry != null) {
                    File file = entry.getLocation();
                    String mime = Files.probeContentType(file.toPath().toAbsolutePath());
                    Log.info("Entry is %0 %1", file.getAbsolutePath(), mime);

                    Headers headers = exchange.getResponseHeaders();
                    headers.set("CONTENT-TYPE", mime);
                    exchange.sendResponseHeaders(200, file.length());
                    InputStream fileIn = new FileInputStream(file);
                    OutputStream output = exchange.getResponseBody();
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = fileIn.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }
                    output.close();
                    Log.info("Finished stream transaction.");
                } else {
                    String html = "";
                    html += "<!DOCTYPE html><html>";
                    html += "<head><title>ViaWare UPnP - Stream Server</title></head>";
                    html += "<body>";
                    html += "<h1>ViaWare UPnP Server v" + ViaWareDLNA.VERSION + " - Stream Server</h1><hr>";
                    html += "Unable to find the specified stream<br>";
                    html += "Copyright 2015 Seth Traverse";
                    html += "</body></html>";
                    byte[] bytes = html.getBytes("UTF-8");

                    Headers headers = exchange.getResponseHeaders();
                    headers.set("CONTENT-TYPE", "text/html");
                    headers.set("CONTENT-LANGUAGE", "en");
                    exchange.sendResponseHeaders(404, bytes.length);

                    exchange.getResponseBody().write(bytes);
                    exchange.getResponseBody().close();
                }
            }
        });

        server.start();
        Log.info("Started stream HTTP server");
    }

}
