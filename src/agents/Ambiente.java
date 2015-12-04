package agents;

import org.w3c.dom.*;
import org.w3c.dom.Node;

import jade.core.behaviours.CyclicBehaviour;
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
import java.util.List;

import locals.*;
import tools.Tool;

public class Ambiente extends Worker {
	private static final long serialVersionUID = 1L;
	ambientBehaviour b;
	Boolean waiting;

	protected void setup() {

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType("Ambiente");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		super.setup();

		b = new ambientBehaviour(this);
		addBehaviour(b);

	}

	protected void takeDown() {
		// retira registo no DF
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	class ambientBehaviour extends TickerBehaviour {
		private static final long serialVersionUID = 1L;

		public ambientBehaviour(Agent a) {
			super(a);
		}

		@Override
		protected void onTick() {
			
			
			
			
			
		}

		private boolean finished = false;


	}
}
