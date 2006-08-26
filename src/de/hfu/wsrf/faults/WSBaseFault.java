package de.hfu.wsrf.faults;

import org.ksoap2.serialization.SoapObject;

import de.hfu.wsrf.addressing.impl.EndpointReference;

//TODO: implement fields as in WSBaseFAults spec 
public class WSBaseFault extends SoapFault {
	
	private String timestamp;
	private String errorCode;
	private String description;
	
	private Object faultCause;
	private EndpointReference originatorReference;
	
	
	public WSBaseFault(SoapObject message) {
		super(message);
		//TODO: parse WS-BAseFault specific fields of super.detail
	}
}
