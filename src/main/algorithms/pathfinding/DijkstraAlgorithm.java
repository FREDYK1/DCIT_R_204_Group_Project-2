package main.algorithms.pathfinding;

import main.models.*;
import java.util.*;

/**
 * Implementation of Dijkstra's Algorithm for finding shortest paths
 * Time Complexity: O((V + E) log V) where V = vertices, E = edges
 */
public class DijkstraAlgorithm {
    
    /**
     * Find shortest path between two nodes using Dijkstra's algorithm
     * @param graph The campus graph
     * @param sourceId Starting node ID
     * @param targetId Destination node ID
     * @return Route object containing the shortest path, or null if no path exists
     */
    public static Route findShortestPath(Graph graph, String sourceId, String targetId) {
        Node source = graph.getNode(sourceId);
        Node target = graph.getNode(targetId);
        
        if (source == null || target == null) {
            return null;
        }
        
        if (sourceId.equals(targetId)) {
            Route route = new Route("Direct Route");
            route.addNode(source);
            return route;
        }
        
        // Distance map: nodeId -> shortest distance from source
        Map<String, Double> distances = new HashMap<>();
        // Previous node map for path reconstruction
        Map<String, String> previous = new HashMap<>();
        // Priority queue: [distance, nodeId]
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>();
        // Visited nodes
        Set<String> visited = new HashSet<>();
        
        // Initialize distances
        for (Node node : graph.getAllNodes()) {
            distances.put(node.getId(), Double.MAX_VALUE);
        }
        distances.put(sourceId, 0.0);
        pq.offer(new NodeDistance(sourceId, 0.0));
        
        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            String currentNodeId = current.nodeId;
            double currentDistance = current.distance;
            
            if (visited.contains(currentNodeId)) {
                continue;
            }
            
            visited.add(currentNodeId);
            
            // If we reached the target, we can stop
            if (currentNodeId.equals(targetId)) {
                break;
            }
            
            // Check all neighbors
            for (Edge edge : graph.getEdgesFrom(currentNodeId)) {
                String neighborId = edge.getDestination().getId();
                
                if (visited.contains(neighborId)) {
                    continue;
                }
                
                double newDistance = currentDistance + edge.getWeight();
                
                if (newDistance < distances.get(neighborId)) {
                    distances.put(neighborId, newDistance);
                    previous.put(neighborId, currentNodeId);
                    pq.offer(new NodeDistance(neighborId, newDistance));
                }
            }
        }
        
        // Reconstruct path
        if (!previous.containsKey(targetId) && !sourceId.equals(targetId)) {
            return null; // No path found
        }
        
        return reconstructPath(graph, previous, sourceId, targetId, distances.get(targetId));
    }
    
    /**
     * Find all shortest paths from a source node to all other nodes
     * @param graph The campus graph
     * @param sourceId Starting node ID
     * @return Map of nodeId -> Route for shortest paths to all reachable nodes
     */
    public static Map<String, Route> findAllShortestPaths(Graph graph, String sourceId) {
        Map<String, Route> allPaths = new HashMap<>();
        Node source = graph.getNode(sourceId);
        
        if (source == null) {
            return allPaths;
        }
        
        // Distance and previous maps
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();
        
        // Initialize
        for (Node node : graph.getAllNodes()) {
            distances.put(node.getId(), Double.MAX_VALUE);
        }
        distances.put(sourceId, 0.0);
        pq.offer(new NodeDistance(sourceId, 0.0));
        
        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            String currentNodeId = current.nodeId;
            double currentDistance = current.distance;
            
            if (visited.contains(currentNodeId)) {
                continue;
            }
            
            visited.add(currentNodeId);
            
            // Check all neighbors
            for (Edge edge : graph.getEdgesFrom(currentNodeId)) {
                String neighborId = edge.getDestination().getId();
                
                if (visited.contains(neighborId)) {
                    continue;
                }
                
                double newDistance = currentDistance + edge.getWeight();
                
                if (newDistance < distances.get(neighborId)) {
                    distances.put(neighborId, newDistance);
                    previous.put(neighborId, currentNodeId);
                    pq.offer(new NodeDistance(neighborId, newDistance));
                }
            }
        }
        
        // Reconstruct all paths
        for (Node node : graph.getAllNodes()) {
            if (!node.getId().equals(sourceId) && previous.containsKey(node.getId())) {
                Route route = reconstructPath(graph, previous, sourceId, node.getId(), 
                                            distances.get(node.getId()));
                if (route != null) {
                    allPaths.put(node.getId(), route);
                }
            }
        }
        
        return allPaths;
    }
    
    /**
     * Reconstruct the path from source to target using the previous node map
     */
    private static Route reconstructPath(Graph graph, Map<String, String> previous, 
                                       String sourceId, String targetId, double totalDistance) {
        List<String> pathNodes = new ArrayList<>();
        String current = targetId;
        
        // Build path backwards
        while (current != null) {
            pathNodes.add(current);
            current = previous.get(current);
        }
        
        if (pathNodes.isEmpty() || !pathNodes.get(pathNodes.size() - 1).equals(sourceId)) {
            return null;
        }
        
        // Reverse to get forward path
        Collections.reverse(pathNodes);
        
        // Create route
        Route route = new Route("Dijkstra Shortest Path");
        
        // Add nodes and edges
        for (int i = 0; i < pathNodes.size(); i++) {
            Node node = graph.getNode(pathNodes.get(i));
            route.addNode(node);
            
            // Add edge if not the last node
            if (i < pathNodes.size() - 1) {
                String currentNodeId = pathNodes.get(i);
                String nextNodeId = pathNodes.get(i + 1);
                
                // Find the edge between these nodes
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
     * Helper class for priority queue in Dijkstra's algorithm
     */
    private static class NodeDistance implements Comparable<NodeDistance> {
        String nodeId;
        double distance;
        
        NodeDistance(String nodeId, double distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }
        
        @Override
        public int compareTo(NodeDistance other) {
            return Double.compare(this.distance, other.distance);
        }
    }
}
