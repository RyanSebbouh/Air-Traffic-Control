package data;

import config.GameConfiguration;
import data.map_elements.Block;

/**
 * This class represents a map. A map contains a 2D array of Blocks representing
 * the grid of the map. It also has methods for getting and setting blocks,
 * checking if a block is on a border or on top/bottom of the map, getting the
 * dimensions of the map and finding all the blocks belonging to a specific
 * piece.
 * 
 * @author M'GHAIETH Fayçal
 */
public class Map {
	private Block[][] blocks;

	private int lineCount = GameConfiguration.LINE_COUNT;
	private int columnCount = GameConfiguration.COLUMN_COUNT;

	/**
	 * Constructor for Map class. Initializes a 2D array of blocks representing the
	 * grid of the map.
	 */
	public Map() {

		blocks = new Block[lineCount][columnCount];
		
		for (int xIndex = 0; xIndex < lineCount; xIndex++) {
			for (int yIndex = 0; yIndex < columnCount; yIndex++) {
				blocks[xIndex][yIndex] = new Block(xIndex, yIndex);
			}
		}
	}

	/**
	 * Returns the block located at the given position in the map.
	 * 
	 * @param line   the line index of the block
	 * @param column the column index of the block
	 * @return the block at the given position or null if the position is out of
	 *         bounds
	 */
	public Block getBlock(int line, int column) {
		if (line < 0 || line >= lineCount || column < 0 || column >= columnCount) {
			return null;
		}
		return blocks[line][column];
	}

	/**
	 * Returns the height of the Map, which is the number of rows.
	 * 
	 * @return the height of the Map
	 */
	public int getHeight() {
		return blocks.length;
	}

	/**
	 * Returns the width of the Map, which is the number of columns.
	 * 
	 * @return the width of the Map
	 */
	public int getWidth() {
		return blocks[0].length;
	}

	/**
	 * Returns the 2D array of Blocks representing the Map.
	 * 
	 * @return the 2D array of Blocks
	 */
	public Block[][] getBlocks() {
		return blocks;
	}

	/**
	 * Returns the number of lines in the Map.
	 * 
	 * @return the number of lines
	 */
	public int getLineCount() {
		return lineCount;
	}

	/**
	 * Returns the number of columns in the Map.
	 * 
	 * @return the number of columns
	 */
	public int getColumnCount() {
		return columnCount;
	}

	/**
	 * Checks if the given Block is located at the top edge of the Map
	 * 
	 * @param block the Block to check
	 * @return true if the Block is at the top edge, false otherwise
	 */
	public boolean isOnTop(Block block) {
		int line = block.getyCoordBlock();
		return line == 0;
	}

	/**
	 * Checks if the given Block is located at the bottom edge of the Map.
	 * 
	 * @param block the Block to check
	 * @return true if the Block is at the bottom edge, false otherwise
	 */
	public boolean isOnBottom(Block block) {
		int line = block.getyCoordBlock();
		return line == lineCount - 1;
	}


	/**
	 * Checks if the given Block is located at the left border of the Map.
	 * 
	 * @param block the Block to check
	 * @return true if the Block is at the left border, false otherwise
	 */
	public boolean isOnLeftBorder(Block block) {
		int column = block.getxCoordBlock();
		return column == 0;
	}
	
	/**
	 * Checks if the given Block is located at the right border of the Map.
	 * 
	 * @param block the Block to check
	 * @return true if the Block is at the right border, false otherwise
	 */
	public boolean isOnRightBorder(Block block) {
		int column = block.getxCoordBlock();
		return column == columnCount - 1;
	}

	/**
	 * Checks if the given Block is located on the border of the Map.
	 * 
	 * @param block the Block to check
	 * @return true if the Block is on the border, false otherwise
	 */
	public boolean isOnBorder(Block block) {
		return isOnTop(block) || isOnBottom(block) || isOnLeftBorder(block) || isOnRightBorder(block);
	}
	
}