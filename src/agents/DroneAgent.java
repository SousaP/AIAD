package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import job.Job;
import job.Job.to_do;
import job.Job.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import agents.Worker.MoveRequest;
import jade.core.*;

import locals.*;
import product.Product;
import tools.Tool;

public class DroneAgent extends Worker {
	private static final long serialVersionUID = 1L;
	private static int LOAD_CAPACITY = 100;
	private static Tool f1;
	private float i;
	private float j;

	ReceiveMessageBehaviourDrone positionDroneBehav;
	MoveRequestDrone moveBehav;

	protected void setup() {
		BATTERY_CAPACITY = 250;
		this.batteryLeft = BATTERY_CAPACITY;
		this.loadLeft = LOAD_CAPACITY;
		VELOCITY = 5;
		f1 = new Tool("f1");
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType("DroneAgent");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		super.setup();
		i = map.get(position).getI();
		j = map.get(position).getJ();
		positionDroneBehav = new ReceiveMessageBehaviourDrone();
		addBehaviour(positionDroneBehav);
		moveBehav = new MoveRequestDrone(this, map.get("I"));
		addBehaviour(moveBehav);
	}

	protected void takeDown() {
		// retira registo no DF
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	class myBehaviour extends SimpleBehaviour {
		private static final long serialVersionUID = 1L;

		public myBehaviour(Agent a) {
			super(a);
		}

		public void action() {
			// ...this is where the real programming goes !!
		}

		private boolean finished = false;

		public boolean done() {
			return finished;
		}

	}

	List<Job> orderJobs(List<Job> jobs_available) {
		// Precisa de um produto -> Pode precisar de uma ferramenta
		// (reward * 3 - time)/fine
		Iterator<Job> it = jobs_available.iterator();
		Map<Job, Double> unsortMap = new HashMap<Job, Double>();
		while (it.hasNext()) {
			Job temp = it.next();
			if (temp.able(this))
				unsortMap.put(temp, temp.getProbabilityOfChoose(map.get(position), this));
		}

		List<Job> resultado = new ArrayList<Job>();

		List<Entry<Job, Double>> sortedValues = entriesSortedByValues(unsortMap);
		for (int i = 0; i < sortedValues.size(); i++) {
			System.out.println(sortedValues.get(i).getKey() + "    " + sortedValues.get(i).getValue());
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

	class ReceiveMessageBehaviourDrone extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		public ReceiveMessageBehaviourDrone() {
			super();
		}

		List<Job> jobs_disponiveis;

		public void action() {
			ACLMessage msg = blockingReceive(500);

			if (msg == null)
				return;

			// Perguntar pela posi�ao
			if (msg.getPerformative() == ACLMessage.CFP) {
				// CFP Message received. Process it
				// String sintoma = msg.getContent();
				ACLMessage reply = msg.createReply();

				if (msg.getConversationId() == "posicao") {

					reply.setPerformative(ACLMessage.INFORM);
					if (position == "tripmode") {
						// System.out.println("Posicao: " + (int) i + ";" +
						// (int) j);
						reply.setContent("Drone" + ";" + (int) i + ";" + (int) j);
					} else {
						// System.out.println("Posicao " + getLocalName() + " "
						// + position);
						reply.setContent(
								getLocalName() + ";" + map.get(position).getI() + ";" + map.get(position).getJ());

					}

					send(reply);

				}

			}

			else if (msg.getPerformative() == ACLMessage.INFORM) {
				// Perguntar pela posi�ao Jobs?
				ACLMessage reply = msg.createReply();

				String split[] = msg.getContent().split(";");
				if (split.length < 2)
					return;

				String content = "";
				// System.out.println("split[0]: " + split[0]);
				// System.out.println("split[1]: " + split[1]);
				// System.out.println("Sender: " + msg.getSender());
				// System.out.println(getLocalName() + ": recebi " +
				// msg.getContent());

				if (split[0].contains("jobs")) {
					jobs_disponiveis = new ArrayList<Job>();
					for (int i = 1; i < split.length; i++) {

						jobs_disponiveis.add(new Job(to_do.valueOf(split[i]), type.valueOf(split[++i]),
								Double.parseDouble(split[++i]), Integer.parseInt(split[++i]),
								Double.parseDouble(split[++i]),
								new Product(new Tool(split[++i]), split[++i],Double.parseDouble(split[++i]), Integer.parseInt(split[++i])),
								map.get(split[++i]),map.get(split[++i])

						));

					}

					/*
					 * System.out.println("Trabalhos disponiveis: "); for(int i
					 * = 0; i < jobs_disponiveis.size(); i++)
					 * System.out.println(jobs_disponiveis.get(i).toString());
					 */
				}
			} else {
				block();
			}
		}
	}

	private class MoveRequestDrone extends TickerBehaviour {
		private static final long serialVersionUID = 1L;

		Local Destiny;
		int counter;
		Local start;
		int distance;
		int midpoint;
		DroneAgent w;

		public MoveRequestDrone(DroneAgent w, Local Destiny) {
			super(w, (10 - VELOCITY) * 100);

			start = map.get(w.position);
			this.Destiny = Destiny;
			counter = 0;
			midpoint = 0;
			this.w = w;
			
			if(w.position.equals(Destiny.getName())){
				stop();
			}
		}

		@Override
		protected void onTick() {
			switch (counter) {
			case 0:
				if (Destiny.getName() == position) {
					System.out.println("Paragem");
					w.batteryLeft = w.batteryLeft - distance;
					stop();
					break;
				}
				distance = (int) (Math.sqrt(
						Math.pow(start.getI() - Destiny.getI(), 2) + Math.pow((start.getJ() - Destiny.getJ()), 2)));
				counter = distance * ((10 - VELOCITY) * 100);
				midpoint = (int) distance / 2;
				position = "tripmode";
				// System.out.println(counter);
				break;
			default:
				counter = counter - ((10 - VELOCITY) * 100);
				midpoint--;
				if (midpoint == 0) {
					j = (start.getJ() + Destiny.getJ()) / 2;
					i = (start.getI() + Destiny.getI()) / 2;
				}
				// System.out.println(counter);
				if (counter == 0) {
					position = Destiny.getName();
					i = Destiny.getI();
					j = Destiny.getJ();
					// System.out.println("STOPED THE COUNTER");
				}
				break;

			}

		}

		public int onEnd() {
			/*
			 * System.out.println("TERMINATED MOVE");
			 * removeBehaviour(moveBehav); moveBehav= new MoveRequestDrone(w,
			 * map.get("L")); addBehaviour(moveBehav);
			 */
			return 1;

		}

	}
	
	public int getBatLeft(){
		return batteryLeft;
	}
	
	public int getMaxBat(){
		return BATTERY_CAPACITY;
	}
	
	public int getLoadLeft() {
		return loadLeft;
	}
	
	public void checkForBattery(){
		double temp = 0;
		Local nearest = null;
		Local L = null;
		double temp2 = 0;
		double distance = Double.MAX_VALUE;
		double distance2 = Double.MAX_VALUE;
		for(int i = 0; i<Jobs_Created.size(); i++){
			distance2 = Math.sqrt(
					Math.pow(map.get(Jobs_Created.get(i).local).getI() - map.get(position).getI(), 2) + Math.pow((map.get(Jobs_Created.get(i).local).getJ() - map.get(position).getJ()), 2));
			if(temp2 < distance2){
				temp2 = distance2;
				L = Jobs_Created.get(i).local;
			}
		}
		for(int i = 0; i<chargers.size(); i++){
			distance = Math.sqrt(
					Math.pow(L.getI() - map.get(chargers.get(i)).getI(), 2) + Math.pow(L.getJ() - map.get(chargers.get(i)).getJ(), 2));
			if(temp < distance){
				temp = distance;
				nearest = chargers.get(i);
			}
		}
		if(batteryLeft < (temp + temp2) && (Jobs_Created.size() != 0)){
			moveBehav = new MoveRequestDrone(this, nearest);
			addBehaviour(moveBehav);
			this.batteryLeft = BATTERY_CAPACITY;
		}
	}
}
