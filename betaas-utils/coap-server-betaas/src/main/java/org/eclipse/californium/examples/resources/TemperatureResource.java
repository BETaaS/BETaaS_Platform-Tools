package org.eclipse.californium.examples.resources;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class TemperatureResource extends BasicResource {
    private String value;
    
    public TemperatureResource(String id) {
    	super(id);
		this.id=id;
		value="0";
	}
    
    @Override
    public void setAttributes() {
    	super.setAttributes();
        // set display name
        getAttributes().setTitle("Temperature Resource");
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        // respond to the request
        exchange.respond(getValue().toString());
    }
	
	@Override
    public void handlePOST(CoapExchange exchange) {
        measurement = exchange.getRequestText().trim();
        value = getMeasurement();
        // respond to the request
        exchange.respond(ResponseCode.CREATED);
    }
	
	@Override
    public void handlePUT(CoapExchange exchange) {
		measurement = exchange.getRequestText().trim();
		value = getMeasurement();
        // respond to the request
        exchange.respond(ResponseCode.CHANGED);
    }

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
