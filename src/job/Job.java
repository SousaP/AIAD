package job;

import locals.Local;
import product.Product;

public class Job {
	public enum to_do { ACQUISITION, MOUNT, TRANSPORT };
	public enum type { BIDS, PRICE };
	
	public to_do the_Job;
	public type job_Type;
	double reward;
	int time;
	double fine;
	Product product;
	Boolean done;
	Local local;
	
	public Job(to_do j,type ty, double r, int t, double f, Product p, Local l){
		the_Job = j;
		job_Type = ty;
		reward = r;
		time = t;
		fine = f;
		product = p;
		done = false;
		local = l;
	}
	
	public void setDone(){
		done = true;
	}
	
	public double getReward(){
		return reward;
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
