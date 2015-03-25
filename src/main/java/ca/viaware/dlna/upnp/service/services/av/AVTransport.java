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

package ca.viaware.dlna.upnp.service.services.av;

import ca.viaware.dlna.upnp.device.devices.MediaServer;
import ca.viaware.dlna.upnp.service.Service;
import ca.viaware.dlna.upnp.service.base.Result;
import ca.viaware.dlna.upnp.service.base.StateVariable;

import java.util.HashMap;

public class AVTransport extends Service<MediaServer> {

    private HashMap<Integer, AVTransportInstance> instances;

    public AVTransport(MediaServer parent) {
        super(parent);

        instances = new HashMap<Integer, AVTransportInstance>();

        //Duplicate the instance actions here so that the context can generate XML for them
        AVTransportInstance instance = new AVTransportInstance(parent, this);
        getActions().putAll(instance.getActions());

        setEventVar("LastChange", "");
        emitEvents();
    }

    @Override
    public Result callAction(String name, HashMap<String, Object> args) {
        if (args.containsKey("InstanceID")) {
            int instanceID = (Integer) args.get("InstanceID");
            if (instances.containsKey(instanceID)) {
                return instances.get(instanceID).callAction(name, args);
            }
        }
        return super.callAction(name, args);
    }

    @Override
    public StateVariable[] getStateVariables() {
        return new StateVariable[] {
            new StateVariable("TransportState", "string", new String[] {
                "STOPPED", "PLAYING"
            }),
            new StateVariable("TransportStatus", "string", new String[] {
                "OK", "ERROR_OCCURRED"
            }),
            new StateVariable("PlaybackStorageMedium", "string", new String[] {
                "NETWORK"
            }),
            new StateVariable("RecordStorageMedium", "string", new String[] {
                "NETWORK"
            }),
            new StateVariable("PossiblePlaybackStorageMedia", "string"),
            new StateVariable("PossibleRecordStorageMedia", "string"),
            new StateVariable("CurrentPlayMode", "string", new String[] {
                "NORMAL"
            }),
            new StateVariable("TransportPlaySpeed", "string", new String[] {
                "1"
            }),
            new StateVariable("RecordMediumWriteStatus", "string", new String[] {
                "NOT_IMPLEMENTED"
            }),
            new StateVariable("CurrentRecordQualityMode", "string", new String[] {
                "NOT_IMPLEMENTED"
            }),
            new StateVariable("PossibleRecordQualityModes", "string"),
            new StateVariable("NumberOfTracks", "ui4", new String[] {
                "Min = 0", "Max = " + Integer.MAX_VALUE
            }),
            new StateVariable("CurrentTrack", "ui4", new String[] {
                "Min = 0", "Step = 1", "Max = " + Integer.MAX_VALUE
            }),
            new StateVariable("CurrentTrackDuration", "string"),
            new StateVariable("CurrentMediaDuration", "string"),
            new StateVariable("CurrentTrackMetaData", "string"),
            new StateVariable("CurrentTrackURI", "string"),
            new StateVariable("AVTransportURI", "string"),
            new StateVariable("AVTransportURIMetaData", "string"),
            new StateVariable("NextAVTransportURI", "string"),
            new StateVariable("NextAVTransportURIMetaData", "string"),
            new StateVariable("RelativeTimePosition", "string"),
            new StateVariable("AbsoluteTimePosition", "string"),
            new StateVariable("RelativeCounterPosition", "i4"),
            new StateVariable("AbsoluteCounterPosition", "i4"),
            new StateVariable("LastChange", "string"),
            new StateVariable("A_ARG_TYPE_SeekMode", "string", new String[] {
                "TRACK_NR", "FRAME" //FRAME optional but I feel it's probably important if we want to do frame-wise seeking...
            }),
            new StateVariable("A_ARG_TYPE_SeekTarget", "string"),
            new StateVariable("A_ARG_TYPE_InstanceID", "ui4")
        };
    }

    @Override
    public String getType() {
        return "AVTransport";
    }

    @Override
    public int getVersion() {
        return 1;
    }

}
