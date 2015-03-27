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

import ca.viaware.api.logging.Log;
import ca.viaware.dlna.Globals;
import ca.viaware.dlna.soap.SoapAction;
import ca.viaware.dlna.soap.SoapReader;
import ca.viaware.dlna.soap.SoapWriter;
import ca.viaware.dlna.webinterface.InterfaceServer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoapUtils {

    public static HashMap<String,String> toHash(Node node) {
        HashMap<String, String> hash = new HashMap<String, String>();
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            hash.put(n.getNodeName(), n.getTextContent());
        }
        return hash;
    }

    public static String docToString(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, String> query(String url, SoapAction action, String soapAction) {
        SoapWriter writer = new SoapWriter();
        writer.addAction(action, soapAction);

        try {
            byte[] soap = writer.getSoap().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Soapaction", soapAction);
            conn.setRequestProperty("Content-type", "text/xml;charset=\"utf-8\"");
            conn.setRequestProperty("User-Agent", Globals.SERVER);
            conn.setRequestProperty("Content-Length", Integer.toString(soap.length));

            conn.getOutputStream().write(soap);

            if (conn.getResponseMessage().contains("200")) {
                SoapReader reader = new SoapReader();
                MimeHeaders headers = new MimeHeaders();
                for (Map.Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {
                    headers.addHeader(entry.getKey(), entry.getValue().get(0));
                }
                SOAPMessage message = MessageFactory.newInstance().createMessage(headers, conn.getInputStream());
                ArrayList<SoapAction> soapActions = reader.readSoap(message);
                if (soapActions.size() > 0) {
                    return soapActions.get(0).getValues();
                }
            } else {
                Log.error("Unable to send SOAP action to %0, reason %1", url, conn.getResponseMessage());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SOAPException e) {
            e.printStackTrace();
        }
        return null;
    }


}
