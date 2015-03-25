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

package ca.viaware.dlna.database.threadsafe;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteQueue;

import java.io.File;

public class DatabaseQueue {

    private SQLiteQueue queue;

    public DatabaseQueue(File file) {
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
