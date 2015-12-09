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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Ambiente extends Worker {
	private static final long serialVersionUID = 1L;
	ambientBehaviour b;
	HashMap<Job, List<AID>> bids = new HashMap<Job, List<AID>>();
	HashMap<Job, AID> winning = new HashMap<Job, AID>();
	HashMap<Local, List<Product>> produtos = new HashMap<Local, List<Product>>();

	private void readProducts() {
		try {

			File inputFile = new File("map.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			NodeList productList = doc.getElementsByTagName("Product");
			for (int temp = 0; temp < productList.getLength(); temp++) {
				Node nNode = productList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					List<Product> value = produtos.get(map.get(eElement.getAttribute("Local")));
					if (value != null) {
						value.add(new Product(new Tool(eElement.getAttribute("tool")), eElement.getAttribute("nome"),
								Double.parseDouble(eElement.getAttribute("preço")),
								Integer.parseInt(eElement.getTextContent())));

						produtos.put(map.get(eElement.getAttribute("Local")), value);
					} else {
						value = new ArrayList<Product>();
						value.add(new Product(new Tool(eElement.getAttribute("Tool")), eElement.getAttribute("nome"),
								Double.parseDouble(eElement.getAttribute("preço")),
								Integer.parseInt(eElement.getTextContent())));

						produtos.put(map.get(eElement.getAttribute("Local")), value);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
		readProducts();

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
			ACLMessage msg = blockingReceive(500);

			if (msg == null)
				return;

			if (msg.getPerformative() == ACLMessage.INFORM) {
				System.out.println(getLocalName() + ": recebi " + msg.getContent());
				ACLMessage reply = msg.createReply();

				String split[] = msg.getContent().split(";");
				if (split.length < 2)
					return;

				String content = "jobs;";
				// System.out.println("split[0]: " + split[0]);
				// System.out.println("split[1]: " + split[1]);
				// System.out.println("Sender: " + msg.getSender());

				if (split[0].contains("jobs") && split[1].contains("?"))
				{	for (int i = 0; i < Jobs_Created.size(); i++)
						if (!(Jobs_Created.get(i).beingDone() || Jobs_Created.get(i).isDone())) {
							content = content + Jobs_Created.get(i).toString();
							// System.out.println("Jobs identificados: " +
							// Jobs_Created.get(i).toString());
						}
				}
				else if (split[0].contains("apanhei")) {
					Job job_to_complete = new Job(to_do.valueOf(split[1]), type.valueOf(split[2]),
							Double.parseDouble(split[3]), Integer.parseInt(split[4]),
							Double.parseDouble(split[5]), new Product(new Tool(split[6]), split[7],
									Double.parseDouble(split[8]), Integer.parseInt(split[9])),
							map.get(split[10]), map.get(split[11]));
					Local l = map.get(job_to_complete.getLocal().getName());
					List<Product> temp = produtos.get(l);
					System.out.println("SZIE  " + temp.size());
					for(int i = 0; i < temp.size(); i++)
						if(temp.get(i).getName().contains(job_to_complete.product.getName())){
						//	System.out.println("Quantidade inicial" + temp.get(i).getQuantidade());
							temp.get(i).removeQ(job_to_complete.product.getQuantidade());
						//	System.out.println("Quantidade Final" + temp.get(i).getQuantidade());
							break;
						}
					return;

				} else if (split[0].contains("depositei")) {
					Job job_to_complete = new Job(to_do.valueOf(split[1]), type.valueOf(split[2]),
							Double.parseDouble(split[3]), Integer.parseInt(split[4]),
							Double.parseDouble(split[5]), new Product(new Tool(split[6]), split[7],
									Double.parseDouble(split[8]), Integer.parseInt(split[9])),
							map.get(split[10]), map.get(split[11]));
					Local l = map.get(job_to_complete.getLocal2().getName());
					List<Product> temp = produtos.get(l);
					System.out.println("SZIE  " + temp.size());
					for(int i = 0; i < temp.size(); i++)
						if(temp.get(i).getName().contains(job_to_complete.product.getName())){
						//	System.out.println("Quantidade inicial" + temp.get(i).getQuantidade());
							temp.get(i).adicionaQ(job_to_complete.product.getQuantidade());
						//	System.out.println("Quantidade Final" + temp.get(i).getQuantidade());
							break;
						}
					return;
				}
				// System.out.println("Jobs identificados: " + content);
				reply.setContent(content);
				// envia mensagem
				send(reply);
			} else if (msg.getPerformative() == ACLMessage.PROPOSE) {

				// System.out.println("Ambiente: Recebi um Propose" +
				// msg.getContent());

				String split[] = msg.getContent().split(";");
				if (split.length < 2)
					return;

				Job job_to_complete = new Job(to_do.valueOf(split[0]), type.valueOf(split[1]),
						Double.parseDouble(split[2]), Integer.parseInt(split[3]),
						Double.parseDouble(split[4]), new Product(new Tool(split[5]), split[6],
								Double.parseDouble(split[7]), Integer.parseInt(split[8])),
						map.get(split[9]), map.get(split[9]));

				// ver se job faz parte da lista de jobs
				for (int i = 0; i < Jobs_Created.size(); i++)
					if (Jobs_Created.get(i).compare(job_to_complete)) {

						// System.out.println("AQUIIIII");
						// verificar se ja acabou ou está a ser realizado ->
						// Responder Failure
						if (Jobs_Created.get(i).beingDone() || Jobs_Created.get(i).isDone()) {
							ACLMessage cfp = new ACLMessage(ACLMessage.FAILURE);
							cfp.addReceiver(msg.getSender());
							// Responde Failure com job na mensagem
							cfp.setContent(Jobs_Created.get(i).toString());
							cfp.setConversationId("job_proposal");
							cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																					// value

							// myAgent.send(cfp);
							send(cfp);
						} else if (Jobs_Created.get(i).job_Type == type.BIDS) {
							ACLMessage cfp;
							ACLMessage avisos;
							if (job_to_complete.getReward() <= Jobs_Created.get(i).getReward()) {
								cfp = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
								cfp.addReceiver(msg.getSender());

								winning.put(Jobs_Created.get(i), msg.getSender());

								avisos = new ACLMessage(ACLMessage.FAILURE);
								if (bids.containsKey(Jobs_Created.get(i)))
									for (int a = 0; a < bids.get(Jobs_Created.get(i)).size(); a++)
										if (!bids.get(Jobs_Created.get(i)).get(a).getLocalName()
												.equals(msg.getSender().getLocalName())) {
											avisos.addReceiver(bids.get(Jobs_Created.get(i)).get(a));
											// System.out.println("A avisar : "
											// +bids.get(Jobs_Created.get(i)).get(a).getLocalName());
										}

								Jobs_Created.get(i).setReward(job_to_complete.getReward() - 1);

								avisos.setContent(Jobs_Created.get(i).toString());

								avisos.setConversationId("job_proposal");
								avisos.setReplyWith("cfp" + System.currentTimeMillis()); // Unique

								send(avisos);

								if (bids.containsKey(Jobs_Created.get(i))) {

									List<AID> temp = bids.get(Jobs_Created.get(i));
									if (!temp.contains(msg.getSender())) {
										temp.add(msg.getSender());
										bids.put(Jobs_Created.get(i), temp);
									}
								} else {
									List<AID> temp = new ArrayList<AID>();
									temp.add(msg.getSender());
									bids.put(Jobs_Created.get(i), temp);
								}

							} else {
								cfp = new ACLMessage(ACLMessage.FAILURE);
								cfp.addReceiver(msg.getSender());
							}

							// Responde Agree com Job na mensagem
							cfp.setContent(Jobs_Created.get(i).toString());

							cfp.setConversationId("job_proposal");
							cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique

							// myAgent.send(cfp);
							send(cfp);
						}

						else {
							ACLMessage cfp = new ACLMessage(ACLMessage.AGREE);
							cfp.addReceiver(msg.getSender());
							// Responde Agree com Job na mensagem
							cfp.setContent(Jobs_Created.get(i).toString());
							cfp.setConversationId("job_proposal");
							cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																					// value
							Jobs_Created.get(i).setbeingDone();
							// myAgent.send(cfp);
							send(cfp);
						}
					}
				// else System.out.println("ALIIIIIIIII");

				// String content = "jobs;";
				// reply.setContent(content);
				// envia mensagem
				// send(reply);
			}

		}

	}

	class ambientBehaviour extends TickerBehaviour {
		private static final long serialVersionUID = 1L;

		public ambientBehaviour(Agent a, long period) {
			super(a, period);

			for (int i = 0; i < 1; i++)
				Jobs_Created.add(createRandomJob());

		}
		/*
		Job createRandomJob() {

			to_do temp = to_do.TRANSPORT;
			Random random = new Random();
			List<Local> keysList = new ArrayList<Local>();
			List<Product> listProdutos = new ArrayList<Product>();

			keysList.addAll(produtos.keySet());
			
			Local levantar = keysList.get(random.nextInt(keysList.size()));

			listProdutos = produtos.get(levantar);

			Product p = listProdutos.get(random.nextInt(listProdutos.size()));
			int Quantidade = getRandomInt(1, p.getQuantidade() / 2);

			// System.out.println(p.toString());

			Product p_job = new Product(new Tool(p.getTool()), p.getName(), Quantidade * p.getPrice(), Quantidade);
			
			Local local = null;
			do{
			local = keysList.get(random.nextInt(keysList.size()));
			}while(local.getName().equals(levantar.getName()));
			
			if (temp == to_do.TRANSPORT)
				return new Job(to_do.TRANSPORT, type.BIDS, getRandomInt(400, 800), getRandomInt(2, 8),
						getRandomInt(50, 300), p_job, levantar, local);
			else
				return new Job(to_do.TRANSPORT, type.BIDS, getRandomInt(400, 800), getRandomInt(1, 5),
						getRandomInt(50, 300), p_job, local, local);

		}
		*/
		Job createRandomJob() {

			to_do temp = to_do.ACQUISITION;
			Random random = new Random();
			
			List<Local> keysList = new ArrayList<Local>();
			List<Product> listProdutos = new ArrayList<Product>();

			
			keysList.addAll(produtos.keySet());
			
			// Retira locais que não são stores
			if(temp == to_do.ACQUISITION) {
				for(Local key:  keysList) {
					if(!stores.contains(key.getName())) {
						keysList.remove(key);
					}
				}
			}
			
			Local levantar = keysList.get(random.nextInt(keysList.size()));

			listProdutos = produtos.get(levantar);

			Product p = listProdutos.get(random.nextInt(listProdutos.size()));
			int Quantidade = getRandomInt(1, p.getQuantidade() / 2);

			// System.out.println(p.toString());

			Product p_job = new Product(new Tool(p.getTool()), p.getName(), Quantidade * p.getPrice(), Quantidade);
			
			Local local = null;
			do{
			local = keysList.get(random.nextInt(keysList.size()));
			}while(local.getName().equals(levantar.getName()));
			
			if (temp == to_do.ACQUISITION)
				return new Job(to_do.ACQUISITION, type.BIDS, getRandomInt(400, 800), getRandomInt(2, 8),
						getRandomInt(50, 300), p_job, levantar, levantar);
			else
			if (temp == to_do.TRANSPORT)
				return new Job(to_do.TRANSPORT, type.BIDS, getRandomInt(400, 800), getRandomInt(2, 8),
						getRandomInt(50, 300), p_job, levantar, local);
			else
				return new Job(to_do.TRANSPORT, type.BIDS, getRandomInt(400, 800), getRandomInt(1, 5),
						getRandomInt(50, 300), p_job, local, local);

		}

		@Override
		protected void onTick() {

			System.out.println("TICK AMBIENTE ");
			for (int i = 0; i < Jobs_Created.size(); i++)
				if (!Jobs_Created.get(i).beingDone()) {

					AID value = winning.get(Jobs_Created.get(i));
					if (value != null) {
						ACLMessage msg = new ACLMessage(ACLMessage.AGREE);
						msg.addReceiver(value);
						// Responde Failure com job na mensagem
						msg.setContent(Jobs_Created.get(i).toString());
						msg.setConversationId("job_GO");
						msg.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																				// value
						send(msg);

						// myAgent.

					}
					winning.remove(Jobs_Created.get(i));
					Jobs_Created.set(i, createRandomJob());
					bids.put(Jobs_Created.get(i), new ArrayList<AID>());

				}

		}

	}
}
