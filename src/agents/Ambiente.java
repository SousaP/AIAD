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
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Ambiente extends Worker {
	private static final long serialVersionUID = 1L;
	ambientBehaviour b;
	HashMap<Job, List<Double>> bids;

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
		// responde a mensagens das aquisi�oes de trabalho
		private static final long serialVersionUID = 1L;

		public SalesBehaviour(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			ACLMessage msg = blockingReceive();
			
			
			if (msg.getPerformative() == ACLMessage.INFORM) {
				//System.out.println(getLocalName() + ": recebi " + msg.getContent());
				ACLMessage reply = msg.createReply();

				String split[] = msg.getContent().split(";");
				if (split.length < 2)
					return;
				

				String content = "jobs;";
				//System.out.println("split[0]: " + split[0]);
				//System.out.println("split[1]: " + split[1]);
				//System.out.println("Sender: " + msg.getSender());
				
				if (split[0].contains("jobs") && split[1].contains("?"))
					for (int i = 0; i < Jobs_Created.size(); i++)
					if(!(Jobs_Created.get(i).beingDone() || Jobs_Created.get(i).isDone()))
					{
						content = content + Jobs_Created.get(i).toString();
					//	System.out.println("Jobs identificados: " + Jobs_Created.get(i).toString());
					}
 				System.out.println("Jobs identificados: "+content);
				reply.setContent(content);
				// envia mensagem
				send(reply);
			}
			
			else if(msg.getPerformative() == ACLMessage.INFORM) {

				ACLMessage reply = msg.createReply();

				String split[] = msg.getContent().split(";");
				if (split.length < 2)
					return;
				

				String content = "jobs;";
				
				if (split[0].contains("jobs") && split[1].contains("?"))
					for (int i = 0; i < Jobs_Created.size(); i++)
					if(!(Jobs_Created.get(i).beingDone() || Jobs_Created.get(i).isDone()))
					{
						content = content + Jobs_Created.get(i).toString();
					//	System.out.println("Jobs identificados: " + Jobs_Created.get(i).toString());
					}
 				System.out.println("Jobs identificados: "+content);
				reply.setContent(content);
				// envia mensagem
				send(reply);
			}
			else if(msg.getPerformative() == ACLMessage.PROPOSE) {

				//System.out.println("Ambiente: Recebi um Propose" + msg.getContent());


				String split[] = msg.getContent().split(";");
				if (split.length < 2)
					return;
			
				Job job_to_complete = new Job(
							to_do.valueOf(split[0]), 
							type.valueOf(split[1]),
							Double.parseDouble(split[2]), 
							Integer.parseInt(split[3]),
							Double.parseDouble(split[4]), 
							new Product(new Tool(split[5]), 
							split[6], 
							Integer.parseInt(split[7])),
							map.get(split[8]));
				
 				// ver se job faz parte da lista de jobs
				for (int i = 0; i < Jobs_Created.size(); i++)
					if(Jobs_Created.get(i).compare(job_to_complete))
					{

						//System.out.println("AQUIIIII");
						//verificar se ja acabou ou est� a ser realizado -> Responder Failure
						if(Jobs_Created.get(i).beingDone() || Jobs_Created.get(i).isDone())
						{
							ACLMessage cfp = new ACLMessage(ACLMessage.FAILURE);
							cfp.addReceiver(msg.getSender());
							//Responde Failure com job na mensagem
							cfp.setContent(Jobs_Created.get(i).toString());
							cfp.setConversationId("job_proposal");
							cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
							
							//myAgent.send(cfp);
							send(cfp);
						}
						else
						{
							ACLMessage cfp = new ACLMessage(ACLMessage.AGREE);
							cfp.addReceiver(msg.getSender());
							// Responde Agree com Job na mensagem
							cfp.setContent(Jobs_Created.get(i).toString());
							cfp.setConversationId("job_proposal");
							cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
							Jobs_Created.get(i).setbeingDone();
							//myAgent.send(cfp);
							send(cfp);
						}
					}
					//else System.out.println("ALIIIIIIIII");
				
				//String content = "jobs;";
				//reply.setContent(content);
				// envia mensagem
				//send(reply);
			}

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

			return new Job(to_do.getRandom(), type.PRICE, getRandomInt(400, 800), getRandomInt(1, 5),
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
