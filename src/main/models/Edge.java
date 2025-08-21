package main.models;

/**
 * Represents an edge/connection between two nodes in the campus graph
 */
public class Edge {
    private Node source;
    private Node destination;
    private double distance; // in meters
    private double weight; // can include traffic, difficulty, etc.
    private int travelTime; // in minutes
    private String pathType; // "walkway", "road", "stairs", etc.
    private double speedKmh; // optional explicit speed for this edge
    
    public Edge(Node source, Node destination, double distance) {
        this.source = source;
        this.destination = destination;
        this.distance = distance;
        this.weight = distance; // Default weight is distance
        this.pathType = "walkway";
        this.speedKmh = 0.0;
        this.travelTime = calculateTravelTime(distance);
    }
    
    public Edge(Node source, Node destination, double distance, double weight, String pathType) {
        this.source = source;
        this.destination = destination;
        this.distance = distance;
        this.weight = weight;
        this.pathType = pathType;
    this.speedKmh = 0.0;
        this.travelTime = calculateTravelTime(distance);
    }
    
    /**
     * Calculate travel time based on distance and path type
     * @param distance Distance in meters
     * @return Travel time in minutes
     */
    private int calculateTravelTime(double distance) {
        double effectiveSpeed = this.speedKmh;
        if (effectiveSpeed <= 0) {
            // Default speeds by path type
            switch (this.pathType == null ? "walkway" : this.pathType.toLowerCase()) {
                case "road":
                case "drive":
                case "car":
                    effectiveSpeed = 30.0; // km/h default driving speed on campus roads
                    break;
                case "bike":
                case "cycle":
                    effectiveSpeed = 12.0; // km/h
                    break;
                case "stairs":
                case "footpath":
                case "walkway":
                default:
                    effectiveSpeed = 5.0; // km/h walking
            }
        }
        double timeInHours = (distance / 1000.0) / effectiveSpeed;
        return (int) Math.ceil(timeInHours * 60); // Convert to minutes
    }
    
    // Getters and setters
    public Node getSource() { return source; }
    public void setSource(Node source) { this.source = source; }
    
    public Node getDestination() { return destination; }
    public void setDestination(Node destination) { this.destination = destination; }
    
    public double getDistance() { return distance; }
    public void setDistance(double distance) { 
        this.distance = distance;
        this.travelTime = calculateTravelTime(distance);
    }
    
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    
    public int getTravelTime() { return travelTime; }
    public void setTravelTime(int travelTime) { this.travelTime = travelTime; }
    
    public String getPathType() { return pathType; }
    public void setPathType(String pathType) { this.pathType = pathType; }

    public double getSpeedKmh() { return speedKmh; }
    public void setSpeedKmh(double speedKmh) {
        this.speedKmh = speedKmh;
        // Recompute travel time with new speed
        this.travelTime = calculateTravelTime(this.distance);
    }
    
    /**
     * Get the reverse edge (destination -> source)
     */
    public Edge getReverse() {
        Edge rev = new Edge(destination, source, distance, weight, pathType);
        if (this.speedKmh > 0) {
            rev.setSpeedKmh(this.speedKmh);
        }
        return rev;
    }
    
    @Override
    public String toString() {
        return String.format("Edge{%s -> %s, distance=%.1fm, time=%dmin, type=%s}", 
                           source.getName(), destination.getName(), distance, travelTime, pathType);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Edge edge = (Edge) obj;
        return source.equals(edge.source) && destination.equals(edge.destination);
    }
    
    @Override
    public int hashCode() {
        return source.hashCode() + destination.hashCode();
    }
}
