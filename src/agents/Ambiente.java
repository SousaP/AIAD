package agents;

import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.core.*;
import job.Job.*;


public class Ambiente extends Worker {
	private static final long serialVersionUID = 1L;
	ambientBehaviour b;

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

		b = new ambientBehaviour(this,1000);
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
		public ambientBehaviour(Agent a, long period) {
			super(a, period);
		}

		private static final long serialVersionUID = 1L;
		
		
		void createRandomJob(){
			to_do temp;
		}
		
		@Override
		protected void onTick() {
			
			
			
			
			
		}


	}
}
