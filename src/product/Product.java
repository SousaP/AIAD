package product;

import tools.Tool;

public class Product {
	Tool t; // tool necessaria para o produzir
	String name;
	int Quantidade;
	int size;
	public double price;

	public Product(Tool t, String n,double p, int Q) {
		this.t = t;
		name = n;
		Quantidade = Q;
		size = 50;
		price = p;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String n){
		name = n;
	}
	public double getPrice() {
		return price;
	}
	
	public String getTool(){
		return t.getName();
	}
	
	public int getQuantidade(){
		return Quantidade;
	}
	
	public int getSize() {
		return size;
	}
	
	public void removeQ(int Q)
	{
		Quantidade -= Q;
	}
	
	public void adicionaQ(int Q)
	{
		Quantidade += Q;
	}
}
