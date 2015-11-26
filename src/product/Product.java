package product;

import tools.Tool;

public class Product {
	Tool t; // tool necessaria para o produzir
	String name;
	int Quantidade;

	public Product(Tool t, String n, int Q) {
		this.t = t;
		name = n;
		Quantidade = Q;
	}

}
