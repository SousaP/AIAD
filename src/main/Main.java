package main;

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
                    "agents.CarAgent",new Object[] { "A;f1"} );
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

