package gui;

import config.GameConfiguration;
import data.map_elements.Obstacle;
import logs.LoggerUtility;
import data.map_elements.Airport;
import manager.AirplaneManager;
import manager.ZoneManager;
import process.Simulation;
import process.SimulationUtility;

import javax.swing.*;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MainGUI extends JFrame implements Runnable, ActionListener {
	private static final Color BACKGROUND_COLOR = new Color(169, 179, 136);

	private static final int HEIGHT_DIV = GameConfiguration.WINDOW_HEIGHT/45;

	private static final long serialVersionUID = 1L;
	
	private final Object lock = new Object();
    //private SimulationUtility simUtil = new SimulationUtility();
    private Simulation sim;
    private Dashboard dash;
    private JPanel buttonPanel;
    private DisplayInfo infoPanel;
    private Container contentPane;
    private boolean Pause = false;
    private ArrayList<Obstacle> obstacles; 
    private final static Dimension preferredSize = new Dimension(GameConfiguration.ADJUSTED_WINDOW_WIDTH,
            GameConfiguration.WINDOW_HEIGHT);
    
	private static Logger logger = LoggerUtility.getLogger(MainGUI.class, "html");


    public MainGUI(String title) {
    	
        super(title);
        this.sim = new Simulation();
        obstacles = SimulationUtility.readCoord("src/raw_data/genes/loc_montagnes.txt", sim.getMap());
        this.dash = new Dashboard(sim);
        this.infoPanel = new DisplayInfo();
        //this.airportInfoPanel = new AirportInfoPanel();
      
        init();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setSize(preferredSize);
        setResizable(false);
        setVisible(true);
    }

    @Override
    public void run() {
        int time = 0;

        while (time <= GameConfiguration.SIMULATION_DURATION) {
            SimulationUtility.unitTime();
            if(time%4 == 0 && time <= 60 && time != 20) { // chaque 5 unité de temps * 25 avions totale = 125
                ZoneManager departingZone = sim.addNewAirplane();
                AirplaneManager nextAirplane = sim.getNextAirplane();

                if (nextAirplane != null) {
                    nextAirplane.setZoneManag(departingZone);
                    departingZone.enterZone(nextAirplane);
                    
                    nextAirplane.setRunning(true);
                    nextAirplane.start();
                }
            }
            if(time == 20) {
                ZoneManager departingZone = sim.addSpecificAirplane(time);
                AirplaneManager nextAirplane = sim.getNextAirplane();

                if (nextAirplane != null) {
                    nextAirplane.setZoneManag(departingZone);
                    departingZone.enterZone(nextAirplane);
                    
                    nextAirplane.setRunning(true);
                    nextAirplane.start();
                }
            }
            if(time == 30) {
                sim.triggerEmergency();
            }
            
            dash.repaint();
            if(!Pause) time++;
        }
        SimulationUtility.windowRefreshTime();
        sim.stopAllPlanes();
    }
    
    /**
     * Add a button that goes on the left panel, leaving a small vertical gap between buttons
     * @param s
     */
    private void addLeftPanelButton(String s) {
        addVerticalSpace(HEIGHT_DIV);
        // Add vertical space of 10 pixels between buttons
        addButton(s, BACKGROUND_COLOR, Color.black);
    }

    /**
     * Initialize the control panel, the info panel, and the simulation screen.
     */
    private void init() {
        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Use BoxLayout with Y_AXIS alignment
        buttonPanel.setBackground(new Color(58, 77, 57)); 
        
        addVerticalSpace(GameConfiguration.WINDOW_HEIGHT/30);
        addButton("Sans grille", BACKGROUND_COLOR, Color.black);
        String[] leftPanelButtonNames = {"Avec grille",
        								 "Airways",
        								 "Exit",
        								 "Speed Down",
        								 "Speed Up",
        								 "Pause",
        								 "Mountains",
        								 "Airports",
        								 "Airplanes",
        								 "Air zones",
        								 };
        for(String s : leftPanelButtonNames) {
        	addLeftPanelButton(s);
        }

        contentPane.add(buttonPanel, BorderLayout.WEST); // Place the button panel on the left

        // Add rigid area to push buttons to the top
        buttonPanel.add(Box.createVerticalGlue());

        // Ajouter le MouseListener à dash
        dash.addMouseListener(new PlaneClick(this));
        dash.addMouseListener(new AirportClick(this));

        contentPane.add(dash, BorderLayout.CENTER);
        //contentPane.add(airportInfoPanel, BorderLayout.EAST);
        contentPane.add(infoPanel, BorderLayout.EAST);

        dash.setPreferredSize(preferredSize);
    }

    private void addButton(String name, Color bg, Color fg) {
        JButton newButton = new JButton(name);
        newButton.setActionCommand(name);
        newButton.addActionListener(this);
        newButton.setBackground(bg);
        newButton.setForeground(fg);
        newButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Align button to the center horizontally
        newButton.setMinimumSize(new Dimension(GameConfiguration.ADJUSTED_WINDOW_WIDTH / 12, 45)); // Set minimum width and height
        newButton.setMaximumSize(new Dimension(GameConfiguration.ADJUSTED_WINDOW_WIDTH / 12, 45)); // Set maximum width and height
        newButton.setPreferredSize(new Dimension(GameConfiguration.ADJUSTED_WINDOW_WIDTH / 12, 45)); // Set preferred width and height
        newButton.setOpaque(true); 
        //newButton.setBorderPainted(true); // Remove default border

        // Create a rounded border
        //newButton.setBorder(BorderFactory.createSoftBevelBorder(20));

        // Add hover effect
        newButton.addMouseListener(new BrightenButton(newButton, bg) );
        buttonPanel.add(newButton);
    }

    private void addVerticalSpace(int height) {
        buttonPanel.add(Box.createRigidArea(new Dimension(0, height)));
    }

    public void addButton(String name, Color bg) {
        addButton(name, bg, Color.WHITE);
    }

    private void handleExitButtonClick() {
        System.exit(0);
    }
    
    private void increaseRefreshRate() {
        sim.increaseRefreshRate(100);
    }

    private void decreaseRefreshRate() {
        sim.decreaseRefreshRate(100);
    }
    
    private void pauseSim() {
    	sim.pause(Pause);
    	Pause = !Pause;
  	}

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Sans grille":
                dash.hideGrille();
                //handleStartButtonClick();
                break;
            case "Avec grille":
                dash.showGrille();
                //handleStartButtonClick();
                break;
            case "Airways":
            	dash.showAirways();
                //handleStartButtonClick();
            	break;
            case "Exit":
                handleExitButtonClick();
                break;
            case "Speed Down":
            	increaseRefreshRate();
                break;
            case "Speed Up":
                decreaseRefreshRate();
                break;
            case "Pause":
                pauseSim();
                break;
            case "Airports":
                dash.AirportName();
                break;
            case "Airplanes":
                dash.AirplanesName();
                break;
            case "Air zones":
            	dash.Zone();
                break;
            case "Mountains":
                dash.Obst();
                break;
        }
    }
    

	public class BrightenButton extends MouseAdapter{
    	JButton newButton;
    	Color backgroundColor;
    	
    	BrightenButton(JButton newButton, Color bg){
    		this.newButton = newButton;
    		backgroundColor = bg;
    	}
    	
        public void mouseEntered(MouseEvent evt) {
            newButton.setBackground(backgroundColor.brighter()); // Change background color when mouse enters
        }

        public void mouseExited(MouseEvent evt) {
            newButton.setBackground(backgroundColor); // Restore original background color when mouse exits
        }
    }


    public class AirportClick extends MouseAdapter {
        public AirportClick(MainGUI mainGUI) {
        }

        public AirportClick() {}

        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX(), y = e.getY();
            ArrayList<Airport> ams = sim.getAirports();
            boolean airportClicked = false;

            for (Airport am : ams) {
                if (am.clicked(x, y)) {
                	infoPanel.displayAirportInfo(am);
                    airportClicked = true;
                    break;
                }
            }

            if (!airportClicked) {
            	infoPanel.hideAirportInfo();
            }
        }
    }

    public class PlaneClick extends MouseAdapter {
        public PlaneClick(MainGUI mainGUI) {
        }

        public PlaneClick() {}

        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX(), y = e.getY();
            ArrayList<AirplaneManager> ams = sim.getAirplaneManagers();
            boolean airplaneClicked = false;
            for (AirplaneManager am : ams) {
                if (am.clicked(x, y)) {
                	infoPanel.displayAirplaneInfo(am,sim);
                    airplaneClicked = true;
                    break;
                }
            }

            if (!airplaneClicked) {
            	infoPanel.hideAirplaneInfo();
            }
        }
    }
    
    public class TileClick extends MouseAdapter{
        private int blockSize = GameConfiguration.BLOCK_SIZE;
    	private final String file_path = "src/raw_data/obstacles/no_gos.txt";
    	public TileClick(MainGUI mg) {
    	}
    	
        public TileClick() {}
    	
    	@Override
    	public void mousePressed(MouseEvent e) {
    		int x = e.getX(), y = e.getY();
    		x /= blockSize;
    		y /= blockSize;
    		addObstacleLocation(x, y);
    	}

    	@Override
    	public void mouseDragged(MouseEvent e) {
    		int x = e.getX(), y = e.getY();
    		x /= blockSize;
    		y /= blockSize;
    		addObstacleLocation(x, y);
    	}
    	
    	private void addObstacleLocation(int x, int y) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file_path, true))) {
                // Log the mouse position to the file
                writer.write(x + ", " + y);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
    	}
    }
}
