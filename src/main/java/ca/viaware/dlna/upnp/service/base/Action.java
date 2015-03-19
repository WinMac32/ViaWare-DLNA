package ca.viaware.dlna.upnp.service.base;

import ca.viaware.api.logging.Log;

import java.util.HashMap;

public abstract class Action {

    private ActionArgument[] in;
    private ActionArgument[] out;

    public Action() {
        this.in = new ActionArgument[0];
        this.out = new ActionArgument[0];
    }

    public Action(ActionArgument[] in, ActionArgument[] out) {
        this.in = in;
        this.out = out;
    }

    public ActionArgument[] getIn() {
        return in;
    }

    public ActionArgument[] getOut() {
        return out;
    }

    public boolean hasArgument(String arg) {
        return getStateVarFor(arg) != null;
    }

    public String getStateVarFor(String arg) {
        for (ActionArgument a : getIn()) {
            if (a.getName().equals(arg)) return a.getStateVariable();
        }
        for (ActionArgument a : getOut()) {
            if (a.getName().equals(arg)) return a.getStateVariable();
        }
        return null;
    }

    public Result run(HashMap<String, Object> parameters) {
        return handle(parameters);
    }

    protected abstract Result handle(HashMap<String, Object> parameters);

}
