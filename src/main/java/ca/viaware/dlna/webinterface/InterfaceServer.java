package ca.viaware.dlna.webinterface;

import ca.viaware.api.logging.Log;
import ca.viaware.dlna.settings.SettingsManager;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;

public class InterfaceServer {

    private HttpServer server;

    public InterfaceServer() throws IOException {
        JSONObject config = SettingsManager.getServerConfig().getJSONObject("webInterface");
        this.server = HttpServer.create(new InetSocketAddress(config.getString("host"), config.getInt("port")), 8);
    }

    public void start() {



        server.start();
        Log.info("Started Web Interface HTTP server");
    }

}
