package process;

import java.util.ArrayList;
import config.GameConfiguration;
import data.Airplane;
import data.map_elements.Block;

public class PathFinding {

	private static final int LOOK_AHEAD = 2;

	/**
	 * Check if the zone is on-screen, i.e. that is within the list of zones
	 * 
	 * @param xZone The x-coordinate of the ZoneManager
	 * @param yZone The y-coordinate of the zone
	 * @return Whether this zone is within range
	 */
	public static boolean checkIfInGrid(int xZone, int yZone) {
		boolean xInRange = ((xZone >= 0) && (xZone < GameConfiguration.ZONE_X_MAX));
		boolean yInRange = ((yZone >= 0) && (yZone < GameConfiguration.ZONE_Y_MAX));
		return xInRange && yInRange;
	}
	
	/**
	 * Calculates and returns all of the zones between two blocks, assuming a journey in a straight line.
	 * @param xStartBlock x-coordinate of the starting block
	 * @param yStartBlock y-coordinate of the starting block
	 * @param xEndBlock x-coordinate of the ending block
	 * @param yEndBlock y-coordinate of the ending block
	 * @return
	 */
    public static ArrayList<Block> getIntermediaryZones(int xStartBlock, int yStartBlock, int xEndBlock, int yEndBlock) {
    	ArrayList<Block> intermediaryZones = new ArrayList<Block>();

    	Block bStart = SimulationUtility.getZoneOfCoords(xStartBlock, yStartBlock);
    	Block bEnd = SimulationUtility.getZoneOfCoords(xEndBlock, yEndBlock);
    	
    	int xStart = bStart.getxCoordBlock(), yStart = bStart.getyCoordBlock(), xEnd = bEnd.getxCoordBlock(), yEnd = bEnd.getyCoordBlock();
        
        int dx = Math.abs(xEnd - xStart);
        int dy = Math.abs(yEnd - yStart);
        int sx = xStart < xEnd ? 1 : -1;
        int sy = yStart < yEnd ? 1 : -1;
        int err = dx - dy;
        int x = xStart;
        int y = yStart;

        while (x != xEnd || y != yEnd) {
            intermediaryZones.add(new Block(x, y));

            int err2 = 2 * err;
            if (err2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (err2 < dx) {
                err += dx;
                y += sy;
            }
        }
        // The last zone is also technically intermediate
        // Since we need to reserve it as well
        intermediaryZones.add(new Block(xEnd, yEnd));

        return intermediaryZones;
    }

	/**
	 * Calculate the next 2 zones the airplane will be in, provided it goes in a
	 * straight line. Does not check whether the zone is within bounds.
	 * 
	 * @param a     The airplane whose trajectory we're calculating
	 * @param dx    The x-part of the airplane's velocity
	 * @param dy    The y-part of the airplane's velocity
	 * @param zoneX The current x-coordinate of the zone the airplane is in
	 * @param zoneY The current y-coordinate of the zone the airplane is in
	 * @return The next zones the airplane will be in
	 */
	public static Block[] calculateNextZone(Airplane a, double dx, double dy, int zoneX, int zoneY) {
		return calculateNextZone(a, dx, dy, zoneX, zoneY, LOOK_AHEAD);
	}

	/**
	 * Calculate the next 'n' zones the airplane will be in, provided it goes in a
	 * straight line. Does not check whether the zone is within bounds.
	 * 
	 * @param a     The airplane whose trajectory we're calculating
	 * @param dx    The x-part of the airplane's velocity
	 * @param dy    The y-part of the airplane's velocity
	 * @param zoneX The current x-coordinate of the zone the airplane is in
	 * @param zoneY The current y-coordinate of the zone the airplane is in
	 * @param n     The amount of zones to calculate
	 * @return The next zones the airplane will be in
	 */
	public static Block[] calculateNextZone(Airplane a, double dx, double dy, int zoneX, int zoneY, int n) {
		double pos[] = a.getExactCoordinatePosition();
		int newZoneX = zoneX, newZoneY = zoneY, oldZoneX = zoneX, oldZoneY = zoneY;
		Block[] z = { null, null };
		for (int i = 0; i < n; i++) {
			while (newZoneX == oldZoneX && newZoneY == oldZoneY) {
				pos[0] += dx;
				pos[1] += dy;
				z[i] = SimulationUtility.getZoneOfCoords((int) Math.round(pos[0]), (int) Math.round(pos[1]));
				Simulation.zoneBlock = z[i];
				newZoneX = z[i].getxCoordBlock();
				newZoneY = z[i].getyCoordBlock();
			}
			
			oldZoneX = newZoneX;
			oldZoneY = newZoneY;
		}
		return z;
	}
}
