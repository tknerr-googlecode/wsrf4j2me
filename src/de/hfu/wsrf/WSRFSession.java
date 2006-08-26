package de.hfu.wsrf;

import java.io.IOException;
import java.util.Vector;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.xmlpull.v1.XmlPullParserException;

import de.hfu.wsrf.addressing.WSAddressing;
import de.hfu.wsrf.faults.WSBaseFault;
import de.hfu.wsrf.lifetime.WSResourceLifetime;
import de.hfu.wsrf.resourceproperties.WSResourceProperties;

/**
 * Class representing a stateful WSRF Session, i.e. a stateful connection
 * to a WS-Resource. Use the WSRFSessionFactory class to create a 
 * WSRFSession instance.
 * 
 * @author Torben
 */
public class WSRFSession implements WSAddressing, WSResourceProperties, WSResourceLifetime {
	
	/** Soap envelope with WSRF specific extensions */
	private WSRFSoapSerializationEnvelope envelope;
	/** HttpTransport with WSRF specific extensions */
	public WSRFHttpTransport transport;
	/** flag for debug mode */
	private boolean debug = false;
	
	/**
	 * Constructor with package-level visibility, to be called
	 * from the WSRFSessionFactory only
	 * @param endpointURI the Endpoint URI of the WS-Resource
	 */
	WSRFSession(String endpointURI) {
		envelope = new WSRFSoapSerializationEnvelope();
		transport = new WSRFHttpTransport(endpointURI);
	}
	
	/**
	 * sends a SOAP request with the given SOAP Action and SOAP Body to 
	 * the WS-Resource. Example:<br>
	 * <code>
	 * //create an element createCounter with namespace http://counter.com
	 * //note: tns:createCounter is the operation exposed by the WSDL document
	 * SoapPrimitive msg = new SoapPrimitive("http://counter.com", "createCounter", "")
	 * 
	 * //send the request with the SOAP Action http://counter.com/CounterPortType/createCounter
	 * Object response = session.sendRequest("http://counter.com/CounterPortType/createCounter", msg);
	 * 
	 * //print the response
	 * System.out.println("value ("+response.getClass().getName()+"): " + response+"\n");
	 * </code>
	 * 
	 * @param soapAction the SOAP action of the request
	 * @param soapBody the SOAP Body of the request, being either a 
	 * SoapPrimitive or SoapObject containing the elements that will
	 * be put in the enclosing soapenv:Body
	 * @return the response soapenv:Body which followed the request. 
	 * Will be either a SoapPrimitive or SoapObject
	 * @throws WSBaseFault if the message could not be understood from
	 * the Endpoint (i.e. the WS-Resource)
 	 * @throws IOException if a connection error occurs
	 * @throws XmlPullParserException if the response could not be parsed
	 */
	public Object sendRequest(String soapAction, Object soapBody) throws IOException, XmlPullParserException, WSBaseFault {
		envelope.setOutputSoapObject(soapBody);
		call(soapAction);
		return envelope.getResponse();
	}
	
	/**
	 * sets the debug mode. If true, then the XML representation of 
	 * each SOAP message being sent or received will be printed to 
	 * the Standard out.
	 * @param debug true to enable debug mode, false otherwise
	 */
	public void setDebug(boolean debug) {
		transport.debug = debug;
		this.debug = debug;
	}
	
	/**
	 * Adds the given namespace with prefix to the global namespace definitions
	 * (as attributes of the soapenv:Envelope Element).<br>
	 * The following prefixes are reserved and can not be added:<br>
	 * <li> "i" for XMLSchema-Instance</li>
	 * <li> "d" for XML-Schema</li>
	 * <li> "c" for SOAP-Encoding</li>
	 * <li> "v" for SOAP-Envelope</li>
	 * <li> "wsa" for WS-Addressing</li>
	 * <li> "wsrp" for WS-ResourceProperties</li>
	 * <li> "wsrl" for WS-ResourceLifetime</li>
	 * <li> "xxx" for temporary use</li>
	 * @param prefix the prefix of the namespace to be added
	 * @param namespace the namespace to be added
	 * @throws IllegalAccessException if the given prefix is already present or is a reserved one
	 */
	public void addGlobalNamespaceDefinition(String prefix, String namespaceURI) throws IllegalAccessException {
		envelope.addNamespaceDefinition(prefix, namespaceURI);
	}
	
	/**
	 * Removes the namespace with the given prefix from the list of additionally
	 * added namespaces
	 * @param prefix the prefix of the namespace to be removed
	 * @throws IllegalAccessException Exception if the given prefix is reserved and thus the
	 * namespace definition can not be removed, or if there is no namespace with
	 * such a prefix
	 */
	public void removeGlobalNamespaceDefinition(String prefix) throws IllegalAccessException {
		envelope.removeNamespaceDefinition(prefix);
	}
	
	/**
	 * private function. Intercepts SoapFault-type IOExceptions and throws a
	 * WSBaseFault instead. Thus we can differentiate between IOException and
	 * SOAP Fault exceptions
	 * @param soapAction the SOAP action of the request
	 * @throws WSBaseFault if the message could not be understood from
	 * the Endpoint (i.e. the WS-Resource)
 	 * @throws IOException if a connection error occurs
	 * @throws XmlPullParserException if the response could not be parsed
	 */
	private void call(String soapAction) throws WSBaseFault, XmlPullParserException, IOException {
		try {
			transport.call(soapAction, envelope);
		} catch (IOException e) {
			if (e instanceof SoapFault) {
				//TODO: fill exception with WSBaseFault details
				throw new WSBaseFault(getLastFault());
			} else {
				throw e;	
			}
		} finally {
			if (debug) {
				System.out.println("request [action="+soapAction+"]:\n"+transport.requestDump.trim());
				System.out.println("response [action="+envelope.addressingInfoIn.action+"]:\n"+transport.responseDump);
			}
		}
	}
	
	/**
	 * returns the SoapObject representing the last WSBaseFault,
	 * should be called after an WSBaseFault Exception has occured.
	 * @return the SoapObject representing the last WSBaseFault, 
	 * or null if no WSBaseFault occured since the last request.
	 */
	public SoapObject getLastFault() {
		if (envelope.lastFault == null) { 
			return null;
		} 
		return (SoapObject) envelope.getResponse();
	}
	
	/**
	 * @inheritDoc
	 * @see de.hfu.wsrf.addressing.WSAddressing#getAddressingInfo()
	 */
	public String getAddressingInfo() {
		return envelope.addressingInfoIn.toString();
	}
	
	/**
	 * @inheritDoc
	 * @see de.hfu.wsrf.resourceproperties.WSResourceProperties#getResourceProperty(java.lang.String, java.lang.String)
	 */
	public SoapObject getResourceProperty(String targetNamespace, String name) throws WSBaseFault, XmlPullParserException, IOException {
		try {
			envelope.namespaceDefinitions.put("wsrp", envelope.wsrf_rp);
			envelope.namespaceDefinitions.put("xxx", targetNamespace);
			SoapPrimitive msg = new SoapPrimitive(envelope.wsrf_rp, "GetResourceProperty", "xxx:"+name);
			envelope.setOutputSoapObject(msg);
			call("http://docs.oasis-open.org/wsrf/rpw-2/GetResourceProperty/GetResourcePropertyRequest");
		} finally {
			envelope.namespaceDefinitions.remove("wsrp");
			envelope.namespaceDefinitions.remove("xxx");
		}
		return (SoapObject) envelope.getResponse();
	}

	/**
	 * @inheritDoc
	 * @see de.hfu.wsrf.resourceproperties.WSResourceProperties#getMultipleResourceProperties(java.lang.String[], java.lang.String[])
	 */
	public SoapObject getMultipleResourceProperties(String[] targetNamespaces, String[] names) throws WSBaseFault, XmlPullParserException, IOException {
		if (targetNamespaces.length != names.length) {
			throw new RuntimeException("array sizes of targetNamespaces[] and String[] don't match!");
		}
		Vector tmpNamespaces = new Vector();
		Vector tmpPrefixes = new Vector();
		try {
			envelope.namespaceDefinitions.put("wsrp", envelope.wsrf_rp);
			SoapObject msg = new SoapObject(envelope.wsrf_rp, "GetMultipleResourceProperties");
			for (int i=0; i<targetNamespaces.length; i++) {
				String prefix = "";
				if (!tmpNamespaces.contains(targetNamespaces[i])) {
					prefix = "xxx"+i;
					tmpNamespaces.addElement(targetNamespaces[i]);
					tmpPrefixes.addElement(prefix);
					envelope.namespaceDefinitions.put(prefix, targetNamespaces[i]);
				} else {
					prefix = (String) tmpPrefixes.elementAt(tmpNamespaces.indexOf(targetNamespaces[i]));
				}
				PropertyInfo info = new PropertyInfo();
				info.name = "ResourceProperty";
				info.namespace = envelope.wsrf_rp;
				info.type = PropertyInfo.STRING_CLASS; //important!!! otherwise it would write xsd:string as attribute
				msg.addProperty(info, prefix+":"+names[i]);
			}
			envelope.setOutputSoapObject(msg);
			call("http://docs.oasis-open.org/wsrf/rpw-2/GetMultipleResourceProperties/GetMultipleResourcePropertiesRequest");
		} finally {
			for (int i=0; i<tmpPrefixes.size(); i++) {
				envelope.namespaceDefinitions.remove(tmpPrefixes.elementAt(i));
			}
			envelope.namespaceDefinitions.remove("wsrp");
		}
		return (SoapObject) envelope.getResponse();
	}

	/**
	 * @inheritDoc
	 * @see de.hfu.wsrf.resourceproperties.WSResourceProperties#getResourcePropertyDocument()
	 */
	public SoapObject getResourcePropertyDocument() throws WSBaseFault, XmlPullParserException, IOException {
		try {
			envelope.namespaceDefinitions.put("wsrp", envelope.wsrf_rp);
			SoapObject msg = new SoapObject(envelope.wsrf_rp, "GetResourcePropertyDocument");
			envelope.setOutputSoapObject(msg);
			call("http://docs.oasis-open.org/wsrf/rpw-2/GetResourcePropertyDocument/GetResourcePropertyDocumentRequest");
		} finally {
			envelope.namespaceDefinitions.remove("wsrp");
		}
		return (SoapObject) envelope.getResponse();
	}

	/**
	 * @inheritDoc
	 * @see de.hfu.wsrf.lifetime.WSResourceLifetime#destroy()
	 */
	public SoapObject destroy() throws WSBaseFault, IOException, XmlPullParserException {
		try {
			envelope.namespaceDefinitions.put("wsrl", envelope.wsrf_rl);
			SoapObject msg = new SoapObject(envelope.wsrf_rl, "Destroy");
			envelope.setOutputSoapObject(msg);
			call("http://docs.oasis-open.org/wsrf/rlw-2/ImmediateResourceTermination/DestroyRequest");
			//reset the epr
			envelope.endpointReference = null;
		} finally {
			envelope.namespaceDefinitions.remove("wsrl");
		}
		return (SoapObject) envelope.getResponse();
	}

}
