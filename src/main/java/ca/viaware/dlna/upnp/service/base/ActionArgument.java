package ca.viaware.dlna.upnp.service.base;

public class ActionArgument {

    private String name;
    private String stateVariable;

    public ActionArgument(String name, String stateVariable) {
        this.name = name;
        this.stateVariable = stateVariable;
    }

    public String getName() {
        return name;
    }

    public String getStateVariable() {
        return stateVariable;
    }
}
