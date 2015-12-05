package agents;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import job.Job;
import jade.core.*;

import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import locals.*;
import tools.Tool;

public class Worker extends Agent {
	private static final long serialVersionUID = 1L;
	List<Local> chargers;
	List<Local> dumps;
	List<Local> hands;
	List<Local> stores;
	List<Local> houses;
	List<Tool> tools;
	protected static int VELOCITY = 0;
	int xmax, ymax;
	ListenableUndirectedWeightedGraph<Local, DefaultWeightedEdge> cityMap = new ListenableUndirectedWeightedGraph<Local, DefaultWeightedEdge>(
			DefaultWeightedEdge.class);
	HashMap<String, Local> map = new HashMap<String, Local>();
	List<Job> Jobs_Created;
	Job myJob;
	double credit;
	String position;
	Boolean Working;
	GetJobBehaviour jobBehav;
	PositionBehaviour positionBehav;
	MoveRequest moveBehav;
	

	String[] splitArguments(Object[] args) {
		String strin_tempo = (String) args[0];
		return strin_tempo.split(";");

	}

	protected void setup() {
		credit = 0;
		Working = false;

		String[] args = {};
		if (getArguments() != null)
			args = splitArguments(getArguments());

		tools = new ArrayList<Tool>();
		if (args != null && args.length > 1) {

			position = args[0];
			tools.add(new Tool(args[1]));

		} else {
			System.out.println("Não especificou o tipo");
		}
		chargers = new ArrayList<Local>();
		dumps = new ArrayList<Local>();
		hands = new ArrayList<Local>();
		stores = new ArrayList<Local>();
		houses = new ArrayList<Local>();
		Jobs_Created = new ArrayList<Job>();
		readMap();
		System.out.println("I read the map ");
		// double len = pathlength(map.get("A"), map.get("L"));
		// System.out.println(len);
		if(getName().contains("ambient"))
			return;
		
		jobBehav = new GetJobBehaviour(this);
		addBehaviour(jobBehav);
		
		if(getName().contains("Drone"))
			return;
		positionBehav = new PositionBehaviour();
		addBehaviour(positionBehav);
		moveBehav = new MoveRequest(this, map.get("L"), pathTo(map.get(position), map.get("L")));
		addBehaviour(moveBehav);
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
					Local local_tempo = new Local(i_temp, j_temp, name);
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
					cityMap.setEdgeWeight(e_temp,
							Math.sqrt(Math.pow((map.get(i_temp).getI() - map.get(j_temp).getI()), 2)
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

	List<DefaultWeightedEdge> pathTo(Local origin, Local destiny) {
		DijkstraShortestPath<Local, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<Local, DefaultWeightedEdge>(
				cityMap, origin, destiny);
		//double length = dijkstra.getPathLength();
		GraphPath<Local, DefaultWeightedEdge> temp = dijkstra.getPath();
		Set<DefaultWeightedEdge> edges = temp.getGraph().edgeSet();
		edges.toArray();
		//Iterator<DefaultWeightedEdge> iter = edges.iterator();

		List<DefaultWeightedEdge> temp1 = dijkstra.getPathEdgeList();
		Iterator<DefaultWeightedEdge> iter1 = temp1.iterator();
		while (iter1.hasNext()) {
			DefaultWeightedEdge edge = iter1.next();

			System.out.println(cityMap.getEdgeTarget(edge).getName());
		}
		return temp1;
	}

	class PositionBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		public PositionBehaviour() {
			super();
		}

		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// CFP Message received. Process it
				//String sintoma = msg.getContent();
				ACLMessage reply = msg.createReply();

				if (msg.getConversationId() == "posicao") {

					reply.setPerformative(ACLMessage.INFORM);
					System.out.println("Posicao " + getLocalName() + " " + position);
					reply.setContent(getLocalName() + ";" + map.get(position).getI() + ";" + map.get(position).getJ());

					send(reply);

				}

			} else {
				block();
			}
		}
	} // End of inner class OfferRequestsServer

	public class MoveRequest extends TickerBehaviour {
		private static final long serialVersionUID = 1L;

		Local Destiny;
		List<DefaultWeightedEdge> caminho;
		int counter;
		DefaultWeightedEdge next;

		public MoveRequest(Worker w, Local Destiny, List<DefaultWeightedEdge> caminho) {
			super(w, (10 - VELOCITY) * 100);

			this.Destiny = Destiny;
			this.caminho = caminho;
			counter = 0;
		}
		
		public void updateMoveResquest(Local Destiny, List<DefaultWeightedEdge> caminho){
			this.Destiny = Destiny;
			this.caminho = caminho;
			counter = 0;
		}

		@Override
		protected void onTick() {
			switch (counter) {
			case 0:
				if (next != null) {
					System.out.println("Antes Paragem");
					position = cityMap.getEdgeTarget(next).getName();
					if (Destiny.getName() == position) {
						System.out.println("Paragem");
						stop();
						break;
					}
				}
				Iterator<DefaultWeightedEdge> iter1 = caminho.iterator();
				next = iter1.next();
				System.out.println(cityMap.getEdgeTarget(next).getName());
				System.out.println(cityMap.getEdgeWeight(next));

				counter = (int) (cityMap.getEdgeWeight(next) * (10 - VELOCITY) * 100);
				System.out.println(counter);
				iter1.remove();
				break;
			default:
				System.out.println("Not Zero");
				counter = counter - (10 - VELOCITY) * 100;
				break;

			}

		}
	}

	public static Job getKeyByValue(HashMap<Job, Double> map, double value) {
		for (Entry<Job, Double> entry : map.entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	List<Job> orderJobs(List<Job> jobs_available) {
		// Precisa de um produto -> Pode precisar de uma ferramenta
		// (reward * 3 - time)/fine
		HashMap<Job, Double> map_l = new HashMap<Job, Double>();

		Iterator<Job> it = jobs_available.iterator();
		while (it.hasNext()) {
			map_l.put(it.next(), it.next().getProbabilityOfChoose(map.get(position)));
		}

		SortedSet<Double> values = new TreeSet<Double>(map_l.values());
		List<Job> sorted_jobs = null;

		Iterator itera = values.iterator();

		while (itera.hasNext()) {
			Iterator temp = itera;
			sorted_jobs.add(getKeyByValue(map_l, values.first()));
			values.remove(temp);
		}

		return sorted_jobs;
	}

	class GetJobBehaviour extends TickerBehaviour {
		private static final long serialVersionUID = 1L;

		public GetJobBehaviour(Agent a) {
			super(a, 2000);
		}

		@Override
		public void onTick() {
			if (!Working) {
				try {
					ACLMessage cfp = new ACLMessage(ACLMessage.INFORM);
					DFAgentDescription template = new DFAgentDescription();
					DFAgentDescription[] result;

					result = DFService.search(myAgent, template);
					// System.out.println("Encontrei estes recursos:");
					AID[] recursos = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						recursos[i] = result[i].getName();
						if (recursos[i].getLocalName().contains("ambient")) {
							cfp.addReceiver(recursos[i]);
							break;
						}
					}

					cfp.setContent("jobs;?");
					System.out.println(getLocalName() + " : " + "jobs;?");
					send(cfp);

				} catch (FIPAException e) {

					e.printStackTrace();
				}
			}
			else
				return;

		}

	}
}
