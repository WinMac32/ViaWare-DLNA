package ca.viaware.dlna.soap;

import java.util.HashMap;

public class SoapAction {

    private String name;
    private HashMap<String, String> values;

    public SoapAction(String name, HashMap<String, String> values) {
        this.name = name;
        this.values = values;
    }

    public SoapAction(String name) {
        this(name, new HashMap<String, String>());
    }

    public void addSoapValue(String name, String value) {
        values.put(name, value);
    }

    public String getSoapValue(String name) {
        return values.get(name);
    }

    public String getName() {
        return name;
    }

    public HashMap<String, String> getValues() {
        return values;
    }
}
