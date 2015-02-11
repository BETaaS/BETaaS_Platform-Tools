package org.eclipse.californium.examples.resources;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class PIRResource extends BasicResource {
    private Boolean value;
    
    public PIRResource(String id) {
    	super(id);
		this.id=id;
		value=false;
	}
    
    @Override
    public void setAttributes() {
    	super.setAttributes();
        // set display name
        getAttributes().setTitle("PIR Resource");
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        // respond to the request
        exchange.respond(getValue().toString());
    }
	
	@Override
    public void handlePOST(CoapExchange exchange) {
        measurement = exchange.getRequestText().trim();
        value = Boolean.valueOf(getMeasurement());
        // respond to the request
        exchange.respond(ResponseCode.CREATED);
    }
	
	@Override
    public void handlePUT(CoapExchange exchange) {
		measurement = exchange.getRequestText().trim();
		value = Boolean.valueOf(getMeasurement());
        // respond to the request
        exchange.respond(ResponseCode.CHANGED);
    }

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}

}
