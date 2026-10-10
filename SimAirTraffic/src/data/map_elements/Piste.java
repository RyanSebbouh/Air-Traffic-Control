package data.map_elements;

import manager.AirplaneManager;
/**
 * 
 * @author MGHAIETH Faycal
 *
 */
public class Piste {
	/**
	 * Identify the piste 
	 */
	private int idPiste;
	
	/**
	 * The airport associated to this piste
	 */
	private Airport airport;
	
	/**
	 * The airplane that is occupying the airplane
	 */
	private AirplaneManager occupyingPlane;
	/**
	 * The state of the piste : occupied or not
	 */
	private Boolean isOccupiedbyPlane;
	
	private int timeToSleep;
	
	public Piste(int id, Airport airport){
		this.idPiste = id;
		this.airport = airport;
		
		this.isOccupiedbyPlane = false;
		
	}

	public int getIdPiste() {
		return idPiste;
	}

	public void setIdPiste(int idPiste) {
		this.idPiste = idPiste;
	}

	public Airport getAirport() {
		return airport;
	}

	public void setAirport(Airport airport) {
		this.airport = airport;
	}

	public AirplaneManager getOccupyingPlane() {
		return occupyingPlane;
	}

	public void setOccupyingPlane(AirplaneManager occupyingPlane) {
		this.occupyingPlane = occupyingPlane;
	}

	public Boolean getIsOccupiedbyPlane() {
		return isOccupiedbyPlane;
	}

	public void setIsOccupiedbyPlane(Boolean isOccupiedbyPlane) {
		this.isOccupiedbyPlane = isOccupiedbyPlane;
	}
	
	public void freePiste() {
		if(isOccupiedbyPlane == true && occupyingPlane != null) {
			isOccupiedbyPlane = false;
			occupyingPlane = null;
		}
	}

	public int getTimeToSleep() {
		return timeToSleep;
	}

	public void setTimeToSleep(int timeToSleep) {
		this.timeToSleep = timeToSleep;
	}

}
