package agents;

import org.w3c.dom.*;
import org.w3c.dom.Node;

import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.core.*;

import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import locals.*;


public class Worker extends Agent {
	private static final long serialVersionUID = 1L;
	List<BatteryChargeCenter> chargers;
	List<Dump> dumps;
	List<HandByHand> hands;
	List<Store> stores;
	List<Warehouse> houses;
	int xmax, ymax;

	int position[];
	
	protected void setup() 
    { 
		chargers = new ArrayList<BatteryChargeCenter>();
		dumps = new ArrayList<Dump>();
		hands = new ArrayList<HandByHand>();
		stores = new ArrayList<Store>();
		houses = new ArrayList<Warehouse>();
		
		 System.out.println("Hello World. ");
    readMap();
    System.out.println("I read the map ");
   
        addBehaviour( new myBehaviour( this ) );
    
    }

	void readMap() {
		try {
			File inputFile = new File("map.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			
			
			NodeList dumpList = doc.getElementsByTagName("dump");
			for (int temp = 0; temp < dumpList.getLength(); temp++) {
				Node nNode = dumpList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String x_temp = eElement.getElementsByTagName("x").item(0).getTextContent();
					String y_temp = eElement.getElementsByTagName("y").item(0).getTextContent();
					dumps.add(new Dump(Integer.parseInt(x_temp),Integer.parseInt(y_temp)));

				}
			}

			NodeList chargeList = doc.getElementsByTagName("charge");
			for (int temp = 0; temp < chargeList.getLength(); temp++) {
				Node nNode = chargeList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String x_temp = eElement.getElementsByTagName("x").item(0).getTextContent();
					String y_temp = eElement.getElementsByTagName("y").item(0).getTextContent();
					chargers.add(new BatteryChargeCenter(Integer.parseInt(x_temp),Integer.parseInt(y_temp)));

				}
			}

			NodeList handsList = doc.getElementsByTagName("hand");
			for (int temp = 0; temp < handsList.getLength(); temp++) {
				Node nNode = handsList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String x_temp = eElement.getElementsByTagName("x").item(0).getTextContent();
					String y_temp = eElement.getElementsByTagName("y").item(0).getTextContent();
					hands.add(new HandByHand(Integer.parseInt(x_temp),Integer.parseInt(y_temp)));

				}
			}

			NodeList storesList = doc.getElementsByTagName("store");
			for (int temp = 0; temp < storesList.getLength(); temp++) {
				Node nNode = storesList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String x_temp = eElement.getElementsByTagName("x").item(0).getTextContent();
					String y_temp = eElement.getElementsByTagName("y").item(0).getTextContent();
					stores.add(new Store(Integer.parseInt(x_temp),Integer.parseInt(y_temp)));

				}
			}

			NodeList houseList = doc.getElementsByTagName("wareHouse");
			for (int temp = 0; temp < houseList.getLength(); temp++) {
				Node nNode = houseList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String x_temp = eElement.getElementsByTagName("x").item(0).getTextContent();
					String y_temp = eElement.getElementsByTagName("y").item(0).getTextContent();
					houses.add(new Warehouse(Integer.parseInt(x_temp),Integer.parseInt(y_temp)));

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void takeDown() {
		// retira registo no DF
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
	 class myBehaviour extends SimpleBehaviour 
     {   
         /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public myBehaviour(Agent a) { 
             super(a);  
         }
         
         public void action() 
         {
            //...this is where the real programming goes !!
         }
         
         private boolean finished = false;
         
         public boolean done() {  
             return finished;  
         }
         
     } // ----------- End myBehaviour
}
