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

package ca.viaware.dlna.library.model;

import ca.viaware.api.logging.Log;
import ca.viaware.api.sql.Database;
import ca.viaware.api.sql.DatabaseResults;
import ca.viaware.api.sql.DatabaseRow;
import ca.viaware.api.sql.exceptions.ViaWareSQLException;
import ca.viaware.api.sql.factory.BaseFactory;
import ca.viaware.dlna.database.ExtendedDatabase;
import ca.viaware.dlna.library.EntryType;
import ca.viaware.dlna.util.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class LibraryFactory extends BaseFactory<LibraryEntry> {

    public LibraryFactory(Database database) {
        super(database, "entries");
    }

    @Override
    protected void generateTable() {
        try {
            getDatabase().query("CREATE TABLE IF NOT EXISTS 'entries' (" +
                "'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE," +
                "'name' VARCHAR NOT NULL," +
                "'location' VARCHAR NOT NULL," +
                "'parent' INTEGER NOT NULL," +
                "'type_id' INTEGER NOT NULL," +
                "'mime' VARCHAR NOT NULL)");

            getDatabase().query("CREATE TABLE IF NOT EXISTS 'song_info' (" +
                "'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE," +
                "'title' VARCHAR," +
                "'album' VARCHAR," +
                "'art_id' INTEGER," +
                "'artist_id' INTEGER)");
            
            getDatabase().query("CREATE TABLE IF NOT EXISTS 'show_info' (" +
                "'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE," +
                "'title' VARCHAR," +
                "'art_id' INTEGER)");

            getDatabase().query("CREATE TABLE IF NOT EXISTS 'movie_info' (" +
                "'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE," +
                "'title' VARCHAR," +
                "'art_id' INTEGER," +
                "'release_date' DATETIME)");

            getDatabase().query("CREATE INDEX IF NOT EXISTS 'parent_index' ON 'entries' ('parent' ASC)");
        } catch (ViaWareSQLException e) {
            e.printStackTrace();
        }
    }

    public void addRootFolder(File folder, String name) {
        LibraryEntry entry = insertAndAdd(new LibraryEntry(-1, name, EntryType.CONTAINER, -1, folder.getAbsolutePath(), "folder"));
        exploreFolder(folder, entry.getId());
        finishStatement();
    }

    private void exploreFolder(File folder, int parent) {
        Log.info("Exploring %0", folder.getPath());
        for (File f : folder.listFiles()) {
            LibraryEntry entry = addFile(f, parent);
            if (f.isDirectory()) exploreFolder(f, entry.getId());
        }
    }

    private LibraryEntry addFile(File f, int parent) {
        int type = EntryType.getEntryTypeFor(f);
        if (type != EntryType.UNRECOGNIZED) {
            LibraryEntry e = new LibraryEntry(-1, FileUtils.removeExtension(f), type, parent, f.getAbsolutePath(), f.isDirectory() ? "folder" : FileUtils.getMime(f));
            insert(e, true);
            e.setId(getDatabase().getLastInsertedID());
            return e;
        }
        return null;
    }

    private LibraryEntry addFile(File f) {
        int parent = getByFile(f.getParentFile()).getId();
        return addFile(f, parent);
    }

    public LibraryEntry addFileNow(File f) {
        LibraryEntry e = addFile(f);
        finishStatement();
        return e;
    }

    public ArrayList<LibraryEntry> getChildren(int parent) {
        ArrayList<LibraryEntry> entries = new ArrayList<LibraryEntry>();
        try {
            String sql = "SELECT * FROM entries WHERE parent=?";
            DatabaseResults results = getDatabase().query(sql, parent);

            for (int i = 0; i < results.getRowCount(); i++) {
                LibraryEntry entry = loadFromQuery(results.getRow(i));
                entries.add(entry);
            }
        } catch (ViaWareSQLException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public LibraryEntry getByFile(File file) {
        String sql = "SELECT * FROM entries WHERE location=?";
        try {
            DatabaseResults results = getDatabase().query(sql, file.getAbsolutePath());
            if (results.getRowCount() > 0) {
                return loadFromQuery(results.getRow(0));
            }
        } catch (ViaWareSQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void verifyFilesystemIntegrity() {
        Log.info("Verifying filesystem integrity...");

        Log.info("Checking for deleted files...");
        int deletedCount = 0;
        ArrayList<LibraryEntry> entries = getAll();
        for (LibraryEntry e : entries) {
            if (e.getLocation().exists()) continue;
            Log.info("File %0 can no longer be found. Purging from database...", e.getLocation().getAbsolutePath());
            deletedCount++;
            remove(e);
        }
        Log.info("Done. Purged %0 files from database.", deletedCount);

        Log.info("Checking for new files...");
        int count  = 0;
        for (LibraryEntry e : getChildren(-1)) {
            count += verifyFolder(e.getLocation(), entries);
        }
        finishStatement();
        Log.info("Done. Added %0 new files to database", count);

        Log.info("Done verifying filesystem integrity.");
    }

    private int verifyFolder(File folder, ArrayList<LibraryEntry> entries) {
        int count = 0;
        for (File f : folder.listFiles()) {
            boolean exists = false;
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).getLocation().getAbsolutePath().equals(f.getAbsolutePath())) {
                    entries.remove(i);
                    exists = true;
                    break;
                }
            }

            if (!exists && EntryType.getEntryTypeFor(f) != EntryType.UNRECOGNIZED) {
                count++;
                Log.info("Found new file %0", f.getAbsolutePath());
                addFile(f);
            }

            if (f.isDirectory()) {
                count += verifyFolder(f, entries);
            }
        }
        return count;
    }

    public ArrayList<LibraryEntry> getAllOfType(int type) {
        ArrayList<LibraryEntry> entries = new ArrayList<LibraryEntry>();

        try {
            String sql = "SELECT * FROM entries WHERE type_id=?";
            DatabaseResults results = getDatabase().query(sql, type);

            for (int i = 0; i < results.getRowCount(); i++) {
                LibraryEntry entry = loadFromQuery(results.getRow(i));
                entries.add(entry);
            }
        } catch (ViaWareSQLException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public int countChildren(int parent) {
        String sql = "SELECT COUNT(*) AS 'count' FROM entries WHERE parent=?";
        try {
            DatabaseResults results = getDatabase().query(sql, parent);
            return results.getRow(0).getInt("count");
        } catch (ViaWareSQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public LibraryEntry loadFromQuery(DatabaseRow databaseRow) {
        return new LibraryEntry(
            databaseRow.getInt("id"),
            databaseRow.getString("name"),
            databaseRow.getInt("type_id"),
            databaseRow.getInt("parent"),
            databaseRow.getString("location"),
            databaseRow.getString("mime")
        );
    }

    @Override
    public void update(LibraryEntry libraryEntry) {
        String sql = "UPDATE entries SET name=?, type_id=?, parent=?, location=?, mime=? WHERE id=?";
        try {
            getDatabase().query(sql,
                libraryEntry.getName(),
                libraryEntry.getTypeID(),
                libraryEntry.getParent(),
                libraryEntry.getLocation().getAbsolutePath(),
                libraryEntry.getMime(),
                libraryEntry.getId()
            );
        } catch (ViaWareSQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(LibraryEntry libraryEntry, boolean useTransaction) {
        String sql = "INSERT INTO entries (name, type_id, parent, location, mime) VALUES (?, ?, ?, ?, ?);";

        ExtendedDatabase db = (ExtendedDatabase) getDatabase();
        if (!useTransaction) finishStatement();

        if (!db.isInTransaction()) {
            db.prepareStatement(sql);
        }
        db.addToStatement(
            libraryEntry.getName(),
            libraryEntry.getTypeID(),
            libraryEntry.getParent(),
            libraryEntry.getLocation().getAbsolutePath(),
            libraryEntry.getMime()
        );
        if (!useTransaction) finishStatement();
    }

    @Override
    public void insert(LibraryEntry libraryEntry) {
        insert(libraryEntry, false);
    }

    private void finishStatement() {
        ExtendedDatabase db = (ExtendedDatabase) getDatabase();
        if (db.isInTransaction()) db.commit();
    }

    public void remove(LibraryEntry object, boolean recurse) {
        try {
            getDatabase().query("DELETE FROM entries WHERE id=?", object.getId());
        } catch (ViaWareSQLException e) {
            e.printStackTrace();
        }

        if (recurse) {
            ArrayList<LibraryEntry> entries = getChildren(object.getId());
            for (LibraryEntry e : entries) {
                remove(e, true);
            }
        }
    }

    @Override
    public void remove(LibraryEntry object) {
        remove(object, false);
    }
}
