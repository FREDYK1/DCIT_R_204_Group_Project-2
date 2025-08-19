package main.algorithms.sorting;

import main.models.Route;
import java.util.*;

/**
 * Implementation of Quick Sort algorithm for sorting routes
 * Time Complexity: O(n log n) average case, O(n^2) worst case
 * Space Complexity: O(log n)
 */
public class QuickSort {
    
    /**
     * Sort routes by distance using Quick Sort
     * @param routes List of routes to sort
     * @return Sorted list of routes (ascending by distance)
     */
    public static List<Route> sortByDistance(List<Route> routes) {
        if (routes == null || routes.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Route> sortedRoutes = new ArrayList<>(routes);
        quickSortByDistance(sortedRoutes, 0, sortedRoutes.size() - 1);
        return sortedRoutes;
    }
    
    /**
     * Sort routes by travel time using Quick Sort
     * @param routes List of routes to sort
     * @return Sorted list of routes (ascending by travel time)
     */
    public static List<Route> sortByTravelTime(List<Route> routes) {
        if (routes == null || routes.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Route> sortedRoutes = new ArrayList<>(routes);
        quickSortByTravelTime(sortedRoutes, 0, sortedRoutes.size() - 1);
        return sortedRoutes;
    }
    
    /**
     * Sort routes by estimated cost using Quick Sort
     * @param routes List of routes to sort
     * @return Sorted list of routes (ascending by cost)
     */
    public static List<Route> sortByCost(List<Route> routes) {
        if (routes == null || routes.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Route> sortedRoutes = new ArrayList<>(routes);
        quickSortByCost(sortedRoutes, 0, sortedRoutes.size() - 1);
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
        quickSort(sortedRoutes, 0, sortedRoutes.size() - 1, comparator);
        return sortedRoutes;
    }
    
    /**
     * Quick sort implementation for distance comparison
     */
    private static void quickSortByDistance(List<Route> routes, int low, int high) {
        if (low < high) {
            int pivotIndex = partitionByDistance(routes, low, high);
            quickSortByDistance(routes, low, pivotIndex - 1);
            quickSortByDistance(routes, pivotIndex + 1, high);
        }
    }
    
    /**
     * Quick sort implementation for travel time comparison
     */
    private static void quickSortByTravelTime(List<Route> routes, int low, int high) {
        if (low < high) {
            int pivotIndex = partitionByTravelTime(routes, low, high);
            quickSortByTravelTime(routes, low, pivotIndex - 1);
            quickSortByTravelTime(routes, pivotIndex + 1, high);
        }
    }
    
    /**
     * Quick sort implementation for cost comparison
     */
    private static void quickSortByCost(List<Route> routes, int low, int high) {
        if (low < high) {
            int pivotIndex = partitionByCost(routes, low, high);
            quickSortByCost(routes, low, pivotIndex - 1);
            quickSortByCost(routes, pivotIndex + 1, high);
        }
    }
    
    /**
     * Generic quick sort with custom comparator
     */
    private static void quickSort(List<Route> routes, int low, int high, Comparator<Route> comparator) {
        if (low < high) {
            int pivotIndex = partition(routes, low, high, comparator);
            quickSort(routes, low, pivotIndex - 1, comparator);
            quickSort(routes, pivotIndex + 1, high, comparator);
        }
    }
    
    /**
     * Partition for distance comparison
     */
    private static int partitionByDistance(List<Route> routes, int low, int high) {
        Route pivot = routes.get(high);
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (routes.get(j).getTotalDistance() <= pivot.getTotalDistance()) {
                i++;
                Collections.swap(routes, i, j);
            }
        }
        
        Collections.swap(routes, i + 1, high);
        return i + 1;
    }
    
    /**
     * Partition for travel time comparison
     */
    private static int partitionByTravelTime(List<Route> routes, int low, int high) {
        Route pivot = routes.get(high);
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (routes.get(j).getTotalTravelTime() <= pivot.getTotalTravelTime()) {
                i++;
                Collections.swap(routes, i, j);
            }
        }
        
        Collections.swap(routes, i + 1, high);
        return i + 1;
    }
    
    /**
     * Partition for cost comparison
     */
    private static int partitionByCost(List<Route> routes, int low, int high) {
        Route pivot = routes.get(high);
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (routes.get(j).getEstimatedCost() <= pivot.getEstimatedCost()) {
                i++;
                Collections.swap(routes, i, j);
            }
        }
        
        Collections.swap(routes, i + 1, high);
        return i + 1;
    }
    
    /**
     * Generic partition with custom comparator
     */
    private static int partition(List<Route> routes, int low, int high, Comparator<Route> comparator) {
        Route pivot = routes.get(high);
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (comparator.compare(routes.get(j), pivot) <= 0) {
                i++;
                Collections.swap(routes, i, j);
            }
        }
        
        Collections.swap(routes, i + 1, high);
        return i + 1;
    }
    
    /**
     * Sort routes by multiple criteria (distance first, then time)
     * @param routes List of routes to sort
     * @return Sorted list of routes
     */
    public static List<Route> sortByDistanceAndTime(List<Route> routes) {
        Comparator<Route> multiCriteriaComparator = (r1, r2) -> {
            int distanceComparison = Double.compare(r1.getTotalDistance(), r2.getTotalDistance());
            if (distanceComparison != 0) {
                return distanceComparison;
            }
            return Integer.compare(r1.getTotalTravelTime(), r2.getTotalTravelTime());
        };
        
        return sort(routes, multiCriteriaComparator);
    }
    
    /**
     * Sort routes by landmark count (routes with more landmarks first)
     * @param routes List of routes to sort
     * @return Sorted list of routes
     */
    public static List<Route> sortByLandmarkCount(List<Route> routes) {
        Comparator<Route> landmarkComparator = (r1, r2) -> 
            Integer.compare(r2.getLandmarksOnRoute().size(), r1.getLandmarksOnRoute().size());
        
        return sort(routes, landmarkComparator);
    }
    
    /**
     * Performance test for Quick Sort
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
     * Utility method to shuffle routes for testing
     * @param routes List of routes to shuffle
     * @return Shuffled list of routes
     */
    public static List<Route> shuffle(List<Route> routes) {
        List<Route> shuffled = new ArrayList<>(routes);
        Collections.shuffle(shuffled);
        return shuffled;
    }
}
