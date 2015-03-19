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
