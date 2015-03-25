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

package ca.viaware.dlna.upnp.service.base;

public class StateVariable {

    private String name;
    private String type;
    private String[] allowedValues;
    private boolean events;

    public StateVariable(String name, String type, String[] allowedValues, boolean events) {
        this.name = name;
        this.type = type;
        this.allowedValues = allowedValues;
        this.events = events;
    }

    public StateVariable(String name, String type, String[] allowedValues) {
        this(name, type, allowedValues, false);
    }

    public StateVariable(String name, String type, boolean events) {
        this(name, type, null, events);
    }

    public StateVariable(String name, String type) {
        this(name, type, null, false);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean hasAllowedValues() {
        return allowedValues != null;
    }

    public String[] getAllowedValues() {
        return allowedValues;
    }

    public boolean isEvents() {
        return events;
    }
}
