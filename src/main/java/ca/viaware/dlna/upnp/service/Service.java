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

package ca.viaware.dlna.upnp.service;

import ca.viaware.dlna.upnp.device.Device;
import ca.viaware.dlna.upnp.service.base.Action;
import ca.viaware.dlna.upnp.service.base.Result;
import ca.viaware.dlna.upnp.service.base.StateVariable;
import ca.viaware.dlna.upnp.service.base.Subscription;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Service<T extends Device> {

    private int uid;

    public abstract String getType();
    public abstract int getVersion();
    public abstract StateVariable[] getStateVariables();

    private HashMap<String, Action> actions;
    private HashMap<String, Object> eventVars;
    private HashMap<String, Object> updatedEventVars;

    private ArrayList<Subscription> subscriptions;

    private T parent;

    public Service(T parent) {
        this.parent = parent;

        this.actions = new HashMap<String, Action>();
        this.eventVars = new HashMap<String, Object>();
        this.updatedEventVars = new HashMap<String, Object>();
        this.subscriptions = new ArrayList<Subscription>();

        this.uid = ServiceManager.getServiceUID();
    }

    protected T getParent() {
        return parent;
    }

    protected void registerAction(String name, Action action) {
        this.actions.put(name, action);
    }

    protected void setEventVar(String name, Object val, boolean emit) {
        this.eventVars.put(name, val);
        this.updatedEventVars.put(name, val);
        if (emit) emitEvents();
    }

    protected void setEventVar(String name, Object val) {
        setEventVar(name, val, false);
    }

    protected void emitEvents() {
        for (Subscription s : subscriptions) {
            s.emitEvent(updatedEventVars);
        }
        this.updatedEventVars.clear();
    }

    public Result callAction(String name, String caller, HashMap<String, Object> args) {
        if (this.actions.containsKey(name)) {
            return this.actions.get(name).run(caller, args);
        }
        return null;
    }

    public int getUid() {
        return uid;
    }

    public HashMap<String, Action> getActions() {
        return actions;
    }

    public StateVariable getStateVariable(String name) {
        StateVariable[] vars = getStateVariables();
        if (vars != null) {
            for (StateVariable v : vars) {
                if (v.getName().equals(name)) return v;
            }
        }
        return null;
    }

    public void addSubscription(Subscription s) {
        this.subscriptions.add(s);
        s.emitEvent(eventVars);
    }

    public Subscription getSubscription(int sid) {
        for (Subscription s : subscriptions) {
            if (s.getId() == sid) return s;
        }
        return null;
    }

    public boolean cancelSubscription(int sid) {
        for (int i = 0; i < subscriptions.size(); i++) {
            if (subscriptions.get(i).getId() == sid) {
                subscriptions.remove(i);
                return true;
            }
        }
        return false;
    }
}
