package data.map_elements;

public class Obstacle {
	
	//Le block occupé par l'obstacle
	private double[] coordinatePosition = new double[2];
	
	public Obstacle(Block b) {
		//this.positionOnMap = blockOccupied;
		coordinatePosition[0] = (double) b.getxCoordBlock();
		coordinatePosition[1] = (double) b.getyCoordBlock();
	}
  
	public Obstacle(int x, int y) {
		//this.positionOnMap = blockOccupied;
		coordinatePosition[0] = (double) x;
		coordinatePosition[1] = (double) y;
	}
	
	/*public Obstacle(Block blockOccupied) {
		this.positionOnMap = blockOccupied;
	}*/
  
	public int getX() {
		return (int) coordinatePosition[0];
  }

	public int getY() {
		return (int) coordinatePosition[1];
	}
	
	@Override
	public String toString() {
		return coordinatePosition[0] + ", " + coordinatePosition[1];
	}
	
	

}