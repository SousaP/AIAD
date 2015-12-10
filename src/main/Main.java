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
			AgentController ambiente = cc.createNewAgent("ambient", "agents.Ambiente", null);
			AgentController simulation = cc.createNewAgent("simulation", "Simulation.SimulationAgent", null);
			// AgentController drone = cc.createNewAgent("Drone 1",
			// "agents.DroneAgent", new Object[] { "A;f1" });

			AgentController car1 = cc.createNewAgent("Car 1", "agents.CarAgent", new Object[] { "B;f1" });
				AgentController car2 = cc.createNewAgent("Car 2", "agents.CarAgent", new Object[] { "C;f2" });
				/*		AgentController car3 = cc.createNewAgent("Car 3", "agents.CarAgent", new Object[] { "A;f1" });
			AgentController car4 = cc.createNewAgent("Car 4", "agents.CarAgent", new Object[] { "M;f1" });
			AgentController car5 = cc.createNewAgent("Car 5", "agents.CarAgent", new Object[] { "A;f1" });
			AgentController car6 = cc.createNewAgent("Car 6", "agents.CarAgent", new Object[] { "I;f1" });
			AgentController car7 = cc.createNewAgent("Car 7", "agents.CarAgent", new Object[] { "L;f1" });
			AgentController car8 = cc.createNewAgent("Car 8", "agents.CarAgent", new Object[] { "B;f1" });
*/
			ambiente.start();
			simulation.start();
			// drone.start();

			car1.start();
			car2.start();
			/*	car3.start();
			car4.start();
			car5.start();
			car6.start();
			car7.start();
			car8.start();
	*/		

		} catch (Exception e) {
			System.err.println(e.getMessage());
			return;
		}
	}
}
