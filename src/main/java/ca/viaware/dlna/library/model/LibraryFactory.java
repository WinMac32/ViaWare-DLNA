package ca.viaware.dlna.library.model;

import ca.viaware.api.logging.Log;
import ca.viaware.api.sql.Database;
import ca.viaware.api.sql.DatabaseResults;
import ca.viaware.api.sql.DatabaseRow;
import ca.viaware.api.sql.exceptions.ViaWareSQLException;
import ca.viaware.api.sql.factory.BaseFactory;
import ca.viaware.dlna.library.EntryType;
import ca.viaware.dlna.library.FormatType;
import ca.viaware.dlna.util.FileUtils;

import javax.xml.crypto.Data;
import java.io.File;
import java.util.ArrayList;

public class LibraryFactory extends BaseFactory<LibraryEntry> {

    public LibraryFactory(Database database) {
        super(database, "entries");
    }

    @Override
    protected void generateTable() {
        try {
            getDatabase().query("CREATE TABLE IF NOT EXISTS '" + getTable() + "' (" +
                "'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE," +
                "'name' VARCHAR NOT NULL," +
                "'location' VARCHAR NOT NULL," +
                "'parent' INTEGER NOT NULL," +
                "'type_id' INTEGER NOT NULL," +
                "'format_id' INTEGER NOT NULL)");
            getDatabase().query("CREATE INDEX IF NOT EXISTS 'parent_index' ON '" + getTable() + "' ('parent' ASC)");
        } catch (ViaWareSQLException e) {
            e.printStackTrace();
        }
    }

    public void addRootFolder(File folder, String name) {
        LibraryEntry entry = insertAndAdd(new LibraryEntry(-1, name, EntryType.CONTAINER, FormatType.NO_FORMAT, -1, folder.getAbsolutePath()));
        exploreFolder(folder, entry.getId());
    }

    private void exploreFolder(File folder, int parent) {
        Log.info("Exploring %0", folder.getPath());
        for (File f : folder.listFiles()) {
            int format = FormatType.NO_FORMAT;
            int type = EntryType.getEntryTypeFor(f);
            if (!f.isDirectory()) {
                format = FormatType.getFormatFor(f.getName());
                if (format == FormatType.NO_FORMAT) continue;
            }
            LibraryEntry entry = insertAndAdd(new LibraryEntry(-1, FileUtils.removeExtension(f), type, format, parent, f.getAbsolutePath()));

            if (f.isDirectory()) exploreFolder(f, entry.getId());
        }
    }

    public ArrayList<LibraryEntry> getChildren(int parent) {
        ArrayList<LibraryEntry> entries = new ArrayList<LibraryEntry>();
        try {
            String sql = "SELECT * FROM " + getTable() + " WHERE parent=?";
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

    public int countChildren(int parent) {
        String sql = "SELECT COUNT(*) AS 'count' FROM " + getTable() + " WHERE parent=?";
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
            databaseRow.getInt("format_id"),
            databaseRow.getInt("parent"),
            databaseRow.getString("location")
        );
    }

    @Override
    public void update(LibraryEntry libraryEntry) {
        String sql = "UPDATE " + getTable() + " SET name=?, type_id=?, format_id=?, parent=?, location=? WHERE id=?";
        try {
            getDatabase().query(sql,
                libraryEntry.getName(),
                libraryEntry.getTypeID(),
                libraryEntry.getFormatID(),
                libraryEntry.getParent(),
                libraryEntry.getLocation().getAbsolutePath(),
                libraryEntry.getId()
            );
        } catch (ViaWareSQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(LibraryEntry libraryEntry) {
        String sql = "INSERT INTO " + getTable() + " (name, type_id, format_id, parent, location) VALUES (?, ?, ?, ?, ?)";
        try {
            getDatabase().query(sql,
                libraryEntry.getName(),
                libraryEntry.getTypeID(),
                libraryEntry.getFormatID(),
                libraryEntry.getParent(),
                libraryEntry.getLocation().getAbsolutePath()
            );
        } catch (ViaWareSQLException e) {
            e.printStackTrace();
        }
    }
}
