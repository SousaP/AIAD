
import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;


public class Trabalhos extends Agent {
	private static final long serialVersionUID = 1L;
	
	// construtor do behaviour
	class TrabalhosBehavior extends SimpleBehaviour {
		private static final long serialVersionUID = 1L;


		// construtor do behaviour
		public TrabalhosBehavior(Agent a) {
			super(a);
		}

		public void action() {
			/*ACLMessage msg = blockingReceive();
			if (msg.getPerformative() == ACLMessage.INFORM) {
				System.out.println(++n + " " + getLocalName() + ": recebi " + msg.getContent());
				// cria resposta
				ACLMessage reply = msg.createReply();
				// preenche conteúdo da mensagem
				if (msg.getContent().equals("ping"))
					reply.setContent("pong");
				else
					reply.setContent("ping");
				// envia mensagem
				send(reply);
			} */
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
	}

		// método done
		public boolean done() {
			return false;
		//	return n == 10;
		}
		
		protected void setup() {
			String tipo = "";
			// obtém argumentos
			Object[] args = getArguments();
			if (args != null && args.length > 0) {
				tipo = (String) args[0];
			} else {
				System.out.println("Não especificou o tipo");
			}

			// regista agente no DF
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setName(getName());
			sd.setType("Agente " + tipo);
			dfd.addServices(sd);
			try {
				DFService.register(this, dfd);
			} catch (FIPAException e) {
				e.printStackTrace();
			}

			TrabalhosBehavior b = new TrabalhosBehavior(this);
			addBehaviour(b);

			// toma a iniciativa se for agente "pong"
			if (tipo.equals("pong")) {
				// pesquisa DF por agentes "ping"
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd1 = new ServiceDescription();
				sd1.setType("Agente ping");
				template.addServices(sd1);
				try {
					DFAgentDescription[] result = DFService.search(this, template);
					// envia mensagem "pong" inicial a todos os agentes "ping"
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					for (int i = 0; i < result.length; ++i)
						msg.addReceiver(result[i].getName());
					msg.setContent("pong");
					send(msg);
				} catch (FIPAException e) {
					e.printStackTrace();
				}
			}
		}

		protected void takeDown() {
			// retira registo no DF
			try {
				DFService.deregister(this);
			} catch (FIPAException e) {
				e.printStackTrace();
			}
		}

	}


