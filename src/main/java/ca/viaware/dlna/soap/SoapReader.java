/*
 * Copyright 2015 Seth Traverse
 *
 * This file is part of ViaWareDLNAServer.
 *
 * ViaWareDLNAServer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ViaWareDLNAServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ViaWareDLNAServer. If not, see <http://www.gnu.org/licenses/>.
 */

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
