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

package ca.viaware.dlna.upnp.service.services.av;

import ca.viaware.dlna.upnp.device.devices.MediaServer;
import ca.viaware.dlna.upnp.service.Service;
import ca.viaware.dlna.upnp.service.base.Action;
import ca.viaware.dlna.upnp.service.base.Result;
import ca.viaware.dlna.upnp.service.base.StateVariable;

import java.util.HashMap;


public class AVTransportInstance extends Service<MediaServer> {

    private AVTransport parent;

    public AVTransportInstance(MediaServer parentDevice, AVTransport parent) {
        super(parentDevice);

        registerAction("SetAVTransportURI", setAvTransportUri());
        registerAction("SetNextAVTransportURI", setNextAvTransportUri());
        registerAction("GetMediaInfo", getMediaInfo());
        registerAction("GetTransportInfo", getTransportInfo());
        registerAction("GetPositionInfo", getPositionInfo());
        registerAction("GetDeviceCapabilities", getDeviceCapabilities());
        registerAction("GetTransportSettings", getTransportSettings());
        registerAction("Stop", stop());
        registerAction("Play", play());
        registerAction("Pause", pause());
        registerAction("Record", record());
        registerAction("Seek", seek());
        registerAction("Next", next());
        registerAction("Previous", previous());
        registerAction("SetPlayMode", setPlayMode());
        registerAction("GetCurrentTransportActions", getCurrentTransportActions());

        this.parent = parent;
    }

    private Action setAvTransportUri() {
        return new Action() {
            @Override
            protected Result handle(HashMap<String, Object> parameters) {
                return new Result();
            }
        };
    }

    private Action setNextAvTransportUri() {
        return new Action() {
            @Override
            protected Result handle(HashMap<String, Object> parameters) {
                return new Result();
            }
        };
    }

    private Action getMediaInfo() {
        return new Action() {
            @Override
            protected Result handle(HashMap<String, Object> parameters) {
                return new Result();
            }
        };
    }

    private Action getTransportInfo() {
        return new Action() {
            @Override
            protected Result handle(HashMap<String, Object> parameters) {
                return new Result();
            }
        };
    }

    private Action getPositionInfo() {
        return new Action() {
            @Override
            protected Result handle(HashMap<String, Object> parameters) {
                return new Result();
            }
        };
    }

    private Action getDeviceCapabilities() {
        return new Action() {
            @Override
            protected Result handle(HashMap<String, Object> parameters) {
                return new Result();
            }
        };
    }

    private Action getTransportSettings() {
        return new Action() {
            @Override
            protected Result handle(HashMap<String, Object> parameters) {
                return new Result();
            }
        };
    }

    private Action stop() {
        return new Action() {
            @Override
            protected Result handle(HashMap<String, Object> parameters) {
                return new Result();
            }
        };
    }

    private Action play() {
        return new Action() {
            @Override
            protected Result handle(HashMap<String, Object> parameters) {
                return new Result();
            }
        };
    }

    private Action pause() {
        return new Action() {
            @Override
            protected Result handle(HashMap<String, Object> parameters) {
                return new Result();
            }
        };
    }

    private Action record() {
        return new Action() {
            @Override
            protected Result handle(HashMap<String, Object> parameters) {
                return new Result();
            }
        };
    }

    private Action seek() {
        return new Action() {
            @Override
            protected Result handle(HashMap<String, Object> parameters) {
                return new Result();
            }
        };
    }

    private Action next() {
        return new Action() {
            @Override
            protected Result handle(HashMap<String, Object> parameters) {
                return new Result();
            }
        };
    }

    private Action previous() {
        return new Action() {
            @Override
            protected Result handle(HashMap<String, Object> parameters) {
                return new Result();
            }
        };
    }

    private Action setPlayMode() {
        return new Action() {
            @Override
            protected Result handle(HashMap<String, Object> parameters) {
                return new Result();
            }
        };
    }

    public Action getCurrentTransportActions() {
        return new Action() {
            @Override
            protected Result handle(HashMap<String, Object> parameters) {
                return new Result();
            }
        };
    }

    @Override
    public StateVariable[] getStateVariables() {
        return new StateVariable[0];
    }

    @Override
    public String getType() {
        return "NEVER-USED";
    }

    @Override
    public int getVersion() {
        return 420;
    }
}
