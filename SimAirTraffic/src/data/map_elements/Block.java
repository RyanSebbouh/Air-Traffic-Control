package data.map_elements;

import java.awt.Graphics;

import config.GameConfiguration;

/**
 * This class represents a block of the map.
 * 
 * A block can be occupied or free.
 * 
 * @author M'GHAIETH Fayçal
 *
 */
public class Block {
	
	public static final int SIZE = GameConfiguration.BLOCK_SIZE;

	private int xCoordBlock;
	private int yCoordBlock;
	
	
	//To know if the block of the map is occupied or not.
	private boolean isOccupied = false;
	
	private boolean haveStorm;

	public Block(int column, int line) {
		this.xCoordBlock = column;
		this.yCoordBlock = line;
	}
	
	 public Block(double coord_futurX, double coord_futurY) {
		 this.xCoordBlock = (int) Math.round(coord_futurX);
		 this.yCoordBlock = (int) Math.round(coord_futurY);
	}

	/**
     * Returns the xCoordBlock of the block.
     * 
     * @return the xCoordBlock of the block
     */
	public int getxCoordBlock() {
		return xCoordBlock;
	}

	/**
     * Returns the yCoordBlock of the block.
     * 
     * @return the yCoordBlock of the block
     */
	public int getyCoordBlock() {
		return yCoordBlock;
	}
	


	 /**
	 * Sets the line of the block.
	 * 
	 * @param line the new line of the block
	 * @deprecated Use {@link #setyCoordBlock(int)} instead
	 */
	public void setLine(int line) {
		setyCoordBlock(line);
	}

	/**
     * Sets the yCoordBlock of the block.
     * 
     * @param yCoordBlock the new yCoordBlock of the block
     */
	public void setyCoordBlock(int line) {
		this.yCoordBlock = line;
	}
	
	 /**
	 * Sets the column of the block.
	 * 
	 * @param column the new column of the block
	 * @deprecated Use {@link #setxCoordBlock(int)} instead
	 */
	public void setColumn(int column) {
		setxCoordBlock(column);
	}

	/**
     * Sets the xCoordBlock of the block.
     * 
     * @param xCoordBlock the new xCoordBlock of the block
     */
	public void setxCoordBlock(int column) {
		this.xCoordBlock = column;
	}
	
	/**
     * Draws the block using the specified graphics object and coordinates.
     * 
     * @param g the graphics object to use for drawing the block
     * @param x the x-coordinate to use for drawing the block
     * @param y the y-coordinate to use for drawing the block
     */
	public void draw(Graphics g, int x, int y) {
        g.fillRect(x, y, SIZE, SIZE);
    }

	
	/**
     * Returns a string representation of the block.
     * 
     * @return a string representation of the block
     */
    public String toString() {
        return "[" + xCoordBlock + ", " + yCoordBlock + "]";
    }
    
    /**
     * Returns the state that defines if the block is selected or not.
     * 
     * @return the state that defines if the block is selected or not
     */
    public boolean getOccupied() {
        return isOccupied;
    }

    /**
     * Sets the state that defines if the block is selected or not.
     * 
     * @param selected the new state that defines if the block is selected or not
     */
    public void setOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }
	
    /**
     * Returns true if the block is out of bounds given the number of lines and columns.
     * 
     * @param numLines the total number of lines in the map
     * @param numColumns the total number of columns in the map
     * @return true if the block is out of bounds, false otherwise
     */
	public boolean isOutOfBounds(int numLines, int numColumns) {
		return yCoordBlock < 0 || yCoordBlock >= numLines || xCoordBlock < 0 || xCoordBlock >= numColumns;
	}

	public boolean isHaveStorm() {
		return haveStorm;
	}

	public void setHaveStorm(boolean haveStorm) {
		this.haveStorm = haveStorm;
	}
	

}