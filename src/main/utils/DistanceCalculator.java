package main.utils;

import main.models.Node;

/**
 * Utility class for distance calculations between locations
 */
public class DistanceCalculator {
    
    // Earth's radius in meters (mean radius)
    private static final double EARTH_RADIUS_METERS = 6371000;
    
    /**
     * Calculate straight-line distance between two nodes using Haversine formula
     * This gives the shortest distance between two points on the Earth's surface
     * @param node1 First node
     * @param node2 Second node
     * @return Distance in meters
     */
    public static double calculateHaversineDistance(Node node1, Node node2) {
        if (node1 == null || node2 == null) {
            return 0.0;
        }
        
        double lat1Rad = Math.toRadians(node1.getLatitude());
        double lng1Rad = Math.toRadians(node1.getLongitude());
        double lat2Rad = Math.toRadians(node2.getLatitude());
        double lng2Rad = Math.toRadians(node2.getLongitude());
        
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLng = lng2Rad - lng1Rad;
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                  Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                  Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_METERS * c;
    }
    
    /**
     * Calculate approximate distance using simple Euclidean formula
     * Less accurate but faster for small distances
     * @param node1 First node
     * @param node2 Second node
     * @return Distance in meters
     */
    public static double calculateEuclideanDistance(Node node1, Node node2) {
        if (node1 == null || node2 == null) {
            return 0.0;
        }
        
        double deltaLat = node2.getLatitude() - node1.getLatitude();
        double deltaLng = node2.getLongitude() - node1.getLongitude();
        
        // Convert degree differences to meters (approximation)
        double latMeters = deltaLat * 111000; // ~111km per degree latitude
        double lngMeters = deltaLng * 111000 * Math.cos(Math.toRadians(node1.getLatitude()));
        
        return Math.sqrt(latMeters * latMeters + lngMeters * lngMeters);
    }
    
    /**
     * Calculate Manhattan distance (sum of absolute differences)
     * Useful for grid-like navigation systems
     * @param node1 First node
     * @param node2 Second node
     * @return Manhattan distance in meters
     */
    public static double calculateManhattanDistance(Node node1, Node node2) {
        if (node1 == null || node2 == null) {
            return 0.0;
        }
        
        double deltaLat = Math.abs(node2.getLatitude() - node1.getLatitude());
        double deltaLng = Math.abs(node2.getLongitude() - node1.getLongitude());
        
        double latMeters = deltaLat * 111000;
        double lngMeters = deltaLng * 111000 * Math.cos(Math.toRadians(node1.getLatitude()));
        
        return latMeters + lngMeters;
    }
    
    /**
     * Calculate bearing (direction) from one node to another
     * @param node1 Starting node
     * @param node2 Destination node
     * @return Bearing in degrees (0-360, where 0 is North)
     */
    public static double calculateBearing(Node node1, Node node2) {
        if (node1 == null || node2 == null) {
            return 0.0;
        }
        
        double lat1Rad = Math.toRadians(node1.getLatitude());
        double lng1Rad = Math.toRadians(node1.getLongitude());
        double lat2Rad = Math.toRadians(node2.getLatitude());
        double lng2Rad = Math.toRadians(node2.getLongitude());
        
        double deltaLng = lng2Rad - lng1Rad;
        
        double y = Math.sin(deltaLng) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) -
                  Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLng);
        
        double bearingRad = Math.atan2(y, x);
        double bearingDeg = Math.toDegrees(bearingRad);
        
        // Normalize to 0-360 degrees
        return (bearingDeg + 360) % 360;
    }
    
    /**
     * Get compass direction from bearing
     * @param bearing Bearing in degrees
     * @return Compass direction as string
     */
    public static String getCompassDirection(double bearing) {
        String[] directions = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
                              "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        
        int index = (int) Math.round(bearing / 22.5) % 16;
        return directions[index];
    }
    
    /**
     * Check if a point is within a circular area
     * @param center Center point
     * @param point Point to check
     * @param radiusMeters Radius in meters
     * @return true if point is within the radius
     */
    public static boolean isWithinRadius(Node center, Node point, double radiusMeters) {
        double distance = calculateHaversineDistance(center, point);
        return distance <= radiusMeters;
    }
    
    /**
     * Find the closest node to a given node from a list
     * @param target Target node
     * @param candidates List of candidate nodes
     * @return Closest node or null if candidates list is empty
     */
    public static Node findClosestNode(Node target, java.util.List<Node> candidates) {
        if (target == null || candidates == null || candidates.isEmpty()) {
            return null;
        }
        
        Node closest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Node candidate : candidates) {
            double distance = calculateHaversineDistance(target, candidate);
            if (distance < minDistance) {
                minDistance = distance;
                closest = candidate;
            }
        }
        
        return closest;
    }
    
    /**
     * Calculate the midpoint between two nodes
     * @param node1 First node
     * @param node2 Second node
     * @return New node representing the midpoint
     */
    public static Node calculateMidpoint(Node node1, Node node2) {
        if (node1 == null || node2 == null) {
            return null;
        }
        
        double lat1Rad = Math.toRadians(node1.getLatitude());
        double lng1Rad = Math.toRadians(node1.getLongitude());
        double lat2Rad = Math.toRadians(node2.getLatitude());
        double lng2Rad = Math.toRadians(node2.getLongitude());
        
        double deltaLng = lng2Rad - lng1Rad;
        
        double bx = Math.cos(lat2Rad) * Math.cos(deltaLng);
        double by = Math.cos(lat2Rad) * Math.sin(deltaLng);
        
        double midLatRad = Math.atan2(Math.sin(lat1Rad) + Math.sin(lat2Rad),
                                     Math.sqrt((Math.cos(lat1Rad) + bx) * (Math.cos(lat1Rad) + bx) + by * by));
        double midLngRad = lng1Rad + Math.atan2(by, Math.cos(lat1Rad) + bx);
        
        double midLat = Math.toDegrees(midLatRad);
        double midLng = Math.toDegrees(midLngRad);
        
        return new Node("midpoint", "Midpoint", midLat, midLng);
    }
    
    /**
     * Convert distance from meters to other units
     * @param meters Distance in meters
     * @param unit Target unit ("km", "miles", "feet")
     * @return Distance in the specified unit
     */
    public static double convertDistance(double meters, String unit) {
        switch (unit.toLowerCase()) {
            case "km":
            case "kilometers":
                return meters / 1000.0;
            case "miles":
                return meters / 1609.344;
            case "feet":
                return meters * 3.28084;
            case "yards":
                return meters * 1.09361;
            default:
                return meters;
        }
    }
    
    /**
     * Format distance for display
     * @param meters Distance in meters
     * @return Formatted string with appropriate units
     */
    public static String formatDistance(double meters) {
        if (meters < 1000) {
            return String.format("%.0f m", meters);
        } else {
            return String.format("%.2f km", meters / 1000.0);
        }
    }
    
    /**
     * Calculate the area of a polygon defined by nodes
     * Uses the shoelace formula
     * @param nodes List of nodes forming the polygon
     * @return Area in square meters (approximate)
     */
    public static double calculatePolygonArea(java.util.List<Node> nodes) {
        if (nodes == null || nodes.size() < 3) {
            return 0.0;
        }
        
        double area = 0.0;
        int n = nodes.size();
        
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            
            // Convert to approximate meters
            double x1 = nodes.get(i).getLongitude() * 111000 * 
                       Math.cos(Math.toRadians(nodes.get(i).getLatitude()));
            double y1 = nodes.get(i).getLatitude() * 111000;
            double x2 = nodes.get(j).getLongitude() * 111000 * 
                       Math.cos(Math.toRadians(nodes.get(j).getLatitude()));
            double y2 = nodes.get(j).getLatitude() * 111000;
            
            area += (x1 * y2 - x2 * y1);
        }
        
        return Math.abs(area) / 2.0;
    }
    
    /**
     * Performance test for different distance calculation methods
     * @param node1 First test node
     * @param node2 Second test node
     * @param iterations Number of test iterations
     * @return Map containing timing results
     */
    public static java.util.Map<String, Long> performanceTest(Node node1, Node node2, int iterations) {
        java.util.Map<String, Long> results = new java.util.HashMap<>();
        
        // Test Haversine formula
        long startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            calculateHaversineDistance(node1, node2);
        }
        long haversineTime = System.nanoTime() - startTime;
        results.put("Haversine", haversineTime / 1_000_000); // Convert to milliseconds
        
        // Test Euclidean formula
        startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            calculateEuclideanDistance(node1, node2);
        }
        long euclideanTime = System.nanoTime() - startTime;
        results.put("Euclidean", euclideanTime / 1_000_000);
        
        // Test Manhattan formula
        startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            calculateManhattanDistance(node1, node2);
        }
        long manhattanTime = System.nanoTime() - startTime;
        results.put("Manhattan", manhattanTime / 1_000_000);
        
        return results;
    }
}
