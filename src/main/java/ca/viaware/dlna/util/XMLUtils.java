package ca.viaware.dlna.util;

public class XMLUtils {

    public static String escape(String xml) {
        return xml
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("'", "&apos;")
            .replace("\"", "&quot;");
    }

}
