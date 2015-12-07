package job;


import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import agents.Worker;
import locals.Local;
import product.Product;
import tools.Tool;

public class Job {
	public enum to_do {
		ACQUISITION,
		MOUNT,
		TRANSPORT;
		public static to_do getRandom() {
	        return values()[(int) (Math.random() * values().length)];
	    }
	};
	public enum type {
		BIDS,
		PRICE;
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
	Local local;
	
	public Job(to_do j,type ty, double r, int t, double f, Product p, Local l){
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
	
	public void setDone(){
		done = true;
	}
	
	public void setbeingDone(){
		beingDone = true;
	}
	
	public double getReward(){
		return reward;
	}
	
	
	public Boolean isDone(){
		return done;
	}
	
	public Boolean beingDone(){
		return beingDone;
	}
	
	//Helps the agent to sort jobs in order to pick the most profitable one
	public double getProbabilityOfChoose(Local myLocal, Worker w){
		boolean fined = false;
		DijkstraShortestPath<Local, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<Local, DefaultWeightedEdge>(
				w.cityMap, myLocal, local);
		// double length = dijkstra.getPathLength();
		if(time * 1000 < (((10 - w.VELOCITY) * 100) * dijkstra.getPathLength()))
			fined = true;
		if(the_Job == to_do.TRANSPORT){
		if(fined)
			return ((reward*3 - fine)/(Math.sqrt(Math.pow(myLocal.getI() - local.getI(), 2)
				+ Math.pow(myLocal.getJ() - (double)local.getJ(), 2))));
		else
			return ((reward*3)/(Math.sqrt(Math.pow(myLocal.getI() - local.getI(), 2)
					+ Math.pow(myLocal.getJ() - (double)local.getJ(), 2))));
		}else
			return ((reward*3 - fine)/time);
	}
	
	public String toString() { 
	    return the_Job.toString() + ";" + job_Type.toString() + ";" + reward + ";" + time + ";" +
	fine + ";" +  product.getTool() + ";" + product.getName()+ ";" +product.getQuantidade() + ";" + local.getName() +";";
	}
	
	public boolean able(Worker W){
		Tool temp = new Tool(product.getTool());
		if(the_Job == to_do.MOUNT && !(W.getTools().contains(temp)))
			return false;
		return true;
		
	}
	
}
