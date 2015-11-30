package Simulation;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import locals.Local;

public class SimulationAgent extends GuiAgent {
	private SimulationFrame window;
	public ListenableUndirectedWeightedGraph<Local, DefaultWeightedEdge> cityMap = new ListenableUndirectedWeightedGraph<Local, DefaultWeightedEdge>(
			DefaultWeightedEdge.class);
	HashMap<String,Local> map = new HashMap<String,Local>();

	public SimulationAgent() {
		super();
	}

	void readMap() {
		
		
		try {
			File inputFile = new File("map.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nodes = doc.getElementsByTagName("points");
			for (int temp = 0; temp < nodes.getLength(); temp++) {
				Node nNode = nodes.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					
					String name = eElement.getAttribute("name");
					int i_temp = Integer.parseInt(eElement.getElementsByTagName("i").item(0).getTextContent());
					int j_temp = Integer.parseInt(eElement.getElementsByTagName("j").item(0).getTextContent());
					Local local_tempo = new Local(i_temp,j_temp,name);
					cityMap.addVertex(local_tempo);
					 map.put(name, local_tempo);
					
					
				}
			}
			
			NodeList edgeList = doc.getElementsByTagName("edge");
			for (int temp = 0; temp < edgeList.getLength(); temp++) {
				Node nNode = edgeList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					
					String i_temp = eElement.getElementsByTagName("p1").item(0).getTextContent();
					String j_temp = eElement.getElementsByTagName("p2").item(0).getTextContent();				
					cityMap.addEdge(map.get(i_temp), map.get(j_temp));
					
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	protected void setup(){
		
		readMap();
		window = new SimulationFrame(this);

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Simulation");
		sd.setName("PlanningAgent");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		addBehaviour(new ReceiveInformBehaviour());
		addBehaviour(new ReceiveRequestBehaviour());
		
		System.out.println("simulationAgent criado");
	}

	public SimulationFrame getWindow() {
		return window;
	}

	protected void onGuiEvent(GuiEvent event) {
		Integer day = (Integer) event.getParameter(0);
		Integer hour = (Integer) event.getParameter(1);
		Integer minute = (Integer) event.getParameter(2);
		String transition = (String) event.getParameter(3);
		//addBehaviour(new SendTimeBehaviour(day, hour, minute, transition));
	}
}
