package main.utils;

import main.models.*;
import java.io.*;
import java.util.*;

/**
 * Utility class for loading campus data from various sources
 */
public class DataLoader {
    private static final String DATA_DIR = "data/";
    private static boolean dataLoaded = false;
    
    /**
     * Load all campus data from files and initialize the system
     */
    public static void loadCampusData() {
        if (dataLoaded) {
            System.out.println("Campus data already loaded");
            return;
        }
        
        try {
            System.out.println("Loading campus data...");
            
            // For now, we'll use hardcoded data since we don't have CSV files
            // In a real implementation, this would read from actual data files
            loadSampleData();
            
            dataLoaded = true;
            System.out.println("Campus data loaded successfully");
            
        } catch (Exception e) {
            System.err.println("Error loading campus data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load locations from CSV file
     * Format: id,name,latitude,longitude,description,isLandmark
     */
    public static List<Node> loadLocationsFromCSV(String filename) {
        List<Node> locations = new ArrayList<>();
        File file = new File(DATA_DIR + filename);
        
        if (!file.exists()) {
            System.out.println("File not found: " + filename + ", using sample data");
            return getSampleLocations();
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // Skip header
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    double latitude = Double.parseDouble(parts[2].trim());
                    double longitude = Double.parseDouble(parts[3].trim());
                    String description = parts[4].trim();
                    boolean isLandmark = Boolean.parseBoolean(parts[5].trim());
                    
                    Node location = new Node(id, name, latitude, longitude, description, isLandmark);
                    locations.add(location);
                }
            }
            
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading locations file: " + e.getMessage());
            return getSampleLocations();
        }
        
        return locations;
    }
    
    /**
     * Load landmarks from CSV file
     * Format: id,name,category,description,locationId,importance
     */
    public static List<Landmark> loadLandmarksFromCSV(String filename, Map<String, Node> nodeMap) {
        List<Landmark> landmarks = new ArrayList<>();
        File file = new File(DATA_DIR + filename);
        
        if (!file.exists()) {
            System.out.println("File not found: " + filename + ", using sample data");
            return getSampleLandmarks(nodeMap);
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // Skip header
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String category = parts[2].trim();
                    String description = parts[3].trim();
                    String locationId = parts[4].trim();
                    double importance = Double.parseDouble(parts[5].trim());
                    
                    Node location = nodeMap.get(locationId);
                    if (location != null) {
                        Landmark landmark = new Landmark(id, name, category, description, location, importance);
                        landmarks.add(landmark);
                    }
                }
            }
            
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading landmarks file: " + e.getMessage());
            return getSampleLandmarks(nodeMap);
        }
        
        return landmarks;
    }
    
    /**
     * Save locations to CSV file
     */
    public static void saveLocationsToCSV(List<Node> locations, String filename) {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_DIR + filename))) {
            // Write header
            pw.println("id,name,latitude,longitude,description,isLandmark");
            
            // Write data
            for (Node location : locations) {
                pw.printf("%s,%s,%.6f,%.6f,%s,%b%n",
                         location.getId(),
                         location.getName(),
                         location.getLatitude(),
                         location.getLongitude(),
                         location.getDescription() != null ? location.getDescription() : "",
                         location.isLandmark());
            }
            
            System.out.println("Locations saved to " + filename);
            
        } catch (IOException e) {
            System.err.println("Error saving locations: " + e.getMessage());
        }
    }
    
    /**
     * Save landmarks to CSV file
     */
    public static void saveLandmarksToCSV(List<Landmark> landmarks, String filename) {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_DIR + filename))) {
            // Write header
            pw.println("id,name,category,description,locationId,importance");
            
            // Write data
            for (Landmark landmark : landmarks) {
                pw.printf("%s,%s,%s,%s,%s,%.2f%n",
                         landmark.getId(),
                         landmark.getName(),
                         landmark.getCategory(),
                         landmark.getDescription() != null ? landmark.getDescription() : "",
                         landmark.getLocation().getId(),
                         landmark.getImportance());
            }
            
            System.out.println("Landmarks saved to " + filename);
            
        } catch (IOException e) {
            System.err.println("Error saving landmarks: " + e.getMessage());
        }
    }
    
    /**
     * Load sample data when files are not available
     */
    private static void loadSampleData() {
        // Create sample CSV files for demonstration
        List<Node> sampleLocations = getSampleLocations();
        Map<String, Node> nodeMap = new HashMap<>();
        for (Node node : sampleLocations) {
            nodeMap.put(node.getId(), node);
        }
        
        List<Landmark> sampleLandmarks = getSampleLandmarks(nodeMap);
        
        // Save to CSV files
        saveLocationsToCSV(sampleLocations, "campus_locations.csv");
        saveLandmarksToCSV(sampleLandmarks, "landmarks.csv");

        // Create sample edges file based on the same sample graph used in RouteService
        List<Edge> sampleEdges = new ArrayList<>();
        // Safe lookup helper
        java.util.function.Function<String, Node> N = id -> nodeMap.get(id);
        // Add sample edges (sourceId, destinationId, distanceMeters, pathType, speedKmh, bidirectional)
        if (N.apply("main_gate") != null && N.apply("great_hall") != null)
            sampleEdges.add(new Edge(N.apply("main_gate"), N.apply("great_hall"), 300, 300, "walkway"));
        if (N.apply("great_hall") != null && N.apply("library") != null)
            sampleEdges.add(new Edge(N.apply("great_hall"), N.apply("library"), 200, 200, "walkway"));
        if (N.apply("library") != null && N.apply("comp_sci") != null)
            sampleEdges.add(new Edge(N.apply("library"), N.apply("comp_sci"), 150, 150, "walkway"));
        if (N.apply("main_gate") != null && N.apply("night_market") != null)
            sampleEdges.add(new Edge(N.apply("main_gate"), N.apply("night_market"), 250, 250, "walkway"));
        if (N.apply("night_market") != null && N.apply("great_hall") != null)
            sampleEdges.add(new Edge(N.apply("night_market"), N.apply("great_hall"), 200, 200, "walkway"));
        if (N.apply("great_hall") != null && N.apply("commonwealth") != null)
            sampleEdges.add(new Edge(N.apply("great_hall"), N.apply("commonwealth"), 400, 400, "walkway"));
        if (N.apply("commonwealth") != null && N.apply("legon") != null)
            sampleEdges.add(new Edge(N.apply("commonwealth"), N.apply("legon"), 100, 100, "walkway"));
        if (N.apply("legon") != null && N.apply("sports") != null)
            sampleEdges.add(new Edge(N.apply("legon"), N.apply("sports"), 200, 200, "walkway"));
        if (N.apply("comp_sci") != null && N.apply("sports") != null)
            sampleEdges.add(new Edge(N.apply("comp_sci"), N.apply("sports"), 300, 300, "walkway"));

        saveEdgesToCSV(sampleEdges, "edges.csv");
        
        // Create sample traffic patterns file
        createSampleTrafficPatternsFile();
    }

    /**
     * Load edges from CSV file
     * Format (header): sourceId,destinationId,distanceMeters,pathType,speedKmh,bidirectional
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

                // Compute distance if not provided
                double distMeters = (distance != null) ? distance :
                        DistanceCalculator.calculateHaversineDistance(src, dst);

                // Build edge with pathType first so default speed is interpreted correctly
                double weight = distMeters; // default weight: distance
                Edge edge = new Edge(src, dst, distMeters, weight, pathType);
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
     * Save edges to CSV file
     */
    public static void saveEdgesToCSV(List<Edge> edges, String filename) {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_DIR + filename))) {
            // header
            pw.println("sourceId,destinationId,distanceMeters,pathType,speedKmh,bidirectional");
            for (Edge e : edges) {
                pw.printf("%s,%s,%.1f,%s,%.1f,%b%n",
                        e.getSource().getId(),
                        e.getDestination().getId(),
                        e.getDistance(),
                        e.getPathType() != null ? e.getPathType() : "walkway",
                        0.0, // unknown speed by default
                        true);
            }
            System.out.println("Edges saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving edges: " + e.getMessage());
        }
    }

    /**
     * Build a Graph from location and edge CSVs
     */
    public static Graph loadGraphFromCSVs(String locationsFile, String edgesFile) {
        List<Node> locations = loadLocationsFromCSV(locationsFile);
        Map<String, Node> nodeMap = new HashMap<>();
        for (Node n : locations) nodeMap.put(n.getId(), n);

        Graph graph = new Graph();
        for (Node n : locations) graph.addNode(n);

        List<Edge> edges = loadEdgesFromCSV(edgesFile, nodeMap);
        for (Edge e : edges) {
            // loadEdgesFromCSV already duplicates reverse edges when bidirectional
            graph.addEdge(e, false);
        }

        return graph;
    }
    
    /**
     * Get sample UG campus locations
     */
    private static List<Node> getSampleLocations() {
        List<Node> locations = new ArrayList<>();
        
        // Main campus locations with realistic GPS coordinates
        locations.add(new Node("main_gate", "Main Gate", 5.6508, -0.1870, "University main entrance", true));
        locations.add(new Node("great_hall", "Great Hall", 5.6520, -0.1850, "Main assembly hall", true));
        locations.add(new Node("library", "Balme Library", 5.6525, -0.1845, "Main university library", true));
        locations.add(new Node("comp_sci", "Computer Science Department", 5.6530, -0.1840, "DCIT Department building", true));
        locations.add(new Node("night_market", "Night Market", 5.6515, -0.1860, "Food and shopping area", true));
        locations.add(new Node("commonwealth", "Commonwealth Hall", 5.6540, -0.1820, "Traditional residential hall", true));
        locations.add(new Node("legon", "Legon Hall", 5.6545, -0.1825, "Traditional residential hall", true));
        locations.add(new Node("sports", "Sports Complex", 5.6550, -0.1830, "Main sports facilities", true));
        locations.add(new Node("medical", "Medical Center", 5.6535, -0.1835, "University health services", true));
        locations.add(new Node("bank", "GCB Bank", 5.6518, -0.1855, "Ghana Commercial Bank", true));
        locations.add(new Node("src", "SRC Building", 5.6522, -0.1848, "Student Representative Council", true));
        locations.add(new Node("pentagon", "Pentagon", 5.6528, -0.1838, "Administrative building", true));
        locations.add(new Node("n_block", "N-Block", 5.6532, -0.1842, "Academic building", false));
        locations.add(new Node("jean_nelson", "Jean Nelson Aka Hall", 5.6548, -0.1818, "Residential hall", true));
        locations.add(new Node("unity_hall", "Unity Hall", 5.6552, -0.1822, "Residential hall", true));
        
        return locations;
    }
    
    /**
     * Get sample landmarks data
     */
    private static List<Landmark> getSampleLandmarks(Map<String, Node> nodeMap) {
        List<Landmark> landmarks = new ArrayList<>();
        
        if (nodeMap.get("main_gate") != null) {
            landmarks.add(new Landmark("lm_main_gate", "Main Gate", "Transport", 
                        "Main entrance to the university", nodeMap.get("main_gate"), 1.0));
        }
        
        if (nodeMap.get("great_hall") != null) {
            landmarks.add(new Landmark("lm_great_hall", "Great Hall", "Academic", 
                        "Main assembly and graduation hall", nodeMap.get("great_hall"), 0.9));
        }
        
        if (nodeMap.get("library") != null) {
            landmarks.add(new Landmark("lm_library", "Balme Library", "Academic", 
                        "Main university library with study areas", nodeMap.get("library"), 0.8));
        }
        
        if (nodeMap.get("comp_sci") != null) {
            landmarks.add(new Landmark("lm_comp_sci", "Computer Science Department", "Academic", 
                        "DCIT Department building", nodeMap.get("comp_sci"), 0.7));
        }
        
        if (nodeMap.get("night_market") != null) {
            landmarks.add(new Landmark("lm_night_market", "Night Market", "Dining", 
                        "Food court and shopping area", nodeMap.get("night_market"), 0.8));
        }
        
        if (nodeMap.get("commonwealth") != null) {
            landmarks.add(new Landmark("lm_commonwealth", "Commonwealth Hall", "Residential", 
                        "Traditional residential hall", nodeMap.get("commonwealth"), 0.6));
        }
        
        if (nodeMap.get("legon") != null) {
            landmarks.add(new Landmark("lm_legon", "Legon Hall", "Residential", 
                        "Traditional residential hall", nodeMap.get("legon"), 0.6));
        }
        
        if (nodeMap.get("sports") != null) {
            landmarks.add(new Landmark("lm_sports", "Sports Complex", "Recreation", 
                        "Main sports and recreation facilities", nodeMap.get("sports"), 0.7));
        }
        
        if (nodeMap.get("bank") != null) {
            landmarks.add(new Landmark("lm_bank", "GCB Bank", "Service", 
                        "Ghana Commercial Bank branch", nodeMap.get("bank"), 0.5));
        }
        
        if (nodeMap.get("medical") != null) {
            landmarks.add(new Landmark("lm_medical", "Medical Center", "Service", 
                        "University health services", nodeMap.get("medical"), 0.7));
        }
        
        return landmarks;
    }
    
    /**
     * Create sample traffic patterns file
     */
    private static void createSampleTrafficPatternsFile() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_DIR + "traffic_patterns.csv"))) {
            pw.println("route_id,time_period,multiplier,description");
            pw.println("main_gate_great_hall,07:00-09:00,1.3,Morning rush hour");
            pw.println("main_gate_great_hall,12:00-14:00,1.2,Lunch time traffic");
            pw.println("main_gate_great_hall,16:00-18:00,1.4,Evening rush hour");
            pw.println("night_market_great_hall,12:00-14:00,1.8,Lunch rush");
            pw.println("night_market_great_hall,18:00-20:00,1.6,Dinner rush");
            pw.println("great_hall_commonwealth,07:00-08:00,1.2,Morning departure");
            pw.println("great_hall_commonwealth,22:00-23:00,1.1,Night return");
            
            System.out.println("Traffic patterns saved to traffic_patterns.csv");
            
        } catch (IOException e) {
            System.err.println("Error creating traffic patterns file: " + e.getMessage());
        }
    }
    
    /**
     * Check if data files exist
     * @return true if all required data files exist
     */
    public static boolean dataFilesExist() {
        String[] requiredFiles = {"campus_locations.csv", "landmarks.csv", "traffic_patterns.csv"};
        
        for (String filename : requiredFiles) {
            File file = new File(DATA_DIR + filename);
            if (!file.exists()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Get data directory path
     */
    public static String getDataDirectory() {
        return DATA_DIR;
    }
    
    /**
     * Reset data loading flag (for testing)
     */
    public static void resetDataLoadingFlag() {
        dataLoaded = false;
    }
}
