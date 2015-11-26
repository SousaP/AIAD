package Path;
import java.util.Arrays;

public class PathLenght {

	final static int TRIED = 2;
	final static int PATH = 3;

	int i_end = 7;
	int j_end = 7;

	/* @formatter:off
	private static int[][] GRID = {
			{ 1, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1 },
			{ 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1 },
			{ 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0 },
			{ 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1 },
			{ 1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1 },
			{ 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } };
	 @formatter:off
*/
	private int[][] grid;
	private int height;
	private int width;

	private int[][] map;
	
	public int custo() {
		int counter = 0;
		for (int i = 0; i < map.length; i++)
			for (int j = 0; j < map[i].length; j++)
				if (map[i][j] == 3)
					counter++;

		return counter;
	}

	/*
	public static void main(String[] args) {
		PathLenght maze = new PathLenght(GRID);
		boolean solved = maze.solve(7,4,7,7);
		System.out.println("Solved: " + solved);
		System.out.println(maze.toString());
		System.out.println("Custo: " + maze.custo());
	}

*/

	public PathLenght(int[][] grid) {
		this.grid = grid;
		this.height = grid.length;
		this.width = grid[0].length;
		this.map = new int[height][width];
	}

	public boolean solve(int i, int j, int i_final, int j_final) {
		i_end = i_final;
		j_end = j_final;
		return traverse(i, j);
	}

	private boolean traverse(int i, int j) {
		if (!isValid(i, j)) {
			return false;
		}

		if (isEnd(i, j)) {
			map[i][j] = PATH;
			return true;
		} else {
			map[i][j] = TRIED;
		}

		// North
		if (traverse(i - 1, j)) {
			map[i - 1][j] = PATH;
			return true;
		}
		// East
		if (traverse(i, j + 1)) {
			map[i][j + 1] = PATH;
			return true;
		}
		// South
		if (traverse(i + 1, j)) {
			map[i + 1][j] = PATH;
			return true;
		}
		// West
		if (traverse(i, j - 1)) {
			map[i][j - 1] = PATH;
			return true;
		}

		return false;
	}

	private boolean isEnd(int i, int j) {
		return i == i_end && j == j_end;
	}

	private boolean isValid(int i, int j) {
		if (inRange(i, j) && isOpen(i, j) && !isTried(i, j)) {
			return true;
		}

		return false;
	}

	private boolean isOpen(int i, int j) {
		return grid[i][j] == 1;
	}

	private boolean isTried(int i, int j) {
		return map[i][j] == TRIED;
	}

	private boolean inRange(int i, int j) {
		return inHeight(i) && inWidth(j);
	}

	private boolean inHeight(int i) {
		return i >= 0 && i < height;
	}

	private boolean inWidth(int j) {
		return j >= 0 && j < width;
	}

	public String toString() {
		String s = "";
		for (int[] row : map) {
			s += Arrays.toString(row) + "\n";
		}

		return s;
	}
	public String toStringGrid() {
		String s = "";
		for (int[] row : grid) {
			s += Arrays.toString(row) + "\n";
		}

		return s;
	}

}