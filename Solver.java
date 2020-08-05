import java.util.Comparator;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.Queue;

public class Solver
{
	private final class ST
	{
		private final class Pair
		{
			private Board key;
			private Board value;

			public Pair(Board key, Board value) {
				this.key = key;
				this.value = value;
			}
		}

		private Stack<Pair> st;

		public ST() { st = new Stack<Pair>(); }

		public Board get(Board key) {
			if (key == null)
				return null;
			for (Pair e : st)
				if (e.key.equals(key))
					return e.value;
			return null;
		}

		public void put(Board key, Board value) {
			if (key == null || value == null)
				return;
			st.push(new Pair(key, value));
		}
	}

	private Stack<Board> solus = new Stack<Board>();
	private boolean solved = false;

	private final class Node
		implements Comparable<Node>
	{
		private final Board board;
		private final int n_moves;

		public Node(Board board, int n_moves) {
			this.board = board;
			this.n_moves = n_moves;
		}

	    public int heuristic()  { return board.manhattan() + n_moves; }

		public boolean isGoal() { return board.isGoal(); }

		public int moves()		{ return n_moves; }

		public Iterable<Node> neighbors() {
			Queue<Node> q = new Queue<Node>();
			for (Board nei : board.neighbors())
				q.enqueue(new Node(nei, n_moves + 1));
			return q;
		}

		public int compareTo(Node that) {
			return Integer.compare(this.heuristic(), that.heuristic());
		}

		public String toString() {
			return board.toString();
		}

	}

	private Stack<Board> solutionBoards(Board current, ST from) {
		Stack<Board> sol = new Stack<Board>();

		sol.push(current);
		while (true) {
			current = from.get(current);
			if (current == null)
				break;
			sol.push(current);
		}
		return sol;
	}

	private boolean alreadyFound(Node current, MinPQ<Node> pq) {
		for (Node node : pq)
			if (current.board.equals(node.board))
				return true;
		return false;
	}

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
		if (initial == null)
			throw new IllegalArgumentException("Argument cannot be null");

	    ST from = new ST();

		MinPQ<Node> open = new MinPQ<Node>();

		open.insert(new Node(initial, 0));
		while (!open.isEmpty()) {
			Node current = open.delMin();
			if (current.isGoal()) {
				solus = solutionBoards(current.board, from);
				solved = true;
				break;
			}
			for (Node nei : current.neighbors()) {
				if (nei.heuristic() <= current.heuristic()) {
					if (!alreadyFound(nei, open)) {
						from.put(nei.board, current.board);
						open.insert(nei);
					}
				}
			}
		}
	}

    // is the initial board solvable? (see below)
    public boolean isSolvable()			{ return solved; }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves()					{ return solus.size()  -1; }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution()	{ return solus; }

    // test client (see below)
	public static void main(String[] args) {

		// create initial board from file
		In in = new In(args[0]);
		int n = in.readInt();
		int[][] tiles = new int[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				tiles[i][j] = in.readInt();
		Board initial = new Board(tiles);

		// solve the puzzle
		Solver solver = new Solver(initial);

		// print solution to standard output
		if (!solver.isSolvable())
			StdOut.println("No solution possible");
		else {
			StdOut.println("Minimum number of moves = " + solver.moves());
			for (Board board : solver.solution())
				StdOut.println(board);
		}
	}
}
