package de.hfu.wsrf;

/**
 * Simple factory class that creates a new stateful WSRFSession for you
 * @author Torben
 */
public class WSRFSessionFactory {
	
	/**
	 * Factory Method that creates a new stateful WSRFSession with the
	 * WS-Resource specified by the given endpointURI
	 * @param endpointURI the Endpoint URI of the WS-Resource 
	 * @return a stateful WSRFSession with the WS-Resource at the given Endpoint
	 */
	public static WSRFSession createSession(String endpointURI) {
		return new WSRFSession(endpointURI);
	}
	
	/**
	 * Default constructor hidden
	 */
	private WSRFSessionFactory(){}
}
