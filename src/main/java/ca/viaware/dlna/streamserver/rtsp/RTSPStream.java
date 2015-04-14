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

package ca.viaware.dlna.streamserver.rtsp;

import java.util.HashMap;

public abstract class RTSPStream {

    private int seq;
    private int id;

    private HashMap<String, RTSPAction> actions;

    public RTSPStream() {
        this.actions = new HashMap<String, RTSPAction>();

        this.seq = 0;
    }

    private void actionCalled() {
        this.seq++;
    }

    protected void registerAction(String name, RTSPAction action) {
        actions.put(name, action);
    }

    public RTSPResult runAction(String action, HashMap<String, String> parameters) {
        actionCalled();
        RTSPResult result = null;

        if (this.actions.containsKey(action)) {
            result = this.actions.get(action).run(parameters);
        } else if (action.equals("OPTIONS")) {
            result = new RTSPResult();
            String options = "";
            for (String key : actions.keySet()) {
                options += key + ", ";
            }
            result.addResponseHeader("Public", options);
        }

        if (result != null) {
            result.addResponseHeader("CSeq", Integer.toString(seq));
        }

        return result;
    }

    public void close() {
        RTSPServer.getInstance().removeStream(id);
    }

    public void setId(int id) {
        this.id = id;
    }
}
