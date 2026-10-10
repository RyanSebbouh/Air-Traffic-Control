package process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import config.GameConfiguration;
import data.Airplane;
import data.Map;
import data.map_elements.Airport;
import data.map_elements.Block;
import manager.AirplaneManager;
import manager.AirportManager;
import manager.ZoneManager;

/**
 * The simulation management class. Its contains and prepares all
 * {@link AirplaneManager}.
 * 
 * The class has also some basic operations needed by the simulation.
 * 
 * @author Yang Mattew, Ryan Sebbouh
 */
public class Simulation {
    private static final int COLUMN_COUNT = GameConfiguration.ZONE_X_MAX;
    private static final int LINE_COUNT = GameConfiguration.ZONE_Y_MAX;
    private int idCount = 0;
    
    private final Object lock = new Object();
    
	private Map map;
	//private SimulationUtility simUtil = new SimulationUtility();

	private volatile ArrayList<ArrayList<ZoneManager>> zoneManager = new ArrayList<ArrayList<ZoneManager>>(); //= ZoneManager.getInstance();
	private volatile HashMap<Integer, ZoneManager> zoneManagersByBlock = new HashMap<Integer, ZoneManager>();
	
	private volatile ArrayList<AirportManager> airportManagers = new ArrayList<AirportManager>();
	/**
	 * Static to make sure that there's only one airplaneManagers instance (there's only one instance of simulation)
	 */
	public ArrayList<AirplaneManager> airplaneManagers = new ArrayList<AirplaneManager>();
	public static Block zoneBlock;
	private ArrayList<Airport> airports = new ArrayList<Airport>();

	public Simulation() {
		map = new Map();
		airports = SimulationUtility.generateAirports();
		
		for(int i=0; i < COLUMN_COUNT; i++) {
			ArrayList<ZoneManager> alz = new ArrayList<ZoneManager>();
            for(int j=0; j < LINE_COUNT; j++) {
            	ZoneManager z = new ZoneManager(false, i, j);
            	
            	alz.add(z); //new AtomicBoolean(false);
            	
            	Integer zoneCoord = i*100+j;
            	zoneManagersByBlock.put(zoneCoord, z);
            }
            zoneManager.add(alz);
		}
		
	}
	
	/**
	 * Gets a specified zone
	 * @param x The x-coordinate of the desired zone
	 * @param y The y-coordinate of the desired zone
	 * @return The specified zone
	 */
	public ZoneManager getZone(int x, int y) {
		return zoneManagersByBlock.get(x*100+y);
	}
	
	/**
	 * Generates a random zone that includes an airport
	 * @return
	 */
	public ZoneManager getRandomZone() {
		Random r = new Random();
		ArrayList<Integer> ali = new ArrayList<Integer>();
		ali.add(r.nextInt(2)*3); // Either 0 or 3
		ali.add(3 - ali.get(0));
		
		Airplane airp = SimulationUtility.generateNewAirplaneWithSrcDst(idCount, ali.get(0), ali.get(1)); idCount++;
		AirplaneManager am = new AirplaneManager(this, airp, map);
		int[] pos = am.getPosition();
		Block b = SimulationUtility.getZoneOfCoords(pos[0], pos[1]);
		
		return zoneManager.get(b.getxCoordBlock()).get(b.getyCoordBlock());
	}

	/**
	 * Generate an airplane starting and going to a hard-coded airport.
	 */
	public ZoneManager addSpecificAirplane(int time) {
		ArrayList<Integer> ali = new ArrayList<Integer>();
		ali.add(7);
		ali.add(3);
		
		Airplane airp = SimulationUtility.generateNewAirplaneWithSrcDst(idCount, ali.get(0), ali.get(1)); idCount++;
		AirplaneManager am = new AirplaneManager(this, airp, map);
		int[] pos = am.getPosition();
		Block b = SimulationUtility.getZoneOfCoords(pos[0], pos[1]);
		airplaneManagers.add(am);
		
		return zoneManager.get(b.getxCoordBlock()).get(b.getyCoordBlock());
	}
	
	/**
	 * Generate a new airplane and add it to the list of airplanes flying
	 */
	public ZoneManager addNewAirplane() {
		Airplane airp = SimulationUtility.generateNewAirplane(airports, idCount); idCount++;
		AirplaneManager am = new AirplaneManager(this, airp, map);
		
		synchronized (lock) {
			airplaneManagers.add(am);
		}

		int[] pos = am.getPosition();
		Block b = SimulationUtility.getZoneOfCoords(pos[0], pos[1]);
		return zoneManager.get(b.getxCoordBlock()).get(b.getyCoordBlock());
	}
	
	
	/**
	 * The same method as addNewAirplane but used for the multithreading test 
	 * @param numDepart the id of starting airport
	 * @param numArrivee the id of destination airport
	 * @return
	 */
	public ZoneManager addNewAirplaneforTEST(int numDepart, int numArrivee) {
		Airplane airp = SimulationUtility.generateNewAirplaneWithSrcDstForTEST(idCount, numDepart, numArrivee); idCount++;
		AirplaneManager am = new AirplaneManager(this, airp, map);
		
		synchronized (lock) {
			airplaneManagers.add(am);
		}

		int[] pos = am.getPosition();
		Block b = SimulationUtility.getZoneOfCoords(pos[0], pos[1]);
		return zoneManager.get(b.getxCoordBlock()).get(b.getyCoordBlock());
	}

	public void addAirplane(AirplaneManager a) {
		airplaneManagers.add(a);
	}
	
	public void removeAirplane(AirplaneManager am) {
		airplaneManagers.remove(am);
	}

	/**
	 * Gets the next airplane that issn't already running; it's synchronized to
	 * prevent multiple threads trying to start the same plane
	 * 
	 * @return The first airplane not running or null if they're all running
	 */
	public AirplaneManager getNextAirplane() {
		for (AirplaneManager am : airplaneManagers) {
			if (am != null && !am.isRunning() && !am.isStarted()) {
				am.setStarted(true);
				return am;
			}
		}
		return null;
	}

	public void startAllPlanes() {
		for (AirplaneManager am : airplaneManagers) {
			am.setRunning(true);
		}
	}

	public void stopAllPlanes() {
		for (AirplaneManager am : airplaneManagers) {
			am.setRunning(false);
		}
	}
	
	/**
	 * Get all of the airplane managers.
	 * @return An {@link ArrayList} of all of the {@link AirplaneManager}s.
	 */
	public ArrayList<AirplaneManager> getAirplaneManagers() {
		return airplaneManagers;
	}
	
	public Map getMap() {
		return map;
	}

	public void increaseRefreshRate(int i) {
		GameConfiguration.Refresh_rate += i;
	}

	public void decreaseRefreshRate(int i) {
		if(GameConfiguration.Refresh_rate > 200) {
			GameConfiguration.Refresh_rate -= i;
		}
	}
	
	public void pause(boolean pause) {
		if(!pause) {
			for (AirplaneManager am : airplaneManagers) {
				am.setAirplaneSpeed(0);
			}
			
		}else {
			for (AirplaneManager am : airplaneManagers) {
				am.setAirplaneSpeed(1 + (double)am.getAirplane().getSpeed()/20);
			}
		}
	}
	
	/**
	 * Triggers an emergency
	 */
	public void triggerEmergency() {
		airplaneManagers.get(5).triggerEmergency();
	}
	
	/**
	 * @deprecated DO NOT USE. Use {@link #getAirports()} instead.
	 */
	public ArrayList<AirportManager> getAirportManagers() {
		return airportManagers;
  }
  
	/**
	 * Gets all the airports on the map
	 * @return
	 */
	public ArrayList<Airport> getAirports() {
		return airports;
	}

	public void setAirports(ArrayList<Airport> airports) {
		this.airports = airports;
	}
	
	public AirplaneManager getAirplaneManagerfromAirplane(Airplane airp) {		
		int id = airp.getAirplaneId();
		
		for(AirplaneManager apm : airplaneManagers) {
			if(apm.getAirplane().getAirplaneId() == id) {
				return apm;
			}
		}
		
		return null;
		
	}
	
	public AirplaneManager getSpecificAirplane(AirplaneManager airp) {		
		int id = airp.getAirplaneId();
		
		for(AirplaneManager apm : airplaneManagers) {
			if(apm.getAirplane().getAirplaneId() == id) {
				return apm;
			}
		}
		
		return null;
		
	}
	
	public Airport getAirportfromSimulation(Airport arrivalAirport) {		
		int id = arrivalAirport.getIndex();
		
		for(Airport apm : airports) {
			if(apm.getIndex() == id) {
				//System.out.println("Le nom de l'airport est : " + id);
				return apm;
			}
		}
		
		return null;
  }
}
