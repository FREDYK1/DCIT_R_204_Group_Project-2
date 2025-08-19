package main.algorithms.pathfinding;

import main.models.*;
import java.util.*;

/**
 * Implementation of Floyd-Warshall Algorithm for all-pairs shortest paths
 * Time Complexity: O(V^3) where V is the number of vertices
 * Space Complexity: O(V^2)
 */
public class FloydWarshallAlgorithm {
    
    /**
     * Compute all-pairs shortest paths using Floyd-Warshall algorithm
     * @param graph The campus graph
     * @return Map of source -> Map of destination -> Route for all shortest paths
     */
    public static Map<String, Map<String, Route>> computeAllPairsShortestPaths(Graph graph) {
        List<Node> nodes = new ArrayList<>(graph.getAllNodes());
        int n = nodes.size();
        
        if (n == 0) {
            return new HashMap<>();
        }
        
        // Create node ID to index mapping
        Map<String, Integer> nodeToIndex = new HashMap<>();
        Map<Integer, String> indexToNode = new HashMap<>();
        for (int i = 0; i < n; i++) {
            nodeToIndex.put(nodes.get(i).getId(), i);
            indexToNode.put(i, nodes.get(i).getId());
        }
        
        // Initialize distance matrix
        double[][] dist = new double[n][n];
        String[][] next = new String[n][n]; // For path reconstruction
        
        // Initialize with infinity
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dist[i][j] = 0;
                } else {
                    dist[i][j] = Double.MAX_VALUE;
                }
                next[i][j] = null;
            }
        }
        
        // Fill direct edges
        for (Edge edge : graph.getAllEdges()) {
            int i = nodeToIndex.get(edge.getSource().getId());
            int j = nodeToIndex.get(edge.getDestination().getId());
            
            if (edge.getWeight() < dist[i][j]) {
                dist[i][j] = edge.getWeight();
                next[i][j] = edge.getDestination().getId();
            }
        }
        
        // Floyd-Warshall main algorithm
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] != Double.MAX_VALUE && 
                        dist[k][j] != Double.MAX_VALUE && 
                        dist[i][k] + dist[k][j] < dist[i][j]) {
                        
                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }
        
        // Construct result map
        Map<String, Map<String, Route>> result = new HashMap<>();
        
        for (int i = 0; i < n; i++) {
            String sourceId = indexToNode.get(i);
            Map<String, Route> destinationRoutes = new HashMap<>();
            
            for (int j = 0; j < n; j++) {
                String destId = indexToNode.get(j);
                
                if (i != j && dist[i][j] != Double.MAX_VALUE) {
                    Route route = reconstructPath(graph, next, nodeToIndex, 
                                                indexToNode, sourceId, destId);
                    if (route != null) {
                        destinationRoutes.put(destId, route);
                    }
                }
            }
            
            result.put(sourceId, destinationRoutes);
        }
        
        return result;
    }
    
    /**
     * Get shortest path between two specific nodes using precomputed results
     * @param graph The campus graph
     * @param sourceId Starting node ID
     * @param targetId Destination node ID
     * @return Route object containing the shortest path
     */
    public static Route getShortestPath(Graph graph, String sourceId, String targetId) {
        if (sourceId.equals(targetId)) {
            Node source = graph.getNode(sourceId);
            if (source != null) {
                Route route = new Route("Floyd-Warshall Direct Route");
                route.addNode(source);
                return route;
            }
            return null;
        }
        
        // For single path request, use a more efficient approach
        return computeSinglePairShortestPath(graph, sourceId, targetId);
    }
    
    /**
     * Compute shortest path between two specific nodes
     * More efficient than computing all pairs when only one path is needed
     */
    private static Route computeSinglePairShortestPath(Graph graph, String sourceId, String targetId) {
        List<Node> nodes = new ArrayList<>(graph.getAllNodes());
        int n = nodes.size();
        
        Map<String, Integer> nodeToIndex = new HashMap<>();
        Map<Integer, String> indexToNode = new HashMap<>();
        for (int i = 0; i < n; i++) {
            nodeToIndex.put(nodes.get(i).getId(), i);
            indexToNode.put(i, nodes.get(i).getId());
        }
        
        if (!nodeToIndex.containsKey(sourceId) || !nodeToIndex.containsKey(targetId)) {
            return null;
        }
        
        int sourceIndex = nodeToIndex.get(sourceId);
        int targetIndex = nodeToIndex.get(targetId);
        
        double[][] dist = new double[n][n];
        String[][] next = new String[n][n];
        
        // Initialize
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dist[i][j] = 0;
                } else {
                    dist[i][j] = Double.MAX_VALUE;
                }
                next[i][j] = null;
            }
        }
        
        // Fill edges
        for (Edge edge : graph.getAllEdges()) {
            int i = nodeToIndex.get(edge.getSource().getId());
            int j = nodeToIndex.get(edge.getDestination().getId());
            
            if (edge.getWeight() < dist[i][j]) {
                dist[i][j] = edge.getWeight();
                next[i][j] = edge.getDestination().getId();
            }
        }
        
        // Floyd-Warshall
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] != Double.MAX_VALUE && 
                        dist[k][j] != Double.MAX_VALUE && 
                        dist[i][k] + dist[k][j] < dist[i][j]) {
                        
                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }
        
        // Check if path exists
        if (dist[sourceIndex][targetIndex] == Double.MAX_VALUE) {
            return null;
        }
        
        return reconstructPath(graph, next, nodeToIndex, indexToNode, sourceId, targetId);
    }
    
    /**
     * Reconstruct path using the next matrix
     */
    private static Route reconstructPath(Graph graph, String[][] next, 
                                       Map<String, Integer> nodeToIndex,
                                       Map<Integer, String> indexToNode,
                                       String sourceId, String targetId) {
        List<String> path = new ArrayList<>();
        String current = sourceId;
        path.add(current);
        
        while (!current.equals(targetId)) {
            int currentIndex = nodeToIndex.get(current);
            int targetIndex = nodeToIndex.get(targetId);
            current = next[currentIndex][targetIndex];
            
            if (current == null) {
                return null; // No path
            }
            
            path.add(current);
        }
        
        // Create route
        Route route = new Route("Floyd-Warshall Shortest Path");
        
        for (int i = 0; i < path.size(); i++) {
            Node node = graph.getNode(path.get(i));
            route.addNode(node);
            
            if (i < path.size() - 1) {
                String currentNodeId = path.get(i);
                String nextNodeId = path.get(i + 1);
                
                // Find edge between current and next node
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
     * Check if there are negative cycles in the graph
     * @param graph The campus graph
     * @return true if negative cycles exist, false otherwise
     */
    public static boolean hasNegativeCycles(Graph graph) {
        List<Node> nodes = new ArrayList<>(graph.getAllNodes());
        int n = nodes.size();
        
        if (n == 0) return false;
        
        Map<String, Integer> nodeToIndex = new HashMap<>();
        for (int i = 0; i < n; i++) {
            nodeToIndex.put(nodes.get(i).getId(), i);
        }
        
        double[][] dist = new double[n][n];
        
        // Initialize
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dist[i][j] = 0;
                } else {
                    dist[i][j] = Double.MAX_VALUE;
                }
            }
        }
        
        // Fill edges
        for (Edge edge : graph.getAllEdges()) {
            int i = nodeToIndex.get(edge.getSource().getId());
            int j = nodeToIndex.get(edge.getDestination().getId());
            dist[i][j] = Math.min(dist[i][j], edge.getWeight());
        }
        
        // Floyd-Warshall
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] != Double.MAX_VALUE && 
                        dist[k][j] != Double.MAX_VALUE && 
                        dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
        
        // Check diagonal for negative values
        for (int i = 0; i < n; i++) {
            if (dist[i][i] < 0) {
                return true;
            }
        }
        
        return false;
    }
}
