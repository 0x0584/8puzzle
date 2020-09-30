import java.util.Comparator;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.Queue;

public class Solver
{
	private Stack<Board> solus = new Stack<Board>();
	private boolean solved = false;

	private final class SearchNode
		implements Comparable<SearchNode>
	{
		private final Board board;
		private final SearchNode from;

		private final int steps;
		private final int score;

		public SearchNode(SearchNode from, Board board) {
			this(from, board, from.steps() + 1);
		}

		public SearchNode(Board board, int steps) {
			this(null, board, steps);
		}

		public SearchNode(SearchNode from, Board board, int steps) {
			this.from = from;
			this.board = board;
			this.steps = steps;
			this.score = board.manhattan() + steps;
		}

		public boolean isGoal() { return board.isGoal(); }

		public int steps()      { return steps; }

		public String toString() { return board.toString(); }

		public Iterable<Board> neighbors() { return board.neighbors(); }

		public boolean safeMove(Board nei) {
			return from == null || !nei.equals(from.board);
		}

		public int compareTo(SearchNode that) {
			return Integer.compare(this.score, that.score);
		}
	}

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
		if (initial == null)
			throw new IllegalArgumentException("Argument cannot be null");

		MinPQ<SearchNode> open = new MinPQ<SearchNode>();
		open.insert(new SearchNode(initial, 0));
		open.insert(new SearchNode(initial.twin(), 0));
		while (!open.isEmpty()) {
			SearchNode current = open.delMin();
			if (current.isGoal()) {
				solutionBoards(current, initial.twin());
				break;
			}
			for (Board nei : current.neighbors())
				if (current.safeMove(nei))
					open.insert(new SearchNode(current, nei));
		}
	}

	private void solutionBoards(SearchNode current, Board twin) {
		Stack<Board> sol = new Stack<Board>();

		while (current.from != null) {
			sol.push(current.board);
			current = current.from;
		}
		sol.push(current.board);
		if (current.board.equals(twin))
			solus = null;
		else
			solus = sol;
		solved = solus != null;
	}

    // is the initial board solvable? (see below)
    public boolean isSolvable()			{ return solved; }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves()					{ return solved ?  (solus.size() - 1) : -1; }

	// sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution()	{ return solus; }

    // test client (see below)
	public static void main(String[] args) {
		In in = new In(args[0]);
		int n = in.readInt();
		int tiles[][] = new int[n][n];

		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				tiles[i][j] = in.readInt();

		Board init = new Board(tiles);
		Solver solver = new Solver(init);
		init = null;
		if (!solver.isSolvable()) {
			StdOut.println("No solution possible");
		} else {
			StdOut.println("Minimum number of moves = " + solver.moves());
			for (Board board : solver.solution())
				StdOut.println(board);
		}
	}
}
