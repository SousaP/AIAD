package main;

import agents.Worker;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class Main {

    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        ContainerController cc = rt.createMainContainer(p);

        try {


            AgentController agent = cc.createNewAgent("Worker 1",
                    "agents.DroneAgent",new Object[] { "A;f1"} );
          /*  AgentController worker = cc.createNewAgent("Worker 2",
            		"agents.Worker",new Object[] { "B;f2"} ); */
            AgentController simulation = cc.createNewAgent("simulation","Simulation.SimulationAgent", null);
    		simulation.start();

            
            agent.start();            
            //worker.start();
           
           
        } catch(Exception e) {
            System.err.println(e.getMessage());
            return;
        }
    }
}


/*

import PingPong.PingPong;
import jade.core.Profile;
import jade.core.ProfileImpl;
import sajas.core.Agent;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.engine.SimInit;

public class Main extends Repast3Launcher {

	private static final boolean BATCH_MODE = true;
	private ContainerController mainContainer;
	private ContainerController agentContainer;
	public static final boolean SEPARATE_CONTAINERS = false;
	public static final int NUM_AGENTS = 2;
	

	public static void main(String[] args) {
		boolean runMode = !BATCH_MODE; // BATCH_MODE or !BATCH_MODE
		SimInit init = new SimInit();
		init.setNumRuns(10);   // works only in batch mode
		init.loadModel(new Main(), null, runMode);
	}
	
	private void launchAgents() {
		try {
			Agent car1 = new Agent();
			Agent car2 = new Agent();
			car1.setArguments(new String[]{"ping"});
			car2.setArguments(new String[]{"pong"});
			agentContainer.acceptNewAgent("Novo agente1", car1);
			agentContainer.acceptNewAgent("Novo agente2", car2);
		}
		catch(Exception e) {
			
		}
	}
	
	@Override
	public String[] getInitParam() {
		return new String[0];
	}


	@Override
	public String getName() {
		return "Transportes";
	}


	@Override
	protected void launchJADE() {
		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		mainContainer = rt.createMainContainer(p1);
		
		if(SEPARATE_CONTAINERS) {
			Profile p2 = new ProfileImpl();
			agentContainer = rt.createAgentContainer(p2);
		} else {
			agentContainer = mainContainer;
		}
		
		launchAgents();
	}

}

*/