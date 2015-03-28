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
import ca.viaware.api.sql.Database;
import ca.viaware.api.sql.DatabaseResults;
import ca.viaware.api.sql.DatabaseRow;
import ca.viaware.api.sql.exceptions.ViaWareSQLException;
import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

public class ExtendedDatabase extends Database {

    private SQLiteStatement statement;
    private boolean inTransaction;

    public ExtendedDatabase(SQLiteConnection connection) {
        super(connection);
    }

    public void prepareStatement(String sql) {
        try {
            statement = connection.prepare(sql);
            connection.exec("BEGIN TRANSACTION;");
            inTransaction = true;
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public void addToStatement(Object... args) {
        try {
            bindParams(statement, args);
            statement.step();
            statement.reset();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public void commit() {
        try {
            connection.exec("COMMIT;");
            inTransaction = false;
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public boolean isInTransaction() {
        return inTransaction;
    }

    //Reduce the logging...
    @Override
    public DatabaseResults query(String sql, Object... args) throws ViaWareSQLException {
        try {
            DatabaseResults results = new DatabaseResults();
            SQLiteStatement statement = connection.prepare(sql);

            bindParams(statement, args);

            while (statement.step()) {
                DatabaseRow row = new DatabaseRow();
                for (int i = 0; i < statement.columnCount(); i++) {
                    row.setColumn(statement.getColumnName(i), statement.columnValue(i));
                }
                results.addRow(row);
            }

            statement.dispose();

            return results;
        } catch (SQLiteException e) {
            throw new ViaWareSQLException(e);
        }
    }

    private void bindParams(SQLiteStatement statement, Object[] args) throws SQLiteException {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Double) {
                statement.bind(i+1, (Double) args[i]);
            } else if (args[i] instanceof Integer) {
                statement.bind(i+1, (Integer) args[i]);
            } else if (args[i] instanceof String) {
                statement.bind(i+1, (String) args[i]);
            } else if (args[i] instanceof Long) {
                statement.bind(i+1, (Long) args[i]);
            } else {
                Log.error("Argument " + i + " is invalid type!");
            }
        }
    }
}
