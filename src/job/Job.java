package job;

import locals.Local;
import product.Product;

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
		
		
		System.out.println("Job Created : "+ the_Job.toString() +" "+ job_Type.toString() + " " + reward);
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
	public double getProbabilityOfChoose(Local myLocal){
		if(the_Job == to_do.TRANSPORT)
		return ((reward*3 - fine)/(Math.sqrt(Math.pow(myLocal.getI() - local.getI(), 2)
				+ Math.pow(myLocal.getJ() - (double)local.getJ(), 2))));
		else
			return ((reward*3 - fine)/time);
	}
	

	
}
