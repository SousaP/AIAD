package product;

import tools.Tool;

public class Product {
	Tool t; // tool necessaria para o produzir
	String name;
	int Quantidade;
	int size;
	public double price;

	public Product(Tool t, String n, int Q) {
		this.t = t;
		name = n;
		Quantidade = Q;
		size = 50;
		price = 20;
	}
	
	public String getName(){
		return name;
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
}
