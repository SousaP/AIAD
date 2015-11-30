package Simulation;

import java.io.IOException;
import java.util.ArrayList;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveRequestBehaviour extends CyclicBehaviour {
	public void action() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
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
