package ca.viaware.dlna.database.threadsafe;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteQueue;

import java.io.File;

public class DatabaseQueueManager {

    private SQLiteQueue queue;

    public DatabaseQueueManager(File file) {
        this.queue = new SQLiteQueue(file);
        this.queue.start();
    }

    public Object run(final SqlInstanceRunner runner) {
        return queue.execute(new SQLiteJob<Object>() {
            @Override
            protected Object job(SQLiteConnection sqLiteConnection) throws Throwable {
                return runner.run(sqLiteConnection);
            }
        }).complete();
    }

}
