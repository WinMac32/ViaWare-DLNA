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

package ca.viaware.dlna.soap;

import ca.viaware.dlna.upnp.service.Service;

import java.util.Map.Entry;

public class SoapWriter {

    private String rawSoap;

    public SoapWriter() {
        this.rawSoap = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
            "<s:Body>";
    }

    public String getSoap() {
        return rawSoap + "</s:Body></s:Envelope>";
    }

    public void addAction(SoapAction action, String namespace) {
        rawSoap += "<m:" + action.getName() + " xmlns:m=\"" + namespace + "\">";
        for (Entry<String, String> entry : action.getValues().entrySet()) {
            rawSoap += "<" + entry.getKey() + ">";
            rawSoap += entry.getValue();
            rawSoap += "</" + entry.getKey() + ">";
        }
        rawSoap += "</m:" + action.getName() + ">";
    }

    public void addAction(SoapAction action, Service service) {
        addAction(action, "urn:schemas-upnp-org:service:" + service.getType() + ":" + service.getVersion());
    }

}
