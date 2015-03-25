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

package ca.viaware.dlna.util;

import java.util.HashMap;

public class HttpParser {

    private String method;
    private String resource;
    private String version;

    private int errorCode;
    private String error;

    private HashMap<String, String> headers;

    public HttpParser() {
        headers = new HashMap<String, String>();
    }

    public void parseRequest(String data) {
        String[] lines = data.split("\r\n");

        String[] header = lines[0].split(" ");
        this.method = header[0];
        this.resource = header[1];
        this.version = header[2];

        parseHeaders(lines);
    }

    public void parseResponse(String data) {
        String[] lines = data.split("\r\n");

        String[] header = lines[0].split(" ");
        this.version = header[0];
        this.errorCode = Integer.parseInt(header[1]);
        this.error = header[2];

        parseHeaders(lines);
    }

    private void parseHeaders(String[] lines) {
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (line.length() != 0 && line.contains(":")) {
                String key = line.substring(0, line.indexOf(":")).trim().toLowerCase();
                String val = line.substring(line.indexOf(":") + 1).trim();
                headers.put(key, val);
            }
        }
    }

    public String getMethod() {
        return method;
    }

    public String getResource() {
        return resource;
    }

    public String getVersion() {
        return version;
    }

    public String getHeader(String key) {
        return hasHeader(key.toLowerCase()) ? headers.get(key.toLowerCase()) : null;
    }

    public boolean hasHeader(String key) {
        return headers.containsKey(key.toLowerCase());
    }

}
