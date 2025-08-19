package main.models;

/**
 * Represents a node/location in the campus graph
 */
public class Node {
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private String description;
    private boolean isLandmark;
    
    public Node(String id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isLandmark = false;
    }
    
    public Node(String id, String name, double latitude, double longitude, String description, boolean isLandmark) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.isLandmark = isLandmark;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isLandmark() { return isLandmark; }
    public void setLandmark(boolean landmark) { isLandmark = landmark; }
    
    @Override
    public String toString() {
        return String.format("Node{id='%s', name='%s', lat=%.6f, lng=%.6f, landmark=%s}", 
                           id, name, latitude, longitude, isLandmark);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return id.equals(node.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
