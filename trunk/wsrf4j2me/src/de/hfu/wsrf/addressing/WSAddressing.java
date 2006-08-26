package de.hfu.wsrf.addressing;

/**
 * Interface which exposes some WS-Addressing Information to the client.
 * Since the WS-Addressing specific stuff is handled internally, the client
 * can not modify the WS-Addressing headers.
 * There's only one public method providing information about the WS-Addressing
 * related headers of the incoming SOAP Response.
 * 
 * @author Torben
 */
public interface WSAddressing {
	
	/**
	 * provides the WS-Addressing headers of the incoming SOAP 
	 * Response as a plain String.
	 * @return the WS-Addressing headers of the incoming SOAP 
	 * Response
	 */
	public String getAddressingInfo();
}
