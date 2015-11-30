package Simulation;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class ReceiveInformBehaviour extends CyclicBehaviour {
	public List<String> mess = new LinkedList<String>();
	public void action() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null) {
			parseContent(msg);
		} else {
			block();
		}
	}

	private void parseContent(ACLMessage msg) {
	}
}
