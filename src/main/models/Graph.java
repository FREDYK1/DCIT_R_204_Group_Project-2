package main.models;

import java.util.*;

/**
 * Represents the campus as a graph data structure
 */
public class Graph {
    private Map<String, Node> nodes;
    private Map<String, List<Edge>> adjacencyList;
    private List<Edge> edges;
    
    public Graph() {
        this.nodes = new HashMap<>();
        this.adjacencyList = new HashMap<>();
        this.edges = new ArrayList<>();
    }
    
    /**
     * Add a node to the graph
     */
    public void addNode(Node node) {
        nodes.put(node.getId(), node);
        adjacencyList.putIfAbsent(node.getId(), new ArrayList<>());
    }
    
    /**
     * Add an edge to the graph (bidirectional by default)
     */
    public void addEdge(Edge edge) {
        addEdge(edge, true);
    }
    
    /**
     * Add an edge to the graph
     * @param edge The edge to add
     * @param bidirectional Whether to add the reverse edge as well
     */
    public void addEdge(Edge edge, boolean bidirectional) {
        // Ensure nodes exist
        addNode(edge.getSource());
        addNode(edge.getDestination());
        
        // Add edge to adjacency list
        adjacencyList.get(edge.getSource().getId()).add(edge);
        edges.add(edge);
        
        // Add reverse edge if bidirectional
        if (bidirectional) {
            Edge reverseEdge = edge.getReverse();
            adjacencyList.get(edge.getDestination().getId()).add(reverseEdge);
            edges.add(reverseEdge);
        }
    }
    
    /**
     * Get a node by its ID
     */
    public Node getNode(String nodeId) {
        return nodes.get(nodeId);
    }
    
    /**
     * Get all nodes in the graph
     */
    public Collection<Node> getAllNodes() {
        return nodes.values();
    }
    
    /**
     * Get all edges from a specific node
     */
    public List<Edge> getEdgesFrom(String nodeId) {
        return adjacencyList.getOrDefault(nodeId, new ArrayList<>());
    }
    
    /**
     * Get all edges in the graph
     */
    public List<Edge> getAllEdges() {
        return edges;
    }
    
    /**
     * Get neighbors of a node
     */
    public List<Node> getNeighbors(String nodeId) {
        List<Node> neighbors = new ArrayList<>();
        List<Edge> nodeEdges = getEdgesFrom(nodeId);
        for (Edge edge : nodeEdges) {
            neighbors.add(edge.getDestination());
        }
        return neighbors;
    }
    
    /**
     * Check if a path exists between two nodes
     */
    public boolean hasPath(String sourceId, String destinationId) {
        if (!nodes.containsKey(sourceId) || !nodes.containsKey(destinationId)) {
            return false;
        }
        
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.offer(sourceId);
        visited.add(sourceId);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(destinationId)) {
                return true;
            }
            
            for (Edge edge : getEdgesFrom(current)) {
                String neighbor = edge.getDestination().getId();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        
        return false;
    }
    
    /**
     * Get landmark nodes
     */
    public List<Node> getLandmarks() {
        return nodes.values().stream()
                   .filter(Node::isLandmark)
                   .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Find nodes by name (case-insensitive partial match)
     */
    public List<Node> findNodesByName(String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        return nodes.values().stream()
                   .filter(node -> node.getName().toLowerCase().contains(lowerSearchTerm))
                   .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    public int getNodeCount() {
        return nodes.size();
    }
    
    public int getEdgeCount() {
        return edges.size();
    }
    
    @Override
    public String toString() {
        return String.format("Graph{nodes=%d, edges=%d}", getNodeCount(), getEdgeCount());
    }
}
