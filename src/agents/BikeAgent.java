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
public class BikeAgent extends Worker {
	private static final long serialVersionUID = 1L;
	
	private static boolean ROAD = true; //true estrada, false ar
	private static int BATTERY_CAPACITY = 350;
	private static int LOAD_CAPACITY = 300;
	private static Tool f1;
	private static Tool f3;
	
	private int batteryLeft;
	private int loadLeft;
	
	protected void setup(){
		batteryLeft = BATTERY_CAPACITY;
		loadLeft = LOAD_CAPACITY;
		VELOCITY = 4;
		f1 = new Tool("f1");
		f3 = new Tool("f3");
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType("BikeAgent");
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
		/**
		* 
		*/
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
