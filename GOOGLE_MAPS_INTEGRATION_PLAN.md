# Google Maps Distance Matrix API Integration Plan

## Overview
This document outlines the plan to integrate Google Maps Distance Matrix API into the UG Navigate application to provide more accurate distance and time calculations for campus navigation.

## Current System Analysis
The current system uses:
- Haversine formula for distance calculations between nodes
- Estimated travel times based on path types and fixed speeds
- CSV-based data storage for campus locations and edges
- Graph-based routing with multiple algorithms (Dijkstra, A*, Floyd-Warshall)

## Integration Goals
1. Use Google Maps Distance Matrix API for more accurate distance/time calculations
2. Maintain backward compatibility with existing functionality
3. Add fallback mechanisms for when Google Maps API is unavailable
4. Store Google Maps data alongside calculated data for comparison

## Implementation Steps

### 1. Google Maps API Configuration
Create configuration management for Google Maps API:
- API key storage (environment variable or config file)
- Rate limiting handling
- Error handling and fallback mechanisms

### 2. Google Maps Distance Matrix Service
Implement a service class to interact with the Google Maps Distance Matrix API:
- HTTP client for API requests
- Request/response parsing
- Caching mechanism to reduce API calls
- Error handling and retry logic

### 3. Edge Model Enhancement
Modify the Edge model to store Google Maps data:
- Add fields for Google Maps distance and duration
- Add methods to update these values from API responses
- Add methods to check if Google Maps data is available

### 4. Distance Calculator Update
Update DistanceCalculator to use Google Maps data when available:
- Add method to fetch Google Maps data for an edge
- Modify existing calculation methods to use Google Maps data as primary source
- Maintain fallback to Haversine/other calculations

### 5. Data Loading Enhancement
Modify DataLoader to fetch and store Google Maps data:
- Add method to batch fetch Google Maps data for all edges
- Update CSV format to include Google Maps data fields
- Add option to refresh Google Maps data

### 6. Route Service Integration
Update RouteService to use Google Maps data for more accurate calculations:
- Modify route calculation to use Google Maps distances/times
- Update traffic service to work with Google Maps data
- Ensure sorting and analysis functions work with new data

### 7. Fallback Mechanisms
Implement fallback mechanisms for when Google Maps API is unavailable:
- Cache Google Maps data locally
- Use calculated distances as backup
- Provide configuration options for API usage

### 8. Testing
Test the integration with sample data:
- Verify Google Maps data is correctly fetched and stored
- Test fallback mechanisms
- Validate route calculations use Google Maps data

## Technical Implementation Details

### Google Maps Distance Matrix API Usage
The API will be called with:
- Origin: Source node coordinates
- Destination: Destination node coordinates
- Mode: Walking (for campus navigation)
- Units: Metric

Example request:
```
https://maps.googleapis.com/maps/api/distancematrix/json?
origins=5.6508,-0.1870&
destinations=5.6520,-0.1850&
mode=walking&
units=metric&
key=YOUR_API_KEY
```

### Data Model Changes

#### Edge Model Enhancement
Add the following fields to the Edge class:
- `googleMapsDistance` (double) - Distance from Google Maps in meters
- `googleMapsDuration` (int) - Duration from Google Maps in seconds
- `googleMapsDataAvailable` (boolean) - Flag indicating if Google Maps data is available
- `googleMapsLastUpdated` (Date) - Timestamp of last Google Maps data update

#### CSV Format Update
Update the edges.csv format to include Google Maps data:
```
sourceId,destinationId,distanceMeters,pathType,speedKmh,bidirectional,googleMapsDistance,googleMapsDuration
```

### Service Layer Implementation

#### GoogleMapsService Class
Create a new service class to handle Google Maps API interactions:
- API key management
- HTTP request handling
- Response parsing
- Caching mechanism
- Error handling

#### Integration with Existing Services
Modify existing services to use Google Maps data:
- RouteService: Use Google Maps distances for route calculation
- TrafficService: Apply traffic multipliers to Google Maps times
- DistanceCalculator: Use Google Maps as primary data source

## Error Handling and Fallbacks

### API Error Handling
- Network timeouts
- Rate limiting
- Invalid API key
- Quota exceeded

### Fallback Strategies
1. Use cached Google Maps data if available
2. Fall back to calculated distances
3. Provide configuration to disable Google Maps integration

## Testing Plan

### Unit Tests
- GoogleMapsService functionality
- Edge model with Google Maps data
- DistanceCalculator with Google Maps integration

### Integration Tests
- End-to-end route calculation with Google Maps data
- Fallback mechanism testing
- Performance testing

### Manual Testing
- Visual verification of route accuracy
- Comparison of calculated vs Google Maps distances
- Error scenario testing

## Performance Considerations

### Caching Strategy
- Cache Google Maps responses locally
- Implement time-based cache invalidation
- Provide cache warming mechanism

### Rate Limiting
- Implement request batching
- Add delays between requests
- Monitor API usage quotas

## Security Considerations

### API Key Management
- Store API key in environment variables
- Avoid committing keys to version control
- Implement key rotation procedures

## Deployment Plan

### Phase 1: Core Implementation
- GoogleMapsService implementation
- Edge model enhancement
- DistanceCalculator update

### Phase 2: Integration
- DataLoader modification
- RouteService integration
- Fallback mechanism implementation

### Phase 3: Testing and Optimization
- Unit testing
- Performance optimization
- Documentation updates

## Configuration Options

### Environment Variables
- `GOOGLE_MAPS_API_KEY` - Google Maps API key
- `GOOGLE_MAPS_ENABLED` - Enable/disable Google Maps integration
- `GOOGLE_MAPS_CACHE_TTL` - Cache time-to-live in minutes

### Runtime Configuration
- Option to force refresh Google Maps data
- Option to disable Google Maps integration
- Logging level configuration

## Future Enhancements

### Additional Google Maps APIs
- Geocoding API for address resolution
- Roads API for road snapping
- Places API for landmark information

### Advanced Features
- Real-time traffic integration
- Multiple transportation modes
- Elevation data integration