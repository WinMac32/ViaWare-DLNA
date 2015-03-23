package ca.viaware.dlna.util;

import ca.viaware.api.utils.StringUtils;

public class UrlParser {

    private String protocol;
    private String host;
    private int port;
    private String path;

    public UrlParser(String url) {
        //Lol whats a regex? :3
        String[] split = url.split("[:][/][/]");
        protocol = split[0];
        split = split[1].split("[/]", 2);
        String ip = split[0];
        String[] ipParts = ip.split("[:]");
        host = ipParts[0];
        if (ip.contains(":")) {
            port = Integer.parseInt(StringUtils.cleanNumber(ipParts[1]));
        } else {
            port = 80;
        }
        path = "/" + split[1];
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }
}
