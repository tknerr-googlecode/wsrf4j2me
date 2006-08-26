package de.hfu.wsrf;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.kxml2.kdom.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import de.hfu.wsrf.addressing.impl.EndpointReference;
import de.hfu.wsrf.addressing.impl.MessageInformation;

/**
 * This class represents a SOAP envelope which keeps track of a WSRF resource's
 * state. It contains an implementation of WS-Addressing, WS-ResourceProperties...
 * 
 * Re-use the envelope instance for subsequent calls in order to access the
 * same WS-Resource.
 * 
 * @author Torben
 */
class WSRFSoapSerializationEnvelope extends SoapSerializationEnvelope {
	
	/** Namespace constant: http://schemas.xmlsoap.org/ws/2004/03/addressing */
    public static final String WSA200403 = "http://schemas.xmlsoap.org/ws/2004/03/addressing";
    /** Namespace constant: http://schemas.xmlsoap.org/ws/2004/08/addressing */
    public static final String WSA200408 = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
    /** Namespace constant: http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceProperties-1.2-draft-01.xsd */
    public static final String WSRF_RP = "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceProperties-1.2-draft-01.xsd";
    /** Namespace constant: http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd */
    public static final String WSRF_RL = "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd";
    
    /** WS-Addressing namespace, set by the constructor */
    public String wsa;
    /** WS-ResourceProperties namespace, set by the constructor */
    public String wsrf_rp;
    /** WS-ResourceLifetime namespace, set by the constructor */
    public String wsrf_rl;
    
    
    /** Vector containing additional (global) namespace definitions */
    Hashtable namespaceDefinitions = new Hashtable();
    
    /** contains the EPR of the associated WS-Resource (if any) */
	EndpointReference endpointReference = null;
	/** contains WSA specific headers of the received SOAP Response */
	MessageInformation addressingInfoIn = new MessageInformation();
	/** contains WSA headers for outgoing SOAP requests. Will be set from the WSRFHttpTransport */
	MessageInformation addressingInfoOut = new MessageInformation();
	
	/** use SoapObject / SoapPrimitive for header too, to be consistent */
	public Object headerIn = null;
	/** override visibility to hide this from user. he should not modify headers */
	private Element[] headerOut = null;
	/** override visibility to hide public variables from user */
	private Object bodyIn, bodyOut;
	/** indicates if the parser parses the soapenv:Header or soapenv:Body */
	private boolean parsingHeader;
	/** will be set if the response is a soapenv:Fault, null otherwise */
	SoapFault lastFault;
	
	
	/**
	 * Default constructor with WSRF specific settings 
	 */
	WSRFSoapSerializationEnvelope() {
		//Globus Toolkit 4 uses SOAP 1.1 and the following namespace definitions
		super(SoapEnvelope.VER11);
		wsa = WSA200403;
		wsrf_rp = WSRF_RP;
		wsrf_rl = WSRF_RL;
		//don't add typing info because we are using doc/lit style
		implicitTypes = true;
		//add additional namespace definitions
		try {
			namespaceDefinitions.put("wsa", wsa);
			//namespaceDefinitions.put("wsr", wsrf_rp); //added on demand
			//namespaceDefinitions.put("wsrl", wsrf_rl); //added on demand
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * hides the super constructor and calls the default
	 * constructor this().
	 * @param version
	 */
	private WSRFSoapSerializationEnvelope(int version) {
		this();
	}
	
	/**
	 * Overridden function. Return the soapenv:Body, no matter if it
	 * is a SoapObject, SoapPrimitive or SoapFault. The client has to take
	 * care of handling / casting the response Object appropriately.
	 * @inheritDoc
	 * @see org.ksoap2.serialization.SoapSerializationEnvelope#getResponse()
	 */
	public Object getResponse() {
		return bodyIn;
	}
	
	/**
	 * Overridden function. Used to insert additional global namespace
	 * definitions before the soapenv:Header and soapenv:Body are written.
	 * @inheritDoc
	 * @see org.ksoap2.SoapEnvelope#write(org.xmlpull.v1.XmlSerializer)
	 */
	public void write(XmlSerializer writer) throws IOException {
		Enumeration nsdefs = namespaceDefinitions.keys();
		while (nsdefs.hasMoreElements()) {
			String prefix = (String) nsdefs.nextElement();
			writer.setPrefix(prefix, (String) namespaceDefinitions.get(prefix));
		}
		super.write(writer);
	}
	
	/**
	 * Overrides the writeHeader() function, which writes only an empty
	 * soapenv:Header. This is the place to include WSRF specific Elements 
	 * in the soapenv:Header.
	 * @inheritDoc
	 * @see org.ksoap2.SoapEnvelope#writeHeader(org.xmlpull.v1.XmlSerializer)
	 */
	public void writeHeader(XmlSerializer writer) throws IOException {
        //write WSA headers for endpoint and action
		if (addressingInfoOut.to!=null) writeObjectBody(writer, addressingInfoOut.to);
		if (addressingInfoOut.action!=null) writeObjectBody(writer, addressingInfoOut.action);
		
		//if we previously received a WS-Resource qualified EPR, include the Resource Identifier in subsequent requests
		if (endpointReference!=null && endpointReference.referenceProperties!=null) {
        	writeObjectBody(writer, endpointReference.referenceProperties);
        }
	}
	
	/**
	 * Overloads the writeObjectBody function to accept SoapPrimitives
	 * @param writer the writer
	 * @param obj the SoapPrimitive to be written
	 * @throws IOException if writer fails for some reason
	 */
	void writeObjectBody(XmlSerializer writer, SoapPrimitive obj) throws IOException {
        writer.startTag(obj.getNamespace(), obj.getName());
        writer.text(obj.toString());
        writer.endTag(obj.getNamespace(), obj.getName());
    }

	
	
	/**
	 * This function is called for each Element (either SoapPrimitive or SoapObject)
	 * being parsed in the soapenv:Header. The namespace and name of the Element
	 * are passed for convenience, they could also be retrieved from the Element itself.
	 * <br>
	 * Any additional processing required for WSRF should be done here.
	 * @param obj the Element being parsed (either SoapPrimitive or SoapObject)
	 * @param namespace the namespace of the Element
	 * @param name the Element name
	 */
	private void processHeaderElement(Object obj, String namespace, String name) {
		
		//System.out.println("header: "+namespace+":"+name+" ["+obj+"]");
		
 		//store WS-Addressing related header info for each message
		if (namespace!=null && namespace.equals(wsa)) {
			if (name==null) throw new RuntimeException("Element without a name");
			if (name.equals("To")) addressingInfoIn.to = (SoapPrimitive) obj;
			if (name.equals("Action")) addressingInfoIn.action = (SoapPrimitive) obj;
			if (name.equals("MessageID")) addressingInfoIn.messageID = (SoapPrimitive) obj;
			if (name.equals("RelatesTo")) addressingInfoIn.relatesTo = (SoapPrimitive) obj;
			if (name.equals("From")) addressingInfoIn.from = new EndpointReference((SoapObject) obj);
			if (name.equals("ReplyTo")) addressingInfoIn.replyTo = new EndpointReference((SoapObject) obj);
			if (name.equals("FaultTo")) addressingInfoIn.faultTo =  new EndpointReference((SoapObject) obj);
		}
	}
	
	/**
	 * This function is called for each Element (either SoapPrimitive or SoapObject)
	 * being parsed in the soapenv:Body. The namespace and name of the Element
	 * are passed for convenience, they could also be retrieved from the Element itself.
	 * <br>
	 * Any additional processing required for WSRF should be done here.
	 * @param obj the Element being parsed (either SoapPrimitive or SoapObject)
	 * @param namespace the namespace of the Element
	 * @param name the Element name
	 */
	private void processBodyElement(Object obj, String namespace, String name) {
		
		//System.out.println("body: "+namespace+":"+name+" ["+obj+"]");
		if (namespace!=null && namespace.equals(wsa)) {
			if (name==null) throw new RuntimeException("Element without a name");
			
			//store EPR for maintaining state in subsequent calls
			if (name.equals("EndpointReference")) {
				SoapObject epr = (SoapObject) obj;
				endpointReference = new EndpointReference(epr);
			}
		}
	}
	
	/**
	 * Overridden function. Parses the soapenv:Header into SoapObject or
	 * SoapPrimitives. This is consistent with the parseBody() function. 
	 * Also sets a flag to indicate that the parser is in the soapenv:Header
	 * section.
	 * @inheritDoc
	 * @see org.ksoap2.SoapEnvelope#parseHeader(org.xmlpull.v1.XmlPullParser)
	 */
	public void parseHeader(XmlPullParser parser) throws IOException, XmlPullParserException {
        headerIn = null;
        addressingInfoIn.clear();
        parsingHeader = true;
        parser.nextTag();
        while (parser.getEventType() == XmlPullParser.START_TAG) {
            Object o = read(parser, null, -1, parser.getNamespace(), parser.getName(), PropertyInfo.OBJECT_TYPE);
            if (headerIn == null)
            	headerIn = o;
            parser.nextTag();
        }
	}
	
	/**
	 * Overriden function. Sets a flag that the parser is in the soapenv:Body
	 * section now. Also changes the SoapFault handling, so that the content
	 * of bodyIn is a SoapObject representing the soapenv:Fault instead of a
	 * SoapFault Exception.
	 * @inheritDoc
	 * @see org.ksoap2.SoapEnvelope#parseBody(org.xmlpull.v1.XmlPullParser)
	 */
	public void parseBody(XmlPullParser parser) throws IOException, XmlPullParserException {
		parsingHeader = false;        
		lastFault = null;
		bodyIn = null;
        parser.nextTag();
        while (parser.getEventType() == XmlPullParser.START_TAG) {
        	if (parser.getNamespace().equals(env) && parser.getName().equals("Fault")) {
        		lastFault = new SoapFault();
            }
        	String rootAttr = parser.getAttributeValue(enc, "root");
            Object o = read(parser, null, -1, parser.getNamespace(), parser.getName(), PropertyInfo.OBJECT_TYPE);
            if ("1".equals(rootAttr) || bodyIn == null)
                bodyIn = o;
            parser.nextTag();
        }
        if (lastFault!=null) throw lastFault;
	}
	
	/**
	 * Overridden function. Same functionality, but we added a
	 * call to processBodyElement(Object, namespace, name) and
	 * processHeaderElement(Object, namespace, name) respectively.
	 * Thus each Element (either a SoapPrimitive or SoapObject) parsed 
	 * in the soapenv:Header and soapenv:Body can be further processed.
	 * <br>
	 * It also fixes a bug with the namespace/name not being applied to 
	 * the read Element.
	 * @inheritDoc
	 * @see org.ksoap2.serialization.SoapSerializationEnvelope#readUnknown(org.xmlpull.v1.XmlPullParser, java.lang.String, java.lang.String)
	 */
	protected Object readUnknown(XmlPullParser parser, String typeNamespace, String typeName) throws IOException, XmlPullParserException {
		//fix broken typeNamespace / typeName handling
		String name = parser.getName();
		String namespace = parser.getNamespace();
        Object result = super.readUnknown(parser, namespace, name);
		
		//here we do the extra processing required for WSRF
		if (parsingHeader) {
			processHeaderElement(result, namespace, name);
		} else {
			processBodyElement(result, namespace, name);
		}
		return result;
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
	void addNamespaceDefinition(String prefix, String namespace) throws IllegalAccessException {
		if (prefix.equals("i") || prefix.equals("d") || prefix.equals("c") || prefix.equals("v") ||
				prefix.equals("wsa") || prefix.equals("wsrp") || prefix.equals("wsrl") || prefix.equals("xxx")) {
			throw new IllegalAccessException("can not add namespace with reserved prefix!");
		} else if (namespaceDefinitions.containsKey(prefix)) {
			throw new IllegalAccessException("prefix already exists with different namespace!");
		}
		namespaceDefinitions.put(prefix, namespace);
	}
	
	/**
	 * Removes the namespace with the given prefix from the list of additionally
	 * added namespaces
	 * @param prefix the prefix of the namespace to be removed
	 * @throws IllegalAccessException Exception if the given prefix is reserved and thus the
	 * namespace definition can not be removed, or if there is no namespace with
	 * such a prefix
	 */
	void removeNamespaceDefinition(String prefix) throws IllegalAccessException {
		if (prefix.equals("i") || prefix.equals("d") || prefix.equals("c") || prefix.equals("v") ||
				prefix.equals("wsa") || prefix.equals("wsrp") || prefix.equals("wsrl") || prefix.equals("xxx")) {
			throw new IllegalAccessException("can not remove namespace with reserved prefix!");
		} else if (!namespaceDefinitions.containsKey(prefix)) {
			throw new IllegalAccessException("no namespace found with the given prefix!");
		}
		namespaceDefinitions.remove(prefix);
	}
}
