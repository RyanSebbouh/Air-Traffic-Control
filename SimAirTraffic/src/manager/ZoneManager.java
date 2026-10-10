package manager;

import data.map_elements.Zone;

public class ZoneManager {
	Zone zoneManaged;
	private AirplaneManager currAirplane;
	private AirplaneManager emergencyAirplane = null;
	// AtomicBoolean isOccupied;

	public ZoneManager(boolean isOcc, int x, int y) {
		zoneManaged = new Zone(x, y);
	}
	
	public synchronized void emergencyReserve(AirplaneManager emergencyAirplane) {
		this.emergencyAirplane = emergencyAirplane;
	}

	/**
	 * Have an airplane attempt to enter a zone. If it cannot, because there is already an airplane, it will wait() until the airplane leaves.
	 * @param enteringAirplane The airplane attempting to enter the zone.
	 */
	public synchronized void enterZone(AirplaneManager enteringAirplane) {
		//System.out.print("enterZone: " + toString());
		//System.out.println(", " + currAirplane != null;

		// Airplane leaves previous zone and frees it before entering next zone
		ZoneManager exitingZone = enteringAirplane.getCurrZoneManag();
		exitingZone.leaveZone();
		
		/**
		 * You must wait if:
		 * 		- The zone is not free OR
		 * 		- The zone is free BUT there is an emergency airplane that needs to pass AND you are not the emergency airplane
		 */
		// if (zone is not free) and (you are not the emergency plane)
		if (currAirplane != null || (emergencyAirplane != null && emergencyAirplane  != enteringAirplane) ) { 
			enteringAirplane.decreaseSpeed();
			enteringAirplane.setWaiting(true);
			
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
			
			// If an emergency happened while we were waiting, we need to keep waiting
			if(emergencyAirplane  != null && emergencyAirplane  != enteringAirplane) {
				try {
					wait();
				} catch (InterruptedException e) {
					System.err.println(e.getMessage());
				}
			}
			
			enteringAirplane.increaseSpeed();
			enteringAirplane.setWaiting(false);
		}

		// New airplane (from parameters) entering this zone
		enteringAirplane.setZoneManag(this);
		this.currAirplane = enteringAirplane;
		// isOccupied.set(true);
	}

	/**
	 * Have the current airplane leave the zone and notify anyone waiting that they can enter.
	 */
	public synchronized void leaveZone() {
		if(currAirplane != null && currAirplane.isHavingEmergency()) {
			this.emergencyAirplane = null;
		}
		this.currAirplane = null;
		notify();

		// isOccupied.set(false);
	}

	public boolean zoneOccupied() {
		return getCurrAirplane() != null;
	}

	public AirplaneManager getCurrAirplane() {
		return currAirplane;
	}

	public void setCurrAirplane(AirplaneManager currAirplane) {
		this.currAirplane = currAirplane;
	}

	public AirplaneManager getEmergencyAirplane() {
		return emergencyAirplane;
	}

	public void setEmergencyAirplane(AirplaneManager emergencyAirplane) {
		this.emergencyAirplane = emergencyAirplane;
	}

	public String toString() {
		return "ZoneManager currently has [" + currAirplane + "], coordinates " + zoneManaged.getxCoord() + ";" + zoneManaged.getyCoord();
	}
}
