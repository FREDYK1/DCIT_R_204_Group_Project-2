package main.services;

import main.models.*;
import main.algorithms.pathfinding.*;
import main.algorithms.sorting.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for route-related operations
 * Provides high-level routing functionality for the UG Navigate system
 */
public class RouteService {
    private Graph campusGraph;
    private LandmarkService landmarkService;
    private TrafficService trafficService;
    
    public RouteService() {
        this.campusGraph = new Graph();
        this.landmarkService = new LandmarkService();
        this.trafficService = new TrafficService();
        initializeCampusData();
    }
    
    /**
     * Find the best route between two locations
     * @param sourceId Starting location ID
     * @param destinationId Destination location ID
     * @param algorithm Algorithm to use ("dijkstra", "astar", "floyd")
     * @return Best route or null if no route exists
     */
    public Route findBestRoute(String sourceId, String destinationId, String algorithm) {
        if (sourceId == null || destinationId == null) {
            return null;
        }
        
        algorithm = algorithm.toLowerCase();
        
        switch (algorithm) {
            case "dijkstra":
                return DijkstraAlgorithm.findShortestPath(campusGraph, sourceId, destinationId);
            case "astar":
                return AStarAlgorithm.findOptimalPath(campusGraph, sourceId, destinationId);
            case "floyd":
                return FloydWarshallAlgorithm.getShortestPath(campusGraph, sourceId, destinationId);
            default:
                // Default to Dijkstra if algorithm not recognized
                return DijkstraAlgorithm.findShortestPath(campusGraph, sourceId, destinationId);
        }
    }
    
    /**
     * Find multiple route options between two locations
     * @param sourceId Starting location ID
     * @param destinationId Destination location ID
     * @param maxRoutes Maximum number of routes to return
     * @return List of alternative routes sorted by preference
     */
    public List<Route> findMultipleRoutes(String sourceId, String destinationId, int maxRoutes) {
        List<Route> routes = new ArrayList<>();
        
        // Get routes from different algorithms
        Route dijkstraRoute = DijkstraAlgorithm.findShortestPath(campusGraph, sourceId, destinationId);
        Route astarRoute = AStarAlgorithm.findOptimalPath(campusGraph, sourceId, destinationId);
        Route floydRoute = FloydWarshallAlgorithm.getShortestPath(campusGraph, sourceId, destinationId);
        
        // Add valid routes
        if (dijkstraRoute != null) {
            dijkstraRoute.setRouteName("Shortest Distance Route");
            routes.add(dijkstraRoute);
        }
        
        if (astarRoute != null && !isDuplicateRoute(astarRoute, routes)) {
            astarRoute.setRouteName("Optimal Route (A*)");
            routes.add(astarRoute);
        }
        
        if (floydRoute != null && !isDuplicateRoute(floydRoute, routes)) {
            floydRoute.setRouteName("Alternative Route");
            routes.add(floydRoute);
        }
        
        // Try to find alternative routes through different intermediate nodes
        List<Node> landmarks = campusGraph.getLandmarks();
        for (Node landmark : landmarks) {
            if (routes.size() >= maxRoutes) break;
            
            Route routeViaLandmark = findRouteViaIntermediate(sourceId, destinationId, landmark.getId());
            if (routeViaLandmark != null && !isDuplicateRoute(routeViaLandmark, routes)) {
                routeViaLandmark.setRouteName("Route via " + landmark.getName());
                routes.add(routeViaLandmark);
            }
        }
        
        // Sort routes by multiple criteria
        routes = MergeSort.sortByMultipleCriteria(routes);
        
        // Return up to maxRoutes
        return routes.stream().limit(maxRoutes).collect(Collectors.toList());
    }
    
    /**
     * Find routes that pass through or near specific landmarks
     * @param sourceId Starting location ID
     * @param destinationId Destination location ID
     * @param landmarkKeyword Keyword to search for landmarks
     * @return List of routes passing through matching landmarks
     */
    public List<Route> findRoutesByLandmark(String sourceId, String destinationId, String landmarkKeyword) {
        List<Route> routes = new ArrayList<>();
        List<Landmark> matchingLandmarks = landmarkService.searchLandmarks(landmarkKeyword);
        
        for (Landmark landmark : matchingLandmarks) {
            Route route = findRouteViaIntermediate(sourceId, destinationId, landmark.getLocation().getId());
            if (route != null) {
                route.setRouteName("Route via " + landmark.getName());
                routes.add(route);
            }
        }
        
        // Sort by preference (shorter routes with relevant landmarks preferred)
        routes = MergeSort.sortByPreferenceScore(routes, 0.4, 0.3, 0.3);
        
        return routes;
    }
    
    /**
     * Find route via an intermediate point
     * @param sourceId Starting location ID
     * @param destinationId Destination location ID
     * @param intermediateId Intermediate point ID
     * @return Combined route or null if not possible
     */
    public Route findRouteViaIntermediate(String sourceId, String destinationId, String intermediateId) {
        Route firstLeg = DijkstraAlgorithm.findShortestPath(campusGraph, sourceId, intermediateId);
        Route secondLeg = DijkstraAlgorithm.findShortestPath(campusGraph, intermediateId, destinationId);
        
        if (firstLeg == null || secondLeg == null) {
            return null;
        }
        
        return combineRoutes(firstLeg, secondLeg);
    }
    
    /**
     * Get all routes from a source to multiple destinations
     * @param sourceId Starting location ID
     * @param destinationIds List of destination IDs
     * @return Map of destination -> best route
     */
    public Map<String, Route> findRoutesToMultipleDestinations(String sourceId, List<String> destinationIds) {
        Map<String, Route> routes = new HashMap<>();
        
        // Use Dijkstra's all-paths method for efficiency
        Map<String, Route> allPaths = DijkstraAlgorithm.findAllShortestPaths(campusGraph, sourceId);
        
        for (String destinationId : destinationIds) {
            Route route = allPaths.get(destinationId);
            if (route != null) {
                routes.put(destinationId, route);
            }
        }
        
        return routes;
    }
    
    /**
     * Sort routes by different criteria
     * @param routes List of routes to sort
     * @param criteria Sorting criteria ("distance", "time", "cost", "landmarks")
     * @param algorithm Sorting algorithm ("quick", "merge")
     * @return Sorted list of routes
     */
    public List<Route> sortRoutes(List<Route> routes, String criteria, String algorithm) {
        if (routes == null || routes.isEmpty()) {
            return new ArrayList<>();
        }
        
        boolean useQuickSort = "quick".equalsIgnoreCase(algorithm);
        
        switch (criteria.toLowerCase()) {
            case "distance":
                return useQuickSort ? QuickSort.sortByDistance(routes) : MergeSort.sortByDistance(routes);
            case "time":
                return useQuickSort ? QuickSort.sortByTravelTime(routes) : MergeSort.sortByTravelTime(routes);
            case "cost":
                return useQuickSort ? QuickSort.sortByCost(routes) : MergeSort.sortByCost(routes);
            case "landmarks":
                return QuickSort.sortByLandmarkCount(routes);
            default:
                return useQuickSort ? QuickSort.sortByDistance(routes) : MergeSort.sortByDistance(routes);
        }
    }
    
    /**
     * Get route statistics and analysis
     * @param routes List of routes to analyze
     * @return Map containing various statistics
     */
    public Map<String, Object> analyzeRoutes(List<Route> routes) {
        Map<String, Object> stats = new HashMap<>();
        
        if (routes == null || routes.isEmpty()) {
            stats.put("count", 0);
            return stats;
        }
        
        double totalDistance = routes.stream().mapToDouble(Route::getTotalDistance).sum();
        double avgDistance = totalDistance / routes.size();
        double minDistance = routes.stream().mapToDouble(Route::getTotalDistance).min().orElse(0);
        double maxDistance = routes.stream().mapToDouble(Route::getTotalDistance).max().orElse(0);
        
        int totalTime = routes.stream().mapToInt(Route::getTotalTravelTime).sum();
        double avgTime = (double) totalTime / routes.size();
        int minTime = routes.stream().mapToInt(Route::getTotalTravelTime).min().orElse(0);
        int maxTime = routes.stream().mapToInt(Route::getTotalTravelTime).max().orElse(0);
        
        long routesWithLandmarks = routes.stream().filter(r -> !r.getLandmarksOnRoute().isEmpty()).count();
        
        stats.put("count", routes.size());
        stats.put("totalDistance", totalDistance);
        stats.put("averageDistance", avgDistance);
        stats.put("minDistance", minDistance);
        stats.put("maxDistance", maxDistance);
        stats.put("totalTime", totalTime);
        stats.put("averageTime", avgTime);
        stats.put("minTime", minTime);
        stats.put("maxTime", maxTime);
        stats.put("routesWithLandmarks", routesWithLandmarks);
        
        return stats;
    }
    
    // Helper methods
    
    private boolean isDuplicateRoute(Route newRoute, List<Route> existingRoutes) {
        for (Route existing : existingRoutes) {
            if (routesAreEqual(newRoute, existing)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean routesAreEqual(Route route1, Route route2) {
        if (route1.getPath().size() != route2.getPath().size()) {
            return false;
        }
        
        for (int i = 0; i < route1.getPath().size(); i++) {
            if (!route1.getPath().get(i).getId().equals(route2.getPath().get(i).getId())) {
                return false;
            }
        }
        
        return true;
    }
    
    private Route combineRoutes(Route firstRoute, Route secondRoute) {
        Route combinedRoute = new Route("Combined Route");
        
        // Add all nodes from first route
        for (Node node : firstRoute.getPath()) {
            combinedRoute.addNode(node);
        }
        
        // Add all edges from first route
        for (Edge edge : firstRoute.getEdges()) {
            combinedRoute.addEdge(edge);
        }
        
        // Add nodes from second route (skip first node as it's duplicate)
        List<Node> secondPath = secondRoute.getPath();
        for (int i = 1; i < secondPath.size(); i++) {
            combinedRoute.addNode(secondPath.get(i));
        }
        
        // Add edges from second route
        for (Edge edge : secondRoute.getEdges()) {
            combinedRoute.addEdge(edge);
        }
        
        return combinedRoute;
    }
    
    private void initializeCampusData() {
        // Initialize with sample UG campus data
        // This would typically load from a data file
        createSampleCampusGraph();
    }
    
    private void createSampleCampusGraph() {
        // Sample UG campus locations
        Node mainGate = new Node("main_gate", "Main Gate", 5.6508, -0.1870, "University entrance", true);
        Node greatHall = new Node("great_hall", "Great Hall", 5.6520, -0.1850, "Main assembly hall", true);
        Node library = new Node("library", "Balme Library", 5.6525, -0.1845, "Main university library", true);
        Node compSci = new Node("comp_sci", "Computer Science Department", 5.6530, -0.1840, "DCIT Department", true);
        Node nightMarket = new Node("night_market", "Night Market", 5.6515, -0.1860, "Food and shopping area", true);
        Node commonwealth = new Node("commonwealth", "Commonwealth Hall", 5.6540, -0.1820, "Residential hall", true);
        Node legon = new Node("legon", "Legon Hall", 5.6545, -0.1825, "Residential hall", true);
        Node sportsComplex = new Node("sports", "Sports Complex", 5.6550, -0.1830, "Sports facilities", true);
        
        // Add nodes to graph
        campusGraph.addNode(mainGate);
        campusGraph.addNode(greatHall);
        campusGraph.addNode(library);
        campusGraph.addNode(compSci);
        campusGraph.addNode(nightMarket);
        campusGraph.addNode(commonwealth);
        campusGraph.addNode(legon);
        campusGraph.addNode(sportsComplex);
        
        // Add edges with realistic distances
        campusGraph.addEdge(new Edge(mainGate, greatHall, 300, 300, "walkway"));
        campusGraph.addEdge(new Edge(greatHall, library, 200, 200, "walkway"));
        campusGraph.addEdge(new Edge(library, compSci, 150, 150, "walkway"));
        campusGraph.addEdge(new Edge(mainGate, nightMarket, 250, 250, "walkway"));
        campusGraph.addEdge(new Edge(nightMarket, greatHall, 200, 200, "walkway"));
        campusGraph.addEdge(new Edge(greatHall, commonwealth, 400, 400, "walkway"));
        campusGraph.addEdge(new Edge(commonwealth, legon, 100, 100, "walkway"));
        campusGraph.addEdge(new Edge(legon, sportsComplex, 200, 200, "walkway"));
        campusGraph.addEdge(new Edge(compSci, sportsComplex, 300, 300, "walkway"));
        
        System.out.println("Sample campus graph created with " + campusGraph.getNodeCount() + 
                          " nodes and " + campusGraph.getEdgeCount() + " edges.");
    }
    
    // Getters
    public Graph getCampusGraph() { return campusGraph; }
    public LandmarkService getLandmarkService() { return landmarkService; }
    public TrafficService getTrafficService() { return trafficService; }
}
