package de.hfu.wsrf.addressing.impl;

import org.ksoap2.serialization.*;


/**
 * Represents a Service Endpoint Reference according to the WS-Addressing
 * Spec (see http://www.w3.org/Submission/ws-addressing/) holding
 * the Endpoint Reference related information.
 * <br>
 * Is being handled internally to maintain the conncection to a
 * specific WS-Resource and is not intenden to be used explicitly. 
 * 
 * @author Torben
 */
public class EndpointReference {
	
	//store EPR properties
	public SoapPrimitive address = null;
	public SoapObject referenceProperties = null;
	public SoapObject referenceParameters = null;
	public SoapPrimitive portType = null;
	public SoapPrimitive serviceName = null;
	public SoapObject policy = null;
	
	//store EPR as SoapObject for later use
	public SoapObject _SoapObject = null;
	
	public EndpointReference(SoapObject epr) {
		//get EPR info
		address = (SoapPrimitive) epr.getProperty("Address"); //mandatory
		try { referenceProperties = (SoapObject) epr.getProperty("ReferenceProperties"); } catch (RuntimeException e) {}
		try { referenceParameters = (SoapObject) epr.getProperty("ReferenceParameters"); } catch (RuntimeException e) {}
		try { policy = (SoapObject) epr.getProperty("Policy"); } catch (RuntimeException e) {}
		try { serviceName = (SoapPrimitive) epr.getProperty("ServiceName"); } catch (RuntimeException e) {}
		try { portType = (SoapPrimitive) epr.getProperty("PortType"); } catch (RuntimeException e) {}
		//store EPR as SoapObject
		_SoapObject = epr;
	}
	
	public String toString() {
		return _SoapObject.toString();
	}
	
}
