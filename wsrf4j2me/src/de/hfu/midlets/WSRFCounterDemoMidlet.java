package de.hfu.midlets;

import java.io.IOException;

import javax.microedition.lcdui.Alert;
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

public class WSRFCounterDemoMidlet extends MIDlet implements CommandListener {
	
	private Form optionsForm = new Form("Options");
    private TextField portField = new TextField("Port: ", "", 5, TextField.DECIMAL);
    private TextField ipField = new TextField("Hostname: ", "", 255, TextField.ANY);
    
    private Form mainForm = new Form("CounterService");
    private TextField numberField = new TextField("Add this: ", "", 6, TextField.DECIMAL);
    private StringItem resultItem = new StringItem("Status", "");
    private Command cmdCreate = new Command("createCounter", Command.SCREEN, 1);
    private Command cmdGet = new Command("getCounterRP", Command.SCREEN, 1);
    private Command cmdAdd = new Command("addToCounter", Command.SCREEN, 1);
    private Command cmdDestroy = new Command("destroy", Command.SCREEN, 1);
    private Command cmdOptions = new Command("options", Command.SCREEN, 1);
    private Command cmdCancel = new Command("cancel", Command.SCREEN, 1);
    private Command cmdSaveOptions = new Command("save", Command.SCREEN, 1);
    private WSRFSession session = null;
    private String serverIP = null;
    private int serverPort = -1;
    private boolean busy = false;
    
    public WSRFCounterDemoMidlet() {
    	
    	optionsForm.addCommand(cmdCancel);
    	optionsForm.addCommand(cmdSaveOptions);
    	optionsForm.append(ipField);
    	optionsForm.append(portField);
    	optionsForm.setCommandListener(this);
    	
        //mainForm.append(numberField);
        mainForm.append(resultItem);
        mainForm.addCommand(cmdOptions);
        mainForm.addCommand(cmdCreate);
        //mainForm.addCommand(cmdAdd); 
        //mainForm.addCommand(cmdDestroy); 
        mainForm.setCommandListener(this);
    }

    public void startApp() {
        Display.getDisplay(this).setCurrent(mainForm);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }
  
    public void createCounterResource() {
    	new Thread() {
    		public void run() {
    			try {
    				//create a new stateful session
    		    	session = WSRFSessionFactory.createSession("http://"+serverIP+":"+serverPort+"/wsrf/services/CounterService");
    				session.setDebug(false);

    				//create counter resource
    				resultItem.setText("creating counter resource [createCounter]...");
					Object response = session.sendRequest("http://counter.com/CounterPortType/createCounter", 
							new SoapObject("http://counter.com", "createCounter"));
					//System.out.println("value ("+response.getClass().getName()+"): " + response+"\n");
					resultItem.setText("resource successfully created! " + response);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (WSBaseFault e) {
					e.printStackTrace();
					resultItem.setText("WSBaseFault when creating resource: " + e.getFaultstring());
				}
				busy = false;
    		}
    	}.start();
    }
        
    public void addToCounterResource(final String number) {
    	new Thread() {
    		public void run() {
    			try {
    				//create counter resource
    				resultItem.setText("adding "+number+" to counter resource...");
    				Object response = session.sendRequest("http://counter.com/CounterPortType/add", 
    						new SoapPrimitive("http://counter.com", "add", number.trim().equals("")?"0":number+""));
					//System.out.println("value ("+response.getClass().getName()+"): " + response+"\n");
					resultItem.setText("new value of counter:" + response);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (WSBaseFault e) {
					e.printStackTrace();
					resultItem.setText("WSBaseFault when adding "+number+" to counter resource: " + e.getFaultstring());
				}
				busy = false;
    		}
    	}.start();
    }
   
    public void getCounterValueResourceProperty() {
    	new Thread() {
    		public void run() {
    			try {
    				//create counter resource
    				resultItem.setText("getting property 'Value' of counter resource...");
    				Object response = session.getResourceProperty("http://counter.com", "Value");
					//System.out.println("value ("+response.getClass().getName()+"): " + response+"\n");
					resultItem.setText("property 'Value' of counter resource is: " + response);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (WSBaseFault e) {
					e.printStackTrace();
					resultItem.setText("WSBaseFault when getting property 'Value' of counter resource: " + e.getFaultstring());
				}
				busy = false;
    		}
    	}.start();
    }
    
    public void destroyCounterResource() {
    	new Thread() {
    		public void run() {
    			try {
    				//create counter resource
    				resultItem.setText("destroying counter resource...");
    				Object response = session.destroy();
					//System.out.println("value ("+response.getClass().getName()+"): " + response+"\n");
					resultItem.setText("counter resource destroyed: " + response);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (WSBaseFault e) {
					e.printStackTrace();
					resultItem.setText("WSBaseFault when destroying counter resource: " + e.getFaultstring());
				}
				busy = false;
    		}
    	}.start();
    }
    


    public void commandAction(Command c, Displayable d) {
    	if (busy) {
    		Display.getDisplay(this).setCurrent(new Alert("busy!"), mainForm);
    		return;
    	}
		if (c == cmdCreate) {
			if (serverPort < 1 || serverIP == null || serverIP.trim().equals("")) {
				Display.getDisplay(this).setCurrent(new Alert("server ip or port not set!"), mainForm);
	    		return;
			}
			busy = true;
			createCounterResource();
			mainForm.addCommand(cmdGet);
			mainForm.addCommand(cmdAdd);
			mainForm.append(numberField);
			mainForm.addCommand(cmdDestroy);
			mainForm.removeCommand(cmdCreate);
			mainForm.removeCommand(cmdOptions);
		} else if (c == cmdAdd){
			busy = true;
			addToCounterResource(numberField.getString().trim());
		} else if (c == cmdDestroy) {
			busy = true;
			destroyCounterResource();
			mainForm.addCommand(cmdCreate);
			mainForm.addCommand(cmdOptions);
			mainForm.removeCommand(cmdAdd);
			mainForm.removeCommand(cmdGet);
			mainForm.removeCommand(cmdDestroy);
			mainForm.delete(mainForm.size()-1);
		} else if (c == cmdGet) {
			busy = true;
			getCounterValueResourceProperty();
		} else if (c == cmdOptions) {
			Display.getDisplay(this).setCurrent(optionsForm);
		} else if (c == cmdCancel) {
			Display.getDisplay(this).setCurrent(mainForm);
		} else if (c == cmdSaveOptions) {
			String ip = ipField.getString();
			String port = portField.getString();
			if (ip == null || ip.trim().equals("") || port == null || port.equals("")) {
				Display.getDisplay(this).setCurrent(new Alert("server ip or port not set!"), optionsForm);
	    		return;
			}
			serverIP = ip;
			serverPort = Integer.parseInt(port);
			Display.getDisplay(this).setCurrent(mainForm);
		}
    }

}
