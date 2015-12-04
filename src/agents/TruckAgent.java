package agents;

import org.w3c.dom.*;
import org.w3c.dom.Node;

import jade.core.behaviours.SimpleBehaviour;
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
public class TruckAgent extends Worker {
	private static final long serialVersionUID = 1L;
	private static boolean ROAD = true; //true estrada, false ar
	private static int BATTERY_CAPACITY = 3000;
	private static int LOAD_CAPACITY = 1000;
	private static Tool f2;
	private static Tool f3;
	private int batteryLeft;
	private int loadLeft;
	
	protected void setup(){
		VELOCITY = 1;
		f2 = new Tool("f2");
		f3 = new Tool("f3");
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType("TuckAgent");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		super.setup();
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

}
