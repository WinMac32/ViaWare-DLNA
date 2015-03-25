/*
 * Copyright 2015 Seth Traverse
 *
 * This file is part of ViaWareDLNAServer.
 *
 * ViaWareDLNAServer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ViaWareDLNAServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ViaWareDLNAServer. If not, see <http://www.gnu.org/licenses/>.
 */

package ca.viaware.dlna.library;

import ca.viaware.dlna.database.ExtendedDatabase;
import ca.viaware.dlna.database.threadsafe.DatabaseQueueManager;
import ca.viaware.dlna.database.threadsafe.SqlInstanceRunner;
import ca.viaware.dlna.library.model.LibraryFactory;
import ca.viaware.dlna.library.model.LibraryInstanceRunner;
import com.almworks.sqlite4java.SQLiteConnection;

public class Library {

    public static Object runInstance(final LibraryInstanceRunner runner) {
        return DatabaseQueueManager.getDatabaseQueue().run(new SqlInstanceRunner() {
            @Override
            public Object run(SQLiteConnection connection) {
                return runner.run(new LibraryFactory(new ExtendedDatabase(connection)));
            }
        });
    }

}
