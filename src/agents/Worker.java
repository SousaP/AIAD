package agents;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import job.Job;
import jade.core.*;

import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
<<<<<<< HEAD
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
=======
import java.util.Set;
>>>>>>> origin/master

import locals.*;
import tools.Tool;

public class Worker extends GuiAgent  {
	private static final long serialVersionUID = 1L;
	List<Local> chargers;
	List<Local> dumps;
	List<Local> hands;
	List<Local> stores;
	List<Local> houses;
	List<Tool> tools;
	int xmax, ymax;
	private ListenableUndirectedWeightedGraph<Local, DefaultWeightedEdge> cityMap = new ListenableUndirectedWeightedGraph<Local, DefaultWeightedEdge>(
			DefaultWeightedEdge.class);
	HashMap<String,Local> map = new HashMap<String,Local>();
	List<Job> Jobs_Created;
	Job myJob;	
	double credit;
	String position;

	String[] splitArguments(Object[] args) {
		String strin_tempo = (String) args[0];
		return strin_tempo.split(";");

	}

	protected void setup() {
		credit = 0;

		String[] args = splitArguments(getArguments());

		tools = new ArrayList<Tool>();
		if (args != null && args.length > 0) {

			position = args[0];
			tools.add(new Tool(args[1]));

			System.out.println("Posicao " + position);

		} else {
			System.out.println("Não especificou o tipo");
		}
		chargers = new ArrayList<Local>();
		dumps = new ArrayList<Local>();
		hands = new ArrayList<Local>();
		stores = new ArrayList<Local>();
		houses = new ArrayList<Local>();
		Jobs_Created = new ArrayList<Job>();
		System.out.println("Hello World. ");
		readMap();
		System.out.println("I read the map ");
		double len = pathlength(map.get("A"), map.get("L"));
		System.out.println(len);

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
					DefaultWeightedEdge e_temp = cityMap.addEdge(map.get(i_temp), map.get(j_temp));
					cityMap.setEdgeWeight(e_temp, Math.sqrt(
							Math.pow((map.get(i_temp).getI() - map.get(j_temp).getI()), 2)
							+ Math.pow((map.get(i_temp).getJ() - map.get(j_temp).getJ()), 2)));
				}
			}
			
			NodeList dumpList = doc.getElementsByTagName("dump");
			for (int temp = 0; temp < dumpList.getLength(); temp++) {
				Node nNode = dumpList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					
					String node_temp = eElement.getElementsByTagName("node").item(0).getTextContent();			
					dumps.add(map.get(node_temp));
				}
			}
			NodeList chargeList = doc.getElementsByTagName("charge");
			for (int temp = 0; temp < chargeList.getLength(); temp++) {
				Node nNode = chargeList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					
					String node_temp = eElement.getElementsByTagName("node").item(0).getTextContent();			
					chargers.add(map.get(node_temp));
				}
			}
			NodeList houseList = doc.getElementsByTagName("wareHouse");
			for (int temp = 0; temp < houseList.getLength(); temp++) {
				Node nNode = houseList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					
					String node_temp = eElement.getElementsByTagName("node").item(0).getTextContent();			
					houses.add(map.get(node_temp));
				}
			}
			NodeList storeList = doc.getElementsByTagName("store");
			for (int temp = 0; temp < storeList.getLength(); temp++) {
				Node nNode = storeList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					
					String node_temp = eElement.getElementsByTagName("node").item(0).getTextContent();			
					stores.add(map.get(node_temp));
				}
			}
			NodeList handList = doc.getElementsByTagName("hand");
			for (int temp = 0; temp < handList.getLength(); temp++) {
				Node nNode = handList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					
					String node_temp = eElement.getElementsByTagName("node").item(0).getTextContent();			
					hands.add(map.get(node_temp));
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

	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	double pathlength(Local origin, Local destiny) {
		DijkstraShortestPath<Local, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<Local, DefaultWeightedEdge>(cityMap, origin, destiny);
		double length = dijkstra.getPathLength();
		GraphPath<Local, DefaultWeightedEdge> temp = dijkstra.getPath();
		Set<DefaultWeightedEdge> edges = temp.getGraph().edgeSet();
		edges.toArray();
		Iterator<DefaultWeightedEdge> iter = edges.iterator();

		List<DefaultWeightedEdge> temp1 = dijkstra.getPathEdgeList();
		Iterator<DefaultWeightedEdge> iter1 = temp1.iterator();
		while(iter1.hasNext()){
			DefaultWeightedEdge edge = iter1.next();

			System.out.println(cityMap.getEdgeTarget(edge).getName());
		}
		return length;
	}
	
	public static Job getKeyByValue(HashMap<Job, Double> map, double value) {
	    for (Entry<Job, Double> entry : map.entrySet()) {
	        if (Objects.equals(value, entry.getValue())) {
	            return entry.getKey();
	        }                   
	    }
	    return null;
	}
	
	List<Job> orderJobs (List<Job> jobs_available) {
		//Precisa de um produto -> Pode precisar de uma ferramenta
		//(reward * 3 - time)/fine
		HashMap<Job, Double> map = new HashMap<Job, Double>();
		
		Iterator<Job> it = jobs_available.iterator();
		while(it.hasNext()){
			map.put(it.next(), it.next().getProbabilityOfChoose());
		}
    
        SortedSet<Double> values = new TreeSet<Double>(map.values());
        List<Job> sorted_jobs = null;
        
        Iterator itera = values.iterator();

        while(itera.hasNext()){
        	Iterator temp = itera;
        	sorted_jobs.add(getKeyByValue(map, values.first()));
        	values.remove(temp); 
        }
        
        return sorted_jobs;
	}

}
	

