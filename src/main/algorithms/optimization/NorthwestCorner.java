package main.algorithms.optimization;


/**
 * Northwest Corner Method for Transportation Optimization
 * A simple method to find initial basic feasible solution for transportation problems
 */
public class NorthwestCorner {
    
    /**
     * Solve transportation problem using Northwest Corner Method
     * @param costs Cost matrix
     * @param supply Supply array
     * @param demand Demand array
     * @return Solution matrix with allocations
     */
    public static double[][] solve(double[][] costs, double[] supply, double[] demand) {
        int rows = costs.length;
        int cols = costs[0].length;
        
        double[][] solution = new double[rows][cols];
        double[] remainingSupply = supply.clone();
        double[] remainingDemand = demand.clone();
        
        int i = 0, j = 0;
        
        // Start from northwest corner (top-left) and move southeast
        while (i < rows && j < cols) {
            if (remainingSupply[i] <= 0) {
                i++;
                continue;
            }
            
            if (remainingDemand[j] <= 0) {
                j++;
                continue;
            }
            
            // Allocate as much as possible
            double allocation = Math.min(remainingSupply[i], remainingDemand[j]);
            solution[i][j] = allocation;
            
            remainingSupply[i] -= allocation;
            remainingDemand[j] -= allocation;
            
            // Move to next position
            if (remainingSupply[i] == 0) {
                i++;
            }
            if (remainingDemand[j] == 0) {
                j++;
            }
        }
        
        return solution;
    }
    
    /**
     * Calculate total cost of the solution
     */
    public static double calculateTotalCost(double[][] solution, double[][] costs) {
        double totalCost = 0;
        for (int i = 0; i < solution.length; i++) {
            for (int j = 0; j < solution[0].length; j++) {
                totalCost += solution[i][j] * costs[i][j];
            }
        }
        return totalCost;
    }
    
    /**
     * Print the solution matrix
     */
    public static void printSolution(double[][] solution, double[][] costs) {
        System.out.println("Northwest Corner Method Solution:");
        System.out.println("=================================");
        
        for (int i = 0; i < solution.length; i++) {
            for (int j = 0; j < solution[0].length; j++) {
                if (solution[i][j] > 0) {
                    System.out.printf("Cell[%d][%d]: %.2f units (cost: %.2f)\n", 
                                    i, j, solution[i][j], costs[i][j]);
                }
            }
        }
        
        double totalCost = calculateTotalCost(solution, costs);
        System.out.printf("Total Cost: %.2f\n", totalCost);
    }
    
    /**
     * Compare Northwest Corner with Vogel Approximation
     */
    public static void compareMethods() {
        double[][] costs = {
            {2, 4, 3, 5},
            {4, 2, 5, 3},
            {3, 5, 2, 4},
            {5, 3, 4, 2}
        };
        
        double[] supply = {100, 150, 200, 120};
        double[] demand = {180, 160, 140, 90};
        
        System.out.println("Comparing Northwest Corner vs Vogel Approximation:");
        System.out.println("=================================================");
        
        // Northwest Corner solution
        double[][] nwSolution = solve(costs, supply, demand);
        double nwCost = calculateTotalCost(nwSolution, costs);
        
        // Vogel Approximation solution
        double[][] vogelSolution = VogelApproximation.solve(costs, supply, demand);
        double vogelCost = VogelApproximation.calculateTotalCost(vogelSolution, costs);
        
        System.out.printf("Northwest Corner Total Cost: %.2f\n", nwCost);
        System.out.printf("Vogel Approximation Total Cost: %.2f\n", vogelCost);
        System.out.printf("Improvement: %.2f%%\n", ((nwCost - vogelCost) / nwCost) * 100);
    }
}

