package gui;
import data.map_elements.Airport;

import javax.swing.*;

import config.GameConfiguration;

import java.awt.*;

public class AirportInfoPanel extends JPanel {
    private JLabel airportIdLabel;
    private JLabel capacityLabel;
    private JLabel pistPlanesLabel;
    private JLabel runway1;
    private JLabel runway2;
    private JLabel runway3;
    private JLabel runway4;
    private JLabel xLabel;
    private JLabel yLabel;
    // Define custom colors
    private Color backgroundColor = new Color(50, 80, 50); // Dark green background
    private Color labelColor = new Color(58, 77, 57); // Light green labels
    private Color lineColor = new Color(169, 179, 136); // White line color
    private String imagePath = "src/images/side_plane.png";
    private ImageIcon icon;

    public AirportInfoPanel() {
        setLayout(new GridLayout(5, 1));
        setBackground(backgroundColor);
        setPreferredSize(new Dimension(200, 150));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // Ensure the panel is opaque
        // setOpaque(true);

        initializeComponents();
    }

    private void initializeComponents() {
        airportIdLabel = createLabel(" ");
        airportIdLabel.setFont(new Font("Arial", Font.BOLD, GameConfiguration.BLOCK_SIZE * 2));
        addSeparator();
        capacityLabel = createLabel("Planes capacity: ");
        pistPlanesLabel = createLabel("occupied runways: ");
        addSeparator();
        runway1 = createLabel("Runway 1: ");
        runway2 = createLabel("Runway 2: ");
        runway3 = createLabel("Runway 3: ");
        runway4 = createLabel("Runway 4: ");
        addSeparator();
        xLabel = createLabel("x : ");
        yLabel = createLabel("y : ");
        addSeparator();
        /*flightsLabel = createLabel("flights :");
        airplaneLabel = createLabel("Airplane Id : ");
        airplaneLabel.setFont(new Font("Arial", Font.BOLD, GameConfiguration.BLOCK_SIZE*3/2));
        flightLabel = createLabel(" ");
        airplane2Label = createLabel("Airplane Id : ");
        airplane2Label.setFont(new Font("Arial", Font.BOLD, GameConfiguration.BLOCK_SIZE*3/2));
        flight2Label = createLabel(" ");*/
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);

        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
//      label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        label.setFont(new Font("Arial", Font.BOLD, GameConfiguration.BLOCK_SIZE));
        add(label);
        add(Box.createVerticalStrut(15));
        return label;
    }

    private void setLabelWithIcon(JLabel label, String text, boolean show) {
    	label.setIconTextGap(5);
        icon = new ImageIcon(imagePath);
        Image image = icon.getImage();
        if (icon != null&&show) {
        	 int width = 20; // Desired width
             int height = 20; // Desired height
             Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

             // Create a new ImageIcon with the scaled image
             ImageIcon scaledIcon = new ImageIcon(scaledImage);

             // Set the scaled icon on the label
             label.setIcon(scaledIcon);
        } else {
            label.setIcon(null);
        }
        label.setText(text);
    }


    public void setAirportInfo(Airport airport) {
        airportIdLabel.setText(airport.getName());
        capacityLabel.setText("Planes capacity: " + airport.getMaxPistes());
        pistPlanesLabel.setText("occupied runways: " + airport.getNumberOfOccupiedPiste());
        /*runway1.setText("" + airport.getTableauPistes()[1].getIsOccupiedbyPlane());
        runway2.setText("" + airport.getTableauPistes()[2].getIsOccupiedbyPlane());
        runway3.setText("" + airport.getTableauPistes()[3].getIsOccupiedbyPlane());*/

        // System.out.print("runway 1: "+airport.getTableauPistes()[1].getIsOccupiedbyPlane());
        // System.out.print("runway 2: "+airport.getTableauPistes()[2].getIsOccupiedbyPlane());
        // System.out.print("runway 3: "+airport.getTableauPistes()[3].getIsOccupiedbyPlane());

        if (airport.getTableauPistes()[0].getIsOccupiedbyPlane() == false) {
            setLabelWithIcon(runway1, "Runway 1: not occupied",false);
        } else {
            setLabelWithIcon(runway1, "Runway 1: Airplane " + airport.getTableauPistes()[0].getOccupyingPlane().getAirplaneId(),true);
        }
        if (!airport.getTableauPistes()[1].getIsOccupiedbyPlane()) {
        	setLabelWithIcon(runway2, "Runway 2: not occupied",false);
        } else {
        	 setLabelWithIcon(runway2, "Runway 2: Airplane " + airport.getTableauPistes()[1].getOccupyingPlane().getAirplaneId(),true);
        }
        if (!airport.getTableauPistes()[2].getIsOccupiedbyPlane()) {
        	setLabelWithIcon(runway3, "Runway 3: not occupied",false);
        } else {
        	 setLabelWithIcon(runway3, "Runway 3: Airplane " + airport.getTableauPistes()[2].getOccupyingPlane().getAirplaneId(),true);
        }
        if (airport.getMaxPistes() == 4) {
            runway4.setVisible(true);
            if (!airport.getTableauPistes()[3].getIsOccupiedbyPlane()) {
             setLabelWithIcon(runway4, "Runway 4: not occupied",false);
            } else {
            	 setLabelWithIcon(runway4, "Runway 4: Airplane " + airport.getTableauPistes()[3].getOccupyingPlane().getAirplaneId(),true);            }
        } else if (airport.getMaxPistes() == 3) {
            runway4.setVisible(false);
        }
        xLabel.setText("x : " + airport.getPosition().getxCoordBlock());
        yLabel.setText("y : " + airport.getPosition().getyCoordBlock());
        // flightsLabel.setText("flights :");
        /*airplaneLabel.setText("Airplane Id : 2");
        flightLabel.setText(+" -> "+);
        airplane2Label.setText("Airplane Id : 5");
        flight2Label.setText("Paris -> Istanbul");*/
    }

    public void clearAirportInfo() {
        airportIdLabel.setText(" ");
        capacityLabel.setText("Planes capacity: ");
        pistPlanesLabel.setText("occupied runways:");
        xLabel.setText("x : ");
        yLabel.setText("y : ");
        runway1.setText("Runway 1: not occupied");
        runway2.setText("Runway 2: not occupied");
        runway3.setText("Runway 3: not occupied");
        runway4.setText("Runway 4: not occupied");
        /*flightsLabel.setText("flights :");
        airplaneLabel.setText("Airplane Id : ");
        flightLabel.setText(" ");
        airplane2Label.setText("Airplane Id : ");
        flight2Label.setText(" ");*/
    }

    private void addSeparator() {
        add(Box.createVerticalStrut(5));
        JLabel separator = new JLabel("-------------------------");
        separator.setForeground(lineColor); // Set line color
        add(separator);
        add(Box.createVerticalStrut(5));
    }

    public void showPanel() {
        setVisible(true);
    }

    // Method to hide the panel
    public void hidePanel() {
        setVisible(false);
    }

}
