package main.utils;

import main.models.Route;

/**
 * Utility class for time-related calculations in route planning
 */
public class TimeCalculator {
    
    // Default walking speeds in km/h for different terrains
    private static final double DEFAULT_WALKING_SPEED = 5.0; // km/h
    private static final double SLOW_WALKING_SPEED = 3.0;    // uphill, stairs
    private static final double FAST_WALKING_SPEED = 6.0;    // downhill, flat
    
    // Time penalties for different path types (in minutes)
    private static final int STAIRS_PENALTY = 2;
    private static final int CONSTRUCTION_PENALTY = 5;
    private static final int CROWDED_AREA_PENALTY = 3;
    
    /**
     * Calculate walking time based on distance and terrain
     * @param distanceMeters Distance in meters
     * @param pathType Type of path ("walkway", "stairs", "road", etc.)
     * @return Estimated walking time in minutes
     */
    public static int calculateWalkingTime(double distanceMeters, String pathType) {
        if (distanceMeters <= 0) {
            return 0;
        }
        
        double speed = getWalkingSpeedForPathType(pathType);
        double distanceKm = distanceMeters / 1000.0;
        double timeHours = distanceKm / speed;
        int baseTimeMinutes = (int) Math.ceil(timeHours * 60);
        
        // Add penalties for specific path types
        int penalty = getPenaltyForPathType(pathType);
        
        return baseTimeMinutes + penalty;
    }
    
    /**
     * Calculate walking time with custom speed
     * @param distanceMeters Distance in meters
     * @param speedKmh Walking speed in km/h
     * @return Estimated walking time in minutes
     */
    public static int calculateWalkingTime(double distanceMeters, double speedKmh) {
        if (distanceMeters <= 0 || speedKmh <= 0) {
            return 0;
        }
        
        double distanceKm = distanceMeters / 1000.0;
        double timeHours = distanceKm / speedKmh;
        return (int) Math.ceil(timeHours * 60);
    }
    
    /**
     * Calculate total travel time for a route considering traffic
     * @param route Route to analyze
     * @param trafficMultiplier Traffic multiplier (1.0 = normal, >1.0 = slower)
     * @return Total travel time in minutes
     */
    public static int calculateRouteTime(Route route, double trafficMultiplier) {
        if (route == null || route.getEdges().isEmpty()) {
            return 0;
        }
        
        int totalTime = 0;
        
        for (var edge : route.getEdges()) {
            int edgeTime = calculateWalkingTime(edge.getDistance(), edge.getPathType());
            totalTime += edgeTime;
        }
        
        // Apply traffic multiplier
        totalTime = (int) Math.ceil(totalTime * trafficMultiplier);
        
        return totalTime;
    }
    
    /**
     * Calculate arrival time given start time and travel duration
     * @param startHour Starting hour (0-23)
     * @param startMinute Starting minute (0-59)
     * @param travelTimeMinutes Travel time in minutes
     * @return Array containing [arrivalHour, arrivalMinute]
     */
    public static int[] calculateArrivalTime(int startHour, int startMinute, int travelTimeMinutes) {
        int totalStartMinutes = startHour * 60 + startMinute;
        int totalArrivalMinutes = totalStartMinutes + travelTimeMinutes;
        
        int arrivalHour = (totalArrivalMinutes / 60) % 24;
        int arrivalMinute = totalArrivalMinutes % 60;
        
        return new int[]{arrivalHour, arrivalMinute};
    }
    
    /**
     * Format time in minutes to human-readable string
     * @param minutes Time in minutes
     * @return Formatted time string
     */
    public static String formatTime(int minutes) {
        if (minutes < 1) {
            return "< 1 minute";
        } else if (minutes == 1) {
            return "1 minute";
        } else if (minutes < 60) {
            return minutes + " minutes";
        } else {
            int hours = minutes / 60;
            int remainingMinutes = minutes % 60;
            
            if (remainingMinutes == 0) {
                return hours == 1 ? "1 hour" : hours + " hours";
            } else {
                return String.format("%d hour%s %d minute%s", 
                                   hours, hours == 1 ? "" : "s",
                                   remainingMinutes, remainingMinutes == 1 ? "" : "s");
            }
        }
    }
    
    /**
     * Format time as HH:MM
     * @param hour Hour (0-23)
     * @param minute Minute (0-59)
     * @return Formatted time string
     */
    public static String formatTime(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }
    
    /**
     * Calculate optimal departure time to arrive at a specific time
     * @param targetHour Target arrival hour
     * @param targetMinute Target arrival minute
     * @param travelTimeMinutes Required travel time
     * @return Array containing [departureHour, departureMinute]
     */
    public static int[] calculateDepartureTime(int targetHour, int targetMinute, int travelTimeMinutes) {
        int targetTotalMinutes = targetHour * 60 + targetMinute;
        int departureTotalMinutes = targetTotalMinutes - travelTimeMinutes;
        
        // Handle negative times (previous day)
        if (departureTotalMinutes < 0) {
            departureTotalMinutes += 24 * 60;
        }
        
        int departureHour = (departureTotalMinutes / 60) % 24;
        int departureMinute = departureTotalMinutes % 60;
        
        return new int[]{departureHour, departureMinute};
    }
    
    /**
     * Calculate time savings between two routes
     * @param route1 First route
     * @param route2 Second route
     * @return Time difference in minutes (positive if route1 is faster)
     */
    public static int calculateTimeSavings(Route route1, Route route2) {
        if (route1 == null || route2 == null) {
            return 0;
        }
        
        return route2.getTotalTravelTime() - route1.getTotalTravelTime();
    }
    
    /**
     * Estimate time based on crowd density
     * @param baseTimeMinutes Base travel time
     * @param crowdDensity Crowd density factor (0.0 = empty, 1.0 = very crowded)
     * @return Adjusted time considering crowds
     */
    public static int adjustTimeForCrowds(int baseTimeMinutes, double crowdDensity) {
        if (crowdDensity < 0) crowdDensity = 0;
        if (crowdDensity > 1) crowdDensity = 1;
        
        // Crowds can slow down travel by up to 50%
        double crowdMultiplier = 1.0 + (crowdDensity * 0.5);
        return (int) Math.ceil(baseTimeMinutes * crowdMultiplier);
    }
    
    /**
     * Calculate time for different mobility options
     * @param distanceMeters Distance in meters
     * @param mobilityOption "walking", "wheelchair", "bicycle", etc.
     * @return Travel time in minutes
     */
    public static int calculateTimeForMobility(double distanceMeters, String mobilityOption) {
        double speed;
        
        switch (mobilityOption.toLowerCase()) {
            case "walking":
                speed = DEFAULT_WALKING_SPEED;
                break;
            case "wheelchair":
                speed = 3.5; // Slightly slower than walking
                break;
            case "bicycle":
                speed = 15.0; // Much faster
                break;
            case "running":
                speed = 10.0;
                break;
            case "elderly":
            case "slow_walking":
                speed = SLOW_WALKING_SPEED;
                break;
            default:
                speed = DEFAULT_WALKING_SPEED;
        }
        
        return calculateWalkingTime(distanceMeters, speed);
    }
    
    /**
     * Get time period description
     * @param hour Hour (0-23)
     * @return Time period description
     */
    public static String getTimePeriod(int hour) {
        if (hour >= 5 && hour < 12) {
            return "Morning";
        } else if (hour >= 12 && hour < 17) {
            return "Afternoon";
        } else if (hour >= 17 && hour < 21) {
            return "Evening";
        } else {
            return "Night";
        }
    }
    
    /**
     * Check if time is within business hours
     * @param hour Hour (0-23)
     * @param minute Minute (0-59)
     * @return true if within typical business hours (8 AM - 6 PM)
     */
    public static boolean isBusinessHours(int hour, int minute) {
        int totalMinutes = hour * 60 + minute;
        int startMinutes = 8 * 60; // 8:00 AM
        int endMinutes = 18 * 60;  // 6:00 PM
        
        return totalMinutes >= startMinutes && totalMinutes < endMinutes;
    }
    
    /**
     * Calculate average speed for a route
     * @param route Route to analyze
     * @return Average speed in km/h
     */
    public static double calculateAverageSpeed(Route route) {
        if (route == null || route.getTotalDistance() <= 0 || route.getTotalTravelTime() <= 0) {
            return 0.0;
        }
        
        double distanceKm = route.getTotalDistance() / 1000.0;
        double timeHours = route.getTotalTravelTime() / 60.0;
        
        return distanceKm / timeHours;
    }
    
    // Private helper methods
    
    private static double getWalkingSpeedForPathType(String pathType) {
        if (pathType == null) {
            return DEFAULT_WALKING_SPEED;
        }
        
        switch (pathType.toLowerCase()) {
            case "stairs":
            case "uphill":
                return SLOW_WALKING_SPEED;
            case "downhill":
            case "flat":
                return FAST_WALKING_SPEED;
            case "rough":
            case "construction":
                return SLOW_WALKING_SPEED;
            default:
                return DEFAULT_WALKING_SPEED;
        }
    }
    
    private static int getPenaltyForPathType(String pathType) {
        if (pathType == null) {
            return 0;
        }
        
        switch (pathType.toLowerCase()) {
            case "stairs":
                return STAIRS_PENALTY;
            case "construction":
                return CONSTRUCTION_PENALTY;
            case "crowded":
            case "busy":
                return CROWDED_AREA_PENALTY;
            default:
                return 0;
        }
    }
    
    /**
     * Time zone utility - convert time to different time zones if needed
     * @param hour Original hour
     * @param minute Original minute
     * @param timezoneOffset Timezone offset in hours
     * @return Adjusted time array [hour, minute]
     */
    public static int[] convertTimezone(int hour, int minute, int timezoneOffset) {
        int totalMinutes = hour * 60 + minute + (timezoneOffset * 60);
        
        // Handle day boundaries
        while (totalMinutes < 0) {
            totalMinutes += 24 * 60;
        }
        while (totalMinutes >= 24 * 60) {
            totalMinutes -= 24 * 60;
        }
        
        return new int[]{totalMinutes / 60, totalMinutes % 60};
    }
    
    /**
     * Calculate buffer time for important appointments
     * @param baseTimeMinutes Base travel time
     * @param importanceLevel Importance level (1-5, where 5 is most important)
     * @return Recommended buffer time in minutes
     */
    public static int calculateBufferTime(int baseTimeMinutes, int importanceLevel) {
        // Buffer time increases with importance and base time
        double bufferPercentage = 0.1 + (importanceLevel * 0.05); // 15% to 35%
        int minBuffer = importanceLevel * 2; // 2-10 minutes minimum
        
        int calculatedBuffer = (int) Math.ceil(baseTimeMinutes * bufferPercentage);
        return Math.max(calculatedBuffer, minBuffer);
    }
}
