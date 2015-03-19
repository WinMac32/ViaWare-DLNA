package ca.viaware.dlna.settings;

import ca.viaware.api.logging.Log;
import ca.viaware.dlna.file.FileReader;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class SettingsManager {

    private static JSONObject serverConfig;

    public static void loadSettings() throws IOException {
        serverConfig = new JSONObject(FileReader.readFile(new File("config/config.json")));
        Log.info("Successfully loaded all settings.");
    }

    public static JSONObject getServerConfig() {
        return serverConfig;
    }
}
