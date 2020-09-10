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

	private final class Node
		implements Comparable<Node>
	{
		private final Board board;
		private Node from = null;

		private final int steps;

		private Queue<Node> neis;

		private Node(Board board, int steps, Queue<Node> neis) {
			this.board = board; this.steps = steps; this.neis = neis;
		}

		public Node(Board board, int steps) {
			this.board = board; this.steps = steps; this.neis = null;
		}

	    public int heuristic()  {
			return board.manhattan() + steps;
		}

		public boolean isGoal() { return board.isGoal(); }

		public int moves()      { return steps; }

		public void setFrom(Node from) {
			if (this.from != null) {
				StdOut.println("Duplicated, old was ");
				StdOut.println(this.from);
				StdIn.readChar();
			}
			this.from = from;
		}

		public Queue<Node> neighbors() {
			if (neis == null) {
				neis = new Queue<Node>();
				for (Board l1 : board.neighbors()) {
					Queue<Node> leafs = new Queue<Node>();
					for (Board l2 : l1.neighbors()) {
						boolean unseen = true;
						Node parent = from;
						while (parent != null) {
							if (l2.equals(parent.board)) {
								unseen = false;
								break;
							}
							parent = parent.from;
						}
						if (unseen && !l2.equals(board))
							leafs.enqueue(new Node(l2, steps + 2));
					}
					neis.enqueue(new Node(l1, steps + 1, leafs));
				}
			}
			return neis;
		}

		public int compareTo(Node that)		{
			return Integer.compare(this.heuristic(), that.heuristic());
		}

		public String toString() {
			return board.toString();
		}

	}

	private Solver() { }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
		if (initial == null)
			throw new IllegalArgumentException("Argument cannot be null");
		MinPQ<Node> open = new MinPQ<Node>();
		Node current = new Node(initial, 0);

		open.insert(current);
		while (!open.isEmpty()) {
			current = open.delMin();
			if (current.isGoal()) {
				solus = solutionBoards(current);
				solved = true;
				break;
			}
			for (Node nei : current.neighbors()) {
				if (nei.neighbors().isEmpty())
					continue;
				nei.setFrom(current);
				open.insert(nei);
			}
		}
	}

	private Stack<Board> solutionBoards(Node current) {
		Stack<Board> sol = new Stack<Board>();

		while (current.from != null) {
			sol.push(current.board);
			current = current.from;
		}
		sol.push(current.board);
		return sol;
	}

    // is the initial board solvable? (see below)
    public boolean isSolvable()			{ return solved; }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves()					{ return solus.size() - 1; }

	// sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution()	{ return solved ? solus : null; }

	private static Solver prepareSolver(String s) {
		In in = new In(s);
		int k = 0, n = in.readInt(),  tiles[][] = new int[n][n], tmp[] = new int[n*n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				tiles[i][j] = in.readInt();
				tmp[k++] = tiles[i][j];
			}
		}

		int invers = 0, asc = 0, desc = 0;

		for (int i = 0; i < k; ++i) {
			if (i > 0) {
				if (tmp[i] < tmp[i-1]) desc++;
				else asc++;
			}
			for (int j = i+1; j < k; ++j) {
				if (tmp[j] > tmp[i] && tmp[i] != 0 && tmp[j] != 0)
					invers++;
			}
		}

		return  invers % 2 == 1 || asc == 0 ? new Solver() : new Solver(new Board(tiles));
	}

    // test client (see below)
	public static void main(String[] args) {
		Solver solver = prepareSolver(args[0]);
		if (!solver.isSolvable()) {
			StdOut.println("No solution possible");
		} else {
			StdOut.println("Minimum number of moves = " + solver.moves());
			for (Board board : solver.solution())
				StdOut.println(board);
		}
	}
}
