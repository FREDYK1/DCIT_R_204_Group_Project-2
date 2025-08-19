package main.algorithms.optimization;


/**
 * Vogel Approximation Method for Transportation Optimization
 * This algorithm is used to find an initial basic feasible solution for transportation problems
 * In the context of UG Navigate, it can be used to optimize route distribution and resource allocation
 */
public class VogelApproximation {
    
    /**
     * Represents a transportation problem cell
     */
    public static class Cell {
        public int row, col;
        public double cost;
        public double allocation;
        
        public Cell(int row, int col, double cost) {
            this.row = row;
            this.col = col;
            this.cost = cost;
            this.allocation = 0;
        }
    }
    
    /**
     * Solve transportation problem using Vogel Approximation Method
     * @param costs Cost matrix
     * @param supply Supply array
     * @param demand Demand array
     * @return Solution matrix with allocations
     */
    public static double[][] solve(double[][] costs, double[] supply, double[] demand) {
        int rows = costs.length;
        int cols = costs[0].length;
        
        // Create working copies
        double[][] solution = new double[rows][cols];
        double[] remainingSupply = supply.clone();
        double[] remainingDemand = demand.clone();
        
        // Continue until all supply and demand are satisfied
        while (hasRemainingSupply(remainingSupply) && hasRemainingDemand(remainingDemand)) {
            // Find the cell with maximum penalty
            Cell maxPenaltyCell = findMaxPenaltyCell(costs, remainingSupply, remainingDemand);
            
            if (maxPenaltyCell == null) break;
            
            // Allocate as much as possible
            double allocation = Math.min(remainingSupply[maxPenaltyCell.row], 
                                       remainingDemand[maxPenaltyCell.col]);
            
            solution[maxPenaltyCell.row][maxPenaltyCell.col] = allocation;
            remainingSupply[maxPenaltyCell.row] -= allocation;
            remainingDemand[maxPenaltyCell.col] -= allocation;
        }
        
        return solution;
    }
    
    /**
     * Find the cell with maximum penalty using Vogel's method
     */
    private static Cell findMaxPenaltyCell(double[][] costs, double[] supply, double[] demand) {
        int rows = costs.length;
        int cols = costs[0].length;
        
        double maxPenalty = -1;
        Cell maxPenaltyCell = null;
        
        // Calculate penalties for each row and column
        for (int i = 0; i < rows; i++) {
            if (supply[i] <= 0) continue;
            
            for (int j = 0; j < cols; j++) {
                if (demand[j] <= 0) continue;
                
                double penalty = calculatePenalty(costs, supply, demand, i, j);
                
                if (penalty > maxPenalty) {
                    maxPenalty = penalty;
                    maxPenaltyCell = new Cell(i, j, costs[i][j]);
                }
            }
        }
        
        return maxPenaltyCell;
    }
    
    /**
     * Calculate penalty for a specific cell
     */
    private static double calculatePenalty(double[][] costs, double[] supply, double[] demand, int row, int col) {
        int rows = costs.length;
        int cols = costs[0].length;
        
        // Find minimum costs in row and column (excluding current cell)
        double minRowCost = Double.MAX_VALUE;
        double secondMinRowCost = Double.MAX_VALUE;
        
        for (int j = 0; j < cols; j++) {
            if (j != col && demand[j] > 0) {
                if (costs[row][j] < minRowCost) {
                    secondMinRowCost = minRowCost;
                    minRowCost = costs[row][j];
                } else if (costs[row][j] < secondMinRowCost && costs[row][j] != minRowCost) {
                    secondMinRowCost = costs[row][j];
                }
            }
        }
        
        double minColCost = Double.MAX_VALUE;
        double secondMinColCost = Double.MAX_VALUE;
        
        for (int i = 0; i < rows; i++) {
            if (i != row && supply[i] > 0) {
                if (costs[i][col] < minColCost) {
                    secondMinColCost = minColCost;
                    minColCost = costs[i][col];
                } else if (costs[i][col] < secondMinColCost && costs[i][col] != minColCost) {
                    secondMinColCost = costs[i][col];
                }
            }
        }
        
        // Calculate penalties
        double rowPenalty = (secondMinRowCost == Double.MAX_VALUE) ? 0 : 
                           (secondMinRowCost - minRowCost);
        double colPenalty = (secondMinColCost == Double.MAX_VALUE) ? 0 : 
                           (secondMinColCost - minColCost);
        
        // Return the maximum penalty
        return Math.max(rowPenalty, colPenalty);
    }
    
    /**
     * Check if there's remaining supply
     */
    private static boolean hasRemainingSupply(double[] supply) {
        for (double s : supply) {
            if (s > 0) return true;
        }
        return false;
    }
    
    /**
     * Check if there's remaining demand
     */
    private static boolean hasRemainingDemand(double[] demand) {
        for (double d : demand) {
            if (d > 0) return true;
        }
        return false;
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
        System.out.println("Vogel Approximation Solution:");
        System.out.println("==============================");
        
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
     * Example usage for route optimization
     */
    public static void optimizeRouteDistribution() {
        // Example: Optimizing route distribution between different campus areas
        // Costs represent travel time between areas
        double[][] costs = {
            {2, 4, 3, 5},  // From Area 1 to Areas 1,2,3,4
            {4, 2, 5, 3},  // From Area 2 to Areas 1,2,3,4
            {3, 5, 2, 4},  // From Area 3 to Areas 1,2,3,4
            {5, 3, 4, 2}   // From Area 4 to Areas 1,2,3,4
        };
        
        double[] supply = {100, 150, 200, 120}; // Available capacity from each area
        double[] demand = {180, 160, 140, 90};  // Required capacity to each area
        
        System.out.println("Optimizing route distribution using Vogel Approximation Method...");
        double[][] solution = solve(costs, supply, demand);
        printSolution(solution, costs);
    }
}

