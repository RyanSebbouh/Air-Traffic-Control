package data;

import java.util.ArrayList;

import data.map_elements.Airport;
import data.map_elements.Block;
import data.map_elements.Piste;

public class Airplane {
	private int airplaneId;
	private double altitude;
	private double[] coordinatePosition = new double[2];
	private Block origin;
	private int currDest = 0;
	
	private Vol vol;
	
	private Piste pisteOccupied;

	/**
	 * The intermediary destinations that the airplane takes to get to its
	 * destination. Includes the final destination itself. </br>
	 * Important: The airplane will travel to these destinations in reverse order,
	 * i.e. its first destination is the last element, and its final destination is
	 * the first element of the list.
	 */
	private ArrayList<Block> intermediateDests = new ArrayList<Block>();
	private ArrayList<Block> allDests = new ArrayList<Block>();
	private int speed;

	/**
	 * Direction in degrees of the airplane
	 */
	private int direction;

	public Airplane(int id, double alt, Airport startingAirp, int dir, int spe) {
		airplaneId = id;
		altitude = alt;
		coordinatePosition[0] = (double) startingAirp.getPosition().getxCoordBlock();
		coordinatePosition[1] = (double) startingAirp.getPosition().getyCoordBlock();

		origin = new Block(startingAirp.getPosition().getxCoordBlock(), startingAirp.getPosition().getyCoordBlock());
		direction = dir % 360;
		speed = spe;
	}

	public int getAirplaneId() {
		return airplaneId;
	}

	public void setAirplaneId(int airplaneId) {
		this.airplaneId = airplaneId;
	}

	public int getAltitude() {
		return Math.round(Math.round(altitude / 500)) * 500; // We only care about 500-step increments
	}

	public double getRealAltitude() {
		return altitude;
	}

	public int getOriginX() {
		return origin.getxCoordBlock();
	}

	public int getOriginY() {
		return origin.getyCoordBlock();
	}

	/**
	 * Returns the i-th destination
	 * 
	 * @param i The destination we want to get
	 * @return The i-th destination
	 */
	public Block getIthBlock(int i) {
		return allDests.get(i);
	}

	public int getCurrDestX() {
		return getIthBlock(currDest).getxCoordBlock();
	}

	public int getCurrDestY() {
		return getIthBlock(currDest).getyCoordBlock();
	}
	
	/**
	 * Gets the Block of the intermediary destination
	 * @return The Block of the destination
	 */
	public Block getDest() {
		return getIthBlock(0);
	}

	/**
	 * Gets the X-coordinate of the intermediary destination
	 * @return The X-coordinate of the intermediary destination
	 */
	public int getDestX() {
		return getIthBlock(0).getxCoordBlock();
	}

	/**
	 * Gets the Y-coordinate of the intermediary destination
	 * @return The Y-coordinate of the intermediary destination
	 */
	public int getDestY() {
		return getIthBlock(0).getyCoordBlock();
	}
	
	public ArrayList<Block> getIntermediateDests(){
		return intermediateDests;
	}
	
	public void setIntermediateDests(ArrayList<Block> newDests){
		intermediateDests = newDests;
	}
	
	public ArrayList<Block> getAllDests(){
		return allDests;
	}
	public void setAllDests(ArrayList<Block> dests){
		allDests = dests;
	}
	
	/**
	 * Adds extra destinations after landing at the airport
	 * @param b The new intermediary destination
	 */
	public void addDestination(Block b) {
		intermediateDests.add(b);
	}
	
	/**
	 * Changes the intermediary destination.
	 * @param b The new intermediary destination
	 */
	public void redirectAirplane(Block b) {
		intermediateDests.add(0,b);
	}
	
	public void finishAddingDests() {
		allDests = new ArrayList<>(intermediateDests);
	}
	
	/**
	 * Finds the airplane's next destination and removes it from the list of destinations
	 * @return The airplane's next destination
	 */
	public Block extractNextDestination() {
		return intermediateDests.remove(0);
	}

	public int getDirection() {
		return direction;
	}

	public int getSpeed() {
		return speed;
	}

	/**
	 * Gets the rounded X value of the airplane. Useful for displaying, since our
	 * coordinate system is {@code int}-only for simplicity
	 * 
	 * @return The rounded X position of the airplane
	 */
	public int getX() {
		return (int) Math.round(coordinatePosition[0]);
	}

	/**
	 * Gets the exact X value of the airplane. Useful for calculations when the
	 * airplane is moving non-orthogonally.
	 * 
	 * @return The exact X position of the airplane
	 */
	public double getExactX() {
		return coordinatePosition[0];
	}

	public int getY() {
		return (int) Math.round(coordinatePosition[1]);
	}

	public double getExactY() {
		return coordinatePosition[1];
	}

	public int[] getCoordinatePosition() {
		int[] coord = { getX(), getY() };
		return coord;
	}

	/**
	 * Returns the exact Block coordinates of the airplane.
	 * @return the exact Block coordinates of the airplane
	 */
	public double[] getExactCoordinatePosition() {
		double[] coord = { getExactX(), getExactY() };
		return coord;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public void setDirection(int direction) {
		this.direction = direction % 360;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setX(double d) {
		coordinatePosition[0] = d;
	}
	

	public void setY(double newY) {
		coordinatePosition[1] = newY;
	}
	
	public void remplaceDestinations(ArrayList<Block> alb) {
		intermediateDests = alb;
	}
	
	public String toString() {
		String dst = "";
		if(intermediateDests.size() > 0) dst = intermediateDests.get(intermediateDests.size()-1).toString();
		else dst = allDests.get(allDests.size() - 1).toString();
		return "Airplane " + airplaneId + " going from " + origin + " to " + dst;
	}

	public void triggerEmergency(Block newAirport) {
		intermediateDests = new ArrayList<Block>();
		intermediateDests.add(newAirport);
	}

	public Vol getVol() {
		return vol;
	}

	public void setVol(Vol vol) {
		this.vol = vol;
	}

	public Piste getPisteOccupied() {
		return pisteOccupied;
	}

	public void setPisteOccupied(Piste pisteOccupied) {
		this.pisteOccupied = pisteOccupied;
	}

	
}
