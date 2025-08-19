package main.demo;

import main.algorithms.pathfinding.*;
import main.algorithms.sorting.*;
import main.algorithms.optimization.*;
import main.models.*;
import main.services.*;
import main.utils.*;

import java.util.*;

/**
 * Comprehensive Demo Application for UG Navigate
 * Demonstrates all implemented algorithms and features
 */
public class AlgorithmDemo {
    
    private static RouteService routeService;
    private static TrafficService trafficService;
    
    public static void main(String[] args) {
        System.out.println("=== UG Navigate: Algorithm Demonstration ===");
        System.out.println("University of Ghana Campus Navigation System");
        System.out.println("DCIT 204 - Data Structures and Algorithms 1\n");
        
        try {
            // Initialize services
            initializeServices();
            
            // Run comprehensive demonstrations
            demonstratePathfindingAlgorithms();
            demonstrateSortingAlgorithms();
            demonstrateOptimizationAlgorithms();
            demonstrateLandmarkSearch();
            demonstrateTrafficAnalysis();
            demonstrateRouteComparison();
            
            System.out.println("\n=== Demo Complete ===");
            System.out.println("All algorithms have been successfully demonstrated!");
            
        } catch (Exception e) {
            System.err.println("Error during demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize all services
     */
    private static void initializeServices() {
        System.out.println("Initializing services...");
        
        // Load campus data
        DataLoader.loadCampusData();
        
        // Initialize services
        routeService = new RouteService();
        trafficService = new TrafficService();
        System.out.println("✓ Services initialized successfully!\n");
    }
    
    /**
     * Demonstrate pathfinding algorithms
     */
    private static void demonstratePathfindingAlgorithms() {
        System.out.println("1. PATHFINDING ALGORITHMS DEMONSTRATION");
        System.out.println("=======================================");
        
        String sourceId = "main_gate";
        String destinationId = "comp_sci";
        
        System.out.println("Finding route from Main Gate to Computer Science Department...\n");
        
        // Dijkstra's Algorithm
        System.out.println("A) Dijkstra's Algorithm (Shortest Path):");
        System.out.println("----------------------------------------");
        Route dijkstraRoute = DijkstraAlgorithm.findShortestPath(
            routeService.getCampusGraph(), sourceId, destinationId);
        if (dijkstraRoute != null) {
            printRouteDetails(dijkstraRoute, "Dijkstra");
        }
        
        // A* Algorithm
        System.out.println("\nB) A* Algorithm (Optimal Path):");
        System.out.println("-------------------------------");
        Route astarRoute = AStarAlgorithm.findOptimalPath(
            routeService.getCampusGraph(), sourceId, destinationId);
        if (astarRoute != null) {
            printRouteDetails(astarRoute, "A*");
        }
        
        // Floyd-Warshall Algorithm
        System.out.println("\nC) Floyd-Warshall Algorithm (All Pairs):");
        System.out.println("----------------------------------------");
        Route floydRoute = FloydWarshallAlgorithm.getShortestPath(
            routeService.getCampusGraph(), sourceId, destinationId);
        if (floydRoute != null) {
            printRouteDetails(floydRoute, "Floyd-Warshall");
        }
        
        System.out.println();
    }
    
    /**
     * Demonstrate sorting algorithms
     */
    private static void demonstrateSortingAlgorithms() {
        System.out.println("2. SORTING ALGORITHMS DEMONSTRATION");
        System.out.println("===================================");
        
        // Create sample routes for sorting
        List<Route> routes = createSampleRoutes();
        
        System.out.println("Original routes (unsorted):");
        printRoutesList(routes);
        
        // Quick Sort by distance
        System.out.println("\nA) Quick Sort by Distance:");
        System.out.println("---------------------------");
        List<Route> quickSorted = QuickSort.sortByDistance(new ArrayList<>(routes));
        printRoutesList(quickSorted);
        
        // Merge Sort by time
        System.out.println("\nB) Merge Sort by Time:");
        System.out.println("----------------------");
        List<Route> mergeSorted = MergeSort.sortByTime(new ArrayList<>(routes));
        printRoutesList(mergeSorted);
        
        // Multi-criteria sorting
        System.out.println("\nC) Multi-Criteria Sorting (Distance + Time):");
        System.out.println("---------------------------------------------");
        List<Route> multiSorted = MergeSort.sortByMultipleCriteria(new ArrayList<>(routes));
        printRoutesList(multiSorted);
        
        System.out.println();
    }
    
    /**
     * Demonstrate optimization algorithms
     */
    private static void demonstrateOptimizationAlgorithms() {
        System.out.println("3. OPTIMIZATION ALGORITHMS DEMONSTRATION");
        System.out.println("=========================================");
        
        // Vogel Approximation Method
        System.out.println("A) Vogel Approximation Method:");
        System.out.println("-------------------------------");
        VogelApproximation.optimizeRouteDistribution();
        
        // Northwest Corner Method
        System.out.println("\nB) Northwest Corner Method:");
        System.out.println("----------------------------");
        double[][] costs = {
            {2, 4, 3, 5},
            {4, 2, 5, 3},
            {3, 5, 2, 4},
            {5, 3, 4, 2}
        };
        double[] supply = {100, 150, 200, 120};
        double[] demand = {180, 160, 140, 90};
        
        double[][] nwSolution = NorthwestCorner.solve(costs, supply, demand);
        NorthwestCorner.printSolution(nwSolution, costs);
        
        // Compare methods
        System.out.println("\nC) Method Comparison:");
        System.out.println("---------------------");
        NorthwestCorner.compareMethods();
        
        // Critical Path Method
        System.out.println("\nD) Critical Path Method:");
        System.out.println("-------------------------");
        CriticalPathMethod.campusRoutePlanningExample();
        
        System.out.println();
    }
    
    /**
     * Demonstrate landmark search functionality
     */
    private static void demonstrateLandmarkSearch() {
        System.out.println("4. LANDMARK SEARCH DEMONSTRATION");
        System.out.println("=================================");
        
        String sourceId = "main_gate";
        String destinationId = "sports";
        
        System.out.println("Finding routes from Main Gate to Sports Complex via landmarks...\n");
        
        // Search by landmark category
        System.out.println("A) Routes via Academic Landmarks:");
        System.out.println("---------------------------------");
        List<Route> academicRoutes = routeService.findRoutesByLandmark(
            sourceId, destinationId, "academic");
        printRoutesList(academicRoutes);
        
        // Search by specific landmark
        System.out.println("\nB) Routes via Bank:");
        System.out.println("-------------------");
        List<Route> bankRoutes = routeService.findRoutesByLandmark(
            sourceId, destinationId, "bank");
        printRoutesList(bankRoutes);
        
        // Search by dining landmarks
        System.out.println("\nC) Routes via Dining Areas:");
        System.out.println("----------------------------");
        List<Route> diningRoutes = routeService.findRoutesByLandmark(
            sourceId, destinationId, "dining");
        printRoutesList(diningRoutes);
        
        System.out.println();
    }
    
    /**
     * Demonstrate traffic analysis
     */
    private static void demonstrateTrafficAnalysis() {
        System.out.println("5. TRAFFIC ANALYSIS DEMONSTRATION");
        System.out.println("=================================");
        
        String sourceId = "main_gate";
        String destinationId = "comp_sci";
        
        System.out.println("Analyzing traffic conditions for route planning...\n");
        
        // Get current traffic conditions
        System.out.println("A) Current Traffic Conditions:");
        System.out.println("-------------------------------");
        Map<String, Double> trafficConditions = trafficService.getCurrentTrafficMultipliers();
        for (Map.Entry<String, Double> entry : trafficConditions.entrySet()) {
            System.out.printf("Area: %s, Traffic Level: %.2f%%\n", 
                            entry.getKey(), entry.getValue() * 100);
        }
        
        // Calculate travel time with traffic
        System.out.println("\nB) Travel Time with Traffic Consideration:");
        System.out.println("-------------------------------------------");
        Route baseRoute = routeService.findBestRoute(sourceId, destinationId, "dijkstra");
        if (baseRoute != null) {
            int baseTime = baseRoute.getTotalTravelTime();
            Map<String, Object> trafficInfo = trafficService.getRouteTrafficInfo(baseRoute);
            double avgMultiplier = (Double) trafficInfo.get("averageTrafficMultiplier");
            int estimatedTrafficTime = (int) (baseTime * avgMultiplier);
            System.out.printf("Base travel time: %d minutes\n", baseTime);
            System.out.printf("Travel time with traffic: %d minutes\n", estimatedTrafficTime);
            System.out.printf("Traffic delay: %d minutes\n", estimatedTrafficTime - baseTime);
        }
        
        // Find optimal route considering traffic
        System.out.println("\nC) Optimal Route with Traffic:");
        System.out.println("-------------------------------");
        Route optimalRoute = routeService.findBestRoute(sourceId, destinationId, "astar");
        if (optimalRoute != null) {
            printRouteDetails(optimalRoute, "Traffic-Optimized");
        }
        
        System.out.println();
    }
    
    /**
     * Demonstrate route comparison
     */
    private static void demonstrateRouteComparison() {
        System.out.println("6. ROUTE COMPARISON DEMONSTRATION");
        System.out.println("=================================");
        
        String sourceId = "main_gate";
        String destinationId = "sports";
        
        System.out.println("Comparing multiple route options...\n");
        
        // Find multiple routes
        List<Route> routes = routeService.findMultipleRoutes(sourceId, destinationId, 5);
        
        System.out.println("Route Comparison Summary:");
        System.out.println("-------------------------");
        System.out.printf("%-25s %-12s %-12s %-15s\n", "Route Name", "Distance(m)", "Time(min)", "Landmarks");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            String landmarks = route.getLandmarksOnRoute().isEmpty() ? "None" : 
                             String.valueOf(route.getLandmarksOnRoute().size());
            System.out.printf("%-25s %-12.1f %-12d %-15s\n",
                            route.getRouteName(),
                            route.getTotalDistance(),
                            route.getTotalTravelTime(),
                            landmarks);
        }
        
        // Find best route by different criteria
        System.out.println("\nBest Routes by Criteria:");
        System.out.println("-----------------------");
        
        Route shortestDistance = routes.stream()
            .min((r1, r2) -> Double.compare(r1.getTotalDistance(), r2.getTotalDistance()))
            .orElse(null);
        
        Route fastestTime = routes.stream()
            .min((r1, r2) -> Integer.compare(r1.getTotalTravelTime(), r2.getTotalTravelTime()))
            .orElse(null);
        
        Route mostLandmarks = routes.stream()
            .max((r1, r2) -> Integer.compare(r1.getLandmarksOnRoute().size(), r2.getLandmarksOnRoute().size()))
            .orElse(null);
        
        if (shortestDistance != null) {
            System.out.println("Shortest Distance: " + shortestDistance.getRouteName());
        }
        if (fastestTime != null) {
            System.out.println("Fastest Time: " + fastestTime.getRouteName());
        }
        if (mostLandmarks != null) {
            System.out.println("Most Landmarks: " + mostLandmarks.getRouteName());
        }
        
        System.out.println();
    }
    
    /**
     * Create sample routes for demonstration
     */
    private static List<Route> createSampleRoutes() {
        List<Route> routes = new ArrayList<>();
        
        // Create sample routes with different characteristics
        Route route1 = new Route("Scenic Route");
        route1.setTotalDistance(1200.0);
        route1.setTotalTravelTime(25);
        
        Route route2 = new Route("Direct Route");
        route2.setTotalDistance(800.0);
        route2.setTotalTravelTime(15);
        
        Route route3 = new Route("Landmark Route");
        route3.setTotalDistance(1500.0);
        route3.setTotalTravelTime(30);
        
        Route route4 = new Route("Quick Route");
        route4.setTotalDistance(900.0);
        route4.setTotalTravelTime(18);
        
        routes.addAll(Arrays.asList(route1, route2, route3, route4));
        return routes;
    }
    
    /**
     * Print route details
     */
    private static void printRouteDetails(Route route, String algorithmName) {
        if (route == null) {
            System.out.println("No route found.");
            return;
        }
        
        System.out.printf("Algorithm: %s\n", algorithmName);
        System.out.printf("Distance: %.1f meters\n", route.getTotalDistance());
        System.out.printf("Time: %d minutes\n", route.getTotalTravelTime());
        System.out.printf("Landmarks: %d\n", route.getLandmarksOnRoute().size());
        
        if (!route.getLandmarksOnRoute().isEmpty()) {
            System.out.print("Landmarks: ");
            for (int i = 0; i < route.getLandmarksOnRoute().size(); i++) {
                if (i > 0) System.out.print(", ");
                System.out.print(route.getLandmarksOnRoute().get(i).getName());
            }
            System.out.println();
        }
    }
    
    /**
     * Print list of routes
     */
    private static void printRoutesList(List<Route> routes) {
        if (routes.isEmpty()) {
            System.out.println("No routes found.");
            return;
        }
        
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            System.out.printf("%d. %s - %.1fm, %dmin\n", 
                            i + 1, route.getRouteName(), 
                            route.getTotalDistance(), route.getTotalTravelTime());
        }
    }
}
