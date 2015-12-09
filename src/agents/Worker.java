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
import job.Job;
import job.Job.to_do;
import job.Job.type;
import jade.core.*;

import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import locals.*;
import product.Product;
import tools.Tool;

public class Worker extends Agent {
	private static final long serialVersionUID = 1L;
	protected static int BATTERY_CAPACITY = 0;
	public List<Local> chargers;
	List<Local> dumps;
	List<Local> hands;
	List<Local> stores;
	List<Local> houses;
	List<Tool> tools;
	public int VELOCITY = 0;
	int xmax, ymax;
	public ListenableUndirectedWeightedGraph<Local, DefaultWeightedEdge> cityMap = new ListenableUndirectedWeightedGraph<Local, DefaultWeightedEdge>(
			DefaultWeightedEdge.class);
	public HashMap<String, Local> map = new HashMap<String, Local>();
	List<Job> Jobs_Created;
	Job myJob;
	public double credit = 150;
	public String position;
	Boolean Working;
	GetJobBehaviour jobBehav;
	ReceiveMessageBehaviour positionBehav;
	MoveRequest moveBehav;
	MountBehaviour mountB;
	HashMap<String, Product> current_Products = new HashMap<String, Product>();
	HashMap<String, Product> saved_Products = new HashMap<String, Product>();

	List<Job> jobs_disponiveis;
	public int batteryLeft;
	protected int loadLeft;

	String[] splitArguments(Object[] args) {
		String strin_tempo = (String) args[0];
		return strin_tempo.split(";");

	}

	protected void setup() {
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

		// double len = pathlength(map.get("A"), map.get("L"));
		// System.out.println(len);
		if (getName().contains("ambient"))
			return;

		jobBehav = new GetJobBehaviour(this);
		addBehaviour(jobBehav);

		if (getName().contains("Drone"))
			return;
		positionBehav = new ReceiveMessageBehaviour();
		addBehaviour(positionBehav);
		// moveBehav = new MoveRequest(this, map.get("A"),
		// pathTo(map.get(position), map.get("A")));
		// addBehaviour(moveBehav);

	}

	public List<Tool> getTools() {
		return tools;
	}

	public String getToolsString() {
		String retorno = "";
		for (int i = 0; i < tools.size(); i++)
			retorno += (tools.get(i).getName());
		return retorno;
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
		// double length = dijkstra.getPathLength();
		GraphPath<Local, DefaultWeightedEdge> temp = dijkstra.getPath();
		Set<DefaultWeightedEdge> edges = temp.getGraph().edgeSet();
		edges.toArray();
		// Iterator<DefaultWeightedEdge> iter = edges.iterator();

		List<DefaultWeightedEdge> temp1 = dijkstra.getPathEdgeList();
		Iterator<DefaultWeightedEdge> iter1 = temp1.iterator();
		while (iter1.hasNext()) {
			DefaultWeightedEdge edge = iter1.next();
		}
		return temp1;
	}

	class ReceiveMessageBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		public ReceiveMessageBehaviour() {
			super();
		}

		public void action() {

			ACLMessage msg = blockingReceive(500);

			if (msg == null)
				return;

			// Perguntar pela posiçao

			if (msg.getPerformative() == ACLMessage.CFP) {
				// CFP Message received. Process it
				// String sintoma = msg.getContent();
				ACLMessage reply = msg.createReply();

				if (msg.getConversationId() == "posicao") {

					reply.setPerformative(ACLMessage.INFORM);
					// System.out.println("Posicao " + getLocalName() + " " +
					// position);
					reply.setContent(getLocalName() + ";" + map.get(position).getI() + ";" + map.get(position).getJ());

					send(reply);

				}

			} else if (msg.getPerformative() == ACLMessage.INFORM) {

				// System.out.println(getLocalName() + ": recebi " +
				// msg.getContent());
				// Perguntar pela posiçao Jobs?
				// ACLMessage reply = msg.createReply();

				String split[] = msg.getContent().split(";");
				if (split.length < 2)
					return;

				String content = "";
				// System.out.println("split[0]: " + split[0]);
				// System.out.println("split[1]: " + split[1]);
				// System.out.println("Sender: " + msg.getSender());

				if (split[0].contains("jobs")) {
					jobs_disponiveis = new ArrayList<Job>();
					for (int i = 1; i < split.length; i++) {

						jobs_disponiveis.add(new Job(to_do.valueOf(split[i]), type.valueOf(split[++i]),
								Double.parseDouble(split[++i]), Integer.parseInt(split[++i]),
								Double.parseDouble(split[++i]), new Product(new Tool(split[++i]), split[++i],
										Double.parseDouble(split[++i]), Integer.parseInt(split[++i])),
								map.get(split[++i]), map.get(split[++i])));
						;
					}

					
					jobs_disponiveis = orderJobs(jobs_disponiveis);
					
					
					Working = true;

					// ______________________________________

					// MessageTemplate mt;

					ACLMessage cfp = new ACLMessage(ACLMessage.PROPOSE);
					cfp.addReceiver(msg.getSender());
					// jobs_disponiveis.get(0) -> job preferivel
					if (jobs_disponiveis.size() < 1) {

						
						if (batteryLeft < BATTERY_CAPACITY / 4) {
							System.out.println("PRECISO DE BATERIA");
							checkForBattery();
							return;
						}
						Working = false;
						return;
					}
					cfp.setContent(jobs_disponiveis.get(0).toString());
					cfp.setConversationId("job_proposal");
					cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																			// value

					// System.out.println("Enviei um propose" +
					// cfp.getContent());
					send(cfp);
					// myAgent.send(cfp);
					// mt =
					// MessageTemplate.and(MessageTemplate.MatchConversationId("job_proposal"),
					// MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));

				}

			} else if (msg.getPerformative() == ACLMessage.FAILURE) {
				System.out.println(getLocalName() + "Recebi um FAILURE " + msg.getContent());
				String split[] = msg.getContent().split(";");
				if (split.length < 2)
					return;

				Job job_rejected = new Job(to_do.valueOf(split[0]), type.valueOf(split[1]),
						Double.parseDouble(split[2]), Integer.parseInt(split[3]),
						Double.parseDouble(split[4]), new Product(new Tool(split[5]), split[6],
								Double.parseDouble(split[7]), Integer.parseInt(split[8])),
						map.get(split[9]), map.get(split[10]));

				if (job_rejected.job_Type.BIDS == type.BIDS && jobs_disponiveis.size() > 1) {
					double newReward = job_rejected.MakeBetterOffer((Worker) myAgent, jobs_disponiveis.get(1));
					if (newReward == -1) {
						Working = false;
						return;
					}
					job_rejected.setReward(newReward);

					ACLMessage cfp = new ACLMessage(ACLMessage.PROPOSE);
					cfp.addReceiver(msg.getSender());
					// jobs_disponiveis.get(0) -> job preferivel
					cfp.setContent(job_rejected.toString());
					cfp.setConversationId("job_proposal");
					cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																			// value

					// System.out.println("Enviei um propose" +
					// cfp.getContent());
					send(cfp);

					return;

				}
				// Fazer novo pedido
				Working = false;

			} else if (msg.getPerformative() == ACLMessage.AGREE) {
				System.out.println(getLocalName() + "Recebi um AGREE " + msg.getContent());
				String split[] = msg.getContent().split(";");
				if (split.length < 2)
					return;

				Job job_accepted = new Job(to_do.valueOf(split[0]), type.valueOf(split[1]),
						Double.parseDouble(split[2]), Integer.parseInt(split[3]),
						Double.parseDouble(split[4]), new Product(new Tool(split[5]), split[6],
								Double.parseDouble(split[7]), Integer.parseInt(split[8])),
						map.get(split[9]), map.get(split[10]));

				// TODO poe a trabalhar ja
				myJob = job_accepted;

				if (myJob.the_Job == to_do.TRANSPORT) {

					List<DefaultWeightedEdge> path = pathTo(map.get(position), map.get(myJob.local.getName()));
					path.addAll(pathTo(map.get(myJob.local.getName()), map.get(myJob.local2.getName())));

					moveBehav = new MoveRequest((Worker) myAgent, myJob.local2, path);
					addBehaviour(moveBehav);

				} else if (myJob.the_Job == to_do.ACQUISITION) {

					List<DefaultWeightedEdge> path = pathTo(map.get(position), map.get(myJob.local.getName()));
					path.addAll(pathTo(map.get(myJob.local.getName()), map.get(myJob.local2.getName())));

					moveBehav = new MoveRequest((Worker) myAgent, myJob.local2, path);
					addBehaviour(moveBehav);

				} else if (myJob.the_Job == to_do.MOUNT) {
					String ools[] = msg.getContent().split(",");
					if (ools.length < 2)
						return;

						mountB = new MountBehaviour((Worker) myAgent);
					
				}

				Working = true;
			} else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
				// ESPERA PELO FIM DO LEILAO
				System.out.println(getLocalName() + "Recebi um ACCEPT_PROPOSAL " + msg.getContent());
				String split[] = msg.getContent().split(";");
				if (split.length < 2)
					return;

				Job job_accepted = new Job(to_do.valueOf(split[0]), type.valueOf(split[1]),
						Double.parseDouble(split[2]), Integer.parseInt(split[3]),
						Double.parseDouble(split[4]), new Product(new Tool(split[5]), split[6],
								Double.parseDouble(split[7]), Integer.parseInt(split[8])),
						map.get(split[9]), map.get(split[10]));

				myJob = job_accepted;
				Working = true;
			} else if (msg.getPerformative() == ACLMessage.CONFIRM) {
				System.out.println(getLocalName() + "Recebi um CONFIRM" + msg.getContent());
				// COMEÇA A TRABALHAR
				Working = true;
			} else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
				System.out.println(getLocalName() + "Recebi um ACCEPT_PROPOSAL " + msg.getContent());
				String split[] = msg.getContent().split(";");
				if (split.length < 2)
					return;

				Job job_accepted = new Job(to_do.valueOf(split[0]), type.valueOf(split[1]),
						Double.parseDouble(split[2]), Integer.parseInt(split[3]),
						Double.parseDouble(split[4]), new Product(new Tool(split[5]), split[6],
								Double.parseDouble(split[7]), Integer.parseInt(split[8])),
						map.get(split[9]), map.get(split[10]));

				myJob = job_accepted;

				Working = true;
			}

			else {
				block();
			}
		}
	} // End of inner class OfferRequestsServer

	public class MoveRequest extends TickerBehaviour {
		private static final long serialVersionUID = 1L;

		Local Destiny;
		List<DefaultWeightedEdge> caminho;
		int counter;
		int time_lasted;
		DefaultWeightedEdge next;
		Iterator<DefaultWeightedEdge> iter1;
		Worker w;

		public MoveRequest(Worker w, Local Destiny, List<DefaultWeightedEdge> caminho) {
			super(w, (10 - VELOCITY) * 100);

			this.Destiny = Destiny;
			this.caminho = caminho;
			counter = 0;
			this.w = w;

			iter1 = caminho.iterator();

			/*
			 * int c = 0; Iterator<DefaultWeightedEdge> iter =
			 * this.caminho.iterator(); System.out.println("COMEÇAR O TICK");
			 * while(iter.hasNext()){
			 * 
			 * DefaultWeightedEdge temp = iter.next();
			 * System.out.println(cityMap.getEdgeSource(temp).getName() + "    "
			 * + cityMap.getEdgeTarget(temp).getName()); c +=
			 * cityMap.getEdgeWeight(temp); } System.out.println(position);
			 */

		}

		@Override
		protected void onTick() {

			switch (counter) {
			case 0:
				if (next != null) {
					// System.out.println("Antes Paragem");
					if (position.equals(cityMap.getEdgeTarget(next).getName()))
						position = cityMap.getEdgeSource(next).getName();
					else
						position = cityMap.getEdgeTarget(next).getName();
					if (Destiny.getName() == position && !iter1.hasNext()) {
						System.out.println("Paragem");

						stop();
						break;
					}
				}

				try {

					next = iter1.next();
				} catch (Exception e) {
					stop();
					break;
				}

				// System.out.println(cityMap.getEdgeTarget(next).getName());
				// System.out.println(cityMap.getEdgeWeight(next));

				counter = (int) (cityMap.getEdgeWeight(next) * (10 - VELOCITY) * 100);
				time_lasted = counter;
				// System.out.println(counter);

				// _______________MENSAGEM PARA AMBIETE QUANDO CHEGA A LOCAL
				// PARA CARREGAR__________

				ACLMessage cfp = new ACLMessage(ACLMessage.INFORM);
				// ID do ambiente
				DFAgentDescription template = new DFAgentDescription();
				DFAgentDescription[] result = null;

				try {
					result = DFService.search(myAgent, template);
				} catch (FIPAException e) {
					e.printStackTrace();
				}
				AID[] recursos = new AID[result.length];
				for (int i = 0; i < result.length; ++i) {
					recursos[i] = result[i].getName();
					if (recursos[i].getLocalName().contains("ambient")) {
						cfp.addReceiver(recursos[i]);
						break;
					}
				}

				// -------------
				if (myJob != null) {
					if (position.equals(myJob.local.getName())) {
						if (myJob.the_Job == to_do.TRANSPORT) {

							cfp.setContent("apanhei;" + myJob.toString());
							cfp.setConversationId("pick_up");
							cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																					// value
							System.out.println("Enviei um APANHEI" + cfp.getContent());
							send(cfp);
						}

						else if (myJob.the_Job == to_do.ACQUISITION) {

							credit -= myJob.product.getPrice();

							cfp.setContent("comprei;" + myJob.toString());
							cfp.setConversationId("comprei");
							cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																					// value
							// System.out.println("Enviei um DEPOSITEI" +
							// cfp.getContent());
							send(cfp);
						}else if (myJob.the_Job == to_do.MOUNT) {
							
							if(position.equals(mountB.p1.getName()))
							{
							Job temp = myJob;
							String split[] = temp.product.getName().split(",");
							temp.product.setName(split[1]);
							cfp.setContent("apanhei;" + myJob.toString());
							cfp.setConversationId("apanhei");
							cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																					// value
							// System.out.println("Enviei um DEPOSITEI" +
							// cfp.getContent());
							send(cfp);
							
							} else if(position.equals(mountB.p2.getName()))
							{
							Job temp = myJob;
							String split[] = temp.product.getName().split(",");
							temp.product.setName(split[2]);
							cfp.setContent("apanhei;" + myJob.toString());
							cfp.setConversationId("apanhei");
							cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																					// value
							// System.out.println("Enviei um DEPOSITEI" +
							// cfp.getContent());
							send(cfp);
							
							}
						

						}

					}

				}

				// _____________________________________________________

				break;
			default:
				// System.out.println("Not Zero");
				counter = counter - ((10 - VELOCITY) * 100);
				batteryLeft--;
				break;

			}

		}

		@Override
		public int onEnd() {

			ACLMessage cfp = new ACLMessage(ACLMessage.INFORM);
			// ID do ambiente
			DFAgentDescription template = new DFAgentDescription();
			DFAgentDescription[] result = null;

			try {
				result = DFService.search(myAgent, template);
			} catch (FIPAException e) {
				e.printStackTrace();
			}
			AID[] recursos = new AID[result.length];
			for (int i = 0; i < result.length; ++i) {
				recursos[i] = result[i].getName();
				if (recursos[i].getLocalName().contains("ambient")) {
					cfp.addReceiver(recursos[i]);
					break;
				}
			}
			if (myJob != null) {
				if (myJob.the_Job == to_do.TRANSPORT || myJob.the_Job == to_do.ACQUISITION) {
					credit += myJob.getReward();
					if (time_lasted > myJob.time)
						credit -= myJob.getFine();
					Working = false;

					System.out.println("Battery " + batteryLeft);

					if (position.equals(myJob.local2.getName())) {

						cfp.setContent("depositei;" + myJob.toString());
						cfp.setConversationId("delivery");
						cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																				// value
						// System.out.println("Enviei um DEPOSITEI" +
						// cfp.getContent());
						send(cfp);
					}
				} else if (myJob.the_Job == to_do.ACQUISITION) {

				} 
				else if(myJob.the_Job == to_do.MOUNT)
				{
					mountB.doingStep = false;
					return 0;
				}
				myJob = null;
				for (int i = 0; i < chargers.size(); i++)
					if (position.equals(chargers.get(i).getName())) {
						myAgent.addBehaviour(new ChargeBehaviour(myAgent));
						Working = true;
						return 0;
					}
			}
			return 0;
		}
	}

	public class ChargeBehaviour extends TickerBehaviour {
		private static final long serialVersionUID = 1L;
		int tick = 0;

		public ChargeBehaviour(Agent a) {
			super(a, 1000);
			Working = true;
		}

		@Override
		protected void onTick() {
			if (tick > 2) {
				System.out.println(getLocalName() + " Charged");
				Working = false;
				batteryLeft = BATTERY_CAPACITY;
				stop();
			} else
				tick++;

		}

	}

	public class MountBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;
		int step = 0;
		Boolean doingStep;
		public Local p1, p2;
		ACLMessage cfp;

		public MountBehaviour(Worker a) {
			// super(a, (myJob.time / 2) * 1000);
			super(a);
			Working = true;
			doingStep = false;
		}

		@Override
		public void action() {
			if (doingStep)
				return;
			switch (step) {
			case 0:
				 cfp = new ACLMessage(ACLMessage.INFORM);
				// ID do ambiente
				DFAgentDescription template = new DFAgentDescription();
				DFAgentDescription[] result = null;

				try {
					result = DFService.search(myAgent, template);
				} catch (FIPAException e) {
					e.printStackTrace();
				}
				AID[] recursos = new AID[result.length];
				for (int i = 0; i < result.length; ++i) {
					recursos[i] = result[i].getName();
					if (recursos[i].getLocalName().contains("ambient")) {
						cfp.addReceiver(recursos[i]);
						break;
					}
				}

				cfp.setContent("Produtos," + myJob.product.getName());
				cfp.setConversationId("pick_up");
				cfp.setReplyWith("cfp" + System.currentTimeMillis());
				System.out.println("Enviei Procura Produtos" + cfp.getContent());
				send(cfp);

				ACLMessage msg = blockingReceive();

				if (msg.getPerformative() == ACLMessage.INFORM) {
					String split[] = msg.getContent().split(";");
					if (split[0].equals("Local")) {
						p1 = map.get(split[1]);
						p2 = map.get(split[2]);

						List<DefaultWeightedEdge> path = pathTo(map.get(position), p1);
						path.addAll(pathTo(p1, p2));
						path.addAll(pathTo(p2, hands.get(0)));
						

						moveBehav = new MoveRequest((Worker) myAgent, hands.get(0), path);

					}
				}
				step++;
				doingStep = true;

				break;
			case 1:
				
				String ools[] = myJob.product.getTool().split(",");
				if (!getToolsString().contains(ools[0])) {
					cfp.setContent("criar;MOUNT;PRICE;"+0.20*myJob.getReward()+";7;0;"+ools[0]+";-,-;0;0;"+position+";"+position+";");	
				}else if(!getToolsString().contains(ools[1])) {
					cfp.setContent("criar;MOUNT;PRICE;"+0.20*myJob.getReward()+";7;0;"+ools[1]+";-,-;0;0;"+position+";"+position+";");	
				}
				break;
			default:
				break;
			}

			/*
			 * if (tick == 1) { System.out.println(getLocalName() +
			 * " Producing " + myJob.product.getName()); tick++; } else if (tick
			 * == 2) { Working = false; loadLeft += myJob.product.getSize();
			 * stop(); } else { tick++; } }
			 */
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
		Iterator<Job> it = jobs_available.iterator();
		Map<Job, Double> unsortMap = new HashMap<Job, Double>();
		while (it.hasNext()) {
			Job temp = it.next();
			if (temp.able(this))
				unsortMap.put(temp, temp.getProbabilityOfChoose(this));
		}

		List<Job> resultado = new ArrayList<Job>();

		List<Entry<Job, Double>> sortedValues = entriesSortedByValues(unsortMap);
		for (int i = 0; i < sortedValues.size(); i++) {
			// System.out.println(sortedValues.get(i).getKey() + " " +
			// sortedValues.get(i).getValue());
			resultado.add(sortedValues.get(i).getKey());
		}

		return resultado;
	}

	static <Job, Double extends Comparable<? super Double>> List<Entry<Job, Double>> entriesSortedByValues(
			Map<Job, Double> map) {

		List<Entry<Job, Double>> sortedEntries = new ArrayList<Entry<Job, Double>>(map.entrySet());

		Collections.sort(sortedEntries, new Comparator<Entry<Job, Double>>() {
			@Override
			public int compare(Entry<Job, Double> e1, Entry<Job, Double> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		return sortedEntries;
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
			} else
				return;

		}

	}

	public int getBatLeft() {
		return 0;
	}

	public int getMaxBat() {
		return BATTERY_CAPACITY;
	}

	public int getLoadLeft() {
		return 0;
	}

	public void checkForBattery() {

		Local nearest = null;
		double temp = Double.MAX_VALUE;
		for (int i = 0; i < chargers.size(); i++) {
			DijkstraShortestPath<Local, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<Local, DefaultWeightedEdge>(
					cityMap, map.get(position), map.get(chargers.get(i).getName()));

			if (temp > dijkstra.getPathLength()) {
				temp = dijkstra.getPathLength();
				nearest = map.get(chargers.get(i).getName());
			}
		}
		// if (batteryLeft < (temp + temp2) && (jobs_disponiveis.size() != 0)) {
		moveBehav = new MoveRequest(this, nearest, pathTo(map.get(position), nearest));
		addBehaviour(moveBehav);
		Working = true;
		// this.batteryLeft = BATTERY_CAPACITY;
		// } */
	}
}
