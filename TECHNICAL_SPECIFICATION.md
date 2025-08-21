# Technical Specification: Google Maps Distance Matrix API Integration

## 1. Edge Model Enhancement

### Current Edge.java Structure
```java
public class Edge {
    private Node source;
    private Node destination;
    private double distance; // in meters
    private double weight; // can include traffic, difficulty, etc.
    private int travelTime; // in minutes
    private String pathType; // "walkway", "road", "stairs", etc.
    private double speedKmh; // optional explicit speed for this edge
    // constructors, getters, setters, etc.
}
```

### Enhanced Edge.java Structure
```java
public class Edge {
    private Node source;
    private Node destination;
    private double distance; // in meters
    private double weight; // can include traffic, difficulty, etc.
    private int travelTime; // in minutes
    private String pathType; // "walkway", "road", "stairs", etc.
    private double speedKmh; // optional explicit speed for this edge
    
    // Google Maps Distance Matrix API data
    private double googleMapsDistance; // in meters
    private int googleMapsDuration; // in seconds
    private boolean googleMapsDataAvailable;
    private long googleMapsLastUpdated; // timestamp
    
    // New constructors to handle Google Maps data
    public Edge(Node source, Node destination, double distance, double weight, String pathType, 
                double googleMapsDistance, int googleMapsDuration) {
        // Initialize all fields
        this.googleMapsDistance = googleMapsDistance;
        this.googleMapsDuration = googleMapsDuration;
        this.googleMapsDataAvailable = (googleMapsDistance > 0 && googleMapsDuration > 0);
        this.googleMapsLastUpdated = System.currentTimeMillis();
    }
    
    // Getters and setters for Google Maps fields
    public double getGoogleMapsDistance() { return googleMapsDistance; }
    public void setGoogleMapsDistance(double googleMapsDistance) { 
        this.googleMapsDistance = googleMapsDistance; 
        this.googleMapsDataAvailable = (googleMapsDistance > 0 && googleMapsDuration > 0);
        this.googleMapsLastUpdated = System.currentTimeMillis();
    }
    
    public int getGoogleMapsDuration() { return googleMapsDuration; }
    public void setGoogleMapsDuration(int googleMapsDuration) { 
        this.googleMapsDuration = googleMapsDuration; 
        this.googleMapsDataAvailable = (googleMapsDistance > 0 && googleMapsDuration > 0);
        this.googleMapsLastUpdated = System.currentTimeMillis();
    }
    
    public boolean isGoogleMapsDataAvailable() { return googleMapsDataAvailable; }
    public long getGoogleMapsLastUpdated() { return googleMapsLastUpdated; }
    
    // Method to get the most accurate distance (Google Maps preferred)
    public double getAccurateDistance() {
        return googleMapsDataAvailable ? googleMapsDistance : distance;
    }
    
    // Method to get the most accurate duration (Google Maps preferred)
    public int getAccurateDuration() {
        return googleMapsDataAvailable ? googleMapsDuration : travelTime * 60; // convert minutes to seconds
    }
}
```

## 2. Google Maps Service Implementation

### GoogleMapsService.java
```java
package main.services;

import main.models.Node;
import main.models.Edge;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GoogleMapsService {
    private static final String API_KEY = System.getenv("GOOGLE_MAPS_API_KEY");
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";
    private static final boolean ENABLED = Boolean.parseBoolean(System.getenv().getOrDefault("GOOGLE_MAPS_ENABLED", "true"));
    
    // Simple in-memory cache
    private static Map<String, Map<String, GoogleMapsData>> cache = new HashMap<>();
    private static final long CACHE_TTL = Long.parseLong(System.getenv().getOrDefault("GOOGLE_MAPS_CACHE_TTL", "3600000")); // 1 hour default
    
    public static class GoogleMapsData {
        public double distance; // meters
        public int duration; // seconds
        public long timestamp;
        
        public GoogleMapsData(double distance, int duration) {
            this.distance = distance;
            this.duration = duration;
            this.timestamp = System.currentTimeMillis();
        }
        
        public boolean isExpired() {
            return (System.currentTimeMillis() - timestamp) > CACHE_TTL;
        }
    }
    
    /**
     * Get distance and duration between two nodes using Google Maps Distance Matrix API
     * @param origin Source node
     * @param destination Destination node
     * @return GoogleMapsData with distance and duration, or null if API call fails
     */
    public static GoogleMapsData getDistanceAndDuration(Node origin, Node destination) {
        if (!ENABLED || API_KEY == null || API_KEY.isEmpty()) {
            return null;
        }
        
        // Check cache first
        String originKey = origin.getLatitude() + "," + origin.getLongitude();
        String destKey = destination.getLatitude() + "," + destination.getLongitude();
        
        if (cache.containsKey(originKey) && cache.get(originKey).containsKey(destKey)) {
            GoogleMapsData cached = cache.get(originKey).get(destKey);
            if (!cached.isExpired()) {
                return cached;
            }
        }
        
        try {
            String urlString = String.format("%s?origins=%f,%f&destinations=%f,%f&mode=walking&units=metric&key=%s",
                    BASE_URL,
                    origin.getLatitude(), origin.getLongitude(),
                    destination.getLatitude(), destination.getLongitude(),
                    API_KEY);
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                // Parse JSON response
                JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
                JsonObject element = jsonResponse.getAsJsonArray("rows").get(0)
                        .getAsJsonObject().getAsJsonArray("elements").get(0).getAsJsonObject();
                
                if (element.get("status").getAsString().equals("OK")) {
                    double distance = element.getAsJsonObject("distance").get("value").getAsDouble();
                    int duration = element.getAsJsonObject("duration").get("value").getAsInt();
                    
                    GoogleMapsData data = new GoogleMapsData(distance, duration);
                    
                    // Cache the result
                    cache.computeIfAbsent(originKey, k -> new HashMap<>()).put(destKey, data);
                    
                    return data;
                }
            }
        } catch (Exception e) {
            System.err.println("Error calling Google Maps API: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Update edge with Google Maps data
     * @param edge Edge to update
     */
    public static void updateEdgeWithGoogleMapsData(Edge edge) {
        GoogleMapsData data = getDistanceAndDuration(edge.getSource(), edge.getDestination());
        if (data != null) {
            edge.setGoogleMapsDistance(data.distance);
            edge.setGoogleMapsDuration(data.duration);
        }
    }
}
```

## 3. DistanceCalculator Enhancement

### Updated DistanceCalculator.java Methods
```java
// Add this method to DistanceCalculator class
/**
 * Get the most accurate distance between two nodes
 * Uses Google Maps data if available, falls back to Haversine calculation
 * @param node1 First node
 * @param node2 Second node
 * @return Distance in meters
 */
public static double getAccurateDistance(Node node1, Node node2) {
    // First try to get Google Maps distance
    GoogleMapsService.GoogleMapsData data = GoogleMapsService.getDistanceAndDuration(node1, node2);
    if (data != null) {
        return data.distance;
    }
    
    // Fall back to Haversine calculation
    return calculateHaversineDistance(node1, node2);
}

/**
 * Get the most accurate travel time between two nodes
 * Uses Google Maps data if available, falls back to calculated time
 * @param node1 First node
 * @param node2 Second node
 * @param pathType Type of path
 * @return Travel time in minutes
 */
public static int getAccurateTravelTime(Node node1, Node node2, String pathType) {
    // First try to get Google Maps duration
    GoogleMapsService.GoogleMapsData data = GoogleMapsService.getDistanceAndDuration(node1, node2);
    if (data != null) {
        return (int) Math.ceil(data.duration / 60.0); // Convert seconds to minutes
    }
    
    // Fall back to calculated time
    double distance = calculateHaversineDistance(node1, node2);
    // Use the same calculation logic as in Edge.calculateTravelTime()
    double speedKmh = getSpeedForPathType(pathType);
    double timeInHours = (distance / 1000.0) / speedKmh;
    return (int) Math.ceil(timeInHours * 60); // Convert to minutes
}

/**
 * Get speed for path type (copied from Edge.calculateTravelTime for consistency)
 * @param pathType Path type
 * @return Speed in km/h
 */
private static double getSpeedForPathType(String pathType) {
    if (pathType == null) pathType = "walkway";
    
    switch (pathType.toLowerCase()) {
        case "road":
        case "drive":
        case "car":
            return 30.0; // km/h default driving speed on campus roads
        case "bike":
        case "cycle":
            return 12.0; // km/h
        case "stairs":
        case "footpath":
        case "walkway":
        default:
            return 5.0; // km/h walking
    }
}
```

## 4. DataLoader Enhancement

### Updated DataLoader.java Methods
```java
/**
 * Load edges from CSV file with Google Maps data support
 * Updated format: sourceId,destinationId,distanceMeters,pathType,speedKmh,bidirectional,googleMapsDistance,googleMapsDuration
 */
public static List<Edge> loadEdgesFromCSV(String filename, Map<String, Node> nodeMap) {
    List<Edge> edges = new ArrayList<>();
    File file = new File(DATA_DIR + filename);

    if (!file.exists()) {
        System.out.println("File not found: " + filename + ", using sample edges if available");
        return edges; // return empty; caller can fall back
    }

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        boolean firstLine = true;
        while ((line = br.readLine()) != null) {
            if (firstLine) { firstLine = false; continue; }
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length < 2) continue;

            String srcId = parts[0].trim();
            String dstId = parts[1].trim();
            Node src = nodeMap.get(srcId);
            Node dst = nodeMap.get(dstId);
            if (src == null || dst == null) continue;

            Double distance = null;
            String pathType = "walkway";
            Double speedKmh = null; // optional
            boolean bidirectional = true;
            
            // Google Maps data (new fields)
            Double googleMapsDistance = null;
            Integer googleMapsDuration = null;

            if (parts.length >= 3 && !parts[2].trim().isEmpty()) {
                try { distance = Double.parseDouble(parts[2].trim()); } catch (NumberFormatException ignore) {}
            }
            if (parts.length >= 4 && !parts[3].trim().isEmpty()) {
                pathType = parts[3].trim();
            }
            if (parts.length >= 5 && !parts[4].trim().isEmpty()) {
                try { speedKmh = Double.parseDouble(parts[4].trim()); } catch (NumberFormatException ignore) {}
            }
            if (parts.length >= 6 && !parts[5].trim().isEmpty()) {
                bidirectional = Boolean.parseBoolean(parts[5].trim());
            }
            
            // Parse Google Maps data (new fields)
            if (parts.length >= 8) {
                if (!parts[6].trim().isEmpty()) {
                    try { googleMapsDistance = Double.parseDouble(parts[6].trim()); } catch (NumberFormatException ignore) {}
                }
                if (!parts[7].trim().isEmpty()) {
                    try { googleMapsDuration = Integer.parseInt(parts[7].trim()); } catch (NumberFormatException ignore) {}
                }
            }

            // Compute distance if not provided
            double distMeters = (distance != null) ? distance :
                    DistanceCalculator.calculateHaversineDistance(src, dst);

            // Build edge with all data
            Edge edge;
            if (googleMapsDistance != null && googleMapsDuration != null) {
                edge = new Edge(src, dst, distMeters, distMeters, pathType, googleMapsDistance, googleMapsDuration);
            } else {
                edge = new Edge(src, dst, distMeters, distMeters, pathType);
            }
            
            if (speedKmh != null && speedKmh > 0) {
                edge.setSpeedKmh(speedKmh);
                // If user set speed, weight by minutes to optimize for time
                double hours = distMeters / 1000.0 / speedKmh;
                double minutes = hours * 60.0;
                edge.setWeight(minutes);
            }
            edges.add(edge);

            if (bidirectional) {
                edges.add(edge.getReverse());
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading edges file: " + e.getMessage());
    }

    return edges;
}

/**
 * Save edges to CSV file with Google Maps data
 */
public static void saveEdgesToCSV(List<Edge> edges, String filename) {
    File dataDir = new File(DATA_DIR);
    if (!dataDir.exists()) {
        dataDir.mkdirs();
    }

    try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_DIR + filename))) {
        // Updated header with Google Maps fields
        pw.println("sourceId,destinationId,distanceMeters,pathType,speedKmh,bidirectional,googleMapsDistance,googleMapsDuration");
        for (Edge e : edges) {
            pw.printf("%s,%s,%.1f,%s,%.1f,%b,%.1f,%d%n",
                    e.getSource().getId(),
                    e.getDestination().getId(),
                    e.getDistance(),
                    e.getPathType() != null ? e.getPathType() : "walkway",
                    e.getSpeedKmh(),
                    true, // bidirectional
                    e.isGoogleMapsDataAvailable() ? e.getGoogleMapsDistance() : 0,
                    e.isGoogleMapsDataAvailable() ? e.getGoogleMapsDuration() : 0);
        }
        System.out.println("Edges saved to " + filename);
    } catch (IOException e) {
        System.err.println("Error saving edges: " + e.getMessage());
    }
}

/**
 * Refresh Google Maps data for all edges
 */
public static void refreshGoogleMapsData(Graph graph) {
    System.out.println("Refreshing Google Maps data for all edges...");
    int updatedCount = 0;
    
    for (Edge edge : graph.getAllEdges()) {
        // Only update if Google Maps is enabled and data is not current
        if (System.getenv().getOrDefault("GOOGLE_MAPS_ENABLED", "true").equals("true")) {
            GoogleMapsService.updateEdgeWithGoogleMapsData(edge);
            if (edge.isGoogleMapsDataAvailable()) {
                updatedCount++;
            }
        }
    }
    
    System.out.println("Updated Google Maps data for " + updatedCount + " edges.");
}
```

## 5. RouteService Integration

### Updated RouteService.java Methods
```java
/**
 * Find the best route between two locations using accurate distance/time data
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

// Update the combineRoutes method to preserve Google Maps data
private Route combineRoutes(Route firstRoute, Route secondRoute) {
    Route combinedRoute = new Route("Combined Route");
    
    // Add all nodes from first route
    for (Node node : firstRoute.getPath()) {
        combinedRoute.addNode(node);
    }
    
    // Add all edges from first route (preserving Google Maps data)
    for (Edge edge : firstRoute.getEdges()) {
        combinedRoute.addEdge(edge);
    }
    
    // Add nodes from second route (skip first node as it's duplicate)
    List<Node> secondPath = secondRoute.getPath();
    for (int i = 1; i < secondPath.size(); i++) {
        combinedRoute.addNode(secondPath.get(i));
    }
    
    // Add edges from second route (preserving Google Maps data)
    for (Edge edge : secondRoute.getEdges()) {
        combinedRoute.addEdge(edge);
    }
    
    return combinedRoute;
}
```

## 6. Algorithm Updates

### Update pathfinding algorithms to use accurate distances

For DijkstraAlgorithm.java, AStarAlgorithm.java, and FloydWarshallAlgorithm.java:
- Modify edge weight calculations to use `edge.getAccurateDistance()` instead of `edge.getDistance()`
- Update travel time calculations to use `edge.getAccurateDuration()` when available

## 7. Configuration

### Environment Variables
- `GOOGLE_MAPS_API_KEY` - Your Google Maps API key
- `GOOGLE_MAPS_ENABLED` - Set to "true" to enable (default) or "false" to disable
- `GOOGLE_MAPS_CACHE_TTL` - Cache time-to-live in milliseconds (default 3600000 = 1 hour)

## 8. Dependencies

Add Gson library for JSON parsing:
```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.9</version>
</dependency>