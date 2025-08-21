# Google Maps Distance Matrix API Integration - Implementation Guide

## Prerequisites

1. **Google Maps API Key**
   - Go to the Google Cloud Console
   - Create a new project or select an existing one
   - Enable the Distance Matrix API
   - Create an API key with restrictions for the Distance Matrix API
   - Set the API key as an environment variable: `GOOGLE_MAPS_API_KEY=your_api_key_here`

2. **Dependencies**
   - Add Gson library to your project for JSON parsing
   - Ensure internet connectivity for API calls

## Implementation Steps

### Step 1: Create Google Maps Service

1. Create `src/main/services/GoogleMapsService.java`
2. Implement the service with:
   - API key management from environment variables
   - HTTP client for Distance Matrix API calls
   - JSON response parsing
   - Caching mechanism
   - Error handling and retry logic

### Step 2: Modify Edge Model

1. Update `src/main/models/Edge.java`:
   - Add Google Maps data fields:
     - `googleMapsDistance` (double)
     - `googleMapsDuration` (int)
     - `googleMapsDataAvailable` (boolean)
     - `googleMapsLastUpdated` (long)
   - Add constructors to handle Google Maps data
   - Add getters/setters for new fields
   - Add methods to get accurate distance/duration

### Step 3: Update Distance Calculator

1. Modify `src/main/utils/DistanceCalculator.java`:
   - Add method to get accurate distance (Google Maps preferred)
   - Add method to get accurate travel time (Google Maps preferred)
   - Maintain fallback to Haversine calculations

### Step 4: Modify Data Loader

1. Update `src/main/utils/DataLoader.java`:
   - Update `loadEdgesFromCSV` method to handle Google Maps data fields
   - Update `saveEdgesToCSV` method to include Google Maps data
   - Add `refreshGoogleMapsData` method
   - Update CSV header to include Google Maps fields

### Step 5: Update Route Service

1. Modify `src/main/services/RouteService.java`:
   - Ensure route calculations use accurate distance/time data
   - Update route combination logic to preserve Google Maps data

### Step 6: Update Pathfinding Algorithms

1. Modify pathfinding algorithms to use accurate distances:
   - `src/main/algorithms/pathfinding/DijkstraAlgorithm.java`
   - `src/main/algorithms/pathfinding/AStarAlgorithm.java`
   - `src/main/algorithms/pathfinding/FloydWarshallAlgorithm.java`

### Step 7: Configuration

1. Set environment variables:
   - `GOOGLE_MAPS_API_KEY=your_api_key_here`
   - `GOOGLE_MAPS_ENABLED=true` (optional, defaults to true)
   - `GOOGLE_MAPS_CACHE_TTL=3600000` (optional, defaults to 1 hour in milliseconds)

### Step 8: Testing

1. Run the application with Google Maps integration
2. Verify that routes use Google Maps data when available
3. Test fallback behavior when API is unavailable
4. Check that CSV files correctly store Google Maps data

## Expected Improvements

After implementation, the application will provide:
- More accurate distance calculations based on actual walking paths
- Realistic travel times from Google Maps data
- Improved route recommendations
- Backward compatibility with existing functionality
- Graceful degradation when Google Maps API is unavailable

## Troubleshooting

### Common Issues

1. **API Key Problems**
   - Ensure the API key is set in environment variables
   - Verify the Distance Matrix API is enabled in Google Cloud Console
   - Check API key restrictions

2. **Network Issues**
   - Verify internet connectivity
   - Check firewall settings
   - Ensure the application can reach Google Maps API endpoints

3. **Rate Limiting**
   - Monitor API usage
   - Implement appropriate delays between requests
   - Use caching to reduce API calls

### Fallback Behavior

If Google Maps API is unavailable:
- The application will fall back to calculated distances
- Existing functionality will continue to work
- Users will be notified of reduced accuracy

## Performance Considerations

1. **Caching**
   - Google Maps responses are cached locally
   - Cache TTL can be configured via environment variables
   - Cached data reduces API usage and improves performance

2. **Batch Processing**
   - For large datasets, implement batch processing of API calls
   - Add delays between requests to avoid rate limiting

3. **Data Refresh**
   - Use the `refreshGoogleMapsData` method to update cached data
   - Schedule periodic data refresh for accuracy

## Security Considerations

1. **API Key Protection**
   - Store API keys in environment variables, not in code
   - Use API key restrictions in Google Cloud Console
   - Monitor API usage for unusual activity

2. **Data Privacy**
   - Only location coordinates are sent to Google Maps
   - No personal information is transmitted
   - Follow data privacy best practices

## Maintenance

1. **Regular Updates**
   - Periodically refresh Google Maps data for accuracy
   - Monitor API usage and adjust caching as needed

2. **Monitoring**
   - Log API usage and errors
   - Monitor cache hit rates
   - Track fallback usage

3. **Upgrades**
   - Keep Gson library updated
   - Monitor Google Maps API changes
   - Update implementation as needed for new features