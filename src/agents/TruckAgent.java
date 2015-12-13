package agents;


import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.core.*;
import tools.Tool;
public class TruckAgent extends Worker {
	private static final long serialVersionUID = 1L;
	private static int LOAD_CAPACITY = 1000;

	
	protected void setup(){
		BATTERY_CAPACITY = 3000;
		this.batteryLeft = BATTERY_CAPACITY;
		this.loadLeft = LOAD_CAPACITY;
		VELOCITY = 1;
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
