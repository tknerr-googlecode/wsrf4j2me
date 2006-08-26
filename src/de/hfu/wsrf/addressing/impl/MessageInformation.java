package de.hfu.wsrf.addressing.impl;

import org.ksoap2.serialization.SoapPrimitive;

/**
 * Holder class for the Message Information Headers according to the 
 * WS-Addressing Spec (see http://www.w3.org/Submission/ws-addressing/)
 * <br>
 * Is being handled internally to maintain the conncection to a
 * specific WS-Resource and is not intenden to be used explicitly. 
 * 
 * @author Torben
 */
public class MessageInformation {
	
	public SoapPrimitive messageID = null;
	public SoapPrimitive relatesTo = null;
	public SoapPrimitive to = null;
	public SoapPrimitive action = null;
	public EndpointReference from = null;
	public EndpointReference replyTo = null;
	public EndpointReference faultTo = null;
	
	public MessageInformation() {
	}
	
	public void clear() {
		messageID = relatesTo = to = action = null;
		from = replyTo = faultTo = null;
	}
	
	public String toString() {
		return "messageID="+messageID+", relatesTo="+relatesTo+", to="+to+", " +
				"action="+action+", from="+from+", replyTo="+replyTo+", faultTo="+faultTo;
	}
}
