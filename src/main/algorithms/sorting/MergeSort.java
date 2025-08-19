package main.algorithms.sorting;

import main.models.Route;
import java.util.*;

/**
 * Implementation of Merge Sort algorithm for sorting routes
 * Time Complexity: O(n log n) guaranteed
 * Space Complexity: O(n)
 * Stable sorting algorithm
 */
public class MergeSort {
    
    /**
     * Sort routes by distance using Merge Sort
     * @param routes List of routes to sort
     * @return Sorted list of routes (ascending by distance)
     */
    public static List<Route> sortByDistance(List<Route> routes) {
        if (routes == null || routes.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Route> sortedRoutes = new ArrayList<>(routes);
        mergeSortByDistance(sortedRoutes, 0, sortedRoutes.size() - 1);
        return sortedRoutes;
    }
    
    /**
     * Sort routes by travel time using Merge Sort
     * @param routes List of routes to sort
     * @return Sorted list of routes (ascending by travel time)
     */
    public static List<Route> sortByTravelTime(List<Route> routes) {
        if (routes == null || routes.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Route> sortedRoutes = new ArrayList<>(routes);
        mergeSortByTravelTime(sortedRoutes, 0, sortedRoutes.size() - 1);
        return sortedRoutes;
    }
    
    /**
     * Sort routes by time using Merge Sort (alias for sortByTravelTime)
     * @param routes List of routes to sort
     * @return Sorted list of routes (ascending by travel time)
     */
    public static List<Route> sortByTime(List<Route> routes) {
        return sortByTravelTime(routes);
    }
    
    /**
     * Sort routes by estimated cost using Merge Sort
     * @param routes List of routes to sort
     * @return Sorted list of routes (ascending by cost)
     */
    public static List<Route> sortByCost(List<Route> routes) {
        if (routes == null || routes.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Route> sortedRoutes = new ArrayList<>(routes);
        mergeSortByCost(sortedRoutes, 0, sortedRoutes.size() - 1);
        return sortedRoutes;
    }
    
    /**
     * Generic sort with custom comparator
     * @param routes List of routes to sort
     * @param comparator Custom comparator for routes
     * @return Sorted list of routes
     */
    public static List<Route> sort(List<Route> routes, Comparator<Route> comparator) {
        if (routes == null || routes.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Route> sortedRoutes = new ArrayList<>(routes);
        mergeSort(sortedRoutes, 0, sortedRoutes.size() - 1, comparator);
        return sortedRoutes;
    }
    
    /**
     * Merge sort implementation for distance comparison
     */
    private static void mergeSortByDistance(List<Route> routes, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            
            mergeSortByDistance(routes, left, mid);
            mergeSortByDistance(routes, mid + 1, right);
            
            mergeByDistance(routes, left, mid, right);
        }
    }
    
    /**
     * Merge sort implementation for travel time comparison
     */
    private static void mergeSortByTravelTime(List<Route> routes, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            
            mergeSortByTravelTime(routes, left, mid);
            mergeSortByTravelTime(routes, mid + 1, right);
            
            mergeByTravelTime(routes, left, mid, right);
        }
    }
    
    /**
     * Merge sort implementation for cost comparison
     */
    private static void mergeSortByCost(List<Route> routes, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            
            mergeSortByCost(routes, left, mid);
            mergeSortByCost(routes, mid + 1, right);
            
            mergeByCost(routes, left, mid, right);
        }
    }
    
    /**
     * Generic merge sort with custom comparator
     */
    private static void mergeSort(List<Route> routes, int left, int right, Comparator<Route> comparator) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            
            mergeSort(routes, left, mid, comparator);
            mergeSort(routes, mid + 1, right, comparator);
            
            merge(routes, left, mid, right, comparator);
        }
    }
    
    /**
     * Merge operation for distance comparison
     */
    private static void mergeByDistance(List<Route> routes, int left, int mid, int right) {
        // Create temporary arrays
        List<Route> leftArray = new ArrayList<>();
        List<Route> rightArray = new ArrayList<>();
        
        // Copy data to temporary arrays
        for (int i = left; i <= mid; i++) {
            leftArray.add(routes.get(i));
        }
        for (int j = mid + 1; j <= right; j++) {
            rightArray.add(routes.get(j));
        }
        
        // Merge the temporary arrays
        int i = 0, j = 0, k = left;
        
        while (i < leftArray.size() && j < rightArray.size()) {
            if (leftArray.get(i).getTotalDistance() <= rightArray.get(j).getTotalDistance()) {
                routes.set(k, leftArray.get(i));
                i++;
            } else {
                routes.set(k, rightArray.get(j));
                j++;
            }
            k++;
        }
        
        // Copy remaining elements
        while (i < leftArray.size()) {
            routes.set(k, leftArray.get(i));
            i++;
            k++;
        }
        
        while (j < rightArray.size()) {
            routes.set(k, rightArray.get(j));
            j++;
            k++;
        }
    }
    
    /**
     * Merge operation for travel time comparison
     */
    private static void mergeByTravelTime(List<Route> routes, int left, int mid, int right) {
        List<Route> leftArray = new ArrayList<>();
        List<Route> rightArray = new ArrayList<>();
        
        for (int i = left; i <= mid; i++) {
            leftArray.add(routes.get(i));
        }
        for (int j = mid + 1; j <= right; j++) {
            rightArray.add(routes.get(j));
        }
        
        int i = 0, j = 0, k = left;
        
        while (i < leftArray.size() && j < rightArray.size()) {
            if (leftArray.get(i).getTotalTravelTime() <= rightArray.get(j).getTotalTravelTime()) {
                routes.set(k, leftArray.get(i));
                i++;
            } else {
                routes.set(k, rightArray.get(j));
                j++;
            }
            k++;
        }
        
        while (i < leftArray.size()) {
            routes.set(k, leftArray.get(i));
            i++;
            k++;
        }
        
        while (j < rightArray.size()) {
            routes.set(k, rightArray.get(j));
            j++;
            k++;
        }
    }
    
    /**
     * Merge operation for cost comparison
     */
    private static void mergeByCost(List<Route> routes, int left, int mid, int right) {
        List<Route> leftArray = new ArrayList<>();
        List<Route> rightArray = new ArrayList<>();
        
        for (int i = left; i <= mid; i++) {
            leftArray.add(routes.get(i));
        }
        for (int j = mid + 1; j <= right; j++) {
            rightArray.add(routes.get(j));
        }
        
        int i = 0, j = 0, k = left;
        
        while (i < leftArray.size() && j < rightArray.size()) {
            if (leftArray.get(i).getEstimatedCost() <= rightArray.get(j).getEstimatedCost()) {
                routes.set(k, leftArray.get(i));
                i++;
            } else {
                routes.set(k, rightArray.get(j));
                j++;
            }
            k++;
        }
        
        while (i < leftArray.size()) {
            routes.set(k, leftArray.get(i));
            i++;
            k++;
        }
        
        while (j < rightArray.size()) {
            routes.set(k, rightArray.get(j));
            j++;
            k++;
        }
    }
    
    /**
     * Generic merge operation with custom comparator
     */
    private static void merge(List<Route> routes, int left, int mid, int right, Comparator<Route> comparator) {
        List<Route> leftArray = new ArrayList<>();
        List<Route> rightArray = new ArrayList<>();
        
        for (int i = left; i <= mid; i++) {
            leftArray.add(routes.get(i));
        }
        for (int j = mid + 1; j <= right; j++) {
            rightArray.add(routes.get(j));
        }
        
        int i = 0, j = 0, k = left;
        
        while (i < leftArray.size() && j < rightArray.size()) {
            if (comparator.compare(leftArray.get(i), rightArray.get(j)) <= 0) {
                routes.set(k, leftArray.get(i));
                i++;
            } else {
                routes.set(k, rightArray.get(j));
                j++;
            }
            k++;
        }
        
        while (i < leftArray.size()) {
            routes.set(k, leftArray.get(i));
            i++;
            k++;
        }
        
        while (j < rightArray.size()) {
            routes.set(k, rightArray.get(j));
            j++;
            k++;
        }
    }
    
    /**
     * Sort routes by multiple criteria with stable sorting
     * @param routes List of routes to sort
     * @return Sorted list of routes
     */
    public static List<Route> sortByMultipleCriteria(List<Route> routes) {
        if (routes == null || routes.isEmpty()) {
            return new ArrayList<>();
        }
        
        // First sort by time (secondary criteria)
        List<Route> sortedRoutes = sortByTravelTime(routes);
        
        // Then sort by distance (primary criteria) - merge sort is stable
        return sortByDistance(sortedRoutes);
    }
    
    /**
     * Sort routes by preference score (weighted combination)
     * @param routes List of routes to sort
     * @param distanceWeight Weight for distance (0.0 - 1.0)
     * @param timeWeight Weight for time (0.0 - 1.0)
     * @param landmarkWeight Weight for landmark count (0.0 - 1.0)
     * @return Sorted list of routes
     */
    public static List<Route> sortByPreferenceScore(List<Route> routes, 
                                                   double distanceWeight, 
                                                   double timeWeight, 
                                                   double landmarkWeight) {
        Comparator<Route> preferenceComparator = (r1, r2) -> {
            double score1 = calculatePreferenceScore(r1, distanceWeight, timeWeight, landmarkWeight);
            double score2 = calculatePreferenceScore(r2, distanceWeight, timeWeight, landmarkWeight);
            return Double.compare(score1, score2);
        };
        
        return sort(routes, preferenceComparator);
    }
    
    /**
     * Calculate preference score for a route
     */
    private static double calculatePreferenceScore(Route route, double distanceWeight, 
                                                 double timeWeight, double landmarkWeight) {
        double normalizedDistance = route.getTotalDistance() / 1000.0; // Normalize to km
        double normalizedTime = route.getTotalTravelTime() / 60.0; // Normalize to hours
        double normalizedLandmarks = 1.0 / (1.0 + route.getLandmarksOnRoute().size()); // Inverse for more landmarks = better
        
        return (normalizedDistance * distanceWeight) + 
               (normalizedTime * timeWeight) + 
               (normalizedLandmarks * landmarkWeight);
    }
    
    /**
     * Performance test for Merge Sort
     * @param routes List of routes to test with
     * @return Execution time in milliseconds
     */
    public static long performanceTest(List<Route> routes) {
        if (routes == null || routes.isEmpty()) {
            return 0;
        }
        
        List<Route> testRoutes = new ArrayList<>(routes);
        long startTime = System.nanoTime();
        sortByDistance(testRoutes);
        long endTime = System.nanoTime();
        
        return (endTime - startTime) / 1_000_000; // Convert to milliseconds
    }
    
    /**
     * Compare Merge Sort vs Quick Sort performance
     * @param routes List of routes to test with
     * @return Map containing performance results
     */
    public static Map<String, Long> compareWithQuickSort(List<Route> routes) {
        Map<String, Long> results = new HashMap<>();
        
        results.put("MergeSort", performanceTest(routes));
        results.put("QuickSort", QuickSort.performanceTest(routes));
        
        return results;
    }
}
