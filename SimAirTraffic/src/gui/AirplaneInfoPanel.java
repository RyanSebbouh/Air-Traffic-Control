package gui;

import data.Airplane;
import process.Simulation;

import javax.swing.*;

import config.GameConfiguration;

import java.awt.*;

public class AirplaneInfoPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel airplaneIdLabel;
    private JLabel altitudeLabel;
    private JLabel destinationLabel;
    private JLabel directionLabel;
    private JLabel speedLabel;
    private JLabel originLabel;
    private JLabel xLabel;
    private JLabel yLabel;
    
    // Define custom colors
    private Color backgroundColor = new Color(50, 80, 50); // Dark green background
    private Color labelColor = new Color(58, 77, 57); // Light green labels
    private Color lineColor = new Color(169, 179, 136); // White line color

    public AirplaneInfoPanel() {
        setLayout(new GridLayout(5, 1));
        setBackground(backgroundColor);
        setPreferredSize(new Dimension(200, 150));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        initializeComponents();
    }

    private void initializeComponents() {
        airplaneIdLabel = createLabel("Airplane ID: ");
        airplaneIdLabel.setFont(new Font("Arial", Font.BOLD, GameConfiguration.BLOCK_SIZE*3/2));
        addSeparator();
        directionLabel = createLabel("Direction: ");
        speedLabel = createLabel("Speed: ");
        altitudeLabel = createLabel("Altitude: ");
        addSeparator();
        originLabel = createLabel("Departure Airport : ");
        destinationLabel = createLabel("Destination: ");
        addSeparator();
        xLabel = createLabel("x : ");
        yLabel = createLabel("y : ");
        addSeparator();
        /*arrivalTime = createLabel("Arrival time :");
        departureTime = createLabel("Departure time :");*/
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        label.setFont(new Font("Arial", Font.BOLD, GameConfiguration.BLOCK_SIZE));
        add(label);
        add(Box.createVerticalStrut(15));
        return label;
    }

    public void setAirplaneInfo(Airplane airplane,Simulation sim) {
        airplaneIdLabel.setText("Airplane ID: " + airplane.getAirplaneId());
        altitudeLabel.setText("Altitude: " + airplane.getAltitude());
        originLabel.setText("Departure: " + sim.getAirportfromSimulation(airplane.getVol().getDepart()).getName());
        destinationLabel.setText("Destination: "+ sim.getAirportfromSimulation(airplane.getVol().getArrivee()).getName());
        directionLabel.setText("Direction: " + airplane.getDirection());
        speedLabel.setText("Speed: " + airplane.getSpeed());
        xLabel.setText("x : "+ airplane.getDestX());
        yLabel.setText("y : "+ airplane.getDestY());
        /*arrivalTime.setText("Arrival time : ");
        departureTime.setText("Departure time : ");*/
    }

    public void clearAirplaneInfo() {
        airplaneIdLabel.setText("Airplane ID: ");
        altitudeLabel.setText("Altitude: ");
        originLabel.setText("Departure Airport: ");
        destinationLabel.setText("Destination: ");
        directionLabel.setText("Direction: ");
        speedLabel.setText("Speed: ");
        xLabel.setText("x : ");
        yLabel.setText("y : ");
        /*arrivalTime.setText("Arrival time :");
        departureTime.setText("Departure time :");*/
    }
    
    private void addSeparator() {
        add(Box.createVerticalStrut(5));
        JLabel separator = new JLabel("-------------------------");
        separator.setForeground(lineColor);
        add(separator);
        add(Box.createVerticalStrut(5));
    }

    // Method to show the panel
    public void showPanel() {
        setVisible(true);
    }

    // Method to hide the panel
    public void hidePanel() {
        setVisible(false);
    }
}
