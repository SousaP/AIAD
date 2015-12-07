package job;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import agents.DroneAgent;
import agents.Worker;
import agents.Worker.MoveRequest;
import locals.Local;
import product.Product;
import tools.Tool;

public class Job {
	public enum to_do {
		ACQUISITION, MOUNT, TRANSPORT;
		public static to_do getRandom() {
			return values()[(int) (Math.random() * values().length)];
		}
	};

	public enum type {
		BIDS, PRICE;
		public static type getRandom() {
			return values()[(int) (Math.random() * values().length)];
		}
	};

	public to_do the_Job;
	public type job_Type;
	double reward;
	int time;
	double fine;
	Product product;
	Boolean done;
	Boolean beingDone;
	public Local local;

	public Job(to_do j, type ty, double r, int t, double f, Product p, Local l) {
		the_Job = j;
		job_Type = ty;
		reward = r;
		time = t;
		fine = f;
		product = p;
		done = false;
		beingDone = false;
		local = l;

	}

	public void setDone() {
		done = true;
	}

	public void setbeingDone() {
		beingDone = true;
	}

	public double getReward() {
		return reward;
	}

	public Boolean isDone() {
		return done;
	}

	public Boolean beingDone() {
		return beingDone;
	}

	// Helps the agent to sort jobs in order to pick the most profitable one
	public double getProbabilityOfChoose(Local myLocal, Worker w) {
		boolean fined = false;
		DijkstraShortestPath<Local, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<Local, DefaultWeightedEdge>(
				w.cityMap, myLocal, local);
		// double length = dijkstra.getPathLength();
		if (time * 1000 < (((10 - w.VELOCITY) * 100) * dijkstra.getPathLength()))
			fined = true;
		if (the_Job == to_do.TRANSPORT) {
			if (fined)
				return ((reward * 3 - fine) / dijkstra.getPathLength());
			else
				return ((reward * 3) / dijkstra.getPathLength());
		} else
			return ((reward * 3 - fine) / time);
	}

	public double getProbabilityOfChoose(Local myLocal, DroneAgent d) {
		boolean fined = false;
		double distance = Math
				.sqrt(Math.pow((myLocal.getI() - local.getI()), 2) + Math.pow((myLocal.getJ() - local.getJ()), 2));
		// double length = dijkstra.getPathLength();
		if (time * 1000 < (((10 - d.VELOCITY) * 100) * distance))
			fined = true;
		if (the_Job == to_do.TRANSPORT) {
			if (fined)
				return ((reward * 3 - fine) / distance);
			else
				return ((reward * 3) / distance);
		} else
			return ((reward * 3 - fine) / time);
	}

	public String toString() {
		return the_Job.toString() + ";" + job_Type.toString() + ";" + reward + ";" + time + ";" + fine + ";"
				+ product.getTool() + ";" + product.getName() + ";" + product.getQuantidade() + ";" + local.getName()
				+ ";";
	}

	public boolean able(Worker W) {
		Tool temp = new Tool(product.getTool());
		if (the_Job == to_do.MOUNT && !(W.getTools().contains(temp)))
			return false;
		if (the_Job == to_do.TRANSPORT && (W.getLoadLeft() < product.getSize()))
			return false;
		if (the_Job == to_do.ACQUISITION && (W.credit < product.price))
			return false;
		double temp1 = 0;
		double temp2 = 0;
		DijkstraShortestPath<Local, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<Local, DefaultWeightedEdge>(
				W.cityMap, W.map.get(W.position), local);
		temp2 = dijkstra.getPathLength();
		for (int i = 0; i < W.chargers.size(); i++) {
			DijkstraShortestPath<Local, DefaultWeightedEdge> dijkstra2 = new DijkstraShortestPath<Local, DefaultWeightedEdge>(
					W.cityMap, local, W.map.get(W.chargers.get(i).getName()));
			if (temp1 < dijkstra2.getPathLength()) {
				temp1 = dijkstra2.getPathLength();
			}
		}
		if (W.batteryLeft < (temp1 + temp2)) {
			return false;
		}
		return true;

	}

	public boolean compare(Job b) {
		// System.out.println(this.toString());
		// System.out.println(b.toString());

		return (the_Job.equals(b.the_Job) && job_Type.equals(b.job_Type) && time == b.time && fine == b.fine
				&& product.getName().equals(b.product.getName()) && product.getQuantidade() == b.product.getQuantidade()
				&& product.getTool().equals(b.product.getTool()) && local.getName().equals(b.local.getName()));
	}

}
