import java.util.Arrays;

public class Node {
	public final int nQUEENS = 25;
	public final int BOARD_SIZE = nQUEENS * nQUEENS;
	public int[] board;
	public int[] currentState;
	public int maxCost;
	public int maxCostIndex;
	public int costOfBoard;
	public int [] costOfQueens;
	
	public Node(int[] initial) {
		this.currentState = new int[initial.length];
		for (int i = 0; i < initial.length; ++i) {
			this.currentState[i]  = initial[i];
		}
		this.costOfBoard = getCostOfBoard(this.currentState);
	}
	

	public int getCostOfBoard(int[] array) {
		int cost = 0;
		for (int i = 0; i < nQUEENS - 1; ++i) {
			for (int j = i + 1; j < nQUEENS; ++j) {
				if (array[i] == array[j] || j - i == Math.abs(array[j] - array[i])) {
					cost++;
				}
			}
		}
		return cost;
	}
}
