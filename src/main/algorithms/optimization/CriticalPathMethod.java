package main.algorithms.optimization;

import java.util.*;

/**
 * Critical Path Method (CPM) for project scheduling and route optimization
 * Used to find the longest path through a network of activities
 */
public class CriticalPathMethod {
    
    /**
     * Represents an activity in the project network
     */
    public static class Activity {
        public String id;
        public String name;
        public int duration;
        public List<String> predecessors;
        public int earliestStart;
        public int earliestFinish;
        public int latestStart;
        public int latestFinish;
        public int slack;
        
        public Activity(String id, String name, int duration) {
            this.id = id;
            this.name = name;
            this.duration = duration;
            this.predecessors = new ArrayList<>();
            this.earliestStart = 0;
            this.earliestFinish = 0;
            this.latestStart = 0;
            this.latestFinish = 0;
            this.slack = 0;
        }
        
        public void addPredecessor(String predId) {
            predecessors.add(predId);
        }
        
        public boolean isCritical() {
            return slack == 0;
        }
    }
    
    /**
     * Find critical path in a project network
     * @param activities List of activities
     * @return Critical path activities
     */
    public static List<Activity> findCriticalPath(List<Activity> activities) {
        // Forward pass - calculate earliest start and finish times
        forwardPass(activities);
        
        // Backward pass - calculate latest start and finish times
        backwardPass(activities);
        
        // Calculate slack for each activity
        calculateSlack(activities);
        
        // Find critical path
        List<Activity> criticalPath = new ArrayList<>();
        for (Activity activity : activities) {
            if (activity.isCritical()) {
                criticalPath.add(activity);
            }
        }
        
        // Sort by earliest start time
        criticalPath.sort((a, b) -> Integer.compare(a.earliestStart, b.earliestStart));
        
        return criticalPath;
    }
    
    /**
     * Forward pass to calculate earliest start and finish times
     */
    private static void forwardPass(List<Activity> activities) {
        Map<String, Activity> activityMap = new HashMap<>();
        for (Activity activity : activities) {
            activityMap.put(activity.id, activity);
        }
        
        // Find activities with no predecessors (start activities)
        List<Activity> startActivities = new ArrayList<>();
        for (Activity activity : activities) {
            if (activity.predecessors.isEmpty()) {
                startActivities.add(activity);
            }
        }
        
        // Process activities in topological order
        Set<String> processed = new HashSet<>();
        Queue<Activity> queue = new LinkedList<>(startActivities);
        
        while (!queue.isEmpty()) {
            Activity current = queue.poll();
            
            if (processed.contains(current.id)) continue;
            processed.add(current.id);
            
            // Calculate earliest start time
            if (current.predecessors.isEmpty()) {
                current.earliestStart = 0;
            } else {
                int maxEarliestFinish = 0;
                for (String predId : current.predecessors) {
                    Activity pred = activityMap.get(predId);
                    if (pred != null) {
                        maxEarliestFinish = Math.max(maxEarliestFinish, pred.earliestFinish);
                    }
                }
                current.earliestStart = maxEarliestFinish;
            }
            
            current.earliestFinish = current.earliestStart + current.duration;
            
            // Add successors to queue
            for (Activity activity : activities) {
                if (activity.predecessors.contains(current.id)) {
                    boolean allPredecessorsProcessed = true;
                    for (String predId : activity.predecessors) {
                        if (!processed.contains(predId)) {
                            allPredecessorsProcessed = false;
                            break;
                        }
                    }
                    if (allPredecessorsProcessed) {
                        queue.offer(activity);
                    }
                }
            }
        }
    }
    
    /**
     * Backward pass to calculate latest start and finish times
     */
    private static void backwardPass(List<Activity> activities) {
        Map<String, Activity> activityMap = new HashMap<>();
        for (Activity activity : activities) {
            activityMap.put(activity.id, activity);
        }
        
        // Find project completion time
        int projectDuration = 0;
        for (Activity activity : activities) {
            projectDuration = Math.max(projectDuration, activity.earliestFinish);
        }
        
        // Find activities with no successors (end activities)
        Set<String> allActivities = new HashSet<>();
        Set<String> predecessors = new HashSet<>();
        
        for (Activity activity : activities) {
            allActivities.add(activity.id);
            predecessors.addAll(activity.predecessors);
        }
        
        List<Activity> endActivities = new ArrayList<>();
        for (Activity activity : activities) {
            if (!predecessors.contains(activity.id)) {
                // This activity is not a predecessor of any other activity
                boolean hasSuccessors = false;
                for (Activity other : activities) {
                    if (other.predecessors.contains(activity.id)) {
                        hasSuccessors = true;
                        break;
                    }
                }
                if (!hasSuccessors) {
                    endActivities.add(activity);
                }
            }
        }
        
        // Process activities in reverse topological order
        Set<String> processed = new HashSet<>();
        Queue<Activity> queue = new LinkedList<>(endActivities);
        
        while (!queue.isEmpty()) {
            Activity current = queue.poll();
            
            if (processed.contains(current.id)) continue;
            processed.add(current.id);
            
            // Calculate latest finish time
            if (endActivities.contains(current)) {
                current.latestFinish = projectDuration;
            } else {
                int minLatestStart = Integer.MAX_VALUE;
                for (Activity activity : activities) {
                    if (activity.predecessors.contains(current.id)) {
                        minLatestStart = Math.min(minLatestStart, activity.latestStart);
                    }
                }
                current.latestFinish = minLatestStart;
            }
            
            current.latestStart = current.latestFinish - current.duration;
            
            // Add predecessors to queue
            for (String predId : current.predecessors) {
                Activity pred = activityMap.get(predId);
                if (pred != null) {
                    boolean allSuccessorsProcessed = true;
                    for (Activity activity : activities) {
                        if (activity.predecessors.contains(pred.id) && !processed.contains(activity.id)) {
                            allSuccessorsProcessed = false;
                            break;
                        }
                    }
                    if (allSuccessorsProcessed) {
                        queue.offer(pred);
                    }
                }
            }
        }
    }
    
    /**
     * Calculate slack for each activity
     */
    private static void calculateSlack(List<Activity> activities) {
        for (Activity activity : activities) {
            activity.slack = activity.latestStart - activity.earliestStart;
        }
    }
    
    /**
     * Print critical path analysis results
     */
    public static void printCriticalPathAnalysis(List<Activity> activities) {
        System.out.println("Critical Path Method Analysis:");
        System.out.println("==============================");
        
        System.out.println("\nActivity Details:");
        System.out.printf("%-10s %-20s %-8s %-8s %-8s %-8s %-8s %-8s\n", 
                         "ID", "Name", "Duration", "ES", "EF", "LS", "LF", "Slack");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (Activity activity : activities) {
            String critical = activity.isCritical() ? "*" : "";
            System.out.printf("%-10s %-20s %-8d %-8d %-8d %-8d %-8d %-8d %s\n",
                            activity.id, activity.name, activity.duration,
                            activity.earliestStart, activity.earliestFinish,
                            activity.latestStart, activity.latestFinish,
                            activity.slack, critical);
        }
        
        List<Activity> criticalPath = findCriticalPath(activities);
        
        System.out.println("\nCritical Path:");
        System.out.println("===============");
        for (int i = 0; i < criticalPath.size(); i++) {
            Activity activity = criticalPath.get(i);
            if (i > 0) System.out.print(" → ");
            System.out.print(activity.name);
        }
        
        int projectDuration = 0;
        for (Activity activity : activities) {
            projectDuration = Math.max(projectDuration, activity.earliestFinish);
        }
        
        System.out.println("\n\nProject Duration: " + projectDuration + " time units");
    }
    
    /**
     * Example: Campus route planning using CPM
     */
    public static void campusRoutePlanningExample() {
        List<Activity> activities = new ArrayList<>();
        
        // Define activities for campus navigation
        Activity a1 = new Activity("A1", "Start from Main Gate", 0);
        Activity a2 = new Activity("A2", "Walk to Great Hall", 5);
        Activity a3 = new Activity("A3", "Visit Library", 10);
        Activity a4 = new Activity("A4", "Go to Computer Science Dept", 8);
        Activity a5 = new Activity("A5", "Stop at Bank", 3);
        Activity a6 = new Activity("A6", "Visit Night Market", 7);
        Activity a7 = new Activity("A7", "Reach Destination", 0);
        
        // Define dependencies
        a2.addPredecessor("A1");
        a3.addPredecessor("A2");
        a4.addPredecessor("A3");
        a5.addPredecessor("A2");
        a6.addPredecessor("A5");
        a7.addPredecessor("A4");
        a7.addPredecessor("A6");
        
        activities.addAll(Arrays.asList(a1, a2, a3, a4, a5, a6, a7));
        
        System.out.println("Campus Route Planning using Critical Path Method:");
        System.out.println("=================================================");
        printCriticalPathAnalysis(activities);
    }
} 