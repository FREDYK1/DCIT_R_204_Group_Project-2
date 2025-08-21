package main.models;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a route from one location to another
 */
public class Route {
    private List<Node> path;
    private List<Edge> edges;
    private double totalDistance;
    private int totalTravelTime;
    private String routeName;
    private List<Node> landmarksOnRoute;
    private double estimatedCost;
    
    public Route() {
        this.path = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.landmarksOnRoute = new ArrayList<>();
        this.totalDistance = 0.0;
        this.totalTravelTime = 0;
        this.estimatedCost = 0.0;
    }
    
    public Route(String routeName) {
        this();
        this.routeName = routeName;
    }
    
    /**
     * Add a node to the route path
     */
    public void addNode(Node node) {
        path.add(node);
        if (node.isLandmark()) {
            landmarksOnRoute.add(node);
        }
    }
    
    /**
     * Add an edge to the route
     */
    public void addEdge(Edge edge) {
        edges.add(edge);
        totalDistance += edge.getDistance();
        totalTravelTime += edge.getTravelTime();
        updateEstimatedCost();
    }
    
    /**
     * Calculate route from edges
     */
    public void calculateFromEdges() {
        if (edges.isEmpty()) return;
        
        path.clear();
        landmarksOnRoute.clear();
        totalDistance = 0.0;
        totalTravelTime = 0;
        
        // Add first node
        addNode(edges.get(0).getSource());
        
        // Add subsequent nodes and calculate totals
        for (Edge edge : edges) {
            addNode(edge.getDestination());
            totalDistance += edge.getDistance();
            totalTravelTime += edge.getTravelTime();
        }
        
        updateEstimatedCost();
    }
    
    /**
     * Update estimated cost based on distance and time
     */
    private void updateEstimatedCost() {
        // Simple cost function: weighted sum of distance and time
        estimatedCost = (totalDistance * 0.001) + (totalTravelTime * 0.1);
    }
    
    /**
     * Get the starting node
     */
    public Node getStart() {
        return path.isEmpty() ? null : path.get(0);
    }
    
    /**
     * Get the ending node
     */
    public Node getEnd() {
        return path.isEmpty() ? null : path.get(path.size() - 1);
    }
    
    /**
     * Check if route passes through a landmark
     */
    public boolean passesThrough(String landmarkName) {
        return landmarksOnRoute.stream()
                              .anyMatch(landmark -> landmark.getName().toLowerCase()
                                                          .contains(landmarkName.toLowerCase()));
    }
    
    /**
     * Get formatted route description
     */
    public String getRouteDescription() {
        if (path.size() < 2) {
            return "Invalid route";
        }
        
        StringBuilder description = new StringBuilder();
        description.append(String.format("Route: %s → %s\n", 
                                        getStart().getName(), getEnd().getName()));
    description.append(String.format("Distance: %.1f meters\n", totalDistance));
        
        if (!landmarksOnRoute.isEmpty()) {
            description.append("Landmarks: ");
            for (int i = 0; i < landmarksOnRoute.size(); i++) {
                if (i > 0) description.append(", ");
                description.append(landmarksOnRoute.get(i).getName());
            }
            description.append("\n");
        }
        
        return description.toString();
    }
    
    /**
     * Get step-by-step directions
     */
    public List<String> getDirections() {
        List<String> directions = new ArrayList<>();
        
        if (edges.isEmpty()) {
            directions.add("No route available");
            return directions;
        }
        
        directions.add(String.format("Start at %s", edges.get(0).getSource().getName()));
        
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            directions.add(String.format("%d. Go %.0fm to %s", 
                                       i + 1, edge.getDistance(), 
                                       edge.getDestination().getName()));
        }
        
        directions.add(String.format("Arrive at %s", getEnd().getName()));
        
        return directions;
    }
    
    // Getters and setters
    public List<Node> getPath() { return path; }
    public void setPath(List<Node> path) { this.path = path; }
    
    public List<Edge> getEdges() { return edges; }
    public void setEdges(List<Edge> edges) { this.edges = edges; }
    
    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }
    
    public int getTotalTravelTime() { return totalTravelTime; }
    public void setTotalTravelTime(int totalTravelTime) { this.totalTravelTime = totalTravelTime; }
    
    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }
    
    public List<Node> getLandmarksOnRoute() { return landmarksOnRoute; }
    public void setLandmarksOnRoute(List<Node> landmarksOnRoute) { this.landmarksOnRoute = landmarksOnRoute; }
    
    public double getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(double estimatedCost) { this.estimatedCost = estimatedCost; }
    
    @Override
    public String toString() {
    return String.format("Route{%s, %.1fm, landmarks=%d}", 
               routeName != null ? routeName : "Unnamed", 
               totalDistance, landmarksOnRoute.size());
    }
}
