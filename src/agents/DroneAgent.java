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
import java.util.List;

import jade.core.*;

import locals.*;
import product.Product;
import tools.Tool;

public class DroneAgent extends Worker {
	private static final long serialVersionUID = 1L;
	private static int BATTERY_CAPACITY = 250;
	private static int LOAD_CAPACITY = 100;
	private static Tool f1;
	private int batteryLeft;
	private int loadLeft;
	private float i;
	private float j;
	
	ReceiveMessageBehaviourDrone positionDroneBehav;
	MoveRequestDrone moveBehav;

	protected void setup() {
		batteryLeft = BATTERY_CAPACITY;
		loadLeft = LOAD_CAPACITY;
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
		moveBehav= new MoveRequestDrone(this, map.get("L"));
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

	class ReceiveMessageBehaviourDrone extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		public ReceiveMessageBehaviourDrone() {
			super();
		}
		List<Job> jobs_disponiveis;
		public void action() {
			ACLMessage msg = blockingReceive();
			//Perguntar pela posiçao
			if(msg.getPerformative() == ACLMessage.CFP) {
				// CFP Message received. Process it
				//String sintoma = msg.getContent();
				ACLMessage reply = msg.createReply();

				if (msg.getConversationId() == "posicao") {

					reply.setPerformative(ACLMessage.INFORM);
					if(position == "tripmode"){
					System.out.println("Posicao: " + (int) i + ";" + (int) j);
					reply.setContent("Drone" + ";" + (int) i + ";" + (int) j);
					}else{
						System.out.println("Posicao " + getLocalName() + " " + position);
						reply.setContent(getLocalName() + ";" + map.get(position).getI() + ";" + map.get(position).getJ());

					}

					send(reply);

				}

			} 
			
			else if (msg.getPerformative() == ACLMessage.INFORM) {
				//Perguntar pela posiçao Jobs?
				ACLMessage reply = msg.createReply();

				String split[] = msg.getContent().split(";");
				if (split.length < 2)
					return;

				String content = "";
				// System.out.println("split[0]: " + split[0]);
				// System.out.println("split[1]: " + split[1]);
				 System.out.println("Sender: " + msg.getSender());
					System.out.println(getLocalName() + ": recebi " + msg.getContent());

				if (split[0].contains("jobs")) {
					jobs_disponiveis = new ArrayList<Job>();
					for (int i = 1; i < split.length; i++) {

						jobs_disponiveis.add(new Job(to_do.valueOf(split[i]), type.valueOf(split[++i]),
								Double.parseDouble(split[++i]), Integer.parseInt(split[++i]),
								Double.parseDouble(split[++i]), new Product(new Tool(split[++i]), split[++i], Integer.parseInt(split[++i])),
								map.get(split[++i])

						));

					}
					
					System.out.println("Trabalhos disponiveis: ");
					for(int i = 0; i < jobs_disponiveis.size(); i++)
						System.out.println(jobs_disponiveis.get(i).toString());

				}
			}
			else {
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
		float m;
		int midpoint;

		public MoveRequestDrone(Worker w, Local Destiny) {
			super(w, (10 - VELOCITY) * 100);

			start = map.get(w.position);
			this.Destiny = Destiny;
			counter = 0;
			midpoint = 0;
		}

		@Override
		protected void onTick() {
			switch (counter) {
			case 0:
				if (Destiny.getName() == position) {
					System.out.println("Paragem");
					stop();
					break;
				}
				distance = (int) (Math.sqrt(
						Math.pow(start.getI() - Destiny.getI(), 2) + Math.pow((start.getJ() - Destiny.getJ()), 2)));
				counter = distance * ((10 - VELOCITY) * 100);
				midpoint = (int) distance / 2;
				position = "tripmode";
				System.out.println(counter);
				break;
			default:
				System.out.println("Not Zero");
				counter = counter - ((10 - VELOCITY) * 100);
				midpoint--;
				if (midpoint == 0) {
					j = (start.getJ() + Destiny.getJ()) / 2;
					i = (start.getI() + Destiny.getI()) / 2;
				}
					System.out.println(counter);
				if (counter == 0) {
					position = Destiny.getName();
					System.out.println("STOPED THE COUNTER");
				}
				break;

			}

		}

	}
}
