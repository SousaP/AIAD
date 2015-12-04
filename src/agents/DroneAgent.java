package agents;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.w3c.dom.*;
import org.w3c.dom.Node;

import agents.Worker.MoveRequest;
import agents.Worker.OfferRequestsServer;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.*;

import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

	protected void setup() {
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
		addBehaviour(new OfferRequestsServer());
		addBehaviour(new MoveRequest(this, map.get("L")));
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

	class OfferRequestsServer extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		public OfferRequestsServer() {
			super();
		}

		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// CFP Message received. Process it
				String sintoma = msg.getContent();
				ACLMessage reply = msg.createReply();

				if (msg.getConversationId() == "posicao") {

					reply.setPerformative(ACLMessage.INFORM);
					System.out.println("Posicao: ");
					reply.setContent("Random" + ";" + (int)i + ";" + (int)j);

					send(reply);

				}

			} else {
				block();
			}
		}
	}
	private class MoveRequest extends TickerBehaviour {
		private static final long serialVersionUID = 1L;

		Local Destiny;
		int counter;
		int distance;
		int step;
		float m;

		public MoveRequest(Worker w, Local Destiny) {
			super(w, (10-VELOCITY) * 100);

			this.Destiny = Destiny;
			counter = 0;
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
				distance = (int) (
						Math.sqrt(Math.pow(map.get(position).getI() - Destiny.getI(), 2)
								+ Math.pow((map.get(position).getJ() - Destiny.getJ()), 2)));
				counter = distance  * (10-VELOCITY) * 100;
				step = distance;
				m = (Destiny.getI() - map.get(position).getI())/(Destiny.getJ() - map.get(position).getJ());
				System.out.println(counter);
				break;
			default:
				System.out.println("Not Zero");
				counter = counter - (10-VELOCITY) * 100;
				i++;
				j = j + m;
				if(counter == 0)
					position = Destiny.getName();
				break;

			}

		}

	}
}
