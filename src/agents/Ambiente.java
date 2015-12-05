package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.core.*;
import job.Job;
import job.Job.*;
import locals.Local;
import product.Product;
import tools.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

		addBehaviour(new ambientBehaviour(this, 7000));
		addBehaviour(new SalesBehaviour(this));

	}

	protected void takeDown() {
		// retira registo no DF
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	public static int getRandomInt(int Min, int Max) {
		return Min + (int) (Math.random() * ((Max - Min) + 1));
	}

	class SalesBehaviour extends CyclicBehaviour {
		// responde a mensagens das aquisiçoes de trabalho
		private static final long serialVersionUID = 1L;

		public SalesBehaviour(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			ACLMessage msg = blockingReceive();
			if (msg.getPerformative() == ACLMessage.INFORM) {
				System.out.println(getLocalName() + ": recebi " + msg.getContent());
				ACLMessage reply = msg.createReply();

				String split[] = msg.getContent().split(";");
				if (split.length < 2)
					return;
				

				String content = "";
				System.out.println("split[0]: " + split[0]);
				System.out.println("split[1]: " + split[1]);
				System.out.println("Sender: " + msg.getSender());
				
				if (split[0].contains("jobs") && split[1].contains("?"))
					for (int i = 0; i < Jobs_Created.size(); i++)
					if(!(Jobs_Created.get(i).beingDone() || Jobs_Created.get(i).isDone()))
					{
						content = content + Jobs_Created.get(i).toString();
						System.out.println("Jobs identificados: " + Jobs_Created.get(i).toString());
					}

				reply.setContent(content);
				// envia mensagem
				send(reply);
			}

		}

	}

	
	class GetJobBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			// TODO Auto-generated method stub

		}
	}

	class ambientBehaviour extends TickerBehaviour {
		public ambientBehaviour(Agent a, long period) {
			super(a, period);

			for (int i = 0; i < 3; i++)
				Jobs_Created.add(createRandomJob());

		}

		private static final long serialVersionUID = 1L;

		Job createRandomJob() {
			String[] produtos = { "Kappa", "Keppo", "PogChamp", "Leeeroy", "RenoRich", "SecretLadin", "DansGame",
					"BibleThump" };
			String[] tools = { "f1", "f2", "f3" };
			Product p = new Product(new Tool(tools[getRandomInt(0, tools.length - 1)]),
					produtos[getRandomInt(0, produtos.length - 1)], getRandomInt(0, 100));

			Random random = new Random();
			List<String> keys = new ArrayList<String>(map.keySet());
			String randomKey = keys.get(random.nextInt(keys.size()));
			Local local = map.get(randomKey);

			return new Job(to_do.getRandom(), type.getRandom(), getRandomInt(400, 800), getRandomInt(1, 5),
					getRandomInt(50, 300), p, local);

		}

		@Override
		protected void onTick() {

			for (int i = 0; i < Jobs_Created.size(); i++)
				if (!Jobs_Created.get(i).beingDone())
					Jobs_Created.set(i, createRandomJob());

		}

	}
}
