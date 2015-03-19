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
