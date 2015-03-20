package ca.viaware.dlna.upnp.service.services;

import ca.viaware.api.logging.Log;
import ca.viaware.dlna.library.EntryType;
import ca.viaware.dlna.library.Library;
import ca.viaware.dlna.library.model.LibraryEntry;
import ca.viaware.dlna.library.model.LibraryFactory;
import ca.viaware.dlna.settings.SettingsManager;
import ca.viaware.dlna.upnp.service.*;
import ca.viaware.dlna.upnp.service.base.Action;
import ca.viaware.dlna.upnp.service.base.ActionArgument;
import ca.viaware.dlna.upnp.service.base.Result;
import ca.viaware.dlna.upnp.service.base.StateVariable;
import ca.viaware.dlna.util.FileUtils;
import ca.viaware.dlna.util.XMLUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ContentDirectory extends Service {

    private JSONObject serverConfig;
    private int updateID = 1;

    public ContentDirectory() {
        serverConfig = SettingsManager.getServerConfig().getJSONObject("streamServer");

        registerAction("GetSearchCapabilities", getSearchCapabilities());
        registerAction("GetSortCapabilities", getSortCapabilities());
        registerAction("GetSystemUpdateID", getSystemUpdateID());
        registerAction("Browse", browse());

        //Let's not support this for now... Spec says it's optional.
        //registerAction("Search", search());

        setEventVar("SystemUpdateID", 0);
        setEventVar("ContainerUpdateIDs", "");
        emitEvents();
    }

    private Action getSearchCapabilities() {
        return new Action(null, new ActionArgument[] {
            new ActionArgument("SearchCaps", "SearchCapabilities")
        }) {
            @Override
            public Result handle(HashMap<String, Object> parameters) {
                Result result = new Result();
                result.put("SearchCaps", "");
                return result;
            }
        };
    }

    private Action getSortCapabilities() {
        return new Action(null, new ActionArgument[] {
            new ActionArgument("SortCaps", "SortCapabilities")
        }) {
            @Override
            public Result handle(HashMap<String, Object> parameters) {
                Result result = new Result();
                result.put("SortCaps", "");
                return result;
            }
        };
    }

    private Action getSystemUpdateID() {
        return new Action(null, new ActionArgument[] {
            new ActionArgument("Id", "SystemUpdateID")
        }) {
            @Override
            public Result handle(HashMap<String, Object> parameters) {
                Result result = new Result();
                result.put("Id", 0);
                return result;
            }
        };
    }

    public Action browse() {
        return new Action(new ActionArgument[] {
            new ActionArgument("ObjectID", "A_ARG_TYPE_ObjectID"),
            new ActionArgument("BrowseFlag", "A_ARG_TYPE_BrowseFlag"),
            new ActionArgument("Filter", "A_ARG_TYPE_Filter"),
            new ActionArgument("StartingIndex", "A_ARG_TYPE_Index"),
            new ActionArgument("RequestedCount", "A_ARG_TYPE_Count"),
            new ActionArgument("SortCriteria", "A_ARG_TYPE_SortCriteria")
        }, new ActionArgument[] {
            new ActionArgument("Result", "A_ARG_TYPE_Result"),
            new ActionArgument("NumberReturned", "A_ARG_TYPE_Count"),
            new ActionArgument("TotalMatches", "A_ARG_TYPE_Count"),
            new ActionArgument("UpdateID", "A_ARG_TYPE_UpdateID")
        }) {
            @Override
            public Result handle(HashMap<String, Object> parameters) {
                Log.info("Browse flag %0", parameters.get("BrowseFlag"));
                ArrayList<LibraryEntry> entries = null;
                LibraryFactory factory = Library.getFactory();

                int id = Integer.parseInt((String) parameters.get("ObjectID"));
                if (id == 0) id = -1; //UPnP spec: ID of 0 refers to root object

                if (parameters.get("BrowseFlag").equals("BrowseDirectChildren")) {
                    Log.info("Browsing for children of %0", parameters.get("ObjectID"));
                    entries = factory.getChildren(id);
                }
                if (parameters.get("BrowseFlag").equals("BrowseMetadata")) {
                    Log.info("Browsing metadata of %0", parameters.get("ObjectID"));
                    entries = new ArrayList<LibraryEntry>();
                    entries.add(factory.get(id));
                }
                if (entries != null) {
                    Log.info("Found %0 entries", entries.size());

                    String xml = XMLUtils.escape(toXML(entries, factory));
                    Log.info("XML: " + toXML(entries, factory));

                    factory.getDatabase().close();

                    Result result = new Result();
                    result.put("Result", xml);
                    result.put("NumberReturned", entries.size());
                    result.put("TotalMatches", entries.size());
                    result.put("UpdateID", updateID++);
                    return result;
                } else {
                    Result result = new Result();
                    result.put("Result", "");
                    result.put("NumberReturned", 0);
                    result.put("TotalMatches", 0);
                    result.put("UpdateID", updateID++);
                    return result;
                }
            }
        };
    }

    private String toXML(ArrayList<LibraryEntry> items, LibraryFactory f) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        xml += "<DIDL-Lite xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\">";

        for (LibraryEntry e : items) {
            String name = XMLUtils.escape(e.getName());
            if (e.getTypeID() == EntryType.CONTAINER) {
                int children = f.countChildren(e.getId());

                xml += "<container id=\"" + e.getId() + "\" restricted=\"true\" parentID=\"" + e.getParent() + "\" childCount=\"" + children + "\" searchable=\"false\">";
                xml += "<dc:title>" + name + "</dc:title>";
                xml += "<upnp:class>object.container.storageFolder</upnp:class>";
                xml += "<upnp:writeStatus>NOT_WRITABLE</upnp:writeStatus>";
                //xml += "<upnp:searchClass includeDerived=\"0\">object.container</upnp:searchClass>";
                xml += "</container>";
            } else {
                xml += "<item id=\"" + e.getId() + "\" restricted=\"true\" parentID=\"" + e.getParent() + "\">";
                xml += "<dc:title>" + name + "</dc:title>";
                //TODO refactor to EntryType and make this system more modular
                switch (e.getTypeID()) {
                    case EntryType.AUDIO:
                        xml += "<upnp:class>object.item.audioItem</upnp:class>";
                        break;
                    case EntryType.VIDEO:
                        xml += "<upnp:class>object.item.videoItem</upnp:class>";
                        break;
                    case EntryType.PICTURE:
                        xml += "<upnp:class>object.item.imageItem</upnp:class>";
                        break;
                }
                xml += "<res protocolInfo=\"http-get:*:" + FileUtils.getMime(e.getLocation()) + ":*\" size=\"" + e.getLocation().length() + "\">";
                xml += "http://" + serverConfig.getString("host") + ":" + serverConfig.getInt("port") + "/" + e.getId();
                xml += "</res>";
                xml += "</item>";
            }
        }

        xml += "</DIDL-Lite>";
        return xml;
    }

    public Action search() {
        return new Action(new ActionArgument[] {
            new ActionArgument("ContainerID", "A_ARG_TYPE_ObjectID"),
            new ActionArgument("SearchCriteria", "A_ARG_TYPE_SearchCriteria"),
            new ActionArgument("StartingIndex", "A_ARG_TYPE_Index"),
            new ActionArgument("RequestedCount", "A_ARG_TYPE_Count"),
            new ActionArgument("SortCriteria", "A_ARG_TYPE_SortCriteria")
        }, new ActionArgument[] {
            new ActionArgument("Result", "A_ARG_TYPE_Result"),
            new ActionArgument("NumberReturned", "A_ARG_TYPE_Count"),
            new ActionArgument("TotalMatches", "A_ARG_TYPE_Count"),
            new ActionArgument("UpdateID", "A_ARG_TYPE_UpdateID")
        }) {
            @Override
            public Result handle(HashMap<String, Object> parameters) {
                Result result = new Result();
                result.put("Result", "Wat");
                result.put("NumberReturned", 0);
                result.put("TotalMatches", 0);
                result.put("UpdateID", 0);
                return result;
            }
        };
    }

    @Override
    public String getType() {
        return "ContentDirectory";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    public StateVariable[] getStateVariables() {
        return new StateVariable[] {
            new StateVariable("A_ARG_TYPE_ObjectID", "string"),
            new StateVariable("A_ARG_TYPE_Result", "string"),
            new StateVariable("A_ARG_TYPE_SearchCriteria", "string"),
            new StateVariable("A_ARG_TYPE_BrowseFlag", "string", new String[] {"BrowseMetadata", "BrowseDirectChildren"}),
            new StateVariable("A_ARG_TYPE_Filter", "string"),
            new StateVariable("A_ARG_TYPE_SortCriteria", "string"),
            new StateVariable("A_ARG_TYPE_Index", "ui4"),
            new StateVariable("A_ARG_TYPE_Count", "ui4"),
            new StateVariable("A_ARG_TYPE_UpdateID", "ui4"),
            new StateVariable("A_ARG_TYPE_TagValueList", "string"),
            new StateVariable("SearchCapabilities", "string"),
            new StateVariable("SortCapabilities", "string"),
            new StateVariable("SystemUpdateID", "ui4", true),
            new StateVariable("ContainerUpdateIDs", "string", true)
        };
    }

}
