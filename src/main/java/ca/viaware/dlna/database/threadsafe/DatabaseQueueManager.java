package ca.viaware.dlna.database.threadsafe;

import ca.viaware.api.logging.Log;

import java.io.File;

public class DatabaseQueueManager {

    private static DatabaseQueue databaseQueue;

    public static void init() {
        databaseQueue = new DatabaseQueue(new File("data/library.db"));
        Log.info("Initialized database.");
    }

    public static DatabaseQueue getDatabaseQueue() {
        return databaseQueue;
    }

}
