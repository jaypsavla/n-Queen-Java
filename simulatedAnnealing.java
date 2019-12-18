import java.util.Random;

public class simulatedAnnealing {
	public static final int nQUEEN = 25;
	public static final double e = 2.71828;
	public static final int[] testCase = new int[] {4,7,1,3,8,9,5,14,18,1,22,23,6,19,14,18,6,24,20,17,11,3,8,14,21};
	public static int numberOfSolutions = 0, numberOfTests = 10000;
	public static long start, end, total = 0;
	public static int steps = 0, totalSteps = 0;
	//Generates a random state
	public static int[] randomState() {
		int[] state = new int[nQUEEN];
		for (int i = 0; i < nQUEEN; ++i) {
			state[i] = new Random().nextInt(nQUEEN);
		}
		return state;
	}
	
	public static Node simulatedAnnealing(Node node) {
		Node current = node;
		for (int t = 1; t < Integer.MAX_VALUE; ++t) {
			double temperature = schedule(t);
//			System.out.println("Temperature = " + temperature);
//			System.out.println("Cost of board = " + current.costOfBoard);
//			System.out.println();
			if (current.costOfBoard == 0) {
				System.out.println("Solution: ");
				printArray(current.currentState);
				numberOfSolutions++;
				return current;
			}
			if (temperature <= 0) {
				System.out.println("Failure: ");
				printArray(current.currentState);
				return current;
			}
			int[] nextState = copyArray(current.currentState);
			nextState[new Random().nextInt(nQUEEN)] = new Random().nextInt(nQUEEN);
			Node next = new Node(nextState);
			int changeInValue = current.costOfBoard - next.costOfBoard;
			if (changeInValue >= 0) {
				//System.out.println("Change in value = " + changeInValue);
				current = next;
				steps++;
			}
			else {
				double probabilityOfNext = Math.exp(changeInValue/temperature);
				//System.out.println("Probability of next = " + probabilityOfNext);
				if (new Random().nextDouble() <= probabilityOfNext) {
					//System.out.println("Probability of next = " + probabilityOfNext);
					current = next;
					steps++;
				}
			}
		}
//		System.out.println("Number of steps: " + steps);
//		printArray(current.currentState);
		return current;
	}
	
	/*
	 * Math.log(t*t + 3*t)/Math.log(3) = 100, 72, 41403
	 * Math.log(2*t*t - 90000*t) = 100, 12, 430
	 */
	
	public static double schedule(int t) {
		//return 24 - .00002*t; // 1599 ms
		return 24 - (Math.log(2*t*t - 92000*t + 450000)); 
	}
	
	public static int[] copyArray(int[] array) {
		int[] copy = new int[array.length];
		int i = 0;
		for (int value: array) {
			copy[i++] = value;
		}
		return copy;
	}
	
	public static void main(String[] args) {
		singleTrial();
		//nTrials();
		
	
	}
	
	public static void singleTrial() {
		start = System.nanoTime();
		int[] initialState = randomState();
		System.out.println("Initial State:");
		printArray(initialState);
		simulatedAnnealing(new Node(initialState));
		//simulatedAnnealing(new Node(testCase));
		end = System.nanoTime();
		System.out.println("Time taken: " + (end - start)/1000000 + " ms");
		System.out.println("Search cost = " + steps);
	}
	
	public static void nTrials() {
		for (int i = 0; i < numberOfTests; ++i) {
			steps = 0;
			start = System.nanoTime();
			simulatedAnnealing(new Node(randomState()));
			end = System.nanoTime();
			total += (end - start);
			totalSteps += steps;
		}
		
		System.out.println("Solution Rate: " + ((numberOfSolutions/numberOfTests)*100) + "% on " + numberOfTests + " tests");
		
		System.out.println("Average time taken: " + (total)/1000000/numberOfTests + " ms");
		System.out.println("Average search cost = " + totalSteps/numberOfTests);	
	}
	
	public static void printArray(int[] arr) {
		for (int i = 0; i < arr.length; ++i) {
			System.out.print(arr[i] + " ");
		}
		System.out.println();
	}
}