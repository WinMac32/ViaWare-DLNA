package ca.viaware.dlna.util;

import java.io.File;
import java.util.Arrays;

public class FileUtils {

    public static String removeExtension(File file) {
        String name = file.getName();
        if (!name.contains(".") || file.isDirectory()) return name;
        String[] parts = name.split("[.]");
        parts = Arrays.copyOf(parts, parts.length - 1);
        String joined = "";
        for (String s : parts) joined += s;
        return joined;
    }

}
