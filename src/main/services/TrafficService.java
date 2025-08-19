package main.services;

import main.models.*;
import java.util.*;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.DayOfWeek;

/**
 * Service class for traffic-related operations
 * Manages traffic patterns and provides dynamic route weighting
 */
public class TrafficService {
    private Map<String, TrafficPattern> trafficPatterns;
    private Map<String, Double> currentTrafficMultipliers;
    private List<TrafficEvent> activeEvents;
    
    public TrafficService() {
        this.trafficPatterns = new HashMap<>();
        this.currentTrafficMultipliers = new HashMap<>();
        this.activeEvents = new ArrayList<>();
        initializeTrafficPatterns();
    }
    
    /**
     * Get current traffic multiplier for an edge
     * @param edgeId Edge identifier (source_destination)
     * @return Traffic multiplier (1.0 = normal, >1.0 = heavy traffic, <1.0 = light traffic)
     */
    public double getTrafficMultiplier(String edgeId) {
        return currentTrafficMultipliers.getOrDefault(edgeId, 1.0);
    }
    
    /**
     * Update traffic conditions based on current time and day
     */
    public void updateTrafficConditions() {
        LocalTime currentTime = LocalTime.now();
        DayOfWeek currentDay = LocalDate.now().getDayOfWeek();
        
        for (Map.Entry<String, TrafficPattern> entry : trafficPatterns.entrySet()) {
            String edgeId = entry.getKey();
            TrafficPattern pattern = entry.getValue();
            
            double multiplier = calculateTrafficMultiplier(pattern, currentTime, currentDay);
            currentTrafficMultipliers.put(edgeId, multiplier);
        }
        
        // Apply event-based traffic modifications
        applyTrafficEvents();
    }
    
    /**
     * Add a traffic event (e.g., construction, ceremony, etc.)
     * @param event Traffic event to add
     */
    public void addTrafficEvent(TrafficEvent event) {
        activeEvents.add(event);
        updateTrafficConditions();
    }
    
    /**
     * Remove expired traffic events
     */
    public void cleanupExpiredEvents() {
        LocalDate today = LocalDate.now();
        activeEvents.removeIf(event -> event.getEndDate().isBefore(today));
    }
    
    /**
     * Get adjusted weight for an edge considering traffic
     * @param edge Original edge
     * @return Adjusted weight considering current traffic conditions
     */
    public double getAdjustedWeight(Edge edge) {
        String edgeId = createEdgeId(edge);
        double trafficMultiplier = getTrafficMultiplier(edgeId);
        return edge.getWeight() * trafficMultiplier;
    }
    
    /**
     * Get traffic information for a route
     * @param route Route to analyze
     * @return Map containing traffic information
     */
    public Map<String, Object> getRouteTrafficInfo(Route route) {
        Map<String, Object> info = new HashMap<>();
        
        double totalTrafficImpact = 0.0;
        double heaviestTrafficMultiplier = 1.0;
        int heavyTrafficSegments = 0;
        
        for (Edge edge : route.getEdges()) {
            String edgeId = createEdgeId(edge);
            double multiplier = getTrafficMultiplier(edgeId);
            totalTrafficImpact += multiplier - 1.0; // Impact relative to normal
            
            if (multiplier > heaviestTrafficMultiplier) {
                heaviestTrafficMultiplier = multiplier;
            }
            
            if (multiplier > 1.2) { // Consider 20% above normal as heavy
                heavyTrafficSegments++;
            }
        }
        
        info.put("totalTrafficImpact", totalTrafficImpact);
        info.put("heaviestTrafficMultiplier", heaviestTrafficMultiplier);
        info.put("heavyTrafficSegments", heavyTrafficSegments);
        info.put("averageTrafficMultiplier", (totalTrafficImpact / route.getEdges().size()) + 1.0);
        info.put("trafficCondition", getTrafficConditionDescription(heaviestTrafficMultiplier));
        
        return info;
    }
    
    /**
     * Get optimal time to travel a route
     * @param route Route to analyze
     * @return Map containing optimal travel time information
     */
    public Map<String, Object> getOptimalTravelTime(Route route) {
        Map<String, Object> result = new HashMap<>();
        
        double minTrafficImpact = Double.MAX_VALUE;
        LocalTime optimalTime = LocalTime.of(6, 0); // Default early morning
        
        // Check different times of day
        for (int hour = 6; hour < 22; hour++) {
            LocalTime testTime = LocalTime.of(hour, 0);
            double totalImpact = 0.0;
            
            for (Edge edge : route.getEdges()) {
                String edgeId = createEdgeId(edge);
                TrafficPattern pattern = trafficPatterns.get(edgeId);
                if (pattern != null) {
                    double multiplier = calculateTrafficMultiplier(pattern, testTime, DayOfWeek.MONDAY);
                    totalImpact += multiplier - 1.0;
                }
            }
            
            if (totalImpact < minTrafficImpact) {
                minTrafficImpact = totalImpact;
                optimalTime = testTime;
            }
        }
        
        result.put("optimalTime", optimalTime);
        result.put("minTrafficImpact", minTrafficImpact);
        result.put("estimatedSavings", calculateTimeSavings(route, minTrafficImpact));
        
        return result;
    }
    
    // Private helper methods
    
    private String createEdgeId(Edge edge) {
        return edge.getSource().getId() + "_" + edge.getDestination().getId();
    }
    
    private double calculateTrafficMultiplier(TrafficPattern pattern, LocalTime time, DayOfWeek day) {
        if (!pattern.isActiveOnDay(day)) {
            return pattern.getOffDayMultiplier();
        }
        
        // Check if time falls in any peak period
        for (TrafficPeak peak : pattern.getPeakPeriods()) {
            if (timeInRange(time, peak.getStartTime(), peak.getEndTime())) {
                return peak.getMultiplier();
            }
        }
        
        return pattern.getNormalMultiplier();
    }
    
    private boolean timeInRange(LocalTime time, LocalTime start, LocalTime end) {
        if (end.isAfter(start)) {
            return !time.isBefore(start) && !time.isAfter(end);
        } else {
            // Handle overnight ranges
            return !time.isBefore(start) || !time.isAfter(end);
        }
    }
    
    private void applyTrafficEvents() {
        LocalDate today = LocalDate.now();
        
        for (TrafficEvent event : activeEvents) {
            if (!event.isActiveOn(today)) {
                continue;
            }
            
            for (String affectedEdge : event.getAffectedEdges()) {
                double currentMultiplier = currentTrafficMultipliers.getOrDefault(affectedEdge, 1.0);
                double eventMultiplier = event.getTrafficMultiplier();
                
                // Apply the more severe condition
                double finalMultiplier = Math.max(currentMultiplier, eventMultiplier);
                currentTrafficMultipliers.put(affectedEdge, finalMultiplier);
            }
        }
    }
    
    private String getTrafficConditionDescription(double multiplier) {
        if (multiplier <= 0.8) return "Light Traffic";
        if (multiplier <= 1.2) return "Normal Traffic";
        if (multiplier <= 1.5) return "Moderate Traffic";
        if (multiplier <= 2.0) return "Heavy Traffic";
        return "Very Heavy Traffic";
    }
    
    private int calculateTimeSavings(Route route, double minTrafficImpact) {
        // Estimate time savings in minutes
        double currentImpact = 0.0;
        for (Edge edge : route.getEdges()) {
            String edgeId = createEdgeId(edge);
            currentImpact += getTrafficMultiplier(edgeId) - 1.0;
        }
        
        double timeDifference = (currentImpact - minTrafficImpact) * route.getTotalTravelTime();
        return Math.max(0, (int) Math.round(timeDifference));
    }
    
    private void initializeTrafficPatterns() {
        // Initialize typical UG campus traffic patterns
        
        // Main routes with higher traffic during class hours
        TrafficPattern mainRoutes = new TrafficPattern("main_routes", 1.0, 0.8);
        mainRoutes.addPeakPeriod(new TrafficPeak(LocalTime.of(7, 0), LocalTime.of(9, 0), 1.3)); // Morning rush
        mainRoutes.addPeakPeriod(new TrafficPeak(LocalTime.of(12, 0), LocalTime.of(14, 0), 1.2)); // Lunch time
        mainRoutes.addPeakPeriod(new TrafficPeak(LocalTime.of(16, 0), LocalTime.of(18, 0), 1.4)); // Evening rush
        
        // Apply to main campus routes
        trafficPatterns.put("main_gate_great_hall", mainRoutes);
        trafficPatterns.put("great_hall_library", mainRoutes);
        trafficPatterns.put("library_comp_sci", mainRoutes);
        
        // Dining area patterns
        TrafficPattern diningRoutes = new TrafficPattern("dining_routes", 1.0, 0.9);
        diningRoutes.addPeakPeriod(new TrafficPeak(LocalTime.of(12, 0), LocalTime.of(14, 0), 1.8)); // Lunch rush
        diningRoutes.addPeakPeriod(new TrafficPeak(LocalTime.of(18, 0), LocalTime.of(20, 0), 1.6)); // Dinner rush
        
        trafficPatterns.put("night_market_great_hall", diningRoutes);
        trafficPatterns.put("main_gate_night_market", diningRoutes);
        
        // Residential area patterns
        TrafficPattern residentialRoutes = new TrafficPattern("residential_routes", 1.0, 0.7);
        residentialRoutes.addPeakPeriod(new TrafficPeak(LocalTime.of(7, 0), LocalTime.of(8, 0), 1.2)); // Morning
        residentialRoutes.addPeakPeriod(new TrafficPeak(LocalTime.of(22, 0), LocalTime.of(23, 0), 1.1)); // Night return
        
        trafficPatterns.put("great_hall_commonwealth", residentialRoutes);
        trafficPatterns.put("commonwealth_legon", residentialRoutes);
        
        updateTrafficConditions();
        
        System.out.println("Initialized traffic patterns for " + trafficPatterns.size() + " routes");
    }
    
    // Inner classes for traffic management
    
    public static class TrafficPattern {
        private String name;
        private double normalMultiplier;
        private double offDayMultiplier;
        private List<TrafficPeak> peakPeriods;
        private Set<DayOfWeek> activeDays;
        
        public TrafficPattern(String name, double normalMultiplier, double offDayMultiplier) {
            this.name = name;
            this.normalMultiplier = normalMultiplier;
            this.offDayMultiplier = offDayMultiplier;
            this.peakPeriods = new ArrayList<>();
            this.activeDays = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, 
                                       DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
        }
        
        public void addPeakPeriod(TrafficPeak peak) {
            peakPeriods.add(peak);
        }
        
        public boolean isActiveOnDay(DayOfWeek day) {
            return activeDays.contains(day);
        }
        
        // Getters
        public String getName() { return name; }
        public double getNormalMultiplier() { return normalMultiplier; }
        public double getOffDayMultiplier() { return offDayMultiplier; }
        public List<TrafficPeak> getPeakPeriods() { return peakPeriods; }
    }
    
    public static class TrafficPeak {
        private LocalTime startTime;
        private LocalTime endTime;
        private double multiplier;
        
        public TrafficPeak(LocalTime startTime, LocalTime endTime, double multiplier) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.multiplier = multiplier;
        }
        
        // Getters
        public LocalTime getStartTime() { return startTime; }
        public LocalTime getEndTime() { return endTime; }
        public double getMultiplier() { return multiplier; }
    }
    
    public static class TrafficEvent {
        private String name;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<String> affectedEdges;
        private double trafficMultiplier;
        
        public TrafficEvent(String name, String description, LocalDate startDate, LocalDate endDate, 
                           double trafficMultiplier) {
            this.name = name;
            this.description = description;
            this.startDate = startDate;
            this.endDate = endDate;
            this.trafficMultiplier = trafficMultiplier;
            this.affectedEdges = new ArrayList<>();
        }
        
        public void addAffectedEdge(String edgeId) {
            affectedEdges.add(edgeId);
        }
        
        public boolean isActiveOn(LocalDate date) {
            return !date.isBefore(startDate) && !date.isAfter(endDate);
        }
        
        // Getters
        public String getName() { return name; }
        public String getDescription() { return description; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public List<String> getAffectedEdges() { return affectedEdges; }
        public double getTrafficMultiplier() { return trafficMultiplier; }
    }
    
    // Getters
    public Map<String, Double> getCurrentTrafficMultipliers() { return new HashMap<>(currentTrafficMultipliers); }
    public List<TrafficEvent> getActiveEvents() { return new ArrayList<>(activeEvents); }
}
