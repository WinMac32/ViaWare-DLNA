/*
 * Copyright 2015 Seth Traverse
 *
 * This file is part of ViaWare DLNA Server.
 *
 * ViaWare DLNA Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ViaWare DLNA Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ViaWare DLNA Server. If not, see <http://www.gnu.org/licenses/>.
 */

package ca.viaware.dlna.database;

import ca.viaware.api.logging.Log;
import ca.viaware.api.sql.exceptions.ViaWareSQLException;
import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;

import java.io.File;

public class ExtendedDatabaseFactory {

    public ExtendedDatabase getDatabase(String path) throws ViaWareSQLException {
        Log.info("Loading database \"" + path + "\"...");
        SQLiteConnection connection = new SQLiteConnection(new File(path));
        try {
            connection.open(true);
        } catch (SQLiteException e) {
            throw new ViaWareSQLException(e);
        }
        Log.info("Database loaded successfully.");
        return new ExtendedDatabase(connection);
    }


}
