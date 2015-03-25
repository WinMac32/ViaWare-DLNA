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

import java.util.HashMap;

public class SoapAction {

    private String name;
    private HashMap<String, String> values;

    public SoapAction(String name, HashMap<String, String> values) {
        this.name = name;
        this.values = values;
    }

    public SoapAction(String name) {
        this(name, new HashMap<String, String>());
    }

    public void addSoapValue(String name, String value) {
        values.put(name, value);
    }

    public String getSoapValue(String name) {
        return values.get(name);
    }

    public String getName() {
        return name;
    }

    public HashMap<String, String> getValues() {
        return values;
    }
}
