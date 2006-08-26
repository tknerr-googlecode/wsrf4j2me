package de.hfu.wsrf.lifetime;

import java.io.IOException;
import java.util.Date;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import de.hfu.wsrf.faults.WSBaseFault;

/**
 * Interface representing functions to query or modify information about
 * a WS-Resource's lifetime, as defined in the WS-ResourceLifetime spec.
 * <br>
 * note: only implemented funtions are included, functions not implemented
 * so far are commented out.
 * 
 * @author Torben
 */
public interface WSResourceLifetime {
	
	/**
	 * destroys the current resource
	 * @return a SoapObject representing empty wsrf-rl:DestroyResponse
	 * @throws WSBaseFault if the resource could not be destroyed for some reason,
	 *  or if the operation is not supported by the resource
 	 * @throws IOException if a connection error occurs
	 * @throws XmlPullParserException if the response could not be parsed
	 */
	public SoapObject destroy()  throws WSBaseFault, IOException, XmlPullParserException;
	
//	public SoapObject currentTime()  throws WSBaseFault, IOException, XmlPullParserException;
//	public SoapObject terminationTime()  throws WSBaseFault, IOException, XmlPullParserException;
//	public SoapObject setTerminationTime(Date isoDate)  throws WSBaseFault, IOException, XmlPullParserException;
//	public SoapObject setTerminationTime(String xsdDuration)  throws WSBaseFault, IOException, XmlPullParserException;
	
}
