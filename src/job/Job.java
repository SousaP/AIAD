package job;

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
	
	public Job(to_do j,type ty, double r, int t, double f, Product p){
		the_Job = j;
		job_Type = ty;
		reward = r;
		time = t;
		fine = f;
		product = p;
		done = false;
	}
	
	public void setDone(){
		done = true;
	}
	
	public double getReward(){
		return reward;
	}
	
	
	//Helps the agent to sort jobs in order to pick the most profitable one
	public double getProbabilityOfChoose(){
		return ((reward*3 - time)/fine);
	}
	
	
}
