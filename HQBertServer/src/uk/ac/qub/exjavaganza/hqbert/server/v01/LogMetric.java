package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.File;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogMetric {
	int count;
	String method, message, urgency, NHS_number, priority;
	Date dateTime;
	SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd'T'hh:mm:ss");

	public void getDataFromLog(){

		try {

			String path = System.getProperty("user.dir");
			
			System.out.println(path+File.separator+"OutFile.log");
			
			//path+File.separator+
			
			File fXmlFile = new File("OutFile.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("record");

			
			
			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				NodeList nNode = (NodeList) nList.item(temp);

				if (((Node) nNode).getNodeType() == Node.ELEMENT_NODE) {
					
					System.out.println("\nCurrent Element :" + ((Node) nNode).getNodeName());
					
					
					Element eElement = (Element) nNode;

					dateTime = ft.parse(eElement.getElementsByTagName("date").item(0).getTextContent());
					method = eElement.getElementsByTagName("method").item(0).getTextContent();
					SplitMessage(eElement.getElementsByTagName("message").item(0).getTextContent());

					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error in LogMetric with the elements");
		}
	}


	public void AvgTimeInTreatmentRoom(){

	}

	public void TimeInTreatmentRoom(){

	}


	public void getDateFromDateTime(){

	}

	public void SplitMessage(String message){

		String[] parts = message.split("(U:|\\nNHS:|\\nP:)"); //("|"["u:",""])
		urgency = parts[0];
		NHS_number = parts[1]; 
		priority = parts[2]; 
	}
}
