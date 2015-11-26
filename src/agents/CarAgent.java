package agents;

import org.w3c.dom.*;
import org.w3c.dom.Node;

import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.core.*;

import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import locals.*;
import tools.Tool;

public class CarAgent extends Worker {

	private static int VELOCITY = 3;
	private static boolean ROAD = true; // true estrada, false ar
	private static int BATTERY_CAPACITY = 500;
	private static int LOAD_CAPACITY = 550;
	private static Tool f1;
	private static Tool f2;

	private int batteryLeft;
	private int loadLeft;
	

	protected void setup(){
		f1 = new Tool("f1");
		f2 = new Tool("f2");
		
	
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
