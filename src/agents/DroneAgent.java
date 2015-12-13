package agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import job.Job;
import job.Job.to_do;
import job.Job.type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import locals.*;
import product.Product;
import tools.Tool;

public class DroneAgent extends Worker {
	private static final long serialVersionUID = 1L;
	private static int LOAD_CAPACITY = 100;
	private float i;
	private float j;

	ReceiveMessageBehaviourDrone positionDroneBehav;
	MoveRequestDrone moveBehav;
	MountBehaviourDrone MountDrone;

	protected void setup() {
		BATTERY_CAPACITY = 250;
		this.batteryLeft = BATTERY_CAPACITY;
		this.loadLeft = LOAD_CAPACITY;
		VELOCITY = 5;
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType("DroneAgent");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		super.setup();
		i = map.get(position).getI();
		j = map.get(position).getJ();
		positionDroneBehav = new ReceiveMessageBehaviourDrone(this);
		addBehaviour(positionDroneBehav);
		// moveBehav = new MoveRequestDrone(this, map.get("I"));
		// addBehaviour(moveBehav);
	}

	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	@Override
	List<Job> orderJobs(List<Job> jobs_available) {
		// Precisa de um produto -> Pode precisar de uma ferramenta
		// (reward * 3 - time)/fine
		Iterator<Job> it = jobs_available.iterator();
		Map<Job, Double> unsortMap = new HashMap<Job, Double>();
		while (it.hasNext()) {
			Job temp = it.next();
			if (temp.able(this))
				unsortMap.put(temp, temp.getProbabilityOfChoose(this));
		//	System.out.println(temp.toString());
		//	System.out.println(temp.getProbabilityOfChoose(this));
		}

		List<Job> resultado = new ArrayList<Job>();

		List<Entry<Job, Double>> sortedValues = entriesSortedByValues(unsortMap);
		for (int i = 0; i < sortedValues.size(); i++) {
			//System.out.println(sortedValues.get(i).getKey() + "    " + sortedValues.get(i).getValue());
			resultado.add(sortedValues.get(i).getKey());
		}

		return resultado;
	}

	@SuppressWarnings("hiding")
	static <Job, Double extends Comparable<? super Double>> List<Entry<Job, Double>> entriesSortedByValues(
			Map<Job, Double> map) {

		List<Entry<Job, Double>> sortedEntries = new ArrayList<Entry<Job, Double>>(map.entrySet());

		Collections.sort(sortedEntries, new Comparator<Entry<Job, Double>>() {
			@Override
			public int compare(Entry<Job, Double> e1, Entry<Job, Double> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		return sortedEntries;
	}

	class ReceiveMessageBehaviourDrone extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;
		int nrPosicoes = 0;

		public ReceiveMessageBehaviourDrone(DroneAgent w) {
			super(w);
		}

		public void action() {

			ACLMessage msg = blockingReceive(500);

			if (msg == null)
				return;

			// Perguntar pela posiçao

			if (msg.getPerformative() == ACLMessage.CFP) {
				// CFP Message received. Process it
				// String sintoma = msg.getContent();
				ACLMessage reply = msg.createReply();

				if (msg.getConversationId() == "posicao") {

					reply.setPerformative(ACLMessage.INFORM);
					if (position == "tripmode") {
						// System.out.println("Posicao: " + (int) i + ";" +
						// (int) j);
						reply.setContent("Drone" + ";" + (int) i + ";" + (int) j);
					} else {
						// System.out.println("Posicao " + getLocalName() + " "
						// + position);
						reply.setContent(
								getLocalName() + ";" + map.get(position).getI() + ";" + map.get(position).getJ());
						if (nrPosicoes == 10) {
							System.out.println(getLocalName() + " Saldo: " + credit);
							nrPosicoes = 0;
						}

					}

					send(reply);

				}

			} else if (msg.getPerformative() == ACLMessage.INFORM) {

				System.out.println(getLocalName() + ": recebi " + msg.getContent());
				// Perguntar pela posiçao Jobs?
				// ACLMessage reply = msg.createReply();

				String split[] = msg.getContent().split(";");
				if (split.length < 2)
					return;

				// String content = "";
				// System.out.println("split[0]: " + split[0]);
				// System.out.println("split[1]: " + split[1]);
				// System.out.println("Sender: " + msg.getSender());

				if (split[0].contains("jobs")) {
					jobs_disponiveis = new ArrayList<Job>();

					for (int i = 1; i < split.length; i++) {
						Job new_job = new Job(to_do.valueOf(split[i]), type.valueOf(split[++i]),
								Double.parseDouble(split[++i]), Integer.parseInt(split[++i]),
								Double.parseDouble(split[++i]), new Product(new Tool(split[++i]), split[++i],
										Double.parseDouble(split[++i]), Integer.parseInt(split[++i])),
								map.get(split[++i]), map.get(split[++i]));

						if (new_job.product.getName().contains("-"))
							new_job.criador = split[++i];
						jobs_disponiveis.add(new_job);

					}

					jobs_disponiveis = orderJobs(jobs_disponiveis);

					System.out.println("Jobs disponiveis :" + jobs_disponiveis.size());

					Working = true;

					// ______________________________________

					// MessageTemplate mt;

					ACLMessage cfp = new ACLMessage(ACLMessage.PROPOSE);
					cfp.addReceiver(msg.getSender());
					// jobs_disponiveis.get(0) -> job preferivel
					if (jobs_disponiveis.size() < 1) {
					//	System.out.println("AQUI");
						if (batteryLeft < BATTERY_CAPACITY / 4) {
							System.out.println("PRECISO DE BATERIA");
							checkForBattery();
							return;
						}
						Working = false;
						return;
					}
					cfp.setContent(jobs_disponiveis.get(0).toString());
					cfp.setConversationId("job_proposal");
					cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																			// value

					// System.out.println("Enviei um propose" +
					// cfp.getContent());
					send(cfp);
					// myAgent.send(cfp);
					// mt =
					// MessageTemplate.and(MessageTemplate.MatchConversationId("job_proposal"),
					// MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));

				}

				if (split[0].equals("Going")) {
					MountDrone.Empregado = split[1];
					MountDrone.step++;

				} else if (split[0].equals("Here")) {

					MountDrone.step++;

				}

			} else if (msg.getPerformative() == ACLMessage.FAILURE) {
				System.out.println(getLocalName() + "Recebi um FAILURE " + msg.getContent());
				String split[] = msg.getContent().split(";");
				if (split.length < 2)
					return;

				Job job_rejected = new Job(to_do.valueOf(split[0]), type.valueOf(split[1]),
						Double.parseDouble(split[2]), Integer.parseInt(split[3]),
						Double.parseDouble(split[4]), new Product(new Tool(split[5]), split[6],
								Double.parseDouble(split[7]), Integer.parseInt(split[8])),
						map.get(split[9]), map.get(split[10]));

				if (job_rejected.job_Type == type.BIDS && jobs_disponiveis.size() > 1) {
					double newReward = job_rejected.MakeBetterOffer((Worker) myAgent, jobs_disponiveis.get(1));
					if (newReward == -1) {
						Working = false;
						return;
					}
					job_rejected.setReward(newReward);

					ACLMessage cfp = new ACLMessage(ACLMessage.PROPOSE);
					cfp.addReceiver(msg.getSender());
					// jobs_disponiveis.get(0) -> job preferivel
					cfp.setContent(job_rejected.toString());
					cfp.setConversationId("job_proposal");
					cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																			// value

					// System.out.println("Enviei um propose" +
					// cfp.getContent());
					send(cfp);

					return;

				}
				// Fazer novo pedido
				Working = false;

			} else if (msg.getPerformative() == ACLMessage.AGREE) {
				System.out.println(getLocalName() + "Recebi um AGREE " + msg.getContent());
				String split[] = msg.getContent().split(";");
				if (split.length < 2)
					return;

				Job job_accepted = new Job(to_do.valueOf(split[0]), type.valueOf(split[1]),
						Double.parseDouble(split[2]), Integer.parseInt(split[3]),
						Double.parseDouble(split[4]), new Product(new Tool(split[5]), split[6],
								Double.parseDouble(split[7]), Integer.parseInt(split[8])),
						map.get(split[9]), map.get(split[10]));

				myJob = job_accepted;

				if (myJob.the_Job == to_do.TRANSPORT) {

					moveBehav = new MoveRequestDrone((DroneAgent) myAgent, myJob.local, myJob.local2);
					addBehaviour(moveBehav);

				} else if (myJob.the_Job == to_do.ACQUISITION) {

					moveBehav = new MoveRequestDrone((DroneAgent) myAgent, myJob.local, myJob.local2);

					addBehaviour(moveBehav);

				} else if (myJob.the_Job == to_do.MOUNT) {

					if (job_accepted.product.getName().contains("-"))
						myJob.criador = split[11];

					MountDrone = new MountBehaviourDrone((Worker) myAgent);
					myAgent.addBehaviour(MountDrone);

					// System.out.println("AGRREEEEEEEEEEEEEEEEEEEEEEEE");

				}

				Working = true;
			} else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
				// ESPERA PELO FIM DO LEILAO
				System.out.println(getLocalName() + "Recebi um ACCEPT_PROPOSAL " + msg.getContent());
				String split[] = msg.getContent().split(";");
				if (split.length < 2)
					return;

				Job job_accepted = new Job(to_do.valueOf(split[0]), type.valueOf(split[1]),
						Double.parseDouble(split[2]), Integer.parseInt(split[3]),
						Double.parseDouble(split[4]), new Product(new Tool(split[5]), split[6],
								Double.parseDouble(split[7]), Integer.parseInt(split[8])),
						map.get(split[9]), map.get(split[10]));

				myJob = job_accepted;
				Working = true;
			} else if (msg.getPerformative() == ACLMessage.CONFIRM) {
				System.out.println(getLocalName() + "Recebi um CONFIRM" + msg.getContent());
				// COMEÇA A TRABALHAR
				Working = true;
			} else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
				System.out.println(getLocalName() + "Recebi um ACCEPT_PROPOSAL " + msg.getContent());
				String split[] = msg.getContent().split(";");
				if (split.length < 2)
					return;

				Job job_accepted = new Job(to_do.valueOf(split[0]), type.valueOf(split[1]),
						Double.parseDouble(split[2]), Integer.parseInt(split[3]),
						Double.parseDouble(split[4]), new Product(new Tool(split[5]), split[6],
								Double.parseDouble(split[7]), Integer.parseInt(split[8])),
						map.get(split[9]), map.get(split[10]));

				myJob = job_accepted;

				Working = true;
			}

			else {
				block();
			}
		}
	} // End of inner class OfferRequestsServer

	private class MoveRequestDrone extends TickerBehaviour {
		private static final long serialVersionUID = 1L;

		Local Destiny;
		Local Destiny2;
		int counter;
		int time_lasted;
		Local start;
		int distance;
		int midpoint;
		DroneAgent w;

		public MoveRequestDrone(DroneAgent w, Local Destiny, Local Destiny2) {
			super(w, (10 - VELOCITY) * 100);

			start = map.get(w.position);
			this.Destiny = Destiny;
			counter = 0;
			midpoint = 0;
			this.Destiny2 = Destiny2;
			this.w = w;

			if (w.position.equals(Destiny.getName())) {
				stop();
			}
		}

		@Override
		protected void onTick() {
			switch (counter) {
			case 0:
				if (Destiny.getName() == position) {
					System.out.println("Paragem");
					w.batteryLeft = w.batteryLeft - distance;
					stop();
					break;
				}
				distance = (int) (Math.sqrt(
						Math.pow(start.getI() - Destiny.getI(), 2) + Math.pow((start.getJ() - Destiny.getJ()), 2)));
				counter = distance * ((10 - VELOCITY) * 100);
				time_lasted = counter;
				midpoint = (int) distance / 2;
				position = "tripmode";
				// System.out.println(counter);

				// System.out.println(counter);

				break;
			default:
				counter = counter - ((10 - VELOCITY) * 100);
				midpoint--;
				if (midpoint == 0) {
					j = (start.getJ() + Destiny.getJ()) / 2;
					i = (start.getI() + Destiny.getI()) / 2;
				}
				// System.out.println(counter);
				if (counter == 0) {
					position = Destiny.getName();
					i = Destiny.getI();
					j = Destiny.getJ();
					// System.out.println("STOPED THE COUNTER");
				}

				// _______________MENSAGEM PARA AMBIETE QUANDO CHEGA A LOCAL
				// PARA CARREGAR__________

				ACLMessage cfp = new ACLMessage(ACLMessage.INFORM);
				// ID do ambiente
				DFAgentDescription template = new DFAgentDescription();
				DFAgentDescription[] result = null;

				try {
					result = DFService.search(myAgent, template);
				} catch (FIPAException e) {
					e.printStackTrace();
				}
				AID[] recursos = new AID[result.length];
				for (int i = 0; i < result.length; ++i) {
					recursos[i] = result[i].getName();
					if (recursos[i].getLocalName().contains("ambient")) {
						cfp.addReceiver(recursos[i]);
						break;
					}
				}

				// -------------
				if (myJob != null) {
					if (position.equals(myJob.local.getName())) {
						if (myJob.the_Job == to_do.TRANSPORT) {

							cfp.setContent("apanhei;" + myJob.toString());
							cfp.setConversationId("pick_up");
							cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																					// value
							System.out.println("Enviei um APANHEI" + cfp.getContent());
							send(cfp);
						}

						else if (myJob.the_Job == to_do.ACQUISITION) {

							credit -= myJob.product.getPrice();

							cfp.setContent("comprei;" + myJob.toString());
							cfp.setConversationId("comprei");
							cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																					// value
							// System.out.println("Enviei um DEPOSITEI" +
							// cfp.getContent());
							send(cfp);
						}

					}

					if (MountDrone != null) {
						if (myJob.the_Job == to_do.MOUNT && !MountDrone.helper && MountDrone.step == 1) {

							if (position.equals(MountDrone.p1.getName())) {
								Job temp = myJob;
								String oldP = temp.product.getName();
								String split[] = oldP.split(",");
								temp.product.setName(split[1]);
								cfp.setContent("apanhei;" + myJob.toString());
								temp.product.setName(oldP);
								cfp.setConversationId("apanhei");
								cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique

								send(cfp);
								 System.out.println("APANHEI P1");

							}
							if (position.equals(MountDrone.p2.getName())) {
								Job temp = myJob;
								String oldP = temp.product.getName();
								String split[] = oldP.split(",");
								temp.product.setName(split[2]);
								cfp.setContent("apanhei;" + myJob.toString());
								temp.product.setName(oldP);
								cfp.setConversationId("apanhei");
								cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique

								send(cfp);

								 System.out.println("APANHEI P2");
								Destiny = MountDrone.p2;
								Destiny2 = hands.get(0);

							}

						}
					}
				}

				break;

			}

		}

		public int onEnd() {

			if (!Destiny.getName().equals(Destiny2.getName())) {
				moveBehav = new MoveRequestDrone((DroneAgent) myAgent, Destiny2, Destiny2);
				addBehaviour(moveBehav);
				return 0;
			}
			ACLMessage cfp = new ACLMessage(ACLMessage.INFORM);
			// ID do ambiente
			DFAgentDescription template = new DFAgentDescription();
			DFAgentDescription[] result = null;

			try {
				result = DFService.search(myAgent, template);
			} catch (FIPAException e) {
				e.printStackTrace();
			}

			AID tempAmbient = null;
			AID[] recursos = new AID[result.length];
			for (int i = 0; i < result.length; ++i) {
				recursos[i] = result[i].getName();
				if (recursos[i].getLocalName().contains("ambient")) {
					tempAmbient = recursos[i];
					cfp.addReceiver(recursos[i]);
					break;
				}
			}
			if (myJob != null) {
				if (myJob.the_Job == to_do.TRANSPORT || myJob.the_Job == to_do.ACQUISITION) {
					credit += myJob.getReward();
					if (time_lasted > myJob.time)
						credit -= myJob.getFine();
					Working = false;

					System.out.println("Battery " + batteryLeft);

					if (position.equals(myJob.local2.getName())) {

						cfp.setContent("depositei;" + myJob.toString());
						cfp.setConversationId("delivery");
						cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																				// value
						// System.out.println("Enviei um DEPOSITEI" +
						// cfp.getContent());
						send(cfp);
					}
				} else if (myJob.the_Job == to_do.ACQUISITION) {

				} else if (myJob.the_Job == to_do.MOUNT && MountDrone.step == 2) {
					System.out.println("Dumping like a boy");

					myAgent.removeBehaviour(MountDrone);
					Working = false;
					return 0;

				}

				else if (myJob.the_Job == to_do.MOUNT && !MountDrone.helper) {

					if (position.equals(myJob.local.getName())) {
						cfp.setContent("depositei;" + myJob.toString());
						cfp.setConversationId("delivery");
						cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																				// value
						// System.out.println("Enviei um DEPOSITEI" +
						// cfp.getContent());
						send(cfp);

						MountDrone.doingStep = false;
						return 0;

					}

					MountDrone.doingStep = false;
					return 0;
				} else if (myJob.the_Job == to_do.MOUNT) {
					MountDrone.doingStep = false;

					if (position.equals(myJob.local.getName())) {
						cfp.setContent("Here;" + myJob.toString());
						cfp.setConversationId("here");
						cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
						cfp.removeReceiver(tempAmbient);
						cfp.addReceiver(MountDrone.Patrao);
						// value
						// System.out.println("Enviei um DEPOSITEI" +
						// cfp.getContent());
						send(cfp);
						return 0;

					}

				}
				myJob = null;
				for (int i = 0; i < chargers.size(); i++)
					if (position.equals(chargers.get(i).getName())) {
						myAgent.addBehaviour(new ChargeBehaviour(myAgent));
						Working = true;
						return 0;
					}
			}
			return 0;
		}

	}

	public class MountBehaviourDrone extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;
		int step;
		Boolean doingStep;
		public Local p1, p2;
		ACLMessage cfp;
		AID Patrao;
		String Empregado;
		Boolean helper = false;
		int tick_espera = 0;

		public MountBehaviourDrone(Worker a) {
			// super(a, (myJob.time / 2) * 1000);
			super(a);
			Working = true;
			doingStep = false;
			step = 0;
			if (myJob.product.getName().contains("-")) {
				step = 8;
				helper = true;
				DFAgentDescription template = new DFAgentDescription();
				DFAgentDescription[] result = null;

				try {
					result = DFService.search(myAgent, template);
				} catch (FIPAException e) {
					e.printStackTrace();
				}
				AID[] recursos = new AID[result.length];
				for (int i = 0; i < result.length; ++i) {
					recursos[i] = result[i].getName();
					if (recursos[i].getLocalName().contains(myJob.criador)) {
						Patrao = recursos[i];
						break;
					}
				}

			}
		}

		@Override
		public void action() {
			if (doingStep)
				return;
			switch (step) {
			case 0:
				// System.out.println("STEP 0");
				cfp = new ACLMessage(ACLMessage.INFORM);
				// ID do ambiente
				DFAgentDescription template = new DFAgentDescription();
				DFAgentDescription[] result = null;

				try {
					result = DFService.search(myAgent, template);
				} catch (FIPAException e) {
					e.printStackTrace();
				}
				AID[] recursos = new AID[result.length];
				for (int i = 0; i < result.length; ++i) {
					recursos[i] = result[i].getName();
					if (recursos[i].getLocalName().contains("ambient")) {
						cfp.addReceiver(recursos[i]);
						break;
					}
				}

				cfp.setContent("Produtos;" + myJob.product.getName());
				cfp.setConversationId("pick_up");
				cfp.setReplyWith("cfp" + System.currentTimeMillis());
				System.out.println("Enviei Procura Produtos" + cfp.getContent());
				send(cfp);

				ACLMessage msg = blockingReceive();

				if (msg.getPerformative() == ACLMessage.INFORM) {
					String split[] = msg.getContent().split(";");
					if (split[0].equals("Local")) {
						p1 = map.get(split[1]);
						p2 = map.get(split[2]);
						double distance1 = Math.sqrt(Math.pow(map.get(position).getJ() - p1.getJ(), 2)
								+ Math.pow(map.get(position).getI() - p1.getI(), 2));
						double distance2 = Math.sqrt(Math.pow(map.get(position).getJ() - p2.getJ(), 2)
								+ Math.pow(map.get(position).getI() - p2.getI(), 2));

						if (distance1 < distance2)
							moveBehav = new MoveRequestDrone((DroneAgent) myAgent, p1, p2);
						else
							moveBehav = new MoveRequestDrone((DroneAgent) myAgent, p2, p1);
						myAgent.addBehaviour(moveBehav);

					}
				}
				step++;
				doingStep = true;

				break;
			case 1:

				// System.out.println("STEP 1");
				String ools[] = myJob.product.getTool().split(",");
				if (!getToolsString().contains(ools[0])) {
					cfp.setContent("criar;MOUNT;PRICE;" + 0.20 * myJob.getReward() + ";7;0;" + ools[0] + ";-,-;0;0;"
							+ position + ";" + position + ";" + getLocalName());
				} else if (!getToolsString().contains(ools[1])) {
					cfp.setContent("criar;MOUNT;PRICE;" + 0.20 * myJob.getReward() + ";7;0;" + ools[1] + ";-,-;0;0;"
							+ position + ";" + position + ";" + getLocalName());
				} else {
					step = 4;
					break;
				}
				send(cfp);
				step++;
				break;
			case 2:
				// System.out.println("STEP 2");
				// System.out.println(tick_espera);
				tick_espera++;

				if (tick_espera > 200) {
					try {

						doingStep = true;

						moveBehav = new MoveRequestDrone((DroneAgent) myAgent, dumps.get(0), dumps.get(0));
						myAgent.addBehaviour(moveBehav);

					} catch (Throwable e) {
						e.printStackTrace();
					}

				}

				break;
			case 3:

				// System.out.println("STEP 3");
				break;
			case 4:

				// System.out.println("STEP 4");
				try {
					Thread.sleep(7000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				step++;
				break;
			case 5:
				// System.out.println("STEP 5");
				moveBehav = new MoveRequestDrone((DroneAgent) myAgent, map.get(myJob.local.getName()),
						map.get(myJob.local.getName()));
				myAgent.addBehaviour(moveBehav);
				step++;
				doingStep = true;
				break;
			case 6:
				// System.out.println("STEP 6");
				try {
					credit += myJob.getReward() * 0.75;

					Working = false;
					step++;
					finalize();
					myAgent.removeBehaviour(this);
				} catch (Throwable e) {
					e.printStackTrace();
				}
				break;
			case 8:

				cfp = new ACLMessage(ACLMessage.INFORM);
				// ID do ambiente

				cfp.addReceiver(Patrao);

				cfp.setContent("Going;" + getLocalName());
				cfp.setConversationId("help");
				cfp.setReplyWith("cfp" + System.currentTimeMillis());
				System.out.println("Enviei Procura Produtos" + cfp.getContent());
				send(cfp);

				// System.out.println("STEP 8");
				moveBehav = new MoveRequestDrone((DroneAgent) myAgent, map.get(myJob.local.getName()),
						map.get(myJob.local.getName()));
				System.out.println("MY POSITION" + position);

				myAgent.addBehaviour(moveBehav);
				doingStep = true;
				step++;
				break;
			case 9:

				// System.out.println("STEP 9");
				try {
					Thread.sleep(7000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				step++;
				break;
			case 10:
				// System.out.println("STEP 10");
				try {

					credit += myJob.getReward();

					Working = false;
					step++;
					finalize();
					myAgent.removeBehaviour(this);
				} catch (Throwable e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}

		}

	}

	@Override
	public void checkForBattery() {
		double temp = 0;
		Local nearest = null;

		double distance = Double.MAX_VALUE;

		for (int i = 0; i < chargers.size(); i++) {
			distance = Math.sqrt(Math.pow(map.get(position).getI() - chargers.get(i).getI(), 2)
					+ Math.pow(map.get(position).getJ() - chargers.get(i).getJ(), 2));
			if (temp < distance) {
				temp = distance;
				nearest = chargers.get(i);
			}
		}

		moveBehav = new MoveRequestDrone(this, nearest, nearest);
		addBehaviour(moveBehav);
		this.batteryLeft = BATTERY_CAPACITY;

	}

}
