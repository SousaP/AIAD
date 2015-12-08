package Simulation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import locals.Local;

public class SimulationAgent extends GuiAgent {
	private static final long serialVersionUID = 1L;
	private SimulationFrame window;
	public List<Local> chargers;
	public List<Local> dumps;
	public List<Local> hands;
	public List<Local> stores;
	public List<Local> houses;

	public List<Local> agentsFinal;
	public List<Local> agentsReceiving;
	public ListenableUndirectedWeightedGraph<Local, DefaultWeightedEdge> cityMap = new ListenableUndirectedWeightedGraph<Local, DefaultWeightedEdge>(
			DefaultWeightedEdge.class);
	HashMap<String, Local> map = new HashMap<String, Local>();

	private class checkAgentsBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;
		private List<AID> agents = new ArrayList<AID>();
		private MessageTemplate mt;
		private int step = 0;
		private int repliesCnt = 0;

		// construtor do behaviour
		public checkAgentsBehaviour(Agent a) {
			super(a);
		}

		public void action() {
			switch (step) {
			case 0:
				repliesCnt = 0;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {

				}
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				//System.out.println(getLocalName() + ": recebi " + cfp.getContent());
				try {
					DFAgentDescription template = new DFAgentDescription();
					DFAgentDescription[] result = DFService.search(myAgent, template);
					// System.out.println("Encontrei estes recursos:");
					AID[] recursos = new AID[result.length];
					agents = new ArrayList<AID>();
					for (int i = 0; i < result.length; ++i) {
						recursos[i] = result[i].getName();
						if (!recursos[i].getLocalName().contains("simulation")
								&& !recursos[i].getLocalName().contains("ambient")) {
							agents.add(recursos[i]);
							cfp.addReceiver(recursos[i]);
						}

					}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}

				cfp.setContent("localizacao");
				cfp.setConversationId("posicao");
				cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																		// value
				myAgent.send(cfp);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("posicao"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));

				//System.out.println("Perguntou pelas posicoes");
				step = 1;
				break;

			case 1:
				ACLMessage reply = receive(mt);
				if (reply != null) {
					// Reply received
					if (reply.getPerformative() == ACLMessage.INFORM) {
						// This is an offer
						String resposta = reply.getContent().toString();
						String[] conjuntoSintomas = resposta.split(";");

						agentsReceiving.add(new Local(Integer.parseInt(conjuntoSintomas[1]),
								Integer.parseInt(conjuntoSintomas[2]), conjuntoSintomas[0]));

						// This is the best offer at present

					}
					repliesCnt++;
					if (repliesCnt == agents.size()) {
						agentsFinal = new ArrayList<Local>(agentsReceiving);
						window.panel.repaint();
						agentsReceiving.clear();
						step = 0;
					}
				} else {
					block();
				}
				break;

			}
		}

	}

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
					cityMap.addEdge(map.get(i_temp), map.get(j_temp));
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

	protected void setup() {

		chargers = new ArrayList<Local>();
		dumps = new ArrayList<Local>();
		hands = new ArrayList<Local>();
		stores = new ArrayList<Local>();
		houses = new ArrayList<Local>();
		agentsFinal = new ArrayList<Local>();
		agentsReceiving = new ArrayList<Local>();
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

		addBehaviour(new checkAgentsBehaviour(this));

	}

	public SimulationFrame getWindow() {
		return window;
	}

	protected void onGuiEvent(GuiEvent event) {
	}

}
