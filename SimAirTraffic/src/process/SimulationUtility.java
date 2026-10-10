package process;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import config.GameConfiguration;
import data.Airplane;
import data.Map;
import data.Vol;
import data.map_elements.Block;
import data.map_elements.Obstacle;
import logs.LoggerUtility;
import data.map_elements.Airport;

/**
 * Static functions useful to run the simulation
 */
public class SimulationUtility {

	private static int speed;
    private static final int ZONE_SIZE = GameConfiguration.ZONE_IN_BLOCK_LENGTH;
    private static int amountOfBlocksToCenterOfZone = Math.round(GameConfiguration.ZONE_IN_BLOCK_LENGTH/2);
	private static final int NB_BLOCK_ZONE = GameConfiguration.ZONE_IN_BLOCK_LENGTH;
	
	private static Logger logger = LoggerUtility.getLogger(SimulationUtility.class, "html");
	
	/**
	 * This is a class for static functions. Constructors are not needed.
	 */
	private SimulationUtility(){}

	/**
	 * Simulates the unit time (for an iteration) defined for the simulation.
	 */
	public static void unitTime() {
		try {
			Thread.sleep(GameConfiguration.Refresh_rate);
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Generates an airplane going from an origin airport to a destination airport
	 * @param airportSys The Object containing the airports
	 * @param id
	 * @param originNum
	 * @param airportNum
	 * @return
	 */
	public static Airplane generateNewAirplaneWithSrcDst(int id, int depart, int arrivee) {
		// Choosing a random flight path
		Random r = new Random();
		
		ArrayList<Airport> ap = generateAirports();
		
		// Extracting the origin and destinations of the flight path
		Block origin = ap.get(depart).getPosition();
		Block dest = ap.get(arrivee).getPosition();
		
		// Set direction
		double radianDirection = Math.atan2(dest.getyCoordBlock() - origin.getyCoordBlock(), dest.getxCoordBlock() - origin.getxCoordBlock());
		int dir = (int)Math.toDegrees(radianDirection);
		
		Vol volAvion = new Vol(ap.get(depart), ap.get(arrivee));
		
		Airplane newA = new Airplane(id, 10000, ap.get(depart), dir, r.nextInt(11));
		newA.setVol(volAvion);
		
		logger.info("Airplane with id " + id +  " now flying flight " + (depart*10+arrivee));
		//System.out.println(GameConfiguration.AIRWAYS);
		
		
		ArrayList<Block> array = GameConfiguration.AIRWAYS.get(depart*10+arrivee);
		if(array == null) {
			System.out.println("ERREUR NULL : " + (depart*10+arrivee));
		}
		for(Block b : GameConfiguration.AIRWAYS.get(depart*10+arrivee)) {
			newA.addDestination(b);
		}
		newA.finishAddingDests();
		return newA;
	}
	
	/**
	 * The same method as generateNewAirplaneWithSrcDst but used for the multithreading test, the difference is when we define the speed of an airplane
	 * @param id
	 * @param depart
	 * @param arrivee
	 * @return
	 */
	public static Airplane generateNewAirplaneWithSrcDstForTEST(int id, int depart, int arrivee) {
		
		ArrayList<Airport> ap = generateAirports();
		
		// Extracting the origin and destinations of the flight path
		Block origin = ap.get(depart).getPosition();
		Block dest = ap.get(arrivee).getPosition();
		
		// Set direction
		double radianDirection = Math.atan2(dest.getyCoordBlock() - origin.getyCoordBlock(), dest.getxCoordBlock() - origin.getxCoordBlock());
		int dir = (int)Math.toDegrees(radianDirection);
		
		Vol volAvion = new Vol(ap.get(depart), ap.get(arrivee));
		
		Airplane newA = new Airplane(id, 10000, ap.get(depart), dir, 6);
		newA.setVol(volAvion);
		
		System.out.println(depart*10+arrivee);
		System.out.println(GameConfiguration.AIRWAYS);
		
		
		ArrayList<Block> array = GameConfiguration.AIRWAYS.get(depart*10+arrivee);
		if(array == null) {
			System.out.println("ERREUR NULL : " + (depart*10+arrivee));
		}
		for(Block b : GameConfiguration.AIRWAYS.get(depart*10+arrivee)) {
			newA.addDestination(b);
		}
		newA.finishAddingDests();
		return newA;
	}

	
	public static Airplane generateNewAirplane(ArrayList<Airport> airports, int id) {
		// Loading possible flight paths
		String filePath = "src/raw_data/airways.txt";
		List<String> lines = SimulationUtility.loadFile(filePath);
		int nbAirports = lines.size();
		
		ArrayList<Airport> ap = airports;
		
		// Choosing a random flight path
		Random r = new Random();
		
		int numAeroportDepart = r.nextInt(nbAirports);
		String destChoices = lines.get(numAeroportDepart);
		int nbDest = destChoices.length() - 1; // Adjusted for indexing
		int destChoice = r.nextInt(nbDest/2 + 1);
		int numAeroportArrivee = Integer.parseInt(destChoices.substring(destChoice*2, destChoice*2+1));
		
		return generateNewAirplaneWithSrcDst(id, numAeroportDepart, numAeroportArrivee);
	}

	/**
	 * Load a file's text contents into a List
	 * @param filePath The text file to read
	 * @return The text contents of the file
	 * @throws IOException Thrown if the file does not exist
	 */
	public static List<String> readLinesFromFile(String filePath) throws IOException {
		List<String> lines = new ArrayList<String>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		}

		return lines;
	}

	/**
	 * Open a non-empty file and return its contents. Handles {@link IllegalArgumentException} in case the file is empty
	 * @param filePath The text file to read from
	 * @return The text contents of the file
	 * @throws IllegalArgumentException Thrown if the file exists but it is empty for some reason
	 */
	public static List<String> loadFile(String filePath) throws IllegalArgumentException{
		List<String> lines = null;
		try {
			lines = readLinesFromFile(filePath);
		} catch (IOException e) {
			System.err.println("File not found (" + filePath + ")");
		}
		if (lines.isEmpty()) {
			throw new IllegalArgumentException("File is empty");
		}
		return lines;
	}
	
	/**
	 * Find and return the locations of the mountains.
	 * @param filePath The text file that stores the mountain coordinates
	 * @param map The map that the airplanes fly on
	 * @return The location of the mountains
	 */
	public static ArrayList<Obstacle> readCoord(String filePath, Map map){

        ArrayList<Obstacle> obstacles = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] coordinates = line.split(", ");
                
                int x = Integer.parseInt(coordinates[0]);
                int y = Integer.parseInt(coordinates[1]);

                Block obstacle = map.getBlock(y, x);
                obstacle.setOccupied(true);
                obstacles.add(new Obstacle(obstacle));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		return obstacles;
	}
	
	/**
	 * Get and return the {@link ArrayList} location of all of the airports
	 * @return The location of all of the airports
	 */
	public static ArrayList<Block> getAllAirports() {
		ArrayList<Block> allAirp = new ArrayList<Block>();
		
		String filePath = "src/raw_data/airport_location.txt";
		List<String> lines = SimulationUtility.loadFile(filePath);
		
		for(int i = 0; i<lines.size(); i++) {
			String currAirport = lines.get(i);
			String[] currAirportTable = currAirport.split(" ");
			allAirp.add(new Block(Integer.parseInt(currAirportTable[0]), Integer.parseInt(currAirportTable[1])));
		}
		return allAirp;
	}
	
	/**
	 * The airways are defined by the start, intermediate, and end blocks that the airplanes fly to.
	 * Read the blocks that defined the airways and associate them to a HashMap.
	 * @return A hashmap of all airways
	 */
	public static HashMap<Integer, ArrayList<Integer>> getAllAirways() {
		HashMap<Integer, ArrayList<Integer>> allAirw = new HashMap<Integer, ArrayList<Integer>>();
		
		String filePath = "src/raw_data/airways.txt";
		List<String> lines = SimulationUtility.loadFile(filePath);
		
		for(int i = 0; i<lines.size(); i++) {
			ArrayList<Integer> possibleDestsInt = new ArrayList<Integer>();
			
			String possibleDests = lines.get(i);
			String[] possibleDestsSplit = possibleDests.split(" ");
			for(String possDest : possibleDestsSplit) {
				possibleDestsInt.add(Integer.parseInt(possDest));
			}
			allAirw.put(i, possibleDestsInt);
		}
		
		return allAirw;
	}
	
	/**
	 * The airports are located at specific blocks
	 * This method gets the locations of the airplanes and orders them from left-most to right-most.
	 * @return The blocks that the airplans are located at.
	 */
	public static ArrayList<Block> getAirportFromFile(){
		ArrayList<Block> alb = new ArrayList<Block>();
		
		String filePath = "src/raw_data/airport_location.txt";
		List<String> lines = SimulationUtility.loadFile(filePath);
		
		for(String entry : lines) {
			String blocks[] = entry.split(",");
			alb.add(new Block(Integer.parseInt(blocks[1]), Integer.parseInt(blocks[2])));
		}
		
		return alb;
	}
	
	/**
	 * The airways are defined by the start, intermediate, and end blocks that the airplanes fly to.
	 * Read the blocks that defined the airways and associate them to a HashMap.
	 * @return A hashmap of all airways
	 */
	public static HashMap<Integer, ArrayList<Block>> getAirwaysFromFile() {
		HashMap<Integer, ArrayList<Block>> hiab = new HashMap<Integer, ArrayList<Block>>();
		
		String filePath = "src/raw_data/airway_detail.txt";
		List<String> lines = SimulationUtility.loadFile(filePath);
		
		ArrayList<Block> ab = new ArrayList<Block>();
		int id=0;
		for(String entry : lines) {
			if(!entry.startsWith("//")) {
				if(!entry.isBlank()) {
					String[] coords = entry.split(" ");
					ab.add(new Block(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
				} else {
					hiab.put(id, ab);
					ab = new ArrayList<Block>();
				}
			} else {
				String[] idTmp = entry.substring(2).split(" -> ");
				id = Integer.parseInt(idTmp[0])*10 + Integer.parseInt(idTmp[1]);
			}
		}
		hiab.put(id, ab);
		
		//System.out.println(hiab);
		return hiab;
	}
	
	/**
	 * Simulated the necessary refresh time for avoiding printing issue.
	 */
	public static void windowRefreshTime() {
		try {
			Thread.sleep(GameConfiguration.Refresh_rate * 10);
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Returns the index of the closest airport to an airplane.
	 * @param airplaneX
	 * @param airplaneY
	 * @return The index of the closest airport, or -1 if we were unable to get the airports
	 */
	public static int calculateClosestAirport_(double airplaneX, double airplaneY) {
	    ArrayList<Block> airports = GameConfiguration.AIRPORTS;
	    int nearestAirport = -1;
	    double minDistance = Double.MAX_VALUE;

	    for(int i=0; i<airports.size(); i++) {
	    	Block airport = airports.get(i);
	    	double distance = calculateDistance(airplaneX, airplaneY, airport.getxCoordBlock(), airport.getyCoordBlock());
	        if (distance < minDistance) {
	            minDistance = distance;
	            nearestAirport = i;
	        }
	    }

	    return nearestAirport;
	}

	private static double calculateDistance(double x1, double y1, double x2, double y2) {
	    return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
    
    /**
     * Checks whether the airplane is within a zone, given the airplane's {@link Block} coordinates and the Zone's {@link Zone} coordinates
     * @param xAirplane The integer x-coordinate of the airplane
     * @param yAirplane The integer y-coordinate of the airplane
     * @param xZone The zone x-coordinate
     * @param yZone The zone y-coordinate
     * @return Whether the x and y coordinates are within the zone's range
     */
    public static boolean coordsInZone(int xAirplane, int yAirplane, int xZone, int yZone) {
    	//System.out.println(xZone + " =?= " + xAirplane / ZONE_SIZE + "; " + yZone + " =?= " + yAirplane / ZONE_SIZE);
    	return (xZone == xAirplane / ZONE_SIZE) && (yZone == yAirplane / ZONE_SIZE);//(xZoneLeftCoord < xAirplane && xAirplane < xZoneLeftCoord + ZONE_SIZE) && (yZoneLeftCoord < yAirplane && yAirplane < yZoneLeftCoord + ZONE_SIZE);
    }

    /**
     * Returns the zone a given coordinate is found in
     * @param xCoord The x-coordinate
     * @param yCoord The y-coordinate
     * @return The zone the coordinate is in
     */
    public static Block getZoneOfCoords(int xCoord, int yCoord) {
    	int xZone = xCoord / ZONE_SIZE;
    	int yZone = yCoord / ZONE_SIZE;
    	return new Block(xZone, yZone);
    }
    
    /**
     * Returns the center Block of a zone
     * @param x The x-coordinate of the zone
     * @param y The y-coordinate of the zone
     * @return The center Block of a zone
     */
    public static Block getZoneCenter(int x, int y) {
    	int bx = x*GameConfiguration.ZONE_IN_BLOCK_LENGTH + amountOfBlocksToCenterOfZone;
    	int by = y*GameConfiguration.ZONE_IN_BLOCK_LENGTH + amountOfBlocksToCenterOfZone;
    	return new Block(bx, by);
    }
    
	 public int getSpeed() {
		return speed;
	}

	public static void setSpeed(int speed) {
		SimulationUtility.speed = speed;
	}
	
	public static ArrayList<Airport> generateAirports() {
		String filename = "src/raw_data/airport_location.txt";
		ArrayList<Airport> listAirports = new ArrayList<Airport>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                	String name = parts[0];
                    int coordX = Integer.parseInt(parts[1].trim());
                    int coordY = Integer.parseInt(parts[2].trim());
                    int maxPlanes = Integer.parseInt(parts[3].trim());
                    
                    Block position = new Block(coordX, coordY);

                    Airport airport = new Airport(name, position, maxPlanes);
                    airport.setIndex(i);
                    i++;
                    listAirports.add(airport);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listAirports;
  }
	
	
    public static ArrayList<Block> calculateCorners(int airportX, int airportY){
        Block airportZone = SimulationUtility.getZoneOfCoords(airportX, airportY);
        int topLeftX = airportZone.getxCoordBlock() * NB_BLOCK_ZONE, topLeftY = airportZone.getyCoordBlock() * NB_BLOCK_ZONE;
        
        ArrayList<Block> circleBlocks = new ArrayList<Block>();
        circleBlocks.add(new Block(topLeftX-1, topLeftY-1));                             // Top left
        circleBlocks.add(new Block(topLeftX+NB_BLOCK_ZONE, topLeftY-1));                 // Top right
        circleBlocks.add(new Block(topLeftX+NB_BLOCK_ZONE, topLeftY+NB_BLOCK_ZONE));     // Bottom right
        circleBlocks.add(new Block(topLeftX-1, topLeftY+NB_BLOCK_ZONE));                // Bottom left
        
        return circleBlocks;
    }
}
