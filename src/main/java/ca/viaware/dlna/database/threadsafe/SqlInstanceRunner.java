package ca.viaware.dlna.database.threadsafe;

import com.almworks.sqlite4java.SQLiteConnection;

public interface SqlInstanceRunner {

    public Object run(SQLiteConnection connection);

}
