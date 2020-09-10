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
				solus = solutionBoards(current, initial.twin());
				solved = solus != null;
				break;
			}
			for (SearchNode nei : current.neighbors()) {
				if (nei.neighbors().isEmpty())
					continue;
				nei.setFrom(current);
				open.insert(nei);
			}
		}

	}

	private Stack<Board> solutionBoards(SearchNode current, Board twin) {
		Stack<Board> sol = new Stack<Board>();

		while (current.from != null) {
			sol.push(current.board);
			current = current.from;
		}
		sol.push(current.board);
		return current.board.equals(twin) ? null : sol;
	}

    // is the initial board solvable? (see below)
    public boolean isSolvable()			{ return solved; }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves()					{ return solved ?  (solus.size() - 1) : -1; }

	// sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution()	{ return solus; }

	private final class SearchNode
		implements Comparable<SearchNode>
	{
		private final Board board;
		private SearchNode from = null;

		private final int steps;
		private final int mann;

		private Queue<SearchNode> neis;

		private SearchNode(Board board, int steps, Queue<SearchNode> neis) {
			this.board = board; this.steps = steps; this.neis = neis;
			this.mann = board.manhattan();
		}

		public SearchNode(Board board, int steps) {
			this.board = board; this.steps = steps; this.neis = null;
			this.mann = board.manhattan();
		}

	    public int heuristic()  {
			return mann  + steps;
		}

		public boolean isGoal() { return board.isGoal(); }

		public int moves()      { return steps; }

		public void setFrom(SearchNode from) {
			if (this.from != null) {
				StdOut.println("Duplicated, old was ");
				StdOut.println(this.from);
				StdIn.readChar();
			}
			this.from = from;
		}

		public Queue<SearchNode> neighbors() {
			if (neis == null) {
				neis = new Queue<SearchNode>();
				for (Board l1 : board.neighbors()) {
					Queue<SearchNode> leafs = new Queue<SearchNode>();
					for (Board l2 : l1.neighbors()) {
						boolean unseen = true;
						SearchNode parent = from;
						while (parent != null) {
							if (l2.equals(parent.board)) {
								unseen = false;
								break;
							}
							parent = parent.from;
						}
						if (unseen && !l2.equals(board))
							leafs.enqueue(new SearchNode(l2, steps + 2));
					}
					neis.enqueue(new SearchNode(l1, steps + 1, leafs));
				}
			}
			return neis;
		}

		public int compareTo(SearchNode that)		{
			return Integer.compare(this.heuristic(), that.heuristic());
		}

		public String toString() {
			return board.toString();
		}

	}

    // test client (see below)
	public static void main(String[] args) {
		In in = new In(args[0]);
		int n = in.readInt();
		int tiles[][] = new int[n][n];

		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				tiles[i][j] = in.readInt();

		Solver solver = new Solver(new Board(tiles));
		if (!solver.isSolvable()) {
			StdOut.println("No solution possible");
		} else {
			StdOut.println("Minimum number of moves = " + solver.moves());
			for (Board board : solver.solution())
				StdOut.println(board);
		}
	}
}
