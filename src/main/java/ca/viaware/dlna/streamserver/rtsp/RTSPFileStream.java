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

import java.io.File;
import java.util.HashMap;

public class RTSPFileStream extends RTSPStream {

    private File file;
    private OutputFormat outputFormat;

    public RTSPFileStream(File file, OutputFormat outputFormat) {
        this.file = file;
        this.outputFormat = outputFormat;

        registerAction("DESCRIBE", describe());
        registerAction("SETUP", setup());
        registerAction("PLAY", play());
        registerAction("PAUSE", pause());
        registerAction("TEARDOWN", tearDown());
    }

    public RTSPAction describe() {
        return new RTSPAction() {
            public RTSPResult run(HashMap<String, String> parameters) {
                return null;
            }
        };
    }

    public RTSPAction setup() {
        return new RTSPAction() {
            public RTSPResult run(HashMap<String, String> parameters) {
                return null;
            }
        };
    }

    public RTSPAction play() {
        return new RTSPAction() {
            public RTSPResult run(HashMap<String, String> parameters) {
                return null;
            }
        };
    }

    public RTSPAction pause() {
        return new RTSPAction() {
            public RTSPResult run(HashMap<String, String> parameters) {
                return null;
            }
        };
    }

    public RTSPAction tearDown() {
        return new RTSPAction() {
            public RTSPResult run(HashMap<String, String> parameters) {
                return null;
            }
        };
    }

}
