package de.hfu.midlets;

import java.io.IOException;

import javax.microedition.io.HttpsConnection;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.xmlpull.v1.XmlPullParserException;

import de.hfu.wsrf.WSRFSession;
import de.hfu.wsrf.WSRFSessionFactory;
import de.hfu.wsrf.faults.WSBaseFault;

public class WSRFPerformanceTestMidlet extends MIDlet implements CommandListener {

    private Form mainForm = new Form("WSRF Performance Test");
    private StringItem resultItem = new StringItem("Status", "");
    private Command cmdStart = new Command("start performance test", Command.SCREEN, 1);
    private WSRFSession session = null;
    private boolean busy = false;
    
    //params
    private final String GRID_HOST = "localhost";
    private final int GRID_PORT = 8080;
    private final int LOOPS = 50;
        
    public WSRFPerformanceTestMidlet() {
        //mainForm.append(numberField);
        mainForm.append(resultItem);
        mainForm.addCommand(cmdStart);
        mainForm.setCommandListener(this);
    }

    public void startApp() {
        Display.getDisplay(this).setCurrent(mainForm);
        //create a new stateful session
    	session = WSRFSessionFactory.createSession("http://"+GRID_HOST+":"+GRID_PORT+"/wsrf/services/myexamples/HelloService");
		session.setDebug(false);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    
	public void startPerfomanceTest(){
		new Thread() {
    		public void run() {
				try {
					session.setDebug(false);
					session.transport.measuretime = true;
					
					System.out.println("transmissiontime,totaltime,msgsize,memory (WSRF)");
					
					for (int k=0; k<LOOPS; k++) {
						
						//measure used memory
						System.gc();
						long usedmemory_start = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
						//measure total time
						long totaltime_start = System.currentTimeMillis();
						
						Object response = session.sendRequest("http://www.hs-furtwangen.de/ns/HelloService/HelloPortType/sayHelloRequest", 
								new SoapPrimitive("http://www.hs-furtwangen.de/ns/HelloService", "sayHello", "Bubb"));
						//System.out.println("value ("+response.getClass().getName()+"): " + response+"\n");
						
						long totaltime = System.currentTimeMillis() - totaltime_start;
						long usedmemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) - usedmemory_start;
						long transmissiontime = session.transport.transmissiontime;
						long messagesize = session.transport.messagesize;
						long processingtime = totaltime - transmissiontime;
						
						//System.out.println(transmissiontime+","+totaltime+","+messagesize+","+usedmemory);
						System.out.println(totaltime+","+messagesize);
//						System.out.println("total duration: "+totaltime_end);
						
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (WSBaseFault e) {
					System.err.println("WSBaseFault when trying to do fancy stuff: " + e.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				busy = false;
    		}
    	}.start();
	}

    public void commandAction(Command c, Displayable d) {
    	if (busy) return;
		if (c == cmdStart) {
			busy = true;
			startPerfomanceTest();
		}
    }

}
