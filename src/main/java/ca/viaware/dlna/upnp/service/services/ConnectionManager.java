package ca.viaware.dlna.upnp.service.services;

import ca.viaware.api.logging.Log;
import ca.viaware.dlna.upnp.device.devices.MediaServer;
import ca.viaware.dlna.upnp.service.*;
import ca.viaware.dlna.upnp.service.base.Action;
import ca.viaware.dlna.upnp.service.base.ActionArgument;
import ca.viaware.dlna.upnp.service.base.Result;
import ca.viaware.dlna.upnp.service.base.StateVariable;

import java.util.HashMap;
import java.util.Map;

public class ConnectionManager extends Service<MediaServer> {

    public ConnectionManager(MediaServer parent) {
        super(parent);

        registerAction("GetProtocolInfo", getProtocolInfo());
        registerAction("GetCurrentConnectionIDs", getCurrentConnectionIDs());
        registerAction("GetCurrentConnectionInfo", getCurrentConnectionInfo());
        registerAction("PrepareForConnection", prepareForConnection());

        setEventVar("SourceProtocolInfo", "");
        setEventVar("SinkProtocolInfo", "");
        setEventVar("CurrentConnectionIDs", "");
        emitEvents();
    }

    @Override
    public String getType() {
        return "ConnectionManager";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public StateVariable[] getStateVariables() {
        return new StateVariable[] {
            new StateVariable("SourceProtocolInfo", "string", true),
            new StateVariable("SinkProtocolInfo", "string", true),
            new StateVariable("CurrentConnectionIDs", "string", true),
            new StateVariable("A_ARG_TYPE_ConnectionStatus", "string", new String[] {
                "OK", "ContentFormatMismatch", "InsufficientBandwidth", "UnreliableChannel", "Unknown"
            }),
            new StateVariable("A_ARG_TYPE_ConnectionManager", "string"),
            new StateVariable("A_ARG_TYPE_Direction", "string", new String[] {
                "Input", "Output"
            }),
            new StateVariable("A_ARG_TYPE_ProtocolInfo", "string"),
            new StateVariable("A_ARG_TYPE_ConnectionID", "i4"),
            new StateVariable("A_ARG_TYPE_AVTransportID", "i4"),
            new StateVariable("A_ARG_TYPE_RcsID", "i4")
        };
    }

    private Action prepareForConnection() {
        return new Action(new ActionArgument[] {
            new ActionArgument("RemoteProtocolInfo", "A_ARG_TYPE_ProtocolInfo"),
            new ActionArgument("PeerConnectionManager", "A_ARG_TYPE_ConnectionManager"),
            new ActionArgument("PeerConnectionID", "A_ARG_TYPE_ConnectionID"),
            new ActionArgument("Direction", "A_ARG_TYPE_Direction")
        }, new ActionArgument[] {
            new ActionArgument("ConnectionID", "A_ARG_TYPE_ConnectionID"),
            new ActionArgument("AVTransportID", "A_ARG_TYPE_AVTransportID")
        }) {
            @Override
            protected Result handle(HashMap<String, Object> parameters) {
                Log.info("Preparing for connection:");
                for (Map.Entry<String, Object> e :parameters.entrySet()) {
                    Log.info("%0 %1", e.getKey(), e.getValue());
                }
                return new Result();
            }
        };
    }

    private Action getProtocolInfo() {
        return new Action(null, new ActionArgument[] {
            new ActionArgument("Source", "SourceProtocolInfo"),
            new ActionArgument("Sink", "SinkProtocolInfo")
        }) {
            @Override
            public Result handle(HashMap<String, Object> parameters) {
                Result result = new Result();
                result.put("Source", "");
                result.put("Sink", "");
                return result;
            }
        };
    }

    private Action getCurrentConnectionIDs() {
        return new Action(null, new ActionArgument[] {
            new ActionArgument("ConnectionIDs", "CurrentConnectionIDs")
        }) {
            @Override
            public Result handle(HashMap<String, Object> parameters) {
                Result result = new Result();
                result.put("ConnectionIDs", "");
                return result;            }
        };
    }

    private Action getCurrentConnectionInfo() {
        return new Action(new ActionArgument[] {
            new ActionArgument("ConnectionID", "A_ARG_TYPE_ConnectionID")
        }, new ActionArgument[] {
            new ActionArgument("RcsID", "A_ARG_TYPE_RcsID"),
            new ActionArgument("AVTransportID", "A_ARG_TYPE_AVTransportID"),
            new ActionArgument("ProtocolInfo", "A_ARG_TYPE_ProtocolInfo"),
            new ActionArgument("PeerConnectionManager", "A_ARG_TYPE_ConnectionManager"),
            new ActionArgument("PeerConnectionID", "A_ARG_TYPE_ConnectionID"),
            new ActionArgument("Direction", "A_ARG_TYPE_Direction"),
            new ActionArgument("Status", "A_ARG_TYPE_ConnectionStatus")
        }) {
            @Override
            public Result handle(HashMap<String, Object> parameters) {
                Result result = new Result();
                result.put("RcsID", 0);
                result.put("AVTransportID", 0);
                result.put("ProtocolInfo", "");
                result.put("PeerConnectionManager", "");
                result.put("PeerConnectionID", 0);
                result.put("Direction", "");
                result.put("Status", "");
                return result;            }
        };
    }

}
