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
