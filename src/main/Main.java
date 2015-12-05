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
			AgentController drone = cc.createNewAgent("Drone 1", "agents.DroneAgent", new Object[] { "A;f1" });
			AgentController car = cc.createNewAgent("Car 1", "agents.CarAgent", new Object[] { "B;f1" });
			AgentController ambiente = cc.createNewAgent("ambient", "agents.Ambiente", null);

			AgentController simulation = cc.createNewAgent("simulation", "Simulation.SimulationAgent", null);
			ambiente.start();
			simulation.start();
			drone.start();
			car.start();

		} catch (Exception e) {
			System.err.println(e.getMessage());
			return;
		}
	}
}
