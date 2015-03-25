package ca.viaware.dlna.upnp.http;

import ca.viaware.api.logging.Log;
import ca.viaware.api.utils.StringUtils;
import ca.viaware.dlna.Globals;
import ca.viaware.dlna.soap.SoapAction;
import ca.viaware.dlna.soap.SoapReader;
import ca.viaware.dlna.soap.SoapWriter;
import ca.viaware.dlna.upnp.device.Device;
import ca.viaware.dlna.upnp.device.devices.MediaServer;
import ca.viaware.dlna.upnp.service.Service;
import ca.viaware.dlna.upnp.service.base.*;
import ca.viaware.dlna.util.DateUtils;
import ca.viaware.dlna.util.HttpUtils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class ServiceContext implements HttpHandler {

    private Service<? extends Device> service;

    public ServiceContext(Service<? extends Device> service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Log.info("HTTP: UPNP-SERVICE: " + path);
        if (path.contains("desc")) handleDesc(exchange);
        if (path.contains("event")) handleEvent(exchange);
        if (path.contains("control")) handleControl(exchange);
    }

    private void handleDesc(HttpExchange exchange) {
        Log.info("Handling description for service %0", service.getType());

        String xml = "<?xml version=\"1.0\"?>";
        xml += "<scpd xmlns=\"urn:schemas-upnp-org:service-1-0\">";
        xml += "<specVersion><major>" + service.getVersion() + "</major><minor>0</minor></specVersion>";
        xml += "<actionList>";

        for (Entry<String, Action> actionEntry : service.getActions().entrySet()) {
            xml += "<action>";
            xml += "<name>" + actionEntry.getKey() + "</name>";
            Action action = actionEntry.getValue();
            xml += "<argumentList>";
            xml += createArgumentList(action.getIn(), "in");
            xml += createArgumentList(action.getOut(), "out");
            xml += "</argumentList>";
            xml += "</action>";
        }
        xml += "</actionList>";

        xml += "<serviceStateTable>";
        for (StateVariable s : service.getStateVariables()) {
            xml += "<stateVariable sendEvents=\"" + (s.isEvents() ? "yes" : "no") + "\">";
            xml += "<name>" + s.getName() + "</name>";
            xml += "<dataType>" + s.getType() + "</dataType>";
            if (s.hasAllowedValues()) {
                xml += "<allowedValueList>";
                for (String allowed : s.getAllowedValues()) {
                    xml += "<allowedValue>" + allowed + "</allowedValue>";
                }
                xml += "</allowedValueList>";
            }
            xml += "</stateVariable>";
        }
        xml += "</serviceStateTable>";

        xml += "</scpd>";

        Log.info("XML sent");

        HttpUtils.sendXML(xml, exchange);
    }

    private String createArgumentList(ActionArgument[] arguments, String direction) {
        if (arguments == null) return "";

        String xml = "";

        for (ActionArgument argument : arguments) {
            xml += "<argument>";
            xml += "<name>" + argument.getName() + "</name>";
            xml += "<direction>" + direction + "</direction>";
            xml += "<relatedStateVariable>" + argument.getStateVariable() + "</relatedStateVariable>";
            xml += "</argument>";
        }
        return xml;
    }

    private void handleControl(HttpExchange exchange) {
        if (exchange.getRequestMethod().equals("POST")) {
            try {
                MimeHeaders headers = new MimeHeaders();
                for (Map.Entry<String, List<String>> entry : exchange.getRequestHeaders().entrySet()) {
                    headers.addHeader(entry.getKey(), entry.getValue().get(0));
                }
                MessageFactory messageFactory = MessageFactory.newInstance();
                SOAPMessage message = messageFactory.createMessage(headers, exchange.getRequestBody());

                SoapReader reader = new SoapReader();
                ArrayList<SoapAction> actions = reader.readSoap(message);

                SoapWriter writer = new SoapWriter();

                for (SoapAction action : actions) {
                    String actionName = action.getName();
                    if (service.getActions().containsKey(actionName)) {
                        Action serviceAction = service.getActions().get(actionName);
                        HashMap<String, Object> vals = new HashMap<String, Object>();

                        for (Entry<String, String> soapVal : action.getValues().entrySet()) {
                            if (serviceAction.hasArgument(soapVal.getKey())) {
                                StateVariable stateVar = service.getStateVariable(serviceAction.getStateVarFor(soapVal.getKey()));
                                if (stateVar.getType().equals("ui4")) {
                                    vals.put(soapVal.getKey(), Long.parseLong(soapVal.getValue()));
                                } else if (stateVar.getType().equals("i4")) {
                                    vals.put(soapVal.getKey(), Integer.parseInt(soapVal.getValue()));
                                } else if (stateVar.getType().equals("string")) {
                                    vals.put(soapVal.getKey(), soapVal.getValue());
                                } else if (stateVar.getType().equals("boolean")) {
                                    vals.put(soapVal.getKey(), Boolean.parseBoolean(soapVal.getValue()));
                                } else {
                                    Log.info("Unknown type!");
                                    vals.put(soapVal.getKey(), soapVal.getValue());
                                }
                            } else {
                                Log.info("Action %0 cannot handle argument %1", actionName, soapVal.getKey());
                            }
                        }

                        Log.info("HTTP: CONTROL: Calling action %0 in service %1", actionName, service.getType());
                        //Call action this way so subclasses of service can override it...
                        //I'm looking at you, AVTransport service :3
                        Result result = service.callAction(actionName, vals);

                        SoapAction response = new SoapAction(actionName + "Response");

                        for (Entry<String, Object> entry : result.entrySet()) {
                            response.addSoapValue(entry.getKey(), entry.getValue().toString());
                        }

                        writer.addAction(response, service);
                    }
                }

                Log.info("Sending SOAP %0", writer.getSoap());

                byte[] bytes = writer.getSoap().getBytes("UTF-8");

                Headers respHeaders = exchange.getResponseHeaders();
                respHeaders.set("CONTENT-TYPE", "text/xml; charset=\"utf-8\"");
                respHeaders.set("SERVER", Globals.SERVER);
                respHeaders.set("DATE", DateUtils.getDate());
                exchange.sendResponseHeaders(200, bytes.length);

                exchange.getResponseBody().write(bytes);
                exchange.getResponseBody().close();

                //HttpUtils.sendXML(writer.getSoap(), exchange);
            } catch (SOAPException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            HttpUtils.sendXML("<msg>Please POST a SOAP request to this service</msg>", exchange);
        }
    }

    private void handleEvent(HttpExchange exchange) {
        Headers headers = exchange.getRequestHeaders();

        if (exchange.getRequestMethod().equals("SUBSCRIBE")) {
            Log.info("HTTP: Got event subscription");
            String timeout = headers.getFirst("TIMEOUT");
            int timeoutSecs = 0;
            if (timeout.contains("Second-")) {
                timeoutSecs = Integer.parseInt(StringUtils.cleanNumber(timeout));
            } else if (timeout.equalsIgnoreCase("infinite")) {
                timeoutSecs = 0; //Fuck em. Yeah. I know this is redundant.
            }

            if (headers.containsKey("SID")) {
                //Event renewal
                Log.info("--> Handling renewal...");

                if (headers.containsKey("CALLBACK") || headers.containsKey("NT")) {
                    httpError(exchange, 400);
                    return;
                }
                int sid = Integer.parseInt(StringUtils.cleanNumber(headers.getFirst("SID")));
                Subscription subscription = service.getSubscription(sid);
                if (subscription != null) {
                    subscription.setDuration(timeoutSecs);
                } else {
                    httpError(exchange, 412);
                }
            } else {
                //Event subscription
                Log.info("--> Handling new subscription...");

                if (!(headers.containsKey("CALLBACK") && headers.getFirst("NT").equalsIgnoreCase("upnp:event"))) {
                    httpError(exchange, 412);
                    return;
                }

                String callback = headers.getFirst("CALLBACK");
                String httpVersion = exchange.getProtocol();

                String[] callbacks = callback.split("[>][<]");
                callbacks[0] = callbacks[0].replace("<", "");
                callbacks[callbacks.length - 1] = callbacks[callbacks.length - 1].replace(">", "");

                Subscription subscription = new Subscription(callbacks, timeoutSecs, httpVersion);
                service.addSubscription(subscription);

                try {
                    HttpUtils.emptyStream(exchange.getRequestBody());

                    headers = exchange.getResponseHeaders();
                    headers.set("SERVER", Globals.SERVER);
                    headers.set("SID", "uuid:" + subscription.getId());
                    headers.set("TIMEOUT", "Second-" + timeoutSecs);
                    headers.set("DATE", DateUtils.getDate());
                    exchange.sendResponseHeaders(200, 0);
                    exchange.getResponseBody().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (exchange.getRequestMethod().equals("UNSUBSCRIBE")) {
            try {
                if (headers.containsKey("NT") || headers.containsKey("CALLBACK")) {
                    httpError(exchange, 400);
                    return;
                }

                int sid = Integer.parseInt(StringUtils.cleanNumber(headers.getFirst("SID")));
                HttpUtils.emptyStream(exchange.getRequestBody());

                if (service.cancelSubscription(sid)) {
                    exchange.sendResponseHeaders(200, 0);
                    exchange.getResponseBody().close();
                } else {
                    httpError(exchange, 412);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            HttpUtils.sendXML("<msg>Please SUBSCRIBE to this event handler</msg>", exchange);
        }
    }

    private void httpError(HttpExchange exchange, int error, String desc) {
        Log.error("HTTP: Error %0 %1", error, desc);
        try {
            HttpUtils.emptyStream(exchange.getRequestBody());
            exchange.sendResponseHeaders(error, 0);
            exchange.getResponseBody().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void httpError(HttpExchange exchange, int error) {
        switch (error) {
            case 400:
                httpError(exchange, error, "Incompatible header fields");
                break;
            case 412:
                httpError(exchange, error, "Precondition Failed");
                break;
        }
        if (error > 500) httpError(exchange, error, "Unable to accept renewal");
    }
}
