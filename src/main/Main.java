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
			AgentController drone = cc.createNewAgent("Drone 1", "agents.DroneAgent", new Object[] { "A;f1" });

			AgentController agent1 = cc.createNewAgent("Bike 1", "agents.BikeAgent", new Object[] { "B;f1" });
			AgentController agent2 = cc.createNewAgent("Car 1", "agents.CarAgent", new Object[] { "C;f2" });
			AgentController agent3 = cc.createNewAgent("Truck 1", "agents.TruckAgent", new Object[] { "A;f1" });
			AgentController agent4 = cc.createNewAgent("Car 2", "agents.CarAgent", new Object[] { "M;f1" });
			AgentController agent5 = cc.createNewAgent("Car 3", "agents.CarAgent", new Object[] { "A;f1" });
			
			
			ambiente.start();
			simulation.start();
			drone.start();

			agent1.start();
			agent2.start();
			agent3.start();
			agent4.start();
			agent5.start();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return;
		}
	}
}
