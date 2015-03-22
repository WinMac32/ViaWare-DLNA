package ca.viaware.dlna.database.threadsafe;

import ca.viaware.api.logging.Log;

import java.io.File;

public class DatabaseQueueManagerManager {

    private static DatabaseQueueManager databaseQueueManager;

    public static void init() {
        databaseQueueManager = new DatabaseQueueManager(new File("data/library.db"));
        Log.info("Initialized database.");
    }

    public static DatabaseQueueManager getDatabaseQueueManager() {
        return databaseQueueManager;
    }

}
