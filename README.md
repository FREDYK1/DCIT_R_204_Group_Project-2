# UG Navigate: Optimal Routing Solution for University of Ghana Campus

## Project Overview

UG Navigate is a comprehensive Java application designed to help users find the best routes from one location to another on the University of Ghana campus. The system incorporates various algorithms and techniques taught in DCIT 204 - Data Structures and Algorithms 1, including pathfinding algorithms, sorting algorithms, and optimization methods.

## Features

### 🗺️ **Pathfinding Algorithms**
- **Dijkstra's Algorithm**: Finds the shortest path between nodes in a graph
- **A* (A Star) Algorithm**: Optimal pathfinding with heuristic estimates
- **Floyd-Warshall Algorithm**: Calculates shortest paths between all pairs of nodes

### 📊 **Sorting Algorithms**
- **Quick Sort**: Efficient sorting of routes by distance
- **Merge Sort**: Stable sorting of routes by time and multiple criteria
- **Multi-criteria Sorting**: Combines distance, time, and landmark factors

### ⚡ **Optimization Algorithms**
- **Vogel Approximation Method**: Transportation optimization for route distribution
- **Northwest Corner Method**: Initial basic feasible solution for transportation problems
- **Critical Path Method**: Project scheduling and route optimization

### 🔍 **Advanced Features**
- **Landmark-based Route Search**: Find routes passing through specific landmarks
- **Traffic Analysis**: Real-time traffic consideration for optimal routing
- **Multiple Route Options**: Compare different route alternatives
- **Interactive GUI**: User-friendly interface for route planning

## Campus Locations

The system includes realistic University of Ghana campus locations:

### Academic Buildings
- **Great Hall**: Main assembly and graduation hall
- **Balme Library**: Main university library
- **Computer Science Department**: DCIT Department building
- **N-Block**: Academic building

### Residential Halls
- **Commonwealth Hall**: Traditional residential hall
- **Legon Hall**: Traditional residential hall
- **Jean Nelson Aka Hall**: Residential hall
- **Unity Hall**: Residential hall

### Services & Facilities
- **Main Gate**: University main entrance
- **Night Market**: Food and shopping area
- **Sports Complex**: Main sports facilities
- **Medical Center**: University health services
- **GCB Bank**: Ghana Commercial Bank
- **SRC Building**: Student Representative Council
- **Pentagon**: Administrative building

## Algorithm Demonstrations

### 1. Pathfinding Algorithms
```java
// Dijkstra's Algorithm
Route dijkstraRoute = DijkstraAlgorithm.findShortestPath(graph, sourceId, destinationId);

// A* Algorithm
Route astarRoute = AStarAlgorithm.findOptimalPath(graph, sourceId, destinationId);

// Floyd-Warshall Algorithm
Route floydRoute = FloydWarshallAlgorithm.getShortestPath(graph, sourceId, destinationId);
```

### 2. Sorting Algorithms
```java
// Quick Sort by distance
List<Route> quickSorted = QuickSort.sortByDistance(routes);

// Merge Sort by time
List<Route> mergeSorted = MergeSort.sortByTime(routes);

// Multi-criteria sorting
List<Route> multiSorted = MergeSort.sortByMultipleCriteria(routes);
```

### 3. Optimization Algorithms
```java
// Vogel Approximation Method
double[][] solution = VogelApproximation.solve(costs, supply, demand);

// Northwest Corner Method
double[][] nwSolution = NorthwestCorner.solve(costs, supply, demand);

// Critical Path Method
List<Activity> criticalPath = CriticalPathMethod.findCriticalPath(activities);
```

## Project Structure

```
src/main/
├── UGNavigateApp.java          # Main application entry point
├── algorithms/
│   ├── pathfinding/            # Pathfinding algorithms
│   │   ├── DijkstraAlgorithm.java
│   │   ├── AStarAlgorithm.java
│   │   └── FloydWarshallAlgorithm.java
│   ├── sorting/                # Sorting algorithms
│   │   ├── QuickSort.java
│   │   └── MergeSort.java
│   └── optimization/           # Optimization algorithms
│       ├── VogelApproximation.java
│       ├── NorthwestCorner.java
│       └── CriticalPathMethod.java
├── models/                     # Data models
│   ├── Node.java              # Campus location nodes
│   ├── Edge.java              # Connections between nodes
│   ├── Graph.java             # Campus graph representation
│   ├── Route.java             # Route information
│   └── Landmark.java          # Landmark data
├── services/                   # Business logic services
│   ├── RouteService.java      # Route calculation service
│   ├── LandmarkService.java   # Landmark management
│   └── TrafficService.java    # Traffic analysis
├── utils/                      # Utility classes
│   ├── DataLoader.java        # Campus data loading
│   ├── DistanceCalculator.java # Distance calculations
│   └── TimeCalculator.java    # Time calculations
├── gui/                        # User interface
│   └── MainFrame.java         # Main GUI window
└── demo/                       # Algorithm demonstrations
    └── AlgorithmDemo.java     # Comprehensive demo application
```

## How to Run

### Prerequisites
- Java 8 or higher
- Any Java IDE (Eclipse, IntelliJ IDEA, NetBeans)

### Running the Application

1. **Main Application (GUI)**:
   ```bash
   java -cp . main.UGNavigateApp
   ```

2. **Algorithm Demo**:
   ```bash
   java -cp . main.demo.AlgorithmDemo
   ```

### Using the GUI

1. **Select Starting Point**: Choose your starting location from the dropdown
2. **Select Destination**: Choose your destination from the dropdown
3. **Choose Algorithm**: Select from Dijkstra, A*, or Floyd-Warshall
4. **Optional Landmark**: Enter a landmark to find routes passing through it
5. **Find Route**: Click "Find Route" to get the optimal path
6. **Multiple Routes**: Click "Find Multiple Routes" to see alternatives

## Algorithm Analysis

### Time Complexity
- **Dijkstra's Algorithm**: O(V²) where V is the number of vertices
- **A* Algorithm**: O(V log V) with good heuristics
- **Floyd-Warshall**: O(V³) for all-pairs shortest paths
- **Quick Sort**: O(n log n) average case
- **Merge Sort**: O(n log n) guaranteed

### Space Complexity
- **Pathfinding Algorithms**: O(V) for distance arrays
- **Sorting Algorithms**: O(n) for temporary storage
- **Optimization Algorithms**: O(V²) for cost matrices

## Key Features Explained

### 1. Divide and Conquer Approach
- **Merge Sort**: Divides routes into smaller sublists, sorts them, and merges
- **Quick Sort**: Partitions routes around a pivot for efficient sorting

### 2. Greedy Approach
- **Dijkstra's Algorithm**: Always selects the unvisited vertex with minimum distance
- **A* Algorithm**: Uses heuristic estimates to guide search toward the goal

### 3. Dynamic Programming
- **Floyd-Warshall**: Builds shortest paths by considering intermediate vertices
- **Critical Path Method**: Calculates earliest and latest times using dynamic programming

### 4. Transportation Optimization
- **Vogel Approximation**: Minimizes total transportation cost
- **Northwest Corner**: Provides initial feasible solution

## Traffic Analysis

The system includes realistic traffic simulation:
- **Peak Hours**: Increased travel times during class changes
- **Area-specific Traffic**: Different congestion levels across campus
- **Time-based Adjustments**: Dynamic route optimization based on current conditions

## Landmark Search

Users can find routes passing through specific landmarks:
- **Academic**: Library, Computer Science Department, Great Hall
- **Dining**: Night Market, Food courts
- **Services**: Bank, Medical Center, SRC Building
- **Residential**: Various halls and accommodation areas

## Future Enhancements

1. **Real-time GPS Integration**: Live location tracking
2. **Mobile Application**: Android/iOS versions
3. **Weather Integration**: Route optimization based on weather conditions
4. **Accessibility Features**: Routes optimized for wheelchair users
5. **Public Transport**: Integration with campus shuttle services

## Contributing

This project was developed as part of DCIT 204 - Data Structures and Algorithms 1 at the University of Ghana. The implementation demonstrates:

- **Algorithm Design**: Efficient problem-solving approaches
- **Data Structures**: Graph representation, priority queues, arrays
- **Software Engineering**: Modular design, clean code principles
- **User Experience**: Intuitive interface design

## Team Members

- DCIT 204 Group Project Team
- University of Ghana
- Department of Computer Science

## License

This project is developed for educational purposes as part of the DCIT 204 course requirements.

---

**Note**: This application is designed specifically for the University of Ghana campus and includes realistic campus locations and landmarks. The algorithms and techniques demonstrated are fundamental to computer science and can be applied to various real-world routing and optimization problems.
