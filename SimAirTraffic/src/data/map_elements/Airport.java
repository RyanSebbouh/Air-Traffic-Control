package data.map_elements;

import org.apache.log4j.Logger;

import config.GameConfiguration;
import logs.LoggerUtility;
import manager.AirplaneManager;
import data.Vol;

/**
 * The class that represent an airport on the map
 * @author MGHAIETH Faycal
 *
 */
public class Airport {
	private String name;
	
	private int index;
	
	private Block position;
	
	//Le maximum de pistes dispo
	private int maxPiste;
	
	private int numberOfOccupiedPiste;
	
    private Piste tableauPistes[];
    private Vol flights[];
    
	private static Logger logger = LoggerUtility.getLogger(Airport.class, "html");

    
    public Airport(String name, Block position, int maxPlanes) {
    	this.name = name;
    	this.position = position;
    	this.maxPiste = maxPlanes;
    	this.tableauPistes = new Piste[maxPlanes];
    	generPistes();
    	
    	this.numberOfOccupiedPiste = 0;
    }

    public void generPistes() {
    	int timeSleep = 5000;
    	for( int i = 0; i<maxPiste; i++) {
    		tableauPistes[i] = new Piste(i, this);
    		tableauPistes[i].setTimeToSleep(timeSleep);
    		timeSleep = timeSleep + 4000;
    	}
    }

	public int getMaxPistes() {
		return maxPiste;
	}

	public void setMaxPistes(int maxPlanes) {
		this.maxPiste = maxPlanes;
	}

	public Block getPosition() {
		return position;
	}

	public void setPosition(Block position) {
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Piste[] getTableauPistes() {
		return tableauPistes;
	}


	public void setTableauPistes(Piste tableauPistes[]) {
		this.tableauPistes = tableauPistes;
	}


	public int getNumberOfOccupiedPiste() {
		return numberOfOccupiedPiste;
	}


	public void setNumberOfOccupiedPiste(int numberOfOccupiedPiste) {
		this.numberOfOccupiedPiste = numberOfOccupiedPiste;
	}
    
	/**
	 * Sert à déclarer une piste de l'aéroport comme occupée
	 * @param airP
	 */
	public void setOccupyingPiste(AirplaneManager airP) {
		tableauPistes[numberOfOccupiedPiste].setOccupyingPlane(airP);
		tableauPistes[numberOfOccupiedPiste].setIsOccupiedbyPlane(true);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	/**
	 * When this function is called, the airplane that occupied a runway of the airport releases it
	 * @param airP The airplane that will free the runway
	 * @return void
	 */
	public void freePistefromAirplane(AirplaneManager airP) {
		for(int i = 0; i<maxPiste; i++) {
			//if(tableauPistes[i].getOccupyingPlane().getAirplaneId() == airP.getAirplaneId()) {
			if(tableauPistes[i].getIsOccupiedbyPlane() && tableauPistes[i].getOccupyingPlane() != null &&
	                tableauPistes[i].getOccupyingPlane().getAirplaneId() == airP.getAirplaneId()) {
				
				tableauPistes[i].setIsOccupiedbyPlane(false);
				tableauPistes[i].setOccupyingPlane(null);
				//airP.setPisteOccupied(null);
				numberOfOccupiedPiste --;
				logger.info("L'avion numéro " + airP.getAirplane().getAirplaneId() + " décolle de l'aéroport " + name + " et libère la piste " + tableauPistes[i].getIdPiste());

				
			}
		}
	}
	

	/**
	 * When this function is called the airplane can occupy a runway of the airport
	 * @param airPM The airplane that will occupy the runway
	 * @return void
	 */
	public void occupyApiste(AirplaneManager airPM) {
		if(tableauPistes[numberOfOccupiedPiste].getIsOccupiedbyPlane() == false) {
			
			//Dire que l'état "estOccupé" de la piste est vrai
			tableauPistes[numberOfOccupiedPiste].setIsOccupiedbyPlane(true);
			
			//Enregistrer le airplaneManager dans la piste
			tableauPistes[numberOfOccupiedPiste].setOccupyingPlane(airPM);
			
			airPM.setOccupiedPiste(tableauPistes[numberOfOccupiedPiste]);
			
        	logger.info("L'avion numéro " + airPM.getAirplaneId() + " a atterrit sur la piste numéro " + tableauPistes[numberOfOccupiedPiste].getIdPiste() + " de l'aéroport " + name);

			if(numberOfOccupiedPiste < maxPiste) {
		        numberOfOccupiedPiste ++;
	        }
		}
		
	}
	/**
	 * The function that verify if their is a runway available in the airport
	 * @return true if there is runways available, false else
	 */
	public boolean arePistesAvailable() {
		if(numberOfOccupiedPiste < maxPiste) {
			//System.out.println("Il y a " + (numberOfOccupiedPiste) + "pistes occupées dans l'aéroport " + name);
			return true;
		}
		else {
			//System.out.println("Il y a aucune " + "pistes disponibles dans l'aéroport " + name);
			return false;
		}
	}
	/**
	 * The goal of this function is to alert the airport to free a runway if there is no runways available
	 * @return void
	 */
	public void emergencyPiste() {
	    Piste p = tableauPistes[numberOfOccupiedPiste - 1];
	    numberOfOccupiedPiste--;
	    
	    if (p.getOccupyingPlane() != null) {
	    	AirplaneManager occupyingPlane = p.getOccupyingPlane();
	        
	        
	    	occupyingPlane.interrupt();
	        logger.info("L'avion " + occupyingPlane.getAirplaneId() + " libère la piste " + p.getIdPiste() + " de l'aéroport " + name + " pour laisser un avion rentrer");
	        
	        p.freePiste();
	    } else {
	    	logger.info("Aucun avion n'occupe actuellement la piste " + p.getIdPiste() + " de l'aéroport " + index);
	    }
	}

	public boolean clicked(int x, int y) {
		int coords[] = {0,0};
		coords[0] = getPosition().getxCoordBlock();
		coords[1] = getPosition().getyCoordBlock();
		int blockSize = GameConfiguration.BLOCK_SIZE;
		//System.out.println("(" + x + ", " + y + "): (" + coords[0] * blockSize + ", " + coords[1] * blockSize + ")");
		
		return (coords[0] * blockSize < x && x < (coords[0]+1) * blockSize && coords[1] * blockSize < y && y < (coords[1]+1) * blockSize);		
	}
  
	public Vol[] getFlights() {
		return flights;
	}

	public void setFlights(Vol flights[]) {
		this.flights = flights;
	}
	
}