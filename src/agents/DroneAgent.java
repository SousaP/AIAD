package agents;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.w3c.dom.*;
import org.w3c.dom.Node;

import agents.Worker.MoveRequest;
import agents.Worker.OfferRequestsServer;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
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

	private class MoveRequest extends TickerBehaviour {
		private static final long serialVersionUID = 1L;

		Local Destiny;
		List<DefaultWeightedEdge> caminho;
		int counter;
		DefaultWeightedEdge next;

		public MoveRequest(Worker w, Local Destiny) {
			super(w, 500);

			this.Destiny = Destiny;
			counter = 0;
			next = null;
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
				System.out.println(cityMap.getEdgeTarget(next).getName());
				System.out.println(cityMap.getEdgeWeight(next));
				counter = (int) (
						Math.sqrt(Math.pow(map.get(position).getI() - Destiny.getI(), 2)
							+ Math.pow((map.get(position).getJ() - Destiny.getJ()), 2)) * 500);
				System.out.println(counter);
				break;
			default:
				System.out.println("Not Zero");
				counter = counter - 500;
				if(counter == 0)
					position = Destiny.getName();
				break;

			}

		}

	}
}
