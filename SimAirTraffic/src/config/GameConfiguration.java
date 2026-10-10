package config;

import java.util.ArrayList;
import java.util.HashMap;

import data.map_elements.Block;
import process.SimulationUtility;

public class GameConfiguration {
	public static final int WINDOWSIZEMULTIPLIER = 8;
	public static final int WINDOW_WIDTH = 160*WINDOWSIZEMULTIPLIER;
	public static final int WINDOW_HEIGHT = 90*WINDOWSIZEMULTIPLIER;
	
	public static final int ADJUSTED_WINDOW_WIDTH = WINDOW_WIDTH + 84; // 60 dim + 24 margin

	// Pour obtenir un rapport 16:9
	public static final int GRIDMULTIPLIER = 5;
	public static final int ZONE_X_MAX = 16;
	public static final int ZONE_Y_MAX = 9;
	public static final int COLUMN_COUNT = ZONE_X_MAX*GRIDMULTIPLIER;
	public static final int LINE_COUNT = ZONE_Y_MAX*GRIDMULTIPLIER;
	
	public static int BLOCK_SIZE = WINDOW_WIDTH/COLUMN_COUNT; // Pour remplir l'écran
	/**
	 * It's worth 5.
	 */
	public static int ZONE_IN_BLOCK_LENGTH = 5;
	public static int ZONE_SIZE = GameConfiguration.BLOCK_SIZE * ZONE_IN_BLOCK_LENGTH;
	
	public static final int AIRPORT_AMOUNT = 10;
	
	/**
	 * Smaller values mean faster updates, i.e. it will run faster.
	 */
	public static int Refresh_rate = 1000;
	
	public static final int SIMULATION_DURATION = 2000;
	public static final int AVION_INTERVALL = 5;
	public static final int BLOCK_LENGTH = 100;
	
	public static final int[] AEROPORT_POSITION = new int [] {200, 450, 700, 900, 1100, 1200, 1500};
	public static final int STATION_MIN_STAY = 3;
	public static final int STATION_MAX_STAY = 6;
	public static final int AVION_MIN_SPEED = 1;
	public static final int AVION_MAX_SPEED = 5;

	public static final ArrayList<Block> AIRPORTS = SimulationUtility.getAirportFromFile();
	public static final HashMap<Integer, ArrayList<Block>> AIRWAYS = SimulationUtility.getAirwaysFromFile();
	
	public static final double ROOT2 = Math.sqrt(2);
	
	public static final String FRAME_DEBUT = "src/images/startFrame.jpg";


}
