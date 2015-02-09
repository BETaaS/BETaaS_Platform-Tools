package org.eclipse.californium.examples.resources;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class WaterSimulatedResource extends BasicResource {
    private Integer value;

    @Override
    public void setAttributes() {
    	super.setAttributes();
        // set display name
        getAttributes().setTitle("PIR Resource");
    }
    
    public WaterSimulatedResource(String id) {
    	super(id);
		this.id=id;
		value = 0;
	}

	@Override
    public void handleGET(CoapExchange exchange) {
        
        // respond to the request
        exchange.respond(getValue().toString());
    }
	
	@Override
    public void handlePOST(CoapExchange exchange) {
        measurement = exchange.getRequestText().trim();
        value = Integer.parseInt(measurement);
        
        // respond to the request
        exchange.respond(ResponseCode.CREATED);
    }
	
	@Override
    public void handlePUT(CoapExchange exchange) {
		measurement = exchange.getRequestText().trim();
        value = Integer.parseInt(measurement);
        // respond to the request
        exchange.respond(ResponseCode.CHANGED);
    }
	
	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

}
