import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
/* Tests: popS, mutR, qualR = %Sol, avgT, avgC
 * 75, .78, .22 = 100, 166, 1160
 * 75, .7, .3 = 100, 177, 1385
 * 75, .9, .1 = 100, 212, 1645
 * 75, .9, .3 = 100, 203, 1292
 * 75, .9, .25 = 100, 146, 1075 
 * 75, .9, .23 = 100, 157, 1158
 * 75, .9, .24 = 100, 180, 1219
 * 75, .9, .27 = 100, 187, 1281
 * 75, .95, .25 = 100, 217, 1379
 * 75, .85, .25 = 100, 173, 1172
 * 75, .8, .25 = 100, 163, 1300
 * 85, .9, .25 = 100, 182, 1149
 * 100, ,9, .1 = 99, 280, 1302
 * 50, .9, .25 = 98, 393, 3176
 * 50, .75, .25 = 100, 181, 1608
 * 50, .8, .25 = 100, 99, 1396 ------> BEST TIME TAKEN (COULD NOT consistently replicate results on additional tests) 
 * 50, .8, .3 = 100, 151, 1807
 * 65, .8, .3 = 100, 154, 1345
 * 65, .8, .25 = 100, 167, 1441
 * 65, .77, .25 = 100, 155, 1429
 * 70, .9, .25 = 100, 148, 1241
 * 70, .85, .25 = 100, 166, 1306
 * 70, .92, .25 = 100, 156, 1319
 * 70, .88, .25 = 100, 137, 1161
 * 70, .875, .25 = 100, 143, 1170
 * 75, .88, .25 = 100, 119, 1020  ------> BEST TIME TAKEN (COULD consistently replicate results on additional tests)
 * 75, .875, .25 = 100, 127, 1071
 * 75, .88, .27 = 100, 171, 1305
 * 75, .88, .23 = 100, 159, 1209
 * 90, .85, .2 = 100, 168, 1169
 * 90, .75, .25 = 100, 205, 1343
 * 90, .8, .25 = 100, 153, 1075
 * 90, .8, .2 = 100, 163, 1065
 * 90, .88, .25 = 100, 174, 1174
 * 90, .83, .25 = 100, 165, 1090
 * 90, .83, .2 = 100, 136, 902 ----> BEST SEARCH COST (COULD consistently replicate results)
 * 90, .81, .2 = 100, 161, 1154
 * 90, .83, .22 = 100, 193, 1236
 * 90, .83, .18 = 100, 143, 972
 * 85, .83, .2 = 100, 166, 1201
 */

public class genetic {
	public static final int nQUEEN = 25;
	public static final int possibleAttacks = nQUEEN * (nQUEEN - 1);
	public static final double mutationRate = .88; 
	public static final double qualifyingRate = .25;
	public static final int populationSize = 75;
	public static final int numberOfTests = 100;
	public static int numberOfSolutions = 0;
	public static long start, end, total = 0;
	public static int steps = 0, totalSteps = 0;
	
	public static Comparator<Node> boardCost =  new Comparator<Node>() {
		public int compare(Node n1, Node n2) {
			return n1.costOfBoard - n2.costOfBoard; 
		}
	};
	
	// generates (size) random states
	public static ArrayList<Node> generatePopulation(int size) {
		ArrayList<Node> population = new ArrayList<>();
		for (int i = 0; i < size; ++i) {
			int[] state = new int[nQUEEN];
			for (int j = 0; j < nQUEEN; ++j) {
				state[j] = new Random().nextInt(nQUEEN);
			}
			population.add(new Node(state));
		}
		return population;
	}
	
	public static Node genetic(ArrayList<Node> initialPopulation) {
		Node solution = checkForFitness(initialPopulation);
		while (steps < 10000) {
			initialPopulation = generateNewPopulation(initialPopulation);
			solution = checkForFitness(initialPopulation);
			if (solution != null) {
				System.out.println("Solution state: ");
				printArray(solution.currentState);
				numberOfSolutions++;
				return solution;
			}
		}
		solution = initialPopulation.get(0);
		System.out.println("Final failed state: ");
		printArray(solution.currentState);
		return solution;
		
	}
	
	public static Node randomSelection(ArrayList<Node> population, double qualRate) {
		while(true) {
			Node child = population.get(new Random().nextInt((int)Math.ceil(population.size()*qualRate)));
			if (new Random().nextDouble() < 1 - (child.costOfBoard/possibleAttacks)) {
				return child;
			}
		}
	}
	
	public static Node reproduce(Node parent_1, Node parent_2) {
		int crossover = new Random().nextInt(nQUEEN);
		int[] childState = new int[parent_1.currentState.length];
		for(int i = 0; i < crossover; ++i) {
			childState[i] = parent_1.currentState[i];
		}
		for(int i = crossover; i < nQUEEN; ++i) {
			childState[i] = parent_2.currentState[i];
		}
		return new Node(childState);
	}
	
	public static ArrayList<Node> generateNewPopulation(ArrayList<Node> previousPopulation) {
		ArrayList<Node> newPopulation = new ArrayList<Node>();
		Collections.sort(previousPopulation, boardCost);
		for (int i = 0; i < previousPopulation.size(); ++i) {
			Node parent_1 = randomSelection(previousPopulation, qualifyingRate);
			Node parent_2 = randomSelection(previousPopulation, qualifyingRate);
			Node child = reproduce(parent_1, parent_2);
			mutate(child, mutationRate);
			//System.out.println("Fitness of this child is: " + child.costOfBoard);
			newPopulation.add(child);
		}
		steps++;
		return newPopulation;
	}
	
	public static Node mutate(Node node, double mutRate) {
		if (new Random().nextDouble() < mutRate) {
			node.currentState[new Random().nextInt(node.currentState.length)] = new Random().nextInt(nQUEEN);
		}
		return node;
	}
	
	public static Node checkForFitness(ArrayList<Node> population) {
		for(Node n: population) {
			if (n.costOfBoard == 0) {
				return n;
			}
			if (n.costOfBoard > 0) {
				return null;
			}
		}
		return null;
	}
	
	public static ArrayList<Node> replaceLists(ArrayList<Node> population, ArrayList<Node> newPopulation) {
		population.clear();
		for (Node n: newPopulation) {
			population.add(n);
		}
		return population;
	}
	
	public static void main(String[] args) {
			singleTrial();
			//nTrials();
	}
	
	public static void singleTrial() {
		start = System.nanoTime();
		genetic(generatePopulation(populationSize));
		end = System.nanoTime();
		System.out.println("Time taken: " + (end - start)/1000000 + " ms");
		System.out.println("Search cost = " + steps);
	}
	
	public static void nTrials() {
		for (int i = 0; i < numberOfTests; ++i) {
			steps = 0;
			start = System.nanoTime();
			genetic(generatePopulation(populationSize));
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
