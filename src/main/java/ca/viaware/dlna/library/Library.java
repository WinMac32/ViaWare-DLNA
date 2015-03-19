package ca.viaware.dlna.library;

import ca.viaware.api.sql.Database;
import ca.viaware.api.sql.DatabaseFactory;
import ca.viaware.api.sql.exceptions.ViaWareSQLException;
import ca.viaware.dlna.library.model.LibraryFactory;

public class Library {

    public static LibraryFactory getFactory() {
        LibraryFactory factory;
        DatabaseFactory dbFactory = new DatabaseFactory();
        try {
            Database db = dbFactory.getDatabase("data/library.db");
            factory = new LibraryFactory(db);
        } catch (ViaWareSQLException e) {
            e.printStackTrace();
            return null;
        }

        return factory;
    }

}
