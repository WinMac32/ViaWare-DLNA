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

package ca.viaware.dlna.library;

import ca.viaware.api.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class EntryType {

    public static final int UNRECOGNIZED = -1;
    public static final int CONTAINER = 0;
    public static final int AUDIO = 1;
    public static final int VIDEO = 2;
    public static final int PICTURE = 3;

    public static int getEntryTypeFor(File file) {
        String filename = file.getName();
        if (file.isDirectory()) return CONTAINER;
        if (!filename.contains(".")) return UNRECOGNIZED;

        try {
            String mime = Files.probeContentType(file.toPath().toAbsolutePath());
            if (mime == null) return UNRECOGNIZED;

            String type = mime.split("[/]")[0];

            if (type.equalsIgnoreCase("audio")) return AUDIO;
            if (type.equalsIgnoreCase("video")) return VIDEO;
            if (type.equalsIgnoreCase("image")) return PICTURE;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return UNRECOGNIZED;
    }

}
