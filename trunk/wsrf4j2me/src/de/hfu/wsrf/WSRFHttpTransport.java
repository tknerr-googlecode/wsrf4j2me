package de.hfu.wsrf;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.transport.HttpTransport;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Extension of the HttpTransport class to include WSRF-specific
 * header infromation before the call is being sent.
 * 
 * @author Torben
 */
public class WSRFHttpTransport extends HttpTransport {
	
	/**
	 * Constructor's visibility reduced to default
	 * @param url
	 */
	WSRFHttpTransport(String url) {
		super(url);
	}
	
	/**
	 * Overriden Function. Inserts WS-Addressing specific information 
	 * in the soapenv:Header before sending the request.
	 * @inheritDoc
	 * @see org.ksoap2.transport.Transport#call(java.lang.String, org.ksoap2.SoapEnvelope)
	 */
	public void call(String soapAction, SoapEnvelope envelope) throws IOException, XmlPullParserException {
		//set WSA headers before sending the SOAP request
		if (envelope instanceof WSRFSoapSerializationEnvelope) {
			WSRFSoapSerializationEnvelope tmp = (WSRFSoapSerializationEnvelope) envelope;
			tmp.addressingInfoOut.to = new SoapPrimitive(tmp.wsa, "To", url);
			if (soapAction==null || soapAction.equals("")) 
				tmp.addressingInfoOut.action = null;
			else
				tmp.addressingInfoOut.action = new SoapPrimitive(tmp.wsa, "Action", soapAction);
			envelope = tmp;
		}
		super.call(soapAction, envelope);
	}
	
}
