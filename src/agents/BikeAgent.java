package agents;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class BikeAgent extends Worker {
	private static final long serialVersionUID = 1L;

	private static int LOAD_CAPACITY = 300;

	protected void setup() {
		BATTERY_CAPACITY = 350;
		batteryLeft = BATTERY_CAPACITY;
		loadLeft = LOAD_CAPACITY;
		VELOCITY = 4;
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

	public int getBatLeft() {
		return batteryLeft;
	}

	public int getMaxBat() {
		return BATTERY_CAPACITY;
	}

	public int getLoadLeft() {
		return loadLeft;
	}
}
