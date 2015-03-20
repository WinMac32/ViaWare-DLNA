package ca.viaware.dlna.util;

import ca.viaware.dlna.file.FileReader;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

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
            InputStream in = exchange.getRequestBody();
            while (in.read() != -1) {}

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
            InputStream in = exchange.getRequestBody();
            while (in.read() != -1) {}

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

}
