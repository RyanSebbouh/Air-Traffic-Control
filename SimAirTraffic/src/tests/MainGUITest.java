package tests;

import config.GameConfiguration;
import data.map_elements.Obstacle;
import gui.Dashboard;
import gui.DisplayInfo;
import gui.InformationPanel;
import data.map_elements.Airport;
import manager.AirplaneManager;
import manager.ZoneManager;
import process.Simulation;
import process.SimulationUtility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
/**
 * This MainGUI class is a special one for the testing of the shared resource competition between 2 planes.
 * It creates a situation of collision to make sure that the wait happened.
 * @author MGHAIETH Faycal
 *
 */
public class MainGUITest extends JFrame implements Runnable, ActionListener {
	private static final long serialVersionUID = 1L;
	
	MultithreadingTest testMulti;
	
	private final Object lock = new Object();
    //private SimulationUtility simUtil = new SimulationUtility();
    private Simulation sim;
    private AirplaneManager airplaneManager;
    private Dashboard dash;
    private JPanel buttonPanel;
    private DisplayInfo infoPanel;
    private Container contentPane;
    private JButton menuButton;
    private ArrayList<Obstacle> obstacles; 
    private final static Dimension preferredSize = new Dimension(GameConfiguration.ADJUSTED_WINDOW_WIDTH,
            GameConfiguration.WINDOW_HEIGHT);
    
	private InformationPanel informationPanel;


    public MainGUITest(String title, MultithreadingTest test) {
    	
        super(title);
        this.testMulti = test;
        
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
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }

        int time = 0;
        
        // Defines when the airplanes test can start
        int tmp = 1;
        
        // This variable is used to wait a little before closing the thread after the plane has done wait()
        //It's to be sure that we can visualize the waiting of the plane on the screen
        int suivi = 0;

        while (time <= GameConfiguration.SIMULATION_DURATION) {
            SimulationUtility.unitTime();
            //First plane to go
            if(tmp == 1) { // chaque 5 unité de temps * 25 avions totale = 125
                ZoneManager departingZone = sim.addNewAirplaneforTEST(0, 4);
                AirplaneManager nextAirplane = sim.getNextAirplane();
                //System.out.println("coucou");

                if (nextAirplane != null) {
                    nextAirplane.setZoneManag(departingZone);
                    departingZone.enterZone(nextAirplane);
                    
                    nextAirplane.setRunning(true);
                    nextAirplane.start();
                }
            }
            //Second plane : goes out only 4 sec after the first one
            if(tmp == 4) { 
                ZoneManager departingZone = sim.addNewAirplaneforTEST(3, 1);
                AirplaneManager nextAirplane = sim.getNextAirplane();

                if (nextAirplane != null) {
                    nextAirplane.setZoneManag(departingZone);
                    departingZone.enterZone(nextAirplane);
                    
                    nextAirplane.setRunning(true);
                    nextAirplane.start();
                }
            }
            
            ArrayList<AirplaneManager> array = sim.getAirplaneManagers();
            
            // In the test, it's always the airplane 0 that has to wait, so we check if he does
            for(AirplaneManager air : array) {
            	if(air.getAirplaneId() == 0) {
            		
            		AirplaneManager airP = air;
            		
            		//If the plane is waiting then the test is true
            		if(airP.getState() == Thread.State.WAITING) {
                    	testMulti.setWaited(true);
                    	suivi++;
                    	
                    }
            		
            	}
            }
            
            //We stop the thread a little bit after the wait so we can see a little bit the waiting before closing the frame
            if(suivi == 2) {
            	return;
            }
            
            dash.repaint();
            time++;
            tmp++;
        }
        SimulationUtility.windowRefreshTime();
        sim.stopAllPlanes();
    }

    private void init() {
        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Use BoxLayout with Y_AXIS alignment
        buttonPanel.setBackground(new Color(58, 77, 57)); 
        
        addVerticalSpace(GameConfiguration.WINDOW_HEIGHT/25);
        addButton("Sans grille", new Color(169, 179, 136), Color.black);
        addVerticalSpace(GameConfiguration.WINDOW_HEIGHT/30); // Add vertical space of 10 pixels between buttons
        addButton("Avec grille", new Color(169, 179, 136), Color.black);
        addVerticalSpace(GameConfiguration.WINDOW_HEIGHT/30); // Add vertical space of 10 pixels between buttons
        addButton("Debug airways", new Color(169, 179, 136), Color.black);
        addVerticalSpace(GameConfiguration.WINDOW_HEIGHT/30); // Add vertical space of 10 pixels between buttons
        addButton("Exit", new Color(169, 179, 136), Color.black);
        addVerticalSpace(GameConfiguration.WINDOW_HEIGHT/30); // Add vertical space of 10 pixels between buttons
        addButton("Speed Down", new Color(169, 179, 136), Color.black);
        addVerticalSpace(GameConfiguration.WINDOW_HEIGHT/30); // Add vertical space of 10 pixels between buttons
        addButton("Speed Up", new Color(169, 179, 136), Color.black);
        addVerticalSpace(GameConfiguration.WINDOW_HEIGHT/30); 
        addButton("Mountains", new Color(169, 179, 136), Color.black);
        addVerticalSpace(GameConfiguration.WINDOW_HEIGHT/30); // Add vertical space of 10 pixels between buttons
        addButton("Airports", new Color(169, 179, 136), Color.black);
        addVerticalSpace(GameConfiguration.WINDOW_HEIGHT/30); // Add vertical space of 10 pixels between buttons
        addButton("Airplanes", new Color(169, 179, 136), Color.black);
        addVerticalSpace(GameConfiguration.WINDOW_HEIGHT/30); // Add vertical space of 10 pixels between buttons
        addButton("Air zones", new Color(169, 179, 136), Color.black);

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

    private void handleStartButtonClick() {
        synchronized (lock) {
            lock.notify();
            buttonPanel.setVisible(true);
        }
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

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Sans grille":
                dash.hideGrille();
                handleStartButtonClick();
                break;
            case "Avec grille":
                dash.showGrille();
                handleStartButtonClick();
                break;
            case "Debug airways":
            	dash.showAirways();
                handleStartButtonClick();
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
        private MainGUITest mainGUI;

        public AirportClick(MainGUITest mainGUI) {
            this.mainGUI = mainGUI;
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
        
        private MainGUITest mainTest;
        
        public PlaneClick(MainGUITest mainTest) {
            this.mainTest = mainTest;
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
        private MainGUITest mainTest;
    	
    	public TileClick(MainGUITest mg) {
    		mainTest = mg;
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
    
    public Simulation getSimulation() {
    	return sim;
    }
}
