package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import agents.Worker.PositionBehaviour;
import jade.core.*;

import locals.*;
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
	
	PositionBehaviourDrone positionDroneBehav;
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
		positionDroneBehav = new PositionBehaviourDrone();
		addBehaviour(positionDroneBehav);
		moveBehav= new MoveRequestDrone(this, map.get("M"));
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

	class PositionBehaviourDrone extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		public PositionBehaviourDrone() {
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
					if(position == "tripmode"){
					System.out.println("Posicao: " + (int) i + ";" + (int) j);
					reply.setContent("Random" + ";" + (int) i + ";" + (int) j);
					}else{
						System.out.println("Posicao " + getLocalName() + " " + position);
						reply.setContent(getLocalName() + ";" + map.get(position).getI() + ";" + map.get(position).getJ());

					}

					send(reply);

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
