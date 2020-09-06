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

	    // SymbolTable<Board, Board> from = new SymbolTable<Board, Board>();

		MinPQ<Node> open = new MinPQ<Node>();
		Node current = new Node(initial, 0);
		open.insert(current);

		while (!open.isEmpty()) {
			current = open.delMin();
//
			// openStats(open);

			// StdOut.println(open.size() + " are currently open ");
			// StdOut.println(current.steps + " Steps. " + current);
			// if (current.board.equals(initial) && current.steps != 0) {
			// 	StdOut.println(" Loopback: ");
			// 	StdIn.readChar();
			// }

			// if (current.board.equals(initial) && current.steps != 0) {
			// 	StdOut.println(open.size() + " Loopback: " + current.steps);
			// 	// StdOut.println(current);
			// 	StdIn.readChar();
			// }

			// StdOut.println(open.size());

			if (current.isGoal()) {
				// StdOut.println("Goal Reached!!");
				solus = solutionBoards(current);
				solved = true;
				break;
			}
// 1 2 3
// 4 5 6
// 7 8 0
			// StdOut.println(" Has " + current.neighbors().size() + " //////////// ") ;
			for (Node nei : current.neighbors()) {
				// Queue<Node> tmp = new Queue<Node>();
				// StdOut.println("Neighbor -> " + nei);
				// for (Node nei2 : nei.neighbors()) {
				// 	if (nei2.equals(current))
				// 		continue;
				// 	tmp.enqueue(nei2);
				// }
				// nei.neis = tmp;
				// bind(from, nei, current);
				// for (Node e : open)
				// 	if (nei.equals(e))
				// 		continue;

				if (nei.neighbors().isEmpty())
					continue;
				open.insert(nei);
				nei.setFrom(current);
			}
			// StdOut.println(" ////// ");
			// StdIn.readChar();
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

    // test client (see below)
	public static void main(String[] args) {

		// create initial board from file
		In in = new In(args[0]);
		StdOut.printf("%s: ", args[0]);
		int n = in.readInt();
		int[][] tiles = new int[n][n];
		int[] tmp = new int[n*n];
		int k = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				tiles[i][j] = in.readInt();
				tmp[k++] = tiles[i][j];
			}
		}

		int invers = 0;
		int asc = 0;
		int desc = 0;

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

		Board initial = new Board(tiles);

		// StdOut.println("IN: " + invers + " " + asc + " " + desc) ;

		// solve the puzzle
		Solver solver = invers % 2 == 1 || asc == 0 ? new Solver() : new Solver(initial);;

		// print solution to standard output
		if (!solver.isSolvable()) {
			StdOut.println("No solution possible");
		} else {
			StdOut.println("Minimum number of moves = " + solver.moves());
			for (Board board : solver.solution())
				StdOut.println(board);
		}
	}
}
