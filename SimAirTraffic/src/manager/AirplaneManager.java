package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import config.GameConfiguration;
import data.Airplane;
import data.Map;
import data.map_elements.Airport;
import data.map_elements.Block;
import data.map_elements.Piste;
import logs.LoggerUtility;
import process.PathFinding;
import process.Simulation;
import process.SimulationUtility;

import org.apache.log4j.Logger;



/**
 * The airplane controller class. Each instance of this class represents an
 * airplane.
 */
public class AirplaneManager extends Thread {
    private boolean running = false;
    private boolean landing = false, unlanding = false;
    private boolean waiting = false;
    private boolean havingEmergency = false;
    private Piste occupiedPiste;
    
    private int timeOfSleeping;
    
	// private static final Logger LOGGER = LogManager.getLogger(
	// AirplaneManager.class );
	private Airplane airplane;
	private Map map;
	private Simulation simulation;
	private AirportManager airportManager;

    private ZoneManager currZoneManag;
    
	private int coord_futurX, coord_futurY;
	private boolean changingZone = false;
	private int currZoneX, currZoneY;
    private double airplaneSpeed;
    private static Logger logger = LoggerUtility.getLogger(AirplaneManager.class, "html");
	
	private boolean altitudeAugmentee = false;
	
	private boolean isSleeping = false;
	
	private ArrayList<ZoneManager> intermedZones = new ArrayList<ZoneManager>();

    /**
     * Calculated via airplane speed; it takes 6 units to land
     */
    private double distToLand;
    
    public AirplaneManager(Simulation sim, Airplane a, Map m) {
    	new Object();
    	
        airplane = a;
        a.extractNextDestination();
        
        map = m;
        simulation = sim;
        
        Block b = a.extractNextDestination();
        coord_futurX = b.getxCoordBlock();
        coord_futurY = b.getyCoordBlock();
        
        airplaneSpeed = 1 + (double)a.getSpeed()/20;
        //airplaneSpeed *= ROOT2; // Ensures we always move a block
        distToLand = 5.0*airplaneSpeed;
        //System.out.println("distToLand: " + distToLand);
        
        setInitialZone();
        locateAndCheck();
    }
    
    
    
    private void setInitialZone() {
        int[] position = getPosition();
        currZoneX = Math.round(position[0]/5);
        currZoneY = Math.round(position[1]/5);
        //reserveZone(currZoneX, currZoneY);
        
        //System.out.println(currZoneX + " " + currZoneY);
        ZoneManager firstZone = simulation.getZone(currZoneX, currZoneY);
        currZoneManag = firstZone;
        //currZoneManag.setCurrAirplane(this);
    }
    
    /**
     * Locates the airplane and checks ahead for any obstacles
     * @return The Block coordinates of the airplane
     */
    private double[] locateAndCheck() {
        double deltaX = coord_futurX - airplane.getExactX();
        double deltaY = coord_futurY - airplane.getExactY();
        double radianDirection = Math.atan2(deltaY, deltaX);
        setDirection((int) Math.toDegrees(radianDirection));
        
        // We only need 1 airplane to reroute to avoid a collision, so we'll arbitrairly assume that east-bound airplanes don't have to reroute
        //if(depX < 0.0 && !circling) checkAhead(depX, depY);
        double[] delta = {deltaX, deltaY};
        
        return delta;
    }

	public void moveAirplane(double deltaX, double deltaY) {
        double radianDirection = Math.atan2(deltaY, deltaX);
        setDirection((int) Math.toDegrees(radianDirection));

        double currdepX = airplane.getExactX();
        double currdepY = airplane.getExactY();
        
        double depX = Math.cos(radianDirection)*airplaneSpeed;
        double depY = Math.sin(radianDirection)*airplaneSpeed;

        double nvdepX = currdepX + depX;
        double nvdepY = currdepY + depY;
        
        airplane.setX(nvdepX);
        airplane.setY(nvdepY);
        
        // Check that the move won't make the airplane change zones
        //System.out.println(depX + "; " + depY);
        
        if(!SimulationUtility.coordsInZone((int) Math.round(airplane.getExactX() + depX), (int) Math.round(airplane.getExactY() + depY), currZoneX, currZoneY)) {
        	changeZone(depX, depY);
        }
	}
	
	/**
	 * Enter a new zone.
	 * @param depX How fast the airplane is moving along the x-axis
	 * @param depY How fast the airplane is moving along the y-axis
	 */
	public void changeZone(double depX, double depY) {
        currZoneX = Math.round(((int) Math.round(airplane.getExactX() + depX))/5);
        currZoneY = Math.round(((int) Math.round(airplane.getExactY() + depY))/5);

        ZoneManager nextZone = simulation.getZone(currZoneX, currZoneY);
        nextZone.enterZone(this);
        changingZone = false;
        locateAndCheck();
	}
	
	private void land() {
        double newAltitude = airplane.getRealAltitude() - 2000;
        airplane.setAltitude(newAltitude);
	}
	
	private void unland() {
        double newAltitude = airplane.getRealAltitude() + 2000;
        airplane.setAltitude(newAltitude);
	}

    @Override
    public void run() {
        while (running) {
            SimulationUtility.unitTime();
            //logger.info("Airplane " + getAirplaneId() + " destinations; " + airplane.getIntermediateDests());

            int x = airplane.getX();
            int y = airplane.getY();
            
            Map map = simulation.getMap();
            
            Block occupiedBlockOnMap = map.getBlock(y, x);
            
            if(occupiedBlockOnMap.getOccupied() == true && altitudeAugmentee == false && unlanding == false && landing == false) {
            	airplane.setAltitude(airplane.getAltitude() + 500);
            	altitudeAugmentee = true;
            }
            
            if(occupiedBlockOnMap.getOccupied() == false && altitudeAugmentee == true && landing == false && unlanding == false) {
            	airplane.setAltitude(airplane.getAltitude() - 500);
            	altitudeAugmentee = false;
            }
            
            //System.out.println(airplane.getRealAltitude());
            if(airplane.getRealAltitude() < 1999.0) {
                landing = false;
            	running = false;
            	
            } else {
                double deltaX, deltaY;
	            
	            	double delta[] = locateAndCheck();
	            	deltaX = delta[0];
	            	deltaY = delta[1];

	                double distance_de_Dest = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
	                
	                // We've arrived at the final destination
	                //System.out.println(airplane.getAirplaneId() + " " + distance_de_Dest + " " + circling + " " + airplane.getIntermediateDests());
	                if (distance_de_Dest < distToLand && airplane.getIntermediateDests().isEmpty()) {
	                	if(altitudeAugmentee == true) {
	                		airplane.setAltitude(airplane.getAltitude() - 500);
	                		altitudeAugmentee = false;
	                	}
	                	
	                	ArrayList<Airport> airports = simulation.getAirports();
	                	Airport landingAirport = null;
	                	for(Airport a : airports) {
	                		Block b = a.getPosition();
	                		if(b.getxCoordBlock() == coord_futurX && b.getyCoordBlock() == coord_futurY) {
	                			landingAirport = a;
	                		}
	                	}
	                	
	                	//Airport landingAirport = new Airport(locOfAirport);
	                	airportManager = new AirportManager(landingAirport);
	                	
	                	if(havingEmergency) {
	                		logger.info("Emergency airplane " + getAirplaneId() + " available runways: " + airplane.getVol().getArrivee().getMaxPistes());
	                	}
	                	
	                	//System.out.println(airplane + ": " + airportManager.getNbPlacesDispos());
	                    if (airplane.getVol().getArrivee().getMaxPistes() != 0) {
	                        landing = true;
	                        unlanding = false;
	                    } else {
	                        landing = false;
	                    }
	                }
	
	                // If the distance is less than what the airplane covers moving 1 unit, then we'll round and say the plane arrived at its intermediate destination
	                if (distance_de_Dest <= airplaneSpeed && !airplane.getIntermediateDests().isEmpty()) {
	                		Block b = airplane.extractNextDestination();
	                		coord_futurX = b.getxCoordBlock();
	                		coord_futurY = b.getyCoordBlock();
	                		
	    	            	delta = locateAndCheck();
	    	            	deltaX = delta[0];
	    	            	deltaY = delta[1];
	                }

	                moveAirplane(deltaX, deltaY);
	                //System.out.println(airplane);
	            if (landing) {
	            	Airport aeroportArrivee = airplane.getVol().getArrivee();
	            	Airport aeroportArriveeSim = simulation.getAirportfromSimulation(aeroportArrivee);
	            	if(aeroportArriveeSim.arePistesAvailable()) {
	            		land();
	            	}else{
	            		aeroportArriveeSim.emergencyPiste();
	            	}
	            }
	            
	            //System.out.println(this + " "+ getIntermediaryDests());
            }
            
            //System.out.println("Altitude de l'avion " + airplane.getAirplaneId()+ " = "+ airplane.getAltitude());
            
            if(airplane.getAltitude() < 1999) {
            	Airport aeroportArrivee = airplane.getVol().getArrivee();
            	Airport aeroportArriveeSim = simulation.getAirportfromSimulation(aeroportArrivee);
            	
            	aeroportArriveeSim.occupyApiste(this);
            	
            	//L'avion a atterit donc l'aéroport d'arrivée est maintenant l'aéroport de départ
                airplane.getVol().setDepart(airplane.getVol().getArrivee());
                //airplane.getVol().getDepart().setOccupyingPiste(airplane);
                
                int numDepart = airplane.getVol().getDepart().getIndex();
                
                //Récuperation de toute les lignes existantes pour chaque aéroport
                HashMap<Integer, ArrayList<Integer>> listOfAllAirLines = SimulationUtility.getAllAirways();
                
                //Récupère les numéros des aéroports vers lesquels on peut aller depuis l'aéroport de départ
                ArrayList<Integer> lines = listOfAllAirLines.get(numDepart);
                
                //Choisi au hasard le numéro d'un aéroport vers lequel on peut aller
                Random r = new Random();
                int k = r.nextInt(lines.size());
                
                //On récupère cet aéroport via son numéro
                Airport arrivee = simulation.getAirports().get(lines.get(k));
                
                airplane.getVol().setArrivee(arrivee);
                int numArrivee = airplane.getVol().getArrivee().getIndex();

                airplane.setIntermediateDests(new ArrayList<Block>());
                
        		for(Block b : GameConfiguration.AIRWAYS.get(numDepart*10 + numArrivee)) {
        			airplane.addDestination(b);
        		}
        		airplane.finishAddingDests();
                
                //System.out.println(numDepart + "," + numArrivee + "," + getAltitude());
                //System.out.println(airplane.getIntermediateDests());

                airplane.extractNextDestination(); // enlever le 1er block parce que ce bloc c'est le block où on se trouve déjà
                Block b = airplane.extractNextDestination();
                coord_futurX = b.getxCoordBlock();
                coord_futurY = b.getyCoordBlock();

        		double newDelta[] = locateAndCheck();
            	double deltaX = newDelta[0];
            	double deltaY = newDelta[1];
                moveAirplane(deltaX, deltaY);
                
                timeOfSleeping = occupiedPiste == null? 5000 : occupiedPiste.getTimeToSleep();
            	currZoneManag.leaveZone();
            	havingEmergency = false;
            	
            	if(intermedZones.size() != 0) {
            		for(ZoneManager z : intermedZones) {
            			if(z.getCurrAirplane() == this) z.setCurrAirplane(null);
            			if(z.getEmergencyAirplane() == this) z.setEmergencyAirplane(null);
            		}
            	}
                
                try {
                	isSleeping = true;
        			Thread.sleep(timeOfSleeping);
        			
        			isSleeping = false;
        			//verifyBeforeUnlanding();
        		} catch (InterruptedException e) {
        			logger.warn("Situation imprévue : l'avion numéro " + getAirplaneId() + " libère une piste pour laisser un autre entrer");
        		}
                	currZoneManag.enterZone(this);

                Airport aeroportDepart = airplane.getVol().getDepart();
               	Airport aeroportDepartSim = simulation.getAirportfromSimulation(aeroportDepart);
               	aeroportDepartSim.freePistefromAirplane(this);
                
                landing = false;
                unlanding = true;
                // re-remplir la liste de destinationa à parcourir jusqu'aiu prochain aéroport
            }
            if(unlanding) {
                unland();
                unlanding = (airplane.getAltitude() < 9999);
            }
        }
        //Simulation.airplaneManagers.remove(this);
        //currZoneManag.leaveZone();
        //simulation.removeAirplane(this);
    }

	/**
	 * Triggers an emergency
	 */
	public void triggerEmergency() {
		logger.info("Airplane with id " + getAirplaneId() + " is now having an emergency.");
		
		int newAirportIndex = SimulationUtility.calculateClosestAirport_(airplane.getExactX(), airplane.getExactY());
		Airport newAirport = simulation.getAirports().get(newAirportIndex);
		
		ArrayList<Block> newDest = new ArrayList<Block>();
		
		newDest.add(new Block(airplane.getX(), airplane.getY()));
		newDest.add(newAirport.getPosition());
		airplane.setIntermediateDests(newDest);
		airplane.finishAddingDests();
		
        airplane.getVol().setArrivee(newAirport);

		airplane.extractNextDestination(); // Don't need to go to where airplane already is
		Block b2 = airplane.extractNextDestination();
		coord_futurX = b2.getxCoordBlock();
		coord_futurY = b2.getyCoordBlock();
		
		havingEmergency = true;
		
		ArrayList<Block> intermedBlocks = PathFinding.getIntermediaryZones(airplane.getX(), airplane.getY(), b2.getxCoordBlock(), b2.getyCoordBlock());
		for(Block b : intermedBlocks) {
			ZoneManager z = simulation.getZone(b.getxCoordBlock(), b.getyCoordBlock());
			intermedZones.add(z);
			logger.info("Reserving: " + z);
			z.emergencyReserve(this);
		}
	}
    
    /**
     * Returns the Airplane's current {@link Block} coordinates
     * @return
     */
    public Block getBlockPosition() {
    	int[] pos = getPosition();
    	return new Block(pos[0], pos[1]);
    }

    /**
     * Gets the airplane's current position
     * @return
     */
    public int[] getPosition() {
        return airplane.getCoordinatePosition();
    }

    public double[] getExactPosition() {
        return airplane.getExactCoordinatePosition();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

	public boolean isStarted() {
		return started;
	}

    private boolean started = false;
	public void setStarted(boolean b) {
		started = b;
	}

	public AirportManager getAirportManager() {
		return airportManager;
	}

	public void setAirportManager(AirportManager airportManager) {
		this.airportManager = airportManager;
	}
	
	public void setDirection(int dir) {
		airplane.setDirection(dir);
	}

	public Airplane getAirplane() {
		return airplane;
	}

	public Map getMap() {
		return map;
	}

	public Simulation getSimulation() {
		return simulation;
	}
	
	public int getAltitude() {
		return airplane.getAltitude();
	}
	
	public int getDirection() {
		return airplane.getDirection();
	}
	
	public int getAirplaneId() {
		return airplane.getAirplaneId();
	}
	
	public Block getCurrentDest() {
		return new Block(coord_futurX, coord_futurY);
	}
	
	public ArrayList<Block> getIntermediaryDests() {
		return airplane.getIntermediateDests();
	}
	
	public ArrayList<Block> getAllDests() {
		return airplane.getAllDests();
	}
	public boolean increament_obstacle() {
		return changingZone;
		
	}

	
	public boolean isWaiting() {
		return waiting;
	}
	
	public boolean isHavingEmergency() {
		return havingEmergency;
	}
	
	/**
	 * Checks whether the airplane has been clicked on
	 * @param x The x coordinate of the click
	 * @param y The y coordinate of the click
	 * @return
	 */
	public boolean clicked(int x, int y) {
		int[] coords = airplane.getCoordinatePosition();
		int blockSize = GameConfiguration.BLOCK_SIZE;
		//System.out.println("(" + x + ", " + y + "): (" + coords[0] * blockSize + ", " + coords[1] * blockSize + ")");
		
		return (coords[0] * blockSize < x && x < (coords[0]+1) * blockSize && coords[1] * blockSize < y && y < (coords[1]+1) * blockSize);		
	}
	
	public void setCoord_futurX(int varX) {
		this.coord_futurX=varX;
	}
	public void setCoord_futurY(int varY) {
		this.coord_futurY=varY;
	}
	
	public ZoneManager getCurrZoneManag() {
		return currZoneManag;
	}
	
	public void setZoneManag(ZoneManager newZoneManag) {
		currZoneManag = newZoneManag;
	}
	
	public double getAirplaneSpeed() {
		return airplaneSpeed;
	}
	
	public void decreaseSpeed() {
		airplaneSpeed -= 1;
	}
	
	public void increaseSpeed() {
		airplaneSpeed += 1;
	}
	
	public void setAirplaneSpeed(double airplaneSpeed) {
		this.airplaneSpeed = airplaneSpeed;
	}

	public Piste getOccupiedPiste() {
		return occupiedPiste;
	}

	public void setOccupiedPiste(Piste occupiedPiste) {
		this.occupiedPiste = occupiedPiste;
	}
	
	public void verifyBeforeUnlanding() {
		Airport aeroportDepart = airplane.getVol().getDepart();
    	Airport aeroportDepartSim = simulation.getAirportfromSimulation(aeroportDepart);
        Piste runways[] = aeroportDepartSim.getTableauPistes();
        
        for (Piste runway : runways) {
        	
            if (runway.getIsOccupiedbyPlane() == true) {
            	
                AirplaneManager occupyingAirplane = runway.getOccupyingPlane();
                
                if (occupyingAirplane.getAirplane().getVol().getArrivee() == airplane.getVol().getArrivee()) {
                	if(occupyingAirplane.isSleeping() == false && occupyingAirplane.getAirplaneId() != airplane.getAirplaneId()) {
                		Random random = new Random();
                		int min = 11, max = 23;
                		int intervalSize = max - min + 1;
                		
                		int delaySeconds = random.nextInt(intervalSize);
                		System.out.println("NOOOOTE : L'avion " + airplane.getAirplaneId() + " qui comptait partir à " + airplane.getVol().getArrivee().getName() + " s'endort pour laisser l'avion " + occupyingAirplane.getAirplaneId() + " partir à l'aéroport de " + occupyingAirplane.getAirplane().getVol().getArrivee().getName());
                		try {
                			isSleeping = true;
                		    Thread.sleep(delaySeconds * 1000);
                		} catch (InterruptedException e) {
                		    e.printStackTrace();
                		}
                		isSleeping = false;
                	}
                }
            }
        }
	}

	public boolean getUnlanding() {
		return unlanding;
	}


	public boolean isSleeping() {
		return isSleeping;
	}



	public void setSleeping(boolean isSleeping) {
		this.isSleeping = isSleeping;
	}



	public int getTimeOfSleeping() {
		return timeOfSleeping;
	}

	public void setWaiting(boolean waiting) {
		this.waiting = waiting;
	}

	public void setTimeOfSleeping(int timeOfSleeping) {
		this.timeOfSleeping = timeOfSleeping;
	}
}
