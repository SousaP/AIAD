package locals;

import java.util.HashMap;

import product.Product;

public class Local {
	String name;
	private int position[];
	HashMap<String, Product> produtos = new HashMap<String, Product>();

	public Local(int i, int j, String n) {
		position = new int[2];
		position[0] = i;
		position[1] = j;
		name = n;

	}

	boolean equals(Local tempL) {
		return (name == tempL.name && position[0] == tempL.position[0] && position[1] == tempL.position[1]);
	}

	public int getI() {
		return position[0];
	}

	public int getJ() {
		return position[1];
	}

	public String getName() {
		return name;
	}

}
