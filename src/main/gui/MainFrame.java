package main.gui;

import main.models.*;
import main.services.RouteService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Main GUI window for UG Navigate application
 */
public class MainFrame extends JFrame {
    private RouteService routeService;
    private JComboBox<String> sourceComboBox;
    private JComboBox<String> destinationComboBox;
    private JComboBox<String> algorithmComboBox;
    private JTextField landmarkField;
    private JTextArea resultArea;
    private JButton findRouteButton;
    private JButton findMultipleButton;
    private JButton searchLandmarkButton;
    private JButton clearButton;
    
    public MainFrame(RouteService routeService) {
        this.routeService = routeService;
        initializeGUI();
        populateLocationComboBoxes();
    }
    
    private void initializeGUI() {
        setTitle("UG Navigate - Campus Route Finder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create main panels
        JPanel inputPanel = createInputPanel();
        JPanel resultPanel = createResultPanel();
        JPanel buttonPanel = createButtonPanel();
        
        // Add panels to frame
        add(inputPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set frame properties
        setSize(800, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(700, 500));
        
        // Set look and feel
        try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(240, 240, 240));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        JLabel titleLabel = new JLabel("UG Navigate - Find Your Way Around Campus");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 51)); // UG colors
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        
        // Source location
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("From:"), gbc);
        
        sourceComboBox = new JComboBox<>();
        sourceComboBox.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(sourceComboBox, gbc);
        
        // Destination location
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("To:"), gbc);
        
        destinationComboBox = new JComboBox<>();
        destinationComboBox.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(destinationComboBox, gbc);
        
        // Algorithm selection
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Algorithm:"), gbc);
        
        algorithmComboBox = new JComboBox<>(new String[]{"Dijkstra", "A*", "Floyd-Warshall"});
        algorithmComboBox.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(algorithmComboBox, gbc);
        
        // Landmark search
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Landmark:"), gbc);
        
        landmarkField = new JTextField();
        landmarkField.setPreferredSize(new Dimension(200, 25));
        landmarkField.setToolTipText("Search for routes via landmarks (e.g., bank, library, hall)");
        gbc.gridx = 1; gbc.gridy = 4;
        panel.add(landmarkField, gbc);
        
        return panel;
    }
    
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Route Results"));
        
        resultArea = new JTextArea();
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setEditable(false);
        resultArea.setBackground(Color.WHITE);
    resultArea.setText("Welcome to UG Navigate!\n\n" +
                          "Select your starting point and destination, then click 'Find Route' to get directions.\n\n" +
                          "Features:\n" +
                          "• Multiple pathfinding algorithms (Dijkstra, A*, Floyd-Warshall)\n" +
                          "• Landmark-based route search\n" +
                          "• Multiple route options\n" +
              "• Real-time traffic consideration\n" +
              "• Distance estimates\n\n" +
                          "Ready to navigate the UG campus!");
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(750, 300));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(240, 240, 240));
        
        findRouteButton = new JButton("Find Route");
        findRouteButton.setBackground(new Color(0, 102, 51));
        findRouteButton.setForeground(Color.WHITE);
        findRouteButton.setFont(new Font("Arial", Font.BOLD, 12));
        findRouteButton.addActionListener(new FindRouteListener());
        
        findMultipleButton = new JButton("Find Multiple Routes");
        findMultipleButton.addActionListener(new FindMultipleRoutesListener());
        
        searchLandmarkButton = new JButton("Search by Landmark");
        searchLandmarkButton.addActionListener(new SearchLandmarkListener());
        
        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearResults());
        
        panel.add(findRouteButton);
        panel.add(findMultipleButton);
        panel.add(searchLandmarkButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    private void populateLocationComboBoxes() {
        // Add default option
        sourceComboBox.addItem("Select starting location...");
        destinationComboBox.addItem("Select destination...");
        
        // Get all nodes from the graph
        for (Node node : routeService.getCampusGraph().getAllNodes()) {
            String displayName = node.getName();
            sourceComboBox.addItem(displayName);
            destinationComboBox.addItem(displayName);
        }
    }
    
    private String getSelectedNodeId(JComboBox<String> comboBox) {
        String selectedName = (String) comboBox.getSelectedItem();
        if (selectedName == null || selectedName.startsWith("Select")) {
            return null;
        }
        
        // Find the node with matching name
        for (Node node : routeService.getCampusGraph().getAllNodes()) {
            if (node.getName().equals(selectedName)) {
                return node.getId();
            }
        }
        return null;
    }
    
    private void clearResults() {
        resultArea.setText("");
        landmarkField.setText("");
    }
    
    private void displayRoute(Route route) {
        if (route == null) {
            resultArea.append("No route found!\n\n");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(route.getRouteName()).append(" ===\n");
        sb.append(route.getRouteDescription()).append("\n");
        
        // Add step-by-step directions
        sb.append("Directions:\n");
        List<String> directions = route.getDirections();
        for (String direction : directions) {
            sb.append("  ").append(direction).append("\n");
        }
        sb.append("\n");
        
        resultArea.append(sb.toString());
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }
    
    private void displayMultipleRoutes(List<Route> routes) {
        if (routes == null || routes.isEmpty()) {
            resultArea.append("No routes found!\n\n");
            return;
        }
        
        resultArea.append(String.format("Found %d route options:\n\n", routes.size()));
        
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            resultArea.append(String.format("OPTION %d: %s\n", i + 1, route.getRouteName()));
            resultArea.append(String.format("Distance: %.0f meters\n", route.getTotalDistance()));
            
            if (!route.getLandmarksOnRoute().isEmpty()) {
                resultArea.append("Landmarks: ");
                for (int j = 0; j < route.getLandmarksOnRoute().size(); j++) {
                    if (j > 0) resultArea.append(", ");
                    resultArea.append(route.getLandmarksOnRoute().get(j).getName());
                }
                resultArea.append("\n");
            }
            resultArea.append("\n");
        }
        
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }
    
    // Event Listeners
    
    private class FindRouteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String sourceId = getSelectedNodeId(sourceComboBox);
            String destId = getSelectedNodeId(destinationComboBox);
            
            if (sourceId == null || destId == null) {
                JOptionPane.showMessageDialog(MainFrame.this, 
                    "Please select both starting location and destination.", 
                    "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (sourceId.equals(destId)) {
                JOptionPane.showMessageDialog(MainFrame.this, 
                    "Starting location and destination cannot be the same.", 
                    "Invalid Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String algorithm = ((String) algorithmComboBox.getSelectedItem()).toLowerCase().replace("*", "star");
            if (algorithm.equals("floyd-warshall")) {
                algorithm = "floyd";
            }
            
            try {
                Route route = routeService.findBestRoute(sourceId, destId, algorithm);
                
                resultArea.append("Searching for route using " + algorithmComboBox.getSelectedItem() + "...\n");
                displayRoute(route);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame.this, 
                    "Error finding route: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private class FindMultipleRoutesListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String sourceId = getSelectedNodeId(sourceComboBox);
            String destId = getSelectedNodeId(destinationComboBox);
            
            if (sourceId == null || destId == null) {
                JOptionPane.showMessageDialog(MainFrame.this, 
                    "Please select both starting location and destination.", 
                    "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                List<Route> routes = routeService.findMultipleRoutes(sourceId, destId, 3);
                
                resultArea.append("Searching for multiple route options...\n");
                displayMultipleRoutes(routes);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame.this, 
                    "Error finding routes: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private class SearchLandmarkListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String sourceId = getSelectedNodeId(sourceComboBox);
            String destId = getSelectedNodeId(destinationComboBox);
            String landmark = landmarkField.getText().trim();
            
            if (sourceId == null || destId == null) {
                JOptionPane.showMessageDialog(MainFrame.this, 
                    "Please select both starting location and destination.", 
                    "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (landmark.isEmpty()) {
                JOptionPane.showMessageDialog(MainFrame.this, 
                    "Please enter a landmark to search for.", 
                    "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                List<Route> routes = routeService.findRoutesByLandmark(sourceId, destId, landmark);
                
                resultArea.append(String.format("Searching for routes via '%s'...\n", landmark));
                displayMultipleRoutes(routes);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame.this, 
                    "Error searching by landmark: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
