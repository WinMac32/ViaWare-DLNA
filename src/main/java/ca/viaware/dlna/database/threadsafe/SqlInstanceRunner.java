package ca.viaware.dlna.database.threadsafe;

import com.almworks.sqlite4java.SQLiteConnection;

public abstract class SqlInstanceRunner {

    private SQLiteConnection connection;

    public Object start(SQLiteConnection connection) {
        this.connection = connection;
        return run(connection);
    }

    protected abstract Object run(SQLiteConnection connection);

}
