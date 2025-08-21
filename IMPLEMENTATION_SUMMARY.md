# Google Maps Distance Matrix API Integration - Implementation Summary

## Overview
This document provides a concise summary of the implementation plan for integrating Google Maps Distance Matrix API into the UG Navigate application to enhance the accuracy of distance and time calculations.

## Key Components to Modify

### 1. Edge Model (src/main/models/Edge.java)
- Add Google Maps data fields: `googleMapsDistance`, `googleMapsDuration`, `googleMapsDataAvailable`, `googleMapsLastUpdated`
- Add methods to get accurate distance/duration (Google Maps preferred)
- Update constructors to handle Google Maps data

### 2. Google Maps Service (New file: src/main/services/GoogleMapsService.java)
- Create service to interact with Google Maps Distance Matrix API
- Implement caching mechanism to reduce API calls
- Add error handling and fallback logic
- Include methods to update edges with Google Maps data

### 3. Distance Calculator (src/main/utils/DistanceCalculator.java)
- Add methods to get accurate distance/time using Google Maps data
- Maintain fallback to Haversine calculations when Google Maps is unavailable
- Add utility methods for speed calculations by path type

### 4. Data Loader (src/main/utils/DataLoader.java)
- Update CSV format to include Google Maps data fields
- Modify edge loading to handle Google Maps data
- Add method to refresh Google Maps data for all edges
- Update edge saving to include Google Maps data

### 5. Route Service (src/main/services/RouteService.java)
- Ensure route calculations use accurate distance/time data
- Update route combination logic to preserve Google Maps data

### 6. Pathfinding Algorithms
- Update Dijkstra, A*, and Floyd-Warshall algorithms to use accurate distances
- Modify edge weight calculations to prefer Google Maps data

## Implementation Flow

1. **Create Google Maps Service** - Handle API interactions and caching
2. **Enhance Edge Model** - Store Google Maps data alongside calculated data
3. **Update Distance Calculator** - Use Google Maps as primary data source
4. **Modify Data Loader** - Handle Google Maps data in CSV files
5. **Integrate with Route Service** - Ensure accurate calculations throughout
6. **Update Pathfinding Algorithms** - Use accurate distances for better routes
7. **Add Configuration** - Environment variables for API key and settings
8. **Test Implementation** - Verify accuracy improvements and fallback behavior

## Benefits

- More accurate distance calculations for campus navigation
- Realistic travel times based on actual walking distances
- Backward compatibility with existing functionality
- Fallback mechanisms for when Google Maps API is unavailable
- Caching to reduce API usage and improve performance

## Dependencies

- Google Maps API key
- Gson library for JSON parsing
- Internet connectivity for API calls

## Configuration

Environment variables:
- `GOOGLE_MAPS_API_KEY` - Your Google Maps API key
- `GOOGLE_MAPS_ENABLED` - Enable/disable Google Maps integration (default: true)
- `GOOGLE_MAPS_CACHE_TTL` - Cache time-to-live in milliseconds (default: 3600000)