package de.hfu.wsrf.resourceproperties;

import java.io.IOException;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import de.hfu.wsrf.faults.WSBaseFault;

/**
 * Interface representing functions to get, query or modify a 
 * WS-Resource's properties, as defined in the WS-ResourceProperties spec.
 * <br>
 * note: only implemented funtions are included, functions not implemented
 * so far are commented out.
 * 
 * @author Torben
 */
public interface WSResourceProperties {
	
	/**
	 * retrieves a property of a resource
	 * @param targetNamespace the namespace of the property
	 * @param name the name of the property
	 * @return a SoapObject containing the property's values
	 * @throws WSBaseFault if the property specified by targetNamespace and
	 * name does not exist, or if the operation is not supported by the resource
 	 * @throws IOException if a connection error occurs
	 * @throws XmlPullParserException if the response could not be parsed
	 */
	public SoapObject getResourceProperty(String targetNamespace, String name) throws WSBaseFault, IOException, XmlPullParserException;
	
	/**
	 * retrieves multiple resource properties at once
	 * @param targetNamespaces a String[] containing the namespaces of the property
	 * @param names a String[] containing the names of the property
	 * @return a SoapObject containing the values of the specified properties
	 * @throws WSBaseFault if one or more of the properties specified by targetNamespaces and
	 * names do not exist, or if the operation is not supported by the resource
 	 * @throws IOException if a connection error occurs
	 * @throws XmlPullParserException if the response could not be parsed
	 */
	public SoapObject getMultipleResourceProperties(String targetNamespaces[], String[] names) throws WSBaseFault, IOException, XmlPullParserException;
	
	/**
	 * retrieves the values of all resource properties
	 * @return a SoapObject containing all properties of the resource
	 * @throws WSBaseFault if the operation is not supported by the resource
 	 * @throws IOException if a connection error occurs
	 * @throws XmlPullParserException if the response could not be parsed
	 */
	public SoapObject getResourcePropertyDocument() throws WSBaseFault, IOException, XmlPullParserException;
	
//	public SoapObject queryResourceProperties(String dialect, String expression) throws WSBaseFault, IOException, XmlPullParserException;
//	
//	public SoapObject putResourcePropertyDocument(SoapObject document) throws WSBaseFault, IOException, XmlPullParserException;
//	public SoapObject insertResourceProperties(Object inserts) throws WSBaseFault, IOException, XmlPullParserException;
//	public SoapObject updateResourceProperties(Object updates) throws WSBaseFault, IOException, XmlPullParserException;
//	public SoapObject setResourceProperties(Object inserts, Object updates, SoapPrimitive[] deletes) throws WSBaseFault, IOException, XmlPullParserException;
//	public SoapObject deleteResourceProperties(SoapPrimitive[] deletes) throws WSBaseFault, IOException, XmlPullParserException;
	
}
