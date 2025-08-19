package main.algorithms.pathfinding;

import main.models.*;
import java.util.*;

/**
 * Implementation of A* (A-Star) Algorithm for pathfinding
 * Uses heuristic function to guide search towards the goal
 * Time Complexity: O(E) in best case, O(b^d) in worst case
 */
public class AStarAlgorithm {
    
    /**
     * Find optimal path using A* algorithm with Euclidean distance heuristic
     * @param graph The campus graph
     * @param sourceId Starting node ID
     * @param targetId Destination node ID
     * @return Route object containing the optimal path, or null if no path exists
     */
    public static Route findOptimalPath(Graph graph, String sourceId, String targetId) {
        Node source = graph.getNode(sourceId);
        Node target = graph.getNode(targetId);
        
        if (source == null || target == null) {
            return null;
        }
        
        if (sourceId.equals(targetId)) {
            Route route = new Route("A* Direct Route");
            route.addNode(source);
            return route;
        }
        
        // f(n) = g(n) + h(n) where g = cost from start, h = heuristic to goal
        Map<String, Double> gScore = new HashMap<>(); // Actual cost from start
        Map<String, Double> fScore = new HashMap<>(); // Estimated total cost
        Map<String, String> previous = new HashMap<>(); // For path reconstruction
        
        // Priority queue ordered by fScore
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>();
        Set<String> closedSet = new HashSet<>();
        Set<String> openSetNodes = new HashSet<>();
        
        // Initialize scores
        for (Node node : graph.getAllNodes()) {
            gScore.put(node.getId(), Double.MAX_VALUE);
            fScore.put(node.getId(), Double.MAX_VALUE);
        }
        
        gScore.put(sourceId, 0.0);
        double heuristic = calculateHeuristic(source, target);
        fScore.put(sourceId, heuristic);
        
        openSet.offer(new AStarNode(sourceId, fScore.get(sourceId)));
        openSetNodes.add(sourceId);
        
        while (!openSet.isEmpty()) {
            AStarNode current = openSet.poll();
            String currentId = current.nodeId;
            openSetNodes.remove(currentId);
            
            // Goal reached
            if (currentId.equals(targetId)) {
                return reconstructPath(graph, previous, sourceId, targetId);
            }
            
            closedSet.add(currentId);
            
            // Examine neighbors
            for (Edge edge : graph.getEdgesFrom(currentId)) {
                String neighborId = edge.getDestination().getId();
                
                if (closedSet.contains(neighborId)) {
                    continue; // Already processed
                }
                
                double tentativeGScore = gScore.get(currentId) + edge.getWeight();
                
                if (!openSetNodes.contains(neighborId)) {
                    openSetNodes.add(neighborId);
                } else if (tentativeGScore >= gScore.get(neighborId)) {
                    continue; // Not a better path
                }
                
                // This is the best path so far
                previous.put(neighborId, currentId);
                gScore.put(neighborId, tentativeGScore);
                
                double h = calculateHeuristic(edge.getDestination(), target);
                fScore.put(neighborId, tentativeGScore + h);
                
                openSet.offer(new AStarNode(neighborId, fScore.get(neighborId)));
            }
        }
        
        return null; // No path found
    }
    
    /**
     * Find path with custom heuristic weight
     * @param graph The campus graph
     * @param sourceId Starting node ID
     * @param targetId Destination node ID
     * @param heuristicWeight Weight for heuristic (1.0 = optimal, >1.0 = faster but suboptimal)
     * @return Route object containing the path
     */
    public static Route findPathWithHeuristic(Graph graph, String sourceId, String targetId, 
                                            double heuristicWeight) {
        Node source = graph.getNode(sourceId);
        Node target = graph.getNode(targetId);
        
        if (source == null || target == null) {
            return null;
        }
        
        if (sourceId.equals(targetId)) {
            Route route = new Route("A* Direct Route");
            route.addNode(source);
            return route;
        }
        
        Map<String, Double> gScore = new HashMap<>();
        Map<String, Double> fScore = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>();
        Set<String> closedSet = new HashSet<>();
        Set<String> openSetNodes = new HashSet<>();
        
        // Initialize
        for (Node node : graph.getAllNodes()) {
            gScore.put(node.getId(), Double.MAX_VALUE);
            fScore.put(node.getId(), Double.MAX_VALUE);
        }
        
        gScore.put(sourceId, 0.0);
        double heuristic = calculateHeuristic(source, target) * heuristicWeight;
        fScore.put(sourceId, heuristic);
        
        openSet.offer(new AStarNode(sourceId, fScore.get(sourceId)));
        openSetNodes.add(sourceId);
        
        while (!openSet.isEmpty()) {
            AStarNode current = openSet.poll();
            String currentId = current.nodeId;
            openSetNodes.remove(currentId);
            
            if (currentId.equals(targetId)) {
                return reconstructPath(graph, previous, sourceId, targetId);
            }
            
            closedSet.add(currentId);
            
            for (Edge edge : graph.getEdgesFrom(currentId)) {
                String neighborId = edge.getDestination().getId();
                
                if (closedSet.contains(neighborId)) {
                    continue;
                }
                
                double tentativeGScore = gScore.get(currentId) + edge.getWeight();
                
                if (!openSetNodes.contains(neighborId)) {
                    openSetNodes.add(neighborId);
                } else if (tentativeGScore >= gScore.get(neighborId)) {
                    continue;
                }
                
                previous.put(neighborId, currentId);
                gScore.put(neighborId, tentativeGScore);
                
                double h = calculateHeuristic(edge.getDestination(), target) * heuristicWeight;
                fScore.put(neighborId, tentativeGScore + h);
                
                openSet.offer(new AStarNode(neighborId, fScore.get(neighborId)));
            }
        }
        
        return null;
    }
    
    /**
     * Calculate Euclidean distance heuristic between two nodes
     * @param node1 First node
     * @param node2 Second node
     * @return Euclidean distance in meters
     */
    private static double calculateHeuristic(Node node1, Node node2) {
        double deltaLat = node2.getLatitude() - node1.getLatitude();
        double deltaLng = node2.getLongitude() - node1.getLongitude();
        
        // Convert lat/lng difference to approximate meters
        // Using simplified calculation (more precise would use Haversine formula)
        double latMeters = deltaLat * 111000; // ~111km per degree latitude
        double lngMeters = deltaLng * 111000 * Math.cos(Math.toRadians(node1.getLatitude()));
        
        return Math.sqrt(latMeters * latMeters + lngMeters * lngMeters);
    }
    
    /**
     * Alternative Manhattan distance heuristic
     * @param node1 First node
     * @param node2 Second node
     * @return Manhattan distance in meters
     */
    public static double calculateManhattanHeuristic(Node node1, Node node2) {
        double deltaLat = Math.abs(node2.getLatitude() - node1.getLatitude());
        double deltaLng = Math.abs(node2.getLongitude() - node1.getLongitude());
        
        double latMeters = deltaLat * 111000;
        double lngMeters = deltaLng * 111000 * Math.cos(Math.toRadians(node1.getLatitude()));
        
        return latMeters + lngMeters;
    }
    
    /**
     * Reconstruct path from previous node map
     */
    private static Route reconstructPath(Graph graph, Map<String, String> previous, 
                                       String sourceId, String targetId) {
        List<String> pathNodes = new ArrayList<>();
        String current = targetId;
        
        while (current != null) {
            pathNodes.add(current);
            current = previous.get(current);
        }
        
        if (pathNodes.isEmpty() || !pathNodes.get(pathNodes.size() - 1).equals(sourceId)) {
            return null;
        }
        
        Collections.reverse(pathNodes);
        
        Route route = new Route("A* Optimal Path");
        
        // Add nodes and edges
        for (int i = 0; i < pathNodes.size(); i++) {
            Node node = graph.getNode(pathNodes.get(i));
            route.addNode(node);
            
            if (i < pathNodes.size() - 1) {
                String currentNodeId = pathNodes.get(i);
                String nextNodeId = pathNodes.get(i + 1);
                
                for (Edge edge : graph.getEdgesFrom(currentNodeId)) {
                    if (edge.getDestination().getId().equals(nextNodeId)) {
                        route.addEdge(edge);
                        break;
                    }
                }
            }
        }
        
        return route;
    }
    
    /**
     * Helper class for A* priority queue
     */
    private static class AStarNode implements Comparable<AStarNode> {
        String nodeId;
        double fScore;
        
        AStarNode(String nodeId, double fScore) {
            this.nodeId = nodeId;
            this.fScore = fScore;
        }
        
        @Override
        public int compareTo(AStarNode other) {
            return Double.compare(this.fScore, other.fScore);
        }
    }
}
