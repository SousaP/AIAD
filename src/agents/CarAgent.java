package agents;

import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import credit.Credit;
import sajas.core.Agent;
import sajas.core.behaviours.SimpleBehaviour;
import sajas.domain.DFService;
import tools.Tool;

public class CarAgent extends Worker {

	private static int VELOCITY = 3;
	private static boolean ROAD = true; // true estrada, false ar
	private static int BATTERY_CAPACITY = 500;
	private static int LOAD_CAPACITY = 550;
	private static Tool f1;
	private static Tool f2;

	private Credit credit;
	private int batteryLeft;
	private int loadLeft;
	

	

}
