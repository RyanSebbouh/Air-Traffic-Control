package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import config.GameConfiguration;
import data.Map;
import data.map_elements.Airport;
import data.map_elements.Block;
import manager.AirplaneManager;
import process.Simulation;
import process.SimulationUtility;

public class PaintStrategy {
	private int blockSize = GameConfiguration.BLOCK_SIZE;
	private int zoneSize = GameConfiguration.ZONE_SIZE;
	private Image airplaneImage;
	private Image airplaneImageEmergency;
	private Image airplaneImageWait;
	private int airplaneLength;
	// private Image obstacleImage;
	private Image airportImage; // Modified to use the airport image

	private Image backgroundImage; // Added for the background image
	private boolean paintZone = false;
	private boolean paintAirportName = false;
	private boolean paintAirplaneId = false;
	

	private String message; // Added JLabel for displaying messages
    private long messageDisplayStartTime;
    private boolean paintObst = false;

	// private SimulationUtility simUtil = new SimulationUtility();

	private boolean hideGrille = false, allAirwaysShown = false;

	public PaintStrategy() {
		try {
			ImageIcon airplaneImageIcon = new ImageIcon("src/images/plane_stL.png");
			airplaneLength = airplaneImageIcon.getIconHeight();

			airplaneImage = ImageIO.read(new File("src/images/plane_stL.png"));
			airplaneImageEmergency = ImageIO.read(new File("src/images/plane_stLRed.png"));
			airplaneImageWait = ImageIO.read(new File("src/images/plane_stLYellow.png"));
			airportImage = ImageIO.read(new File("src/images/location.png"));
			backgroundImage = ImageIO.read(new File("src/images/map3.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void paint(Simulation sim, Graphics graphics) {
		Map map = sim.getMap();
		ArrayList<Airport> aps = sim.getAirports();
		ArrayList<AirplaneManager> ams = sim.getAirplaneManagers();

		SimulationUtility.readCoord("src/raw_data/genes/loc_montagnes.txt", map);
		Block pathBlock = Simulation.zoneBlock;

		paintMap(map, graphics);
		paintAirports(aps, graphics);
		paintAirways(ams, graphics);
		//paintEmergencyAirways(ams, graphics);
		paintAccurateAirplanes(ams, graphics);
		displayEmergency(ams);
		paintMessage(graphics);
		if (!hideGrille) paintPathDebugging(pathBlock, graphics);
		
		if (paintAirportName) {
			paintAirportsName(aps, graphics);
		}
		if (paintAirplaneId) {
			paintAirplaneName(ams, graphics);
		}
		if (paintZone) {
			paintZone(sim, graphics);
		}
		if (paintObst) {
			paintObstacle(map, graphics);
		}
		// paintAirplanes(ams, graphics);
		// paintObstacles(obs, graphics);
		// paintInfo(graphics);

	}

	public void paintMap(Map map, Graphics graphics) {
		// Draw background image
		graphics.drawImage(backgroundImage, 0, 0, map.getColumnCount() * blockSize, map.getLineCount() * blockSize,
				null);

		if (!hideGrille) {
			Block[][] blocks = map.getBlocks();

			for (int lineIndex = 0; lineIndex < map.getLineCount(); lineIndex++) {
				for (int columnIndex = 0; columnIndex < map.getColumnCount(); columnIndex++) {
					Block block = blocks[lineIndex][columnIndex];

					/*
					 * if (block.getOccupied() == true) { graphics.setColor(Color.RED); } else {
					 */
					if ((lineIndex + columnIndex) % 2 == 0) {
						graphics.setColor(new Color(0.5f, 0.5f, 0.5f, 0.5f));
					} else {
						graphics.setColor(new Color(0.25f, 0.25f, 0.25f, 0.5f));
					}
					// }

					block.draw(graphics, columnIndex * blockSize, lineIndex * blockSize);
				}
			}
		}
	}

	public void paintObstacle(Map map, Graphics graphics) {
		// Draw background image
		/*
		 * graphics.drawImage(backgroundImage, 0, 0, map.getColumnCount() * blockSize,
		 * map.getLineCount() * blockSize, null);
		 */

		if (paintObst) {
			Block[][] blocks = map.getBlocks();

			for (int lineIndex = 0; lineIndex < map.getLineCount(); lineIndex++) {
				for (int columnIndex = 0; columnIndex < map.getColumnCount(); columnIndex++) {
					Block block = blocks[lineIndex][columnIndex];

					if (block.getOccupied() == true) {
						graphics.setColor(Color.RED);
						block.draw(graphics, columnIndex * blockSize, lineIndex * blockSize);
					} /*
						 * else { if ((lineIndex + columnIndex) % 2 == 0) { graphics.setColor(new
						 * Color(0.5f, 0.5f, 0.5f, 0.5f)); } else { graphics.setColor(new Color(0.25f,
						 * 0.25f, 0.25f, 0.5f)); } }
						 */

				}
			}
		}
	}

	public void paintZone(Simulation sim, Graphics graphics) {
		if (paintZone) {
			int nX = GameConfiguration.ZONE_X_MAX;
			int nY = GameConfiguration.ZONE_Y_MAX;
			for (int x = 0; x < nX; x++) {
				for (int y = 0; y < nY; y++) {
					if (sim.getZone(x, y).getCurrAirplane() != null || sim.getZone(x,y).getEmergencyAirplane() != null) {
						graphics.setColor(new Color(0.0f, 0.0f, 1.0f, 0.75f));
					} else {
						if ((x + y) % 2 == 0) {
							graphics.setColor(new Color(0.5f, 0.5f, 0.5f, 0.25f));
						} else {
							graphics.setColor(new Color(0.25f, 0.25f, 0.25f, 0.25f));
						}
					}

					graphics.fillRect(x * zoneSize, y * zoneSize, zoneSize, zoneSize);

				}
			}
		}
	}
	

	public void paintAirports(ArrayList<Airport> airpManagers, Graphics graphics) {
		for (Airport am : airpManagers) {
			Block b = am.getPosition();

			// Draw the airport image instead of filling oval
			graphics.drawImage(airportImage, b.getxCoordBlock() * blockSize, b.getyCoordBlock() * blockSize, blockSize,
					blockSize, null);
		}
	}

	public void paintAirportsName(ArrayList<Airport> aps, Graphics graphics) {

		// Iterate over the airports in the ArrayList<AirportManager>
		for (int i = 0; i < aps.size(); i++) {
			Airport a = aps.get(i);
			Block b = a.getPosition();
			graphics.setColor(Color.BLACK);

			// Draw the city name on top of the airport image
			graphics.drawString(a.getName(), b.getxCoordBlock() * blockSize, b.getyCoordBlock() * blockSize - 10);
		}
	}

	public void paintAirways(ArrayList<AirplaneManager> airpManagers, Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2f));

		if (!allAirwaysShown) {
			for (AirplaneManager am : airpManagers) {
				
				if(am.isHavingEmergency()) {
					g2.setColor(new Color(1f, 0f, 0f, 0.33f));
				} else {
					g2.setColor(new Color(1f, 1f, 1f, 0.33f));
				}
				
				ArrayList<Block> airwayPath = am.getAllDests();
				for (int i = 0; i < airwayPath.size() - 1; i++) {
					Block bSrc = airwayPath.get(i);
					Block bDst = airwayPath.get(i + 1);

					int originX = bSrc.getxCoordBlock();
					int originY = bSrc.getyCoordBlock();
					int destX = bDst.getxCoordBlock();
					int destY = bDst.getyCoordBlock();
					g2.drawLine(originX * blockSize + blockSize / 2, originY * blockSize + blockSize / 2,
							destX * blockSize + blockSize / 2, destY * blockSize + blockSize / 2);
				}
			}
		} else {
			g2.setColor(new Color(1f, 1f, 1f, 0.33f));
			for (ArrayList<Block> airwayPath : GameConfiguration.AIRWAYS.values()) {
				for (int i = 0; i < airwayPath.size() - 1; i++) {
					Block bSrc = airwayPath.get(i);
					Block bDst = airwayPath.get(i + 1);

					int originX = bSrc.getxCoordBlock();
					int originY = bSrc.getyCoordBlock();
					int destX = bDst.getxCoordBlock();
					int destY = bDst.getyCoordBlock();
					g2.drawLine(originX * blockSize + blockSize / 2, originY * blockSize + blockSize / 2,
							destX * blockSize + blockSize / 2, destY * blockSize + blockSize / 2);
				}
			}
		}
	}

	private Image rotateImage(Image originalImage, int degrees) {
		// 180 because the airplane image is the wrong way round
		double radians = Math.toRadians(degrees + 180);

		// Create an AffineTransform to apply rotation
		AffineTransform transform = new AffineTransform();
		transform.rotate(radians, originalImage.getWidth(null) / 2.0, originalImage.getHeight(null) / 2.0);

		// Create a new BufferedImage and apply the transformation
		Image rotatedImage = new BufferedImage(originalImage.getWidth(null), originalImage.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) rotatedImage.getGraphics();
		g2d.setTransform(transform);
		g2d.drawImage(originalImage, 0, 0, null);

		return rotatedImage;
	}

	public void paintAirplanes(ArrayList<AirplaneManager> ams, Graphics graphics) {
		synchronized (ams) {
			for (AirplaneManager am : ams) {
				synchronized (am) {
					int[] coord = am.getPosition();
					int direction = am.getDirection();
					Image rotatedAirplaneImage;
					int altitude = am.getAltitude();
					if (altitude != 0) {
						if(am.isHavingEmergency()) {
							rotatedAirplaneImage = rotateImage(airplaneImageEmergency, direction);
						}else {
							rotatedAirplaneImage = rotateImage(airplaneImage, direction);
						}

						int airplaneImageSize = blockSize; // (int) ((double)blockSize * altitudeScaled);
						// System.out.println(altitude + " " + altitudeScaled + " " +
						// airplaneImageSize);

						graphics.drawImage(rotatedAirplaneImage, coord[0] * blockSize, coord[1] * blockSize,
								airplaneImageSize, airplaneImageSize, null);
					}
				}
			}
		}
	}
	
	public void paintAirplaneName(ArrayList<AirplaneManager> ams, Graphics graphics) {
		synchronized (ams) {
			for (AirplaneManager am : ams) {
				synchronized (am) {
					int[] coord = am.getPosition();
					int altitude = am.getAltitude();
					if (altitude != 0) {
						graphics.setColor(Color.BLACK);
						graphics.drawString("Id "+am.getAirplaneId(), coord[0] * blockSize, (coord[1]-1) * blockSize);
					}
				}
			}
		}
	}

	public void paintAccurateAirplanes(ArrayList<AirplaneManager> ams, Graphics graphics) {
		Graphics2D graphics2d = (Graphics2D) graphics;
		synchronized (ams) {
			for (AirplaneManager am : ams) {
				synchronized (am) {
					double[] coordDouble = am.getExactPosition();
					int direction = am.getDirection();

					int altitude = am.getAltitude();
					if (altitude != 0) {
						Image airplImage = am.isHavingEmergency()? airplaneImageEmergency : airplaneImage;
						airplImage = am.isWaiting() ? airplaneImageWait : airplImage;
						
						Image rotatedAirplaneImage = rotateImage(airplImage, direction);

						// Scale altitude from [0; 10000] to [0;1]
						int airplaneImageSize = blockSize; // (int) ((double)blockSize * altitudeScaled);
						// System.out.println(altitude + " " + altitudeScaled + " " +
						// airplaneImageSize);

						double scaleTo = (double) airplaneImageSize / (double) airplaneLength;
						AffineTransform at = AffineTransform.getTranslateInstance(coordDouble[0] * blockSize,
								coordDouble[1] * blockSize);
						at.scale(scaleTo, scaleTo);
						graphics2d.drawImage(rotatedAirplaneImage, at, null);
					}
				}
			}
		}
	}
    public void displayMessage(String message) {
        this.message = message;
        this.messageDisplayStartTime = System.currentTimeMillis();
    }
    
    public void displayEmergency(ArrayList<AirplaneManager> ams) {
    	//synchronized (ams) {
    	for (AirplaneManager am : ams) {
    		if(am.isHavingEmergency()) {
    			displayMessage("Airplane "+am.getAirplane().getAirplaneId()+" is having an emergency landing");
    		}
    	}
    	//}
    }

    /**
     * Writes a message to the screen.
     * @param graphics
     */
    public void paintMessage(Graphics graphics) {
        if (message != null) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - messageDisplayStartTime;
            // Display message for 4 seconds
            if (elapsedTime <= 4000) {
                graphics.setColor(new Color(50, 80, 50, 190));
                graphics.fillRect(0, GameConfiguration.WINDOW_HEIGHT-100, GameConfiguration.WINDOW_WIDTH, blockSize*5);

                FontMetrics fm = graphics.getFontMetrics();
                int stringWidth = fm.stringWidth("This plane is in Emergency landing: ");
                int x = (GameConfiguration.WINDOW_WIDTH - stringWidth) / 3;
                // Calculate the position to center the string vertically
                int y = GameConfiguration.WINDOW_HEIGHT - (blockSize * 7) / 2 + fm.getHeight() / 4;

                Font font = graphics.getFont();
                int newSize = font.getSize() * 2;
                Font newFont = font.deriveFont((float) newSize);
                graphics.setFont(newFont);

                //Airplane airplane = showAMInfo.getAirplane();
                //if (showAMInfo.isHavingEmergency()) {
                    graphics.setColor(Color.red);
                    graphics.drawString(message, x, y);
                //}

                // Reset the message after displaying for 4 seconds
                if (elapsedTime >= 4000) {
                    message = null;
                }
            } else {
                message = null; // Clear the message after 4 seconds
            }
        }
    }

	/*
	 * public void paintObstacles(ArrayList<Obstacle> obs, Graphics graphics) { for
	 * (Obstacle ob : obs) { graphics.drawImage(obstacleImage, ob.getxCoordBlock() *
	 * blockSize, ob.getyCoordBlock() * blockSize, blockSize, blockSize, null); } }
	 */

	
	  /*public void paintInfo(Graphics graphics) { 
		  if (showAMInfo != null) {
			  graphics.setColor(new Color(50, 80, 50)); 
			  graphics.fillRect(0, 0, blockSize *9, GameConfiguration.WINDOW_HEIGHT);
			  
			  Font f = graphics.getFont(); int newSize = f.getSize() * 4; 
			  Font newF =f.deriveFont(newSize);
			 
			  Airplane airp = showAMInfo.getAirplane(); graphics.setColor(new Color(1f, 1f,1f, 0.75f)); 
			  graphics.setFont(newF);
			  
			  graphics.drawString("Airplane ID: " + airp.getAirplaneId(), blockSize, (int)
			  (newSize * 0.5)); 
			  graphics.drawString("Heading: " + airp.getDirection(),blockSize, (int) (newSize * 1)); 
			  graphics.drawString("Speed: " + airp.getSpeed(), blockSize, (int) (newSize * 1.5));
			  graphics.drawString("Altitude: " + airp.getAltitude(), blockSize, (int)(newSize * 2)); 
			  graphics.drawString("Position: " + airp.getX() + ", " + airp.getY(), blockSize, (int) (newSize * 2.5)); 
		  } 
	  }*/
	 

	/**
	 * Debug: Show which zone the airplane will be in next
	 * 
	 * @param zoneBlock
	 * @param g
	 */
	public void paintPathDebugging(Block zoneBlock, Graphics g) {
		if (zoneBlock != null) {
			int xIndex = zoneBlock.getxCoordBlock(), yIndex = zoneBlock.getyCoordBlock();

			g.setColor(new Color(1f, 0.25f, 0.25f, 0.25f));
			g.fillRect(xIndex * zoneSize, yIndex * zoneSize, zoneSize, zoneSize);
		}
	}

	public void hideGrille() {
		hideGrille = true;
	}

	public void hideZone() {
		paintZone = false;
	}

	public void showZone() {
		paintZone = true;
	}

	public void hideObst() {
		paintObst = false;
	}

	public void showObst() {
		paintObst = true;
	}

	public void showGrille() {
		hideGrille = false;
	}

	public void showAirways() {
		allAirwaysShown = true;
	}

	public void setAM(AirplaneManager am) {
	}

	public void hideName() {
		paintAirportName = false;
	}

	public void showName() {
		paintAirportName = true;
	}
	
	public void hidePlaneId() {
		paintAirplaneId = false;
	}

	public void showPlaneId() {
		paintAirplaneId = true;
	}

	public boolean isPaintAirportName() {
		return paintAirportName;
	}
	
	public boolean isPaintZone() {
		return paintZone;
	}

	public void setPaintZone(boolean paintZone) {
		this.paintZone = paintZone;
	}

	public boolean isPaintObst() {
		return paintObst;
	}

	public void setPaintObst(boolean paintObst) {
		this.paintObst = paintObst;
	}
	
    public boolean isPaintAirplaneId() {
		return paintAirplaneId;
	}

}
