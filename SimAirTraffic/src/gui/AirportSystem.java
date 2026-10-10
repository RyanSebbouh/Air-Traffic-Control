package gui;
import java.util.ArrayList;
import data.Airplane;
import data.map_elements.Airport;

/**
 * This class builds the system of airports that the airplanes go to.
 * 
 * The airports are located at specified positions.
 * 
 * @author Yang Mattew
 */
public class AirportSystem {
	 
	/**
	 * The total amount of airports in service
	 */
	private int airportAmount;

	/**
	 * The amount of airports that have no more room for incoming or departing
	 * airplanes
	 */
	private int airportsOccupied = 0;

	private ArrayList<Airport> airports = new ArrayList<>();


	public AirportSystem(int totalAmount) {
		this.airportAmount = totalAmount;
	}

	public void addAirport(int i, Airport airp) {
	    airports.add(airp);
	}

	
	public void fillUpAirport() {
		airportsOccupied++;
	}
	
	public int getAirportsOccupied() {
		return airportsOccupied;
	}
	
	public ArrayList<Airport> getAirports(){
		return airports;
	}
	private ArrayList<Airplane> airplanes = new ArrayList<>();

    public void addAirplane(Airplane airplane) {
        airplanes.add(airplane);
    }

    public void removeAirplane(Airplane airplane) {
        airplanes.remove(airplane);
    }

    public ArrayList<Airplane> getAirplanes() {
        return airplanes;
    }
    
    public Airport getNearestAirport(Airplane airplane) {
        Airport nearestAirport = null;
        double minDistance = Double.MAX_VALUE;

        for (Airport airport : airports) {
            double distance = calculateDistance(airplane, airport);
            if (distance < minDistance) {
                minDistance = distance;
                nearestAirport = airport;
            }
        }

        return nearestAirport;
    }

    private double calculateDistance(Airplane airplane, Airport airport) {
        double airplaneX = airplane.getExactX();
        double airplaneY = airplane.getExactY();
        double airportX = airport.getPosition().getxCoordBlock();
        double airportY = airport.getPosition().getyCoordBlock();

        // Distance euclidienne entre l'avion et l'aéroport
        return Math.sqrt(Math.pow(airplaneX - airportX, 2) + Math.pow(airplaneY - airportY, 2));
    }


}
