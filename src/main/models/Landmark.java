package main.models;

/**
 * Represents a landmark on the UG campus
 */
public class Landmark {
    private String id;
    private String name;
    private String category;
    private String description;
    private Node location;
    private double importance; // 1.0 = most important, 0.0 = least important
    
    public Landmark(String id, String name, String category, Node location) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.location = location;
        this.importance = 0.5; // Default importance
        this.description = "";
    }
    
    public Landmark(String id, String name, String category, String description, 
                   Node location, double importance) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.location = location;
        this.importance = Math.max(0.0, Math.min(1.0, importance)); // Clamp between 0 and 1
    }
    
    /**
     * Check if this landmark matches a search term
     */
    public boolean matches(String searchTerm) {
        String lowerSearch = searchTerm.toLowerCase();
        return name.toLowerCase().contains(lowerSearch) ||
               category.toLowerCase().contains(lowerSearch) ||
               description.toLowerCase().contains(lowerSearch);
    }
    
    /**
     * Get landmark type based on category
     */
    public LandmarkType getType() {
        String lowerCategory = category.toLowerCase();
        if (lowerCategory.contains("academic")) return LandmarkType.ACADEMIC;
        if (lowerCategory.contains("residential")) return LandmarkType.RESIDENTIAL;
        if (lowerCategory.contains("dining")) return LandmarkType.DINING;
        if (lowerCategory.contains("recreation")) return LandmarkType.RECREATION;
        if (lowerCategory.contains("service")) return LandmarkType.SERVICE;
        if (lowerCategory.contains("transport")) return LandmarkType.TRANSPORT;
        return LandmarkType.OTHER;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Node getLocation() { return location; }
    public void setLocation(Node location) { this.location = location; }
    
    public double getImportance() { return importance; }
    public void setImportance(double importance) { 
        this.importance = Math.max(0.0, Math.min(1.0, importance)); 
    }
    
    @Override
    public String toString() {
        return String.format("Landmark{name='%s', category='%s', importance=%.2f}", 
                           name, category, importance);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Landmark landmark = (Landmark) obj;
        return id.equals(landmark.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    /**
     * Enum for landmark types
     */
    public enum LandmarkType {
        ACADEMIC("Academic Buildings"),
        RESIDENTIAL("Residential Halls"),
        DINING("Dining & Food"),
        RECREATION("Recreation & Sports"),
        SERVICE("Services & Facilities"),
        TRANSPORT("Transportation"),
        OTHER("Other");
        
        private final String displayName;
        
        LandmarkType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
