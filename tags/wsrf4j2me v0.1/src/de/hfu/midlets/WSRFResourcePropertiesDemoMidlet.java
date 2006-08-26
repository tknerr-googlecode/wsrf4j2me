package de.hfu.midlets;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.xmlpull.v1.XmlPullParserException;

import de.hfu.wsrf.WSRFSession;
import de.hfu.wsrf.WSRFSessionFactory;
import de.hfu.wsrf.faults.WSBaseFault;

public class WSRFResourcePropertiesDemoMidlet extends MIDlet implements CommandListener {

    private Form mainForm = new Form("Resource Property Testing");
    private StringItem resultItem = new StringItem("Status", "");
    private Command cmdRPTest = new Command("test Resource Properties", Command.SCREEN, 1);
    private WSRFSession session = null;
    private boolean busy = false;
    
    public WSRFResourcePropertiesDemoMidlet() {
        //mainForm.append(numberField);
        mainForm.append(resultItem);
        mainForm.addCommand(cmdRPTest);
        mainForm.setCommandListener(this);
    }

    public void startApp() {
        Display.getDisplay(this).setCurrent(mainForm);
        
        //create a new stateful session with CounterService Endpoint
    	session = WSRFSessionFactory.createSession("http://localhost:8081/wsrf/services/CounterService");
		
    	session.setDebug(false);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    
	public void testResourceProperties(){
		new Thread() {
    		public void run() {
				try {
					session.setDebug(false);
					
					//create a counter resource (parameters: SOAPAction, namespace and operation name)
					Object response = session.sendRequest("http://counter.com/CounterPortType/createCounter", 
							new SoapObject("http://counter.com", "createCounter"));
					System.out.println("value ("+response.getClass().getName()+"): " + response+"\n");
					
					response = session.sendRequest("http://counter.com/CounterPortType/add", 
							new SoapPrimitive("http://counter.com", "add", "12"));
					System.out.println("value ("+response.getClass().getName()+"): " + response+"\n");
					
					/* this will fail - operation not supported */
//					response = session.getResourcePropertyDocument();
//					System.out.println("value ("+response.getClass().getName()+"): " + response+"\n");
					
					/* destroy and recreate the resource */
					response = session.destroy();
					System.out.println("value ("+response.getClass().getName()+"): " + response+"\n");
					
					response = session.sendRequest("http://counter.com/CounterPortType/createCounter", 
							new SoapObject("http://counter.com", "createCounter"));
					System.out.println("value ("+response.getClass().getName()+"): " + response+"\n");
					
					response = session.sendRequest("http://counter.com/CounterPortType/add", 
							new SoapPrimitive("http://counter.com", "add", "21"));
					System.out.println("value ("+response.getClass().getName()+"): " + response+"\n");
					
					/* get single resource property */
					response = session.getResourceProperty("http://counter.com", "Value");
					System.out.println("value ("+response.getClass().getName()+"): " + response+"\n");
					
					response = session.getResourceProperty(
							"http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd", "CurrentTime");
					System.out.println("value ("+response.getClass().getName()+"): " + response+"\n");
					
					response = session.getResourceProperty(
							"http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd", "Topic");
					System.out.println("value ("+response.getClass().getName()+"): " + response+"\n");
					
					/* get multiple resource properties */
					String tns[] = new String[]{
							"http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd",
							"http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd",
							"http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd",
							"http://counter.com", 
							"http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd",
							"http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd"};
					String names[] = new String[] {"Topic", "TopicExpressionDialects", "FixedTopicSet", "Value", "CurrentTime", "TerminationTime"};
					response = session.getMultipleResourceProperties(tns, names);
					System.out.println("value ("+response.getClass().getName()+"): " + response+"\n");

					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (WSBaseFault e) {
					System.err.println("WSBaseFault when trying to do fancy stuff: " + e.toString());
				}
				busy = false;
    		}
    	}.start();
	}


    public void commandAction(Command c, Displayable d) {
    	if (busy) return;
		if (c == cmdRPTest) {
			busy = true;
			testResourceProperties();
		}
    }

}
