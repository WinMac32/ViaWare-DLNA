package ca.viaware.dlna.soap;

import ca.viaware.dlna.util.SoapUtils;

import javax.xml.soap.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SoapReader {

    public ArrayList<SoapAction> readSoap(SOAPMessage soap) throws SOAPException {
        ArrayList<SoapAction> actions = new ArrayList<SoapAction>();

        SOAPBody body = soap.getSOAPBody();

        Iterator actionIter = body.getChildElements();
        while (actionIter.hasNext()) {
            SOAPElement action = (SOAPElement) actionIter.next();

            String name = action.getElementName().getLocalName();
            HashMap<String, String> vals = SoapUtils.toHash(action);

            actions.add(new SoapAction(name, vals));
        }

        return actions;
    }

}
