package agents;

import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.core.*;
import tools.Tool;

public class CarAgent extends Worker {
	private static final long serialVersionUID = 1L;
	private static boolean ROAD = true; // true estrada, false ar
	private static int LOAD_CAPACITY = 550;
	private static Tool f1;
	private static Tool f2;
	

	protected void setup(){
		BATTERY_CAPACITY = 500;
		this.batteryLeft = BATTERY_CAPACITY;
		loadLeft = LOAD_CAPACITY;
		f1 = new Tool("f1");
		f2 = new Tool("f2");
		VELOCITY = 3;
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType("CarAgent");
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
	
	public int getBatLeft(){
		return batteryLeft;
	}
	
	public int getMaxBat(){
		return BATTERY_CAPACITY;
	}
	
	public int getLoadLeft() {
		return loadLeft;
	}


}
