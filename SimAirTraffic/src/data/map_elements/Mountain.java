package data.map_elements;

public class Mountain extends Obstacle{

	//L'altitude permet de déterminer si la montagne va entrer en collision ou non avec un avion en vol
	private int altitude;
	
	public Mountain(Block b, int altitude) {
		super(b);
		this.altitude = altitude;
		
	}

	public int getAltitude() {
		return altitude;
	}

	public void setAltitude(int altitude) {
		this.altitude = altitude;
	}

}
