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

package ca.viaware.dlna.library.model;

import ca.viaware.api.sql.factory.obj.DatabaseObject;

import java.io.File;

public class LibraryEntry extends DatabaseObject {

    private String name;
    private int typeID;
    private String mime;
    private int parent;

    private File location;

    public LibraryEntry(int id, String name, int typeID, int parent, String location, String mime) {
        super(id);
        this.name = name;
        this.typeID = typeID;
        this.mime = mime;
        this.parent = parent;
        this.location = new File(location);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTypeID() {
        return typeID;
    }

    public int getParent() {
        return parent;
    }

    public File getLocation() {
        return location;
    }

    public String getMime() {
        return mime;
    }
}
