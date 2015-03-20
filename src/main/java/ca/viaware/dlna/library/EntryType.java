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
