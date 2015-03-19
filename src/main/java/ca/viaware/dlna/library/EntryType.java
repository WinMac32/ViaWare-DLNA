package ca.viaware.dlna.library;

import java.io.File;

public class EntryType {

    public static final int UNRECOGNIZED = -1;
    public static final int CONTAINER = 0;
    public static final int AUDIO = 1;
    public static final int VIDEO = 2;
    public static final int PICTURE = 3;

    private static String[] audioExts = {
        "mp3", "ogg", "mka", "wma"
    };

    private static String[] videoExts = {
        "mp4", "mkv", "wmv"
    };

    private static String[] pictureExts = {
        "png", "jpg", "jpeg", "gif", "bmp"
    };

    private static boolean isIn(String[] a, String val) {
        for (String s : a) if (s.equalsIgnoreCase(val)) return true;
        return false;
    }

    public static int getEntryTypeFor(File file) {
        String filename = file.getName();
        if (file.isDirectory()) return CONTAINER;
        if (!filename.contains(".")) return UNRECOGNIZED;

        String[] parts = filename.split("[.]");
        String ext = parts[parts.length - 1];

        if (isIn(audioExts, ext)) return AUDIO;
        if (isIn(videoExts, ext)) return VIDEO;
        if (isIn(pictureExts, ext)) return PICTURE;

        return UNRECOGNIZED;
    }

}
