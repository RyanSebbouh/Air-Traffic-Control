
package gui;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import data.map_elements.Airport;
import manager.AirplaneManager;
import process.Simulation;

public class DisplayInfo extends JPanel {

    private static final long serialVersionUID = 1L;
	private AirplaneInfoPanel airplaneinfoPanel;
    private AirportInfoPanel airportInfoPanel;
    public DisplayInfo() {
        init();
    }

    private void init() {
        airportInfoPanel = new AirportInfoPanel();
        airplaneinfoPanel = new AirplaneInfoPanel();

        // Hide both info panels by default
        airportInfoPanel.hidePanel();
        airplaneinfoPanel.hidePanel();

        // Use BoxLayout with Y_AXIS alignment to stack panels vertically
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(50, 80, 50));
        setVisible(true);

        // Initialize cardLayout
        //cardLayout = new CardLayout();

        // Add airportInfoPanel and airplaneinfoPanel to DisplayInfo with unique names
        add(airplaneinfoPanel, "AirplanePanel");
        add(airportInfoPanel, "AirportPanel");
    }

    public void displayAirportInfo(Airport am) {
        airportInfoPanel.showPanel();
        // Toggle between airportInfoPanel and airplaneinfoPanel
        //cardLayout.show(this, "AirportPanel"); // Show airport panel
        airportInfoPanel.setAirportInfo(am);
    }

    public void displayAirplaneInfo(AirplaneManager am,Simulation sim) {
        airplaneinfoPanel.showPanel();
        // Toggle between airportInfoPanel and airplaneinfoPanel
        //cardLayout.show(this, "AirplanePanel"); // Show airplane panel
        airplaneinfoPanel.setAirplaneInfo(am.getAirplane(),sim);
    }

    public void hideAirportInfo() {
        airportInfoPanel.hidePanel();
        // Hide airportInfoPanel if no airport is clicked
        airportInfoPanel.clearAirportInfo();
    }

    public void hideAirplaneInfo() {
        airplaneinfoPanel.hidePanel();
        // Hide airplaneinfoPanel if no airplane is clicked
        airplaneinfoPanel.clearAirplaneInfo();
    }
    
}