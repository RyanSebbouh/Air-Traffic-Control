package data;

import data.map_elements.Airport;

public class Vol {
	
	private Airport depart;
	
	private Airport arrivee;
	
	private Airplane airplane;
	
	public Vol(Airport depart, Airport arrivee) {
		this.depart = depart;
		this.arrivee = arrivee;
	}

	public Airport getDepart() {
		return depart;
	}

	public void setDepart(Airport depart) {
		this.depart = depart;
	}

	public Airport getArrivee() {
		return arrivee;
	}

	public void setArrivee(Airport arrivee) {
		this.arrivee = arrivee;
	}

	public Airplane getAirplane() {
		return airplane;
	}

	public void setAirplane(Airplane airplane) {
		this.airplane = airplane;
	}

}
