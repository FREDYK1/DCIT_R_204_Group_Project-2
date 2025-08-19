package main.services;

import main.models.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for landmark-related operations
 * Manages campus landmarks and provides search functionality
 */
public class LandmarkService {
    private List<Landmark> landmarks;
    private Map<String, List<Landmark>> categoryMap;
    
    public LandmarkService() {
        this.landmarks = new ArrayList<>();
        this.categoryMap = new HashMap<>();
        initializeLandmarks();
    }
    
    /**
     * Search for landmarks by keyword
     * @param keyword Search term to match against landmark name, category, or description
     * @return List of matching landmarks
     */
    public List<Landmark> searchLandmarks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchTerm = keyword.toLowerCase().trim();
        
        return landmarks.stream()
                       .filter(landmark -> landmark.matches(searchTerm))
                       .sorted(Comparator.comparingDouble(Landmark::getImportance).reversed())
                       .collect(Collectors.toList());
    }
    
    /**
     * Get landmarks by category
     * @param category Landmark category
     * @return List of landmarks in the specified category
     */
    public List<Landmark> getLandmarksByCategory(String category) {
        return categoryMap.getOrDefault(category.toLowerCase(), new ArrayList<>());
    }
    
    /**
     * Get landmarks by type
     * @param type Landmark type enum
     * @return List of landmarks of the specified type
     */
    public List<Landmark> getLandmarksByType(Landmark.LandmarkType type) {
        return landmarks.stream()
                       .filter(landmark -> landmark.getType() == type)
                       .collect(Collectors.toList());
    }
    
    /**
     * Find landmarks within a certain distance of a location
     * @param centerNode Center location
     * @param maxDistance Maximum distance in meters
     * @return List of nearby landmarks
     */
    public List<Landmark> findLandmarksNearLocation(Node centerNode, double maxDistance) {
        List<Landmark> nearbyLandmarks = new ArrayList<>();
        
        for (Landmark landmark : landmarks) {
            double distance = calculateDistance(centerNode, landmark.getLocation());
            if (distance <= maxDistance) {
                nearbyLandmarks.add(landmark);
            }
        }
        
        // Sort by distance (closest first)
        nearbyLandmarks.sort((l1, l2) -> {
            double dist1 = calculateDistance(centerNode, l1.getLocation());
            double dist2 = calculateDistance(centerNode, l2.getLocation());
            return Double.compare(dist1, dist2);
        });
        
        return nearbyLandmarks;
    }
    
    /**
     * Get most important landmarks (high importance score)
     * @param limit Maximum number of landmarks to return
     * @return List of most important landmarks
     */
    public List<Landmark> getMostImportantLandmarks(int limit) {
        return landmarks.stream()
                       .sorted(Comparator.comparingDouble(Landmark::getImportance).reversed())
                       .limit(limit)
                       .collect(Collectors.toList());
    }
    
    /**
     * Add a new landmark
     * @param landmark Landmark to add
     */
    public void addLandmark(Landmark landmark) {
        if (landmark != null && !landmarks.contains(landmark)) {
            landmarks.add(landmark);
            
            // Update category map
            String category = landmark.getCategory().toLowerCase();
            categoryMap.computeIfAbsent(category, k -> new ArrayList<>()).add(landmark);
        }
    }
    
    /**
     * Remove a landmark
     * @param landmarkId ID of landmark to remove
     * @return true if landmark was removed, false otherwise
     */
    public boolean removeLandmark(String landmarkId) {
        Landmark toRemove = null;
        for (Landmark landmark : landmarks) {
            if (landmark.getId().equals(landmarkId)) {
                toRemove = landmark;
                break;
            }
        }
        
        if (toRemove != null) {
            landmarks.remove(toRemove);
            
            // Update category map
            String category = toRemove.getCategory().toLowerCase();
            List<Landmark> categoryLandmarks = categoryMap.get(category);
            if (categoryLandmarks != null) {
                categoryLandmarks.remove(toRemove);
                if (categoryLandmarks.isEmpty()) {
                    categoryMap.remove(category);
                }
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Get all available categories
     * @return Set of all landmark categories
     */
    public Set<String> getAllCategories() {
        return landmarks.stream()
                       .map(Landmark::getCategory)
                       .collect(Collectors.toSet());
    }
    
    /**
     * Get statistics about landmarks
     * @return Map containing various statistics
     */
    public Map<String, Object> getLandmarkStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalCount", landmarks.size());
        stats.put("categories", getAllCategories());
        stats.put("categoryCounts", getCategoryCounts());
        stats.put("averageImportance", getAverageImportance());
        stats.put("typeCounts", getTypeCounts());
        
        return stats;
    }
    
    /**
     * Get landmark by ID
     * @param landmarkId Landmark ID
     * @return Landmark if found, null otherwise
     */
    public Landmark getLandmarkById(String landmarkId) {
        return landmarks.stream()
                       .filter(landmark -> landmark.getId().equals(landmarkId))
                       .findFirst()
                       .orElse(null);
    }
    
    /**
     * Calculate straight-line distance between two nodes using Haversine formula
     * @param node1 First node
     * @param node2 Second node
     * @return Distance in meters
     */
    private double calculateDistance(Node node1, Node node2) {
        double lat1 = Math.toRadians(node1.getLatitude());
        double lng1 = Math.toRadians(node1.getLongitude());
        double lat2 = Math.toRadians(node2.getLatitude());
        double lng2 = Math.toRadians(node2.getLongitude());
        
        double deltaLat = lat2 - lat1;
        double deltaLng = lng2 - lng1;
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                  Math.cos(lat1) * Math.cos(lat2) *
                  Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return 6371000 * c; // Earth's radius in meters
    }
    
    private Map<String, Integer> getCategoryCounts() {
        Map<String, Integer> counts = new HashMap<>();
        for (Landmark landmark : landmarks) {
            String category = landmark.getCategory();
            counts.put(category, counts.getOrDefault(category, 0) + 1);
        }
        return counts;
    }
    
    private double getAverageImportance() {
        if (landmarks.isEmpty()) return 0.0;
        return landmarks.stream()
                       .mapToDouble(Landmark::getImportance)
                       .average()
                       .orElse(0.0);
    }
    
    private Map<String, Integer> getTypeCounts() {
        Map<String, Integer> counts = new HashMap<>();
        for (Landmark.LandmarkType type : Landmark.LandmarkType.values()) {
            long count = landmarks.stream()
                                 .filter(landmark -> landmark.getType() == type)
                                 .count();
            if (count > 0) {
                counts.put(type.getDisplayName(), (int) count);
            }
        }
        return counts;
    }
    
    /**
     * Initialize sample UG campus landmarks
     */
    private void initializeLandmarks() {
        // Academic Buildings
        Node mainGateNode = new Node("main_gate", "Main Gate", 5.6508, -0.1870);
        Node greatHallNode = new Node("great_hall", "Great Hall", 5.6520, -0.1850);
        Node libraryNode = new Node("library", "Balme Library", 5.6525, -0.1845);
        Node compSciNode = new Node("comp_sci", "Computer Science Department", 5.6530, -0.1840);
        Node nightMarketNode = new Node("night_market", "Night Market", 5.6515, -0.1860);
        Node commonwealthNode = new Node("commonwealth", "Commonwealth Hall", 5.6540, -0.1820);
        Node legonNode = new Node("legon", "Legon Hall", 5.6545, -0.1825);
        Node sportsNode = new Node("sports", "Sports Complex", 5.6550, -0.1830);
        
        // Create landmarks
        addLandmark(new Landmark("lm_main_gate", "Main Gate", "Transport", 
                    "Main entrance to the university", mainGateNode, 1.0));
        addLandmark(new Landmark("lm_great_hall", "Great Hall", "Academic", 
                    "Main assembly and graduation hall", greatHallNode, 0.9));
        addLandmark(new Landmark("lm_library", "Balme Library", "Academic", 
                    "Main university library with study areas", libraryNode, 0.8));
        addLandmark(new Landmark("lm_comp_sci", "Computer Science Department", "Academic", 
                    "DCIT Department building", compSciNode, 0.7));
        addLandmark(new Landmark("lm_night_market", "Night Market", "Dining", 
                    "Food court and shopping area", nightMarketNode, 0.8));
        addLandmark(new Landmark("lm_commonwealth", "Commonwealth Hall", "Residential", 
                    "Traditional residential hall", commonwealthNode, 0.6));
        addLandmark(new Landmark("lm_legon", "Legon Hall", "Residential", 
                    "Traditional residential hall", legonNode, 0.6));
        addLandmark(new Landmark("lm_sports", "Sports Complex", "Recreation", 
                    "Main sports and recreation facilities", sportsNode, 0.7));
        
        // Additional landmarks
        Node bankNode = new Node("bank", "GCB Bank", 5.6518, -0.1855);
        Node medicalNode = new Node("medical", "Medical Center", 5.6535, -0.1835);
        Node srcNode = new Node("src", "SRC Building", 5.6522, -0.1848);
        
        addLandmark(new Landmark("lm_bank", "GCB Bank", "Service", 
                    "Ghana Commercial Bank branch", bankNode, 0.5));
        addLandmark(new Landmark("lm_medical", "Medical Center", "Service", 
                    "University health services", medicalNode, 0.7));
        addLandmark(new Landmark("lm_src", "SRC Building", "Service", 
                    "Students Representative Council offices", srcNode, 0.6));
        
        System.out.println("Initialized " + landmarks.size() + " campus landmarks");
    }
    
    // Getters
    public List<Landmark> getAllLandmarks() { return new ArrayList<>(landmarks); }
    public int getLandmarkCount() { return landmarks.size(); }
}
