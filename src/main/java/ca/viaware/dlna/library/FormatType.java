package ca.viaware.dlna.library;

public class FormatType {

    public static final int NO_FORMAT = -1;

    public static final int MPEG3 = 0;
    public static final int MPEG4 = 1;
    public static final int MATROSKA = 2;
    public static final int OGG = 3;
    public static final int AAC = 4;
    public static final int WINDOWS_MEDIA = 5;

    public static int getFormat(String ext) {
        if (ext.equals("mp3")) return MPEG3;
        if (ext.equals("mp4")) return MPEG4;
        if (ext.equals("mkv") || ext.equals("mka")) return MATROSKA;
        if (ext.equals("ogg")) return OGG;
        if (ext.equals("aac")) return AAC;
        if (ext.equals("wmv") || ext.equals("wma")) return WINDOWS_MEDIA;

        return -1;
    }

    public static int getFormatFor(String filename) {
        if (!filename.contains(".")) return NO_FORMAT;

        String[] parts = filename.split("[.]");
        String ext = parts[parts.length - 1];

        return getFormat(ext.toLowerCase());
    }

}
