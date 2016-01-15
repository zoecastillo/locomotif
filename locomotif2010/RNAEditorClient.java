package rnaeditor;


import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.w3c.dom.Element;


/**
 * @author Janina Reeder
 *
 * This class defines the client side of the webservice communication
 */
public class RNAEditorClient {
	
	final static String server = "bibiwstest.techfak.uni-bielefeld.de";
	final static String urlstring = "http://bibiwstest.techfak.uni-bielefeld.de/wsdl/Locomotif.wsdl";

	
	/**
	 * The request function of the webservice
	 * 
	 * @param domdoc, Object containing the Dom Document (JDom output)
	 * @return the String holding the id
	 */
	public static String rnaeditorRequest(String email, org.w3c.dom.Document domdoc){
		String id = null;
		
		try{
            /* declare where to find the describing WSDL */
            final URL wsdl = new URL(urlstring);
            /* prepare the call (the same for all called methods) */
            Service ser = new Service(wsdl, new QName("http://" + server + "/Locomotif/axis/LocomotifPort",
                    "LocomotifImplementationService"));
            Call call = (Call) ser.createCall(new QName("LocomotifPort"), "request");
            /* call and get id */
            id = (String) call.invoke(new Object[] { email, domdoc });
		}
		catch (RemoteException e) {
			//on error WS will throw a soapfault as hobitstatuscode
			Element root = ((AxisFault) e).lookupFaultDetail(
					new QName( "http://hobit.sf.net/hobitStatuscode.xsd", "hobitStatuscode"));
			if (root == null) { 
				id = "Exception: ws remote error (no Hobitstatuscode): " + e.toString();
				//id = "Exception: An error occurred during matcher compilation.";
				return id;
			}
			String description = root.getLastChild().getFirstChild().getNodeValue();
			String code = root.getFirstChild().getFirstChild().getNodeValue();
			id = "Exception: " + code + ", desc: "+ description;
			//Using this kind of Webservice there is only one one field for giving back a
			//error message. When an exception occours, the client side of Axis will throw
			//an RemoteException which includes the class name of the thrown exception.
			//There is no way to get more information like the original stacktrace !!!
			//System.err.println("ws remote error (" + e.toString() + ")");
		} 
		catch (MalformedURLException e) {
			id = "Exception: failed (" + e.toString() + ")";
		} 
		catch (ServiceException e) {
			id = "Exception: Service unavailable (" + e.toString() + ")";
		} 	
		return id;
	}
	
	/**
	 * Method for performing the response function on client side
	 * 
	 * @param id, String holding the id of the process
	 * @return a String holding the response DOM document
	 */
	public static String rnaeditorResponse(String id){
		String answer = null;
		try {
            /* declare where to find the describing WSDL */
            final URL wsdl = new URL(urlstring);
            /* prepare the call (the same for all called methods) */
            Service ser = new Service(wsdl, new QName("http://" + server + "/Locomotif/axis/LocomotifPort",
                    "LocomotifImplementationService"));
            Call call = (Call) ser.createCall(new QName("LocomotifPort"), "response");
            /* call and get id */
            answer = (String) call.invoke(new Object[] { id });
            
            // error handling with proper information for the user
		} catch (RemoteException e) {
			//on error WS will throw a soapfault as hobitstatuscode
			Element root = ((AxisFault) e).lookupFaultDetail(
					new QName( "http://hobit.sf.net/hobitStatuscode.xsd", "hobitStatuscode"));
			if (root == null) { 
				return "Exception: ws remote error (no Hobitstatuscode): " + e.toString();
			}
			String description = root.getLastChild().getFirstChild().getNodeValue();
			String code = root.getFirstChild().getFirstChild().getNodeValue();
			return "Exception: " + code + ", desc: "+ description;
			//Using this kind of Webservice there is only one one field for giving back a
			//error message. When an axception occours, the client side of Axis will throw
			//an RemoteException which includes the class name of the thrown exception.
			//There is no way to get more information like the original stacktrace !!!
			//System.err.println("result not available (STATUS :: " + e.toString() + ")");
		} 

		catch (MalformedURLException e) {
			answer = "Exception: failed (" + e.toString() + ")";
		} 
		catch (ServiceException e) {
			answer = "Exception: Service unavailable (" + e.toString() + ")";
		} 	
		catch (IllegalArgumentException x) {
			answer = "Exception: Happens if the parser does not support JAXP 1.2";
			//} catch (ParserConfigurationException e) {
			//  System.err.println(e.toString());
			//} catch (SAXException e) {
			//  System.err.println(e.toString());
			//  System.err.println(e.getMessage());
		} 

		
		return answer;
	}

	
	/**
	 * The run function of the webservice
	 * 
	 * @param sequence, String holding the sequence in FASTA format
	 * @param mid, the id of the matcher that is to be run
	 * @return the String holding the id
	 */
	public static String rnaeditorRun(String email, String sequence, String mid){
		String id = null;
		
		try{
            /* declare where to find the describing WSDL */
            final URL wsdl = new URL(urlstring);
            /* prepare the call (the same for all called methods) */
            Service ser = new Service(wsdl, new QName("http://" + server + "/Locomotif/axis/LocomotifPort",
                    "LocomotifImplementationService"));
            Call call = (Call) ser.createCall(new QName("LocomotifPort"), "run");
            /* call and get id */
	
            id = (String) call.invoke(new Object[] { email, sequence, mid });
		}
		catch (RemoteException e) {
			//on error WS will throw a soapfault as hobitstatuscode
			Element root = ((AxisFault) e).lookupFaultDetail(
					new QName( "http://hobit.sf.net/hobitStatuscode.xsd", "hobitStatuscode"));
			if (root == null) { 
				id = "Exception: ws remote error (no Hobitstatuscode): " + e.toString();
				return id;
			}
			String description = root.getLastChild().getFirstChild().getNodeValue();
			String code = root.getFirstChild().getFirstChild().getNodeValue();
			id = "Exception: " + code + ", desc: "+ description;
			//Using this kind of Webservice there is only one one field for giving back a
			//error message. When an exception occours, the client side of Axis will throw
			//an RemoteException which includes the class name of the thrown exception.
			//There is no way to get more information like the original stacktrace !!!
			//System.err.println("ws remote error (" + e.toString() + ")");
		} 
		catch (MalformedURLException e) {
			id = "Exception: failed (" + e.toString() + ")";
		} 
		catch (ServiceException e) {
			id = "Exception: Service unavailable (" + e.toString() + ")";
		} 		
		return id;
	}
	
	/**
	 * Method for performing the response function on client side
	 * 
	 * @param id, String holding the id of the process
	 * @return a String holding the response DOM document
	 */
	public static String rnaeditorMatchresult(String id){
	    String answer = null;
	    try {
		// declare where to find the describing WSDL 
		final URL wsdl = new URL(urlstring);
		// prepare the call (the same for all called methods)
		Service ser = new Service(wsdl, new QName("http://" + server + "/Locomotif/axis/LocomotifPort",
							  "LocomotifImplementationService"));
		Call call = (Call) ser.createCall(new QName("LocomotifPort"), "matchresult");
		//call and get id
		answer = (String) call.invoke(new Object[] { id });
            
		// error handling with proper information for the user
	    } catch (RemoteException e) {
		//on error WS will throw a soapfault as hobitstatuscode
		Element root = ((AxisFault) e).lookupFaultDetail(
								 new QName( "http://hobit.sf.net/hobitStatuscode.xsd", "hobitStatuscode"));
		if (root == null) { 
			return "Exception: ws remote error (no Hobitstatuscode): " + e.toString();
		}
		String description = root.getLastChild().getFirstChild().getNodeValue();
		String code = root.getFirstChild().getFirstChild().getNodeValue();
		return "Exception: " + code + ", desc: "+ description;
		//Using this kind of Webservice there is only one one field for giving back a
		//error message. When an axception occours, the client side of Axis will throw
		//an RemoteException which includes the class name of the thrown exception.
		//There is no way to get more information like the original stacktrace !!!
		//System.err.println("result not available (STATUS :: " + e.toString() + ")");
	    }
		catch (MalformedURLException e) {
			answer = "Exception: failed (" + e.toString() + ")";
		} 
		catch (ServiceException e) {
			answer = "Exception: Service unavailable (" + e.toString() + ")";
		} 	
		catch (IllegalArgumentException x) {
			answer = "Exception: Happens if the parser does not support JAXP 1.2";
			//} catch (ParserConfigurationException e) {
			//  System.err.println(e.toString());
			//} catch (SAXException e) {
			//  System.err.println(e.toString());
			//  System.err.println(e.getMessage());
		} 

	    return answer;
	}
	
}
