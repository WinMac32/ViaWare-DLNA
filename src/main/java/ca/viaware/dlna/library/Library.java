package ca.viaware.dlna.library;

import ca.viaware.dlna.database.ExtendedDatabase;
import ca.viaware.dlna.database.threadsafe.DatabaseQueueManagerManager;
import ca.viaware.dlna.database.threadsafe.SqlInstanceRunner;
import ca.viaware.dlna.library.model.LibraryFactory;
import ca.viaware.dlna.library.model.LibraryInstanceRunner;
import com.almworks.sqlite4java.SQLiteConnection;

public class Library {

    public static Object runInstance(final LibraryInstanceRunner runner) {
        return DatabaseQueueManagerManager.getDatabaseQueueManager().run(new SqlInstanceRunner() {
            @Override
            public Object run(SQLiteConnection connection) {
                return runner.run(new LibraryFactory(new ExtendedDatabase(connection)));
            }
        });
    }

}
