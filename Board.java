import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Queue;

public class Board {
	private final int[] cells;
	private final int dim;
	private final int blank_x, blank_y;

	// private methods ////////////////////////////////////////////////////////////

	private int  getCell(int x, int y)			{ return cells[dim * y + x]; }

	private void setCell(int val, int x, int y) { cells[dim * y + x] = val;  }

	private void setCell(int[] cells, int val, int x, int y) {
		cells[dim * y + x] = val;
	}
	private boolean atPlace(int x, int y)		{
		return getCell(x, y) == 0 ? true : getCell(x, y) == x + y * dim + 1;
	}


	private int[] cloneCells() {
		int[] tmp = new int[cells.length];
		for (int i = 0; i < cells.length; ++i)
			tmp[i] = cells[i];
		return tmp;
	}

	private int[] blankSwap(int x, int y) {
		int[] cells = cloneCells();
		setCell(cells, getCell(x, y), blank_x, blank_y);
		setCell(cells, 0, x, y);
		return cells;
	}


	private Board(int[] cells, int bx, int by) {
		this.dim = (int)Math.sqrt(cells.length);
		this.cells = cells;
		this.blank_x = bx; this.blank_y = by;
	}

	///////////////////////////////////////////////////////////////////////////////

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
		dim = tiles.length;
	    cells = new int[dim * dim];
		int bx = 0, by = 0;
		for (int y = 0; y < dim; ++y) {
			for (int x = 0; x < dim; ++x) {
				setCell(tiles[y][x], x, y);
				if (tiles[y][x] == 0) {
					bx = x; by = y;
				}
			}
		}
		this.blank_x = bx;
		this.blank_y = by;
	}

    // string representation of this board
    public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("" + dim + "\n");
		for (int y = 0; y < dim; ++y)
			for (int x = 0; x < dim; ++x)
				sb.append(" " + getCell(x, y) + (x + 1 == dim ? "\n" : " "));
		return sb.toString();
	}

    // board dimension n
    public int dimension() { return dim; }

    // number of tiles out of place
    public int hamming() {
		int n = 0;
		for (int y = 0; y < dim; ++y)
			for (int x = 0; x < dim; ++x)
				if (!atPlace(x, y))
					n++;
		return n;
	}

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
		int n = 0;
		for (int y = 0; y < dim; ++y)
			for (int x = 0; x < dim; ++x)
				if (!atPlace(x, y)) {
					int y2 = (getCell(x, y) - 1) / dim;
					int x2 = (getCell(x, y) - 1) % dim;
					int val = Math.abs((Math.abs(x - x2) + Math.abs(y - y2)));
					n += val;
				}
		return n;
	}

    // is this board the goal board?
    public boolean isGoal() { return hamming() == 0; }

    // does this board equal y?
    public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (obj == null || obj.getClass() != this.getClass())
			return false;

		Board that = (Board) obj;
		if (this.dim != that.dim)
			return false;
		for (int i = 0; i < cells.length; ++i)
			if (this.cells[i] != that.cells[i])
				return false;
		return true;
	}

    // all neighboring boards
    public Iterable<Board> neighbors() {
		Queue<Board> q = new Queue<>();

		final int[] dx = {0,  0, 1, -1};
		final int[] dy = {1, -1, 0,  0};

		for (int i = 0; i < 4; ++i) {
			int x2 = blank_x + dx[i], y2 = blank_y + dy[i];
			if (x2 < 0 || x2 >= dim) continue;
			if (y2 < 0 || y2 >= dim) continue;
			q.enqueue(new Board(blankSwap(x2, y2), x2, y2));
		}

		return q;
	}

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
		int i = 0;
		while (cells[i] == 0) i++;
		int j = i + 1;
		while (cells[j] == 0) j++;
		int[] tmp = cloneCells();
		int foo = tmp[i];
		tmp[i] = tmp[j];
		tmp[j] = foo;
		return new Board(tmp, blank_x, blank_y);
	}

    // unit testing (not graded)
    public static void main(String[] args) {
		int dim = StdIn.readInt();
		int[][] tiles = new int[dim][dim];

		for (int y = 0; y < dim; ++y)
			for (int x = 0; x < dim; ++x)
				tiles[y][x] = StdIn.readInt();

		Board board = new Board(tiles);
		StdOut.println("Board");
		StdOut.println(board.toString());
		StdOut.println("Hamming distance: " + board.hamming());
		StdOut.println("Manhattan distance: " + board.manhattan());
		StdOut.println("Twin");
		StdOut.println(board.twin());
		StdOut.println("Neighbors");
		for (Board b : board.neighbors())
			StdOut.println(b.toString());
	}

}
