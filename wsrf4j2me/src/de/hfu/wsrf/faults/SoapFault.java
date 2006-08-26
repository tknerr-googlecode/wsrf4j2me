package de.hfu.wsrf.faults;

import org.ksoap2.serialization.SoapObject;

/**
 * We use our own implementation of SoapFault, because
 * org.ksoap2.SoapFault extends IOException, but we need
 * to extend it from Exception. <br>
 * Also, our detail field is a SoapObject instead of a
 * KDom Node. This is consitent with the rest of the API,
 * which hides the underlying KXml stuff.
 * 
 * @author Torben
 */
public class SoapFault extends Exception {
    
	private String faultcode;
    private String faultstring;
    private String faultactor;
    private SoapObject detail;
    
    public SoapFault(SoapObject faultMsg) {
    	if (faultMsg == null) {
    		faultstring = "Unexpected! Fault SoapObject is null!";
    		return;
    	}
    	Object tmp = null;
    	try {
    		tmp = faultMsg.getProperty("faultcode");
    		if (tmp!=null) faultcode = tmp.toString();
		} catch (RuntimeException e) {
			faultcode = "";
		}
		try {
    		tmp = faultMsg.getProperty("faultstring");
    		if (tmp!=null) faultstring = tmp.toString();
		} catch (RuntimeException e) {
			faultstring = "";
		}
		try {
    		tmp = faultMsg.getProperty("faultactor");
    		if (tmp!=null) faultactor = tmp.toString();
		} catch (RuntimeException e) {
			faultactor = "";
		}
		try {
    		tmp = faultMsg.getProperty("detail");
    		if (tmp!=null && tmp instanceof SoapObject) detail = (SoapObject) tmp;
		} catch (RuntimeException e) {
			detail = null;
		}
    }
    
    public String toString() {
        return "SoapFault - faultcode: '" + faultcode + "' faultstring: '" + faultstring + "' faultactor: '" + faultactor + "' detail: " + detail;
    }

	public SoapObject getDetail() {
		return detail;
	}

	public String getFaultactor() {
		return faultactor;
	}

	public String getFaultcode() {
		return faultcode;
	}

	public String getFaultstring() {
		return faultstring;
	}
}
