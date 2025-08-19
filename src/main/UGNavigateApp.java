package main;

import main.gui.MainFrame;
import main.services.RouteService;
import main.utils.DataLoader;
import javax.swing.SwingUtilities;

/**
 * UG Navigate - Main Application Entry Point
 * Optimal Routing Solution for University of Ghana Campus
 * 
 * @author DCIT 204 Group Project Team
 */
public class UGNavigateApp {
    private static RouteService routeService;
    
    public static void main(String[] args) {
        System.out.println("=== UG Navigate: Optimal Routing Solution ===");
        System.out.println("University of Ghana Campus Navigation System");
        System.out.println("DCIT 204 - Data Structures and Algorithms 1\n");
        
        try {
            // Initialize the application
            initializeApplication();
            
            // Launch the GUI
            SwingUtilities.invokeLater(() -> {
                try {
                    new MainFrame(routeService).setVisible(true);
                } catch (Exception e) {
                    System.err.println("Error launching GUI: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error initializing application: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize the application components
     */
    private static void initializeApplication() {
        System.out.println("Initializing UG Navigate...");
        
        // Load campus data
        DataLoader.loadCampusData();
        System.out.println("✓ Campus data loaded");
        
        // Initialize route service
        routeService = new RouteService();
        System.out.println("✓ Route service initialized");
        
        System.out.println("✓ UG Navigate initialized successfully!\n");
    }
}
