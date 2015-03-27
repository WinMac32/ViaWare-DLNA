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

import ca.viaware.dlna.file.FileReader;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class HttpUtils {

    public static void sendXML(File file, HttpExchange exchange) {
        try {
            String xml = FileReader.readFile(file);
            sendXML(xml, exchange);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendXML(String xml, HttpExchange exchange) {
        try {
            emptyStream(exchange.getRequestBody());

            byte[] bytes = xml.getBytes("UTF-8");

            Headers headers = exchange.getResponseHeaders();
            headers.set("CONTENT-TYPE", "text/xml");
            headers.set("CONTENT-LANGUAGE", "en");
            exchange.sendResponseHeaders(200, bytes.length);

            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendImage(File file, String mime, HttpExchange exchange) {
        try {
            emptyStream(exchange.getRequestBody());

            byte[] bytes = FileReader.readFileRaw(file);

            Headers headers = exchange.getResponseHeaders();
            headers.set("CONTENT-TYPE", mime);
            headers.set("CONTENT-LANGUAGE", "en");
            exchange.sendResponseHeaders(200, bytes.length);

            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void emptyStream(InputStream stream) throws IOException {
        while (stream.read() != -1) {}
    }

}
