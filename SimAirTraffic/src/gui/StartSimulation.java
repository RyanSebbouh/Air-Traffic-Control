package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.border.EmptyBorder;

import config.GameConfiguration;

public class StartSimulation extends JPanel {
    private Image airplaneImage;
    private Image cloudImage;
    private ArrayList<Cloud> clouds;
    private JButton startButton;
    private JButton settingsButton;


    private int airplaneX = GameConfiguration.WINDOW_WIDTH/3 + 50; // Initial x-coordinate of the airplane
    
    public StartSimulation() {
        JFrame frame = new JFrame("Air Traffic");
        init();
        frame.add(this);
        // Set the frame size to the preferred size
        frame.setSize(GameConfiguration.WINDOW_WIDTH, GameConfiguration.WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void init() {
        setLayout(null); // Set layout to absolute positioning

        // Set background color to light blue
        setBackground(new Color(173, 216, 230));

        try {
            airplaneImage = ImageIO.read(new File("src/images/airplane2.png"));
            cloudImage = ImageIO.read(new File("src/images/cloud.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize clouds
        clouds = new ArrayList<Cloud>();
        for (int i = 0; i < 5; i++) {
            clouds.add(new Cloud(cloudImage));
        }

        // Set up start button
        startButton = new JButton("Start Simulation");
        startButton.setBounds(GameConfiguration.WINDOW_WIDTH/2-GameConfiguration.WINDOW_WIDTH/11, GameConfiguration.WINDOW_HEIGHT/2+50, 200, 60); // Position and size of the button
        startButton.setForeground(new Color(104, 126, 255));
        startButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 14)); // Set button font to Comic Sans MS with size 14

        add(startButton); // Add button to the panel
      

        // Action listener for the start button
        startButton.addActionListener(new StartActionListener());
        
     // Set up settings button
        settingsButton = new JButton("Settings");
        settingsButton.setBounds(GameConfiguration.WINDOW_WIDTH / 2 - GameConfiguration.WINDOW_WIDTH / 11,
                GameConfiguration.WINDOW_HEIGHT / 2 + 120, 200, 60); // Position and size of the button
        settingsButton.setForeground(new Color(104, 126, 255));
        settingsButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 14)); // Set button font to Comic Sans MS with size 14
        add(settingsButton); // Add button to the panel

        // Action listener for the settings button
        settingsButton.addActionListener(new SettingsActionListener());
        


        // Set up a timer for cloud animation
        Timer timer = new Timer(40, new CloudTimerActionListener());
        timer.start();
    }
   

    @Override
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
        paintClouds(g);
        paintAirplane(g);
        drawTitle(g); // Call method to draw title
    }

    public void paintClouds(Graphics g) {
        // Draw clouds
        for (Cloud cloud : clouds) {
            g.drawImage(cloudImage, cloud.getX(), cloud.getY(), this);
        }
    }

    public void paintAirplane(Graphics g) {
        // Draw airplane

    	g.drawImage(airplaneImage, airplaneX+10, GameConfiguration.WINDOW_HEIGHT/5, 250, 250, this); // Adjust the position as needed
    }
    
    private void drawTitle(Graphics g) {
        // Set title color
    	Color titleColor = new Color(104, 126, 255); // RGB values for #0D9276
        g.setColor(titleColor);

        // Set title font
        Font titleFont = new Font("Comic Sans MS", Font.BOLD, 60); // Change font properties as needed
        g.setFont(titleFont);

        // Get the width of the title
        FontMetrics fm = g.getFontMetrics();
        int titleWidth = fm.stringWidth("Air Traffic Control");

        // Calculate position to center the title
        int x = (getWidth() - titleWidth) / 2;
        int y = getHeight() / 5; // Adjust vertical position as needed

        // Draw title
        g.drawString("Air Traffic Control", x, y);
    }


    private void moveClouds() {
        for (Cloud cloud : clouds) {
            cloud.move();
        }
    }
    
    private void openNewFrame() {
        
        // Code to open the new frame
        MainGUI launcher = new MainGUI("Aerien game");
        Thread guiThread = new Thread(launcher);
        guiThread.start();
        // Close the current frame
        SwingUtilities.getWindowAncestor(this).dispose();
    }
    
 // Method to open settings frame
    private void openSettingsFrame() {
        JFrame settingsFrame = new JFrame("Simulation Speed");
        settingsFrame.setSize(400, 200);
        settingsFrame.setLocationRelativeTo(null); // Center the frame on the screen
        settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this frame on close

        // Create a panel for settings
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayout(3, 2)); // Adjust layout as needed
        settingsPanel.setBackground(new Color(104, 126, 255)); // Set background color
        settingsPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add space around the panel


        // Add components to the settings panel
        JLabel speedLabel = new JLabel("Modify the Simulation Speed (200 the fastest) :");
        speedLabel.setForeground(Color.WHITE); // Set text color to white
        Font labelFont = new Font("Comic Sans MS", Font.BOLD, 15); // Change font properties as needed
        speedLabel.setFont(labelFont);
        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 200, 5000, 2000); // Adjust min, max, and initial value as needed
        speedSlider.setMajorTickSpacing(1000); // Adjust tick spacing as needed
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setForeground(Color.WHITE); // Set text color to white
        Font sliderFont = new Font("Comic Sans MS", Font.PLAIN, 12); // Change font properties as needed
        speedSlider.setFont(sliderFont);

        // Add components to the settings panel
        settingsPanel.add(speedLabel);
        settingsPanel.add(speedSlider);

        // Create and add a button to apply settings
        JButton applyButton = new JButton("Apply");
        applyButton.setForeground(new Color(104, 126, 255));
        Font buttonFont = new Font("Comic Sans MS", Font.BOLD, 15); // Change font properties as needed
        applyButton.setFont(buttonFont);
        applyButton.addActionListener(new ApplyButtonActionListener(speedSlider, settingsFrame));
        settingsPanel.add(applyButton);

        // Add the settings panel to the frame
        settingsFrame.add(settingsPanel);

        // Set frame visibility
        settingsFrame.setVisible(true);
    }

    // Method to update simulation speed (dummy implementation)
    private void updateSimulationSpeed(int speed) {
    	GameConfiguration.Refresh_rate = speed;
    }

    /************* Private classes defined here *************/
    
    /**
     * The {@link ActionListener} for the Start button.
     */
	private final class StartActionListener implements ActionListener {
		private final class StartTimerActionListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                airplaneX += 30; // Increase movement to 40 pixels
                repaint(); // Repaint the panel to show airplane movement
                if (airplaneX > GameConfiguration.WINDOW_WIDTH) { // Stop timer if airplane disappears from the panel
                    ((Timer)e.getSource()).stop();
                    openNewFrame(); // Open new frame
                }
            }
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// Move airplane to the right until it disappears
			Timer timer = new Timer(13, new StartTimerActionListener());
		    timer.start(); // Start the timer
		}
	}

	private final class SettingsActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
		    openSettingsFrame(); // Open settings frame
		}
	}

    private final class CloudTimerActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
		    moveClouds();
		    repaint(); // Repaint the panel to show cloud movement
		}
	}

	private final class ApplyButtonActionListener implements ActionListener {
		private final JSlider speedSlider;
		private final JFrame settingsFrame;

		private ApplyButtonActionListener(JSlider speedSlider, JFrame settingsFrame) {
			this.speedSlider = speedSlider;
			this.settingsFrame = settingsFrame;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		    // Get the selected speed from the slider
		    int selectedSpeed = speedSlider.getValue();
		    // Update the simulation speed
		    updateSimulationSpeed(selectedSpeed);
		    // Close the settings frame
		    settingsFrame.dispose();
		}
	}
    
    private static class Cloud {
        private int x;
        private int y;
        private int size;
        private int speed;
        private int imageSize;

        /**
         * Create a cloud.
         * @param strife
         */
        public Cloud(Image strife) {
        	imageSize = strife.getWidth(null);
            
            this.x = (int) (Math.random() * GameConfiguration.WINDOW_WIDTH);
            this.y = (int) (Math.random() * GameConfiguration.WINDOW_HEIGHT);
            this.size = (int) (Math.random() * 10) + 10;
            this.speed = (int) (Math.random() * 4) + 1;
            
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getSize() {
            return size;
        }

        public void move() {
            x += speed;
            if (x > GameConfiguration.WINDOW_WIDTH) {
                x = -imageSize; // Wrap around to the left side when cloud goes off the right side
            }
        }
    }

}
