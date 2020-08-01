

public class Board {
	private final int[][] tiles;
	private final int dim;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
		this.tiles = new int[tiles.length][tiles.length];
		this.dim = tiles.length;
		for (int i = 0; i < tiles.length; ++i)
			for (int j = 0; j < tiles.length; ++j)
				this.tiles[i][j] = tiles[i][j];
	}

    // string representation of this board
    public String toString() {
		String s = "" + tiles.length + "\n";
		for (int i = 0; i < dim; ++i) {
			for (int j = 0; j < dim; ++j)
				s += (" " + tiles[i][j]);
			s += "\n";
		}
		return s;
	}

    // board dimension n
    public int dimension() { return tiles.length; }

    // number of tiles out of place
    public int hamming() {
		int n = 0;

		for (int i = 0; i < dim; ++i) {
			for (int j = 0; j < dim; ++j) {
				int cell = tiles[i][j];
				if (i + j + 1 != cell
					|| (cell == 0 && i != dim - 1 && j != dim - 1))
					n++;
			}
		}
		return n;
	}

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {

	}

    // is this board the goal board?
    public boolean isGoal() {

	}

    // does this board equal y?
    public boolean equals(Object y) {

	}

    // all neighboring boards
    public Iterable<Board> neighbors() {

	}

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {

	}

    // unit testing (not graded)
    public static void main(String[] args) {

	}

}
