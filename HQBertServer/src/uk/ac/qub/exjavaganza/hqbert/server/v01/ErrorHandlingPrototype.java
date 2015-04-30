/**
 * Alternative log class. This one uses in-house means of error handling. 
 * The log file in this case is Outfile.log. I'll need to have a word with somebody who's dealing
 * with the HQBertServer about how we want to use something like this. Please note that this 
 * class is currently exemplar in form. 
 */
package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.IOException;
import java.util.logging.*;

import org.junit.internal.runners.ErrorReportingRunner;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
 

/**
 * @author Jack Ferguson
 *
 */
public class ErrorHandlingPrototype {  //Class begins

	private static ErrorHandlingPrototype instance = null;
		   
	Handler handler;
	
	public static Logger logger;
	
	protected ErrorHandlingPrototype() {
		setupLogger();
	}
	
	 public static ErrorHandlingPrototype getInstance() {
	      if(instance == null) {
	         instance = new ErrorHandlingPrototype();
	      }
	      return instance;
	   }

	/**
	 * Setting up the logger.
	 */
	public void setupLogger() {
		
		handler = null;
			
			try {
				handler = new FileHandler("OutFile.xml");

			} catch (SecurityException | IOException e) {
				e.printStackTrace();
			}

		Logger.getLogger("uk.ac.qub.week").addHandler(handler);
		logger = Logger.getLogger("uk.ac.qub.week");
	}
	
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub

		ERR.setupLogger();
		
			logger.log(Level.WARNING, "Outputs error warning.");
			logger.log(Level.INFO, "about some dude");
	
	}*/
	
	public void logData(Urgency urgency, String NHS_number, boolean priority){ 
		
		logger.log(Level.INFO, "U:"+ urgency + "\nNHS:" + NHS_number + "\nP:"+priority); //(Level.FINE, "|"+ urgency + "|" + NHS_number + "|"+priority)
	}
	
	
	public void setUpXML(){
		 try {
			 
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root elements
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("company");
				doc.appendChild(rootElement);
		 
				// staff elements
				Element staff = doc.createElement("Staff");
				rootElement.appendChild(staff);
		 
				// set attribute to staff element
				Attr attr = doc.createAttribute("id");
				attr.setValue("1");
				staff.setAttributeNode(attr);
		 
				// shorten way
				// staff.setAttribute("id", "1");
		 
				// firstname elements
				Element firstname = doc.createElement("firstname");
				firstname.appendChild(doc.createTextNode("yong"));
				staff.appendChild(firstname);
		 
				// lastname elements
				Element lastname = doc.createElement("lastname");
				lastname.appendChild(doc.createTextNode("mook kim"));
				staff.appendChild(lastname);
		 
				// nickname elements
				Element nickname = doc.createElement("nickname");
				nickname.appendChild(doc.createTextNode("mkyong"));
				staff.appendChild(nickname);
		 
				// salary elements
				Element salary = doc.createElement("salary");
				salary.appendChild(doc.createTextNode("100000"));
				staff.appendChild(salary);
		 
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File("C:\\file.xml"));
		 
				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);
		 
				transformer.transform(source, result);
		 
				System.out.println("File saved!");
		 
			  } catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			  } catch (TransformerException tfe) {
				tfe.printStackTrace();
			  }
			}
	}
