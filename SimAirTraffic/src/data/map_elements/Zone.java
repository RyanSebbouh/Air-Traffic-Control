package data.map_elements;

public class Zone {
	int xCoord, yCoord;

	public Zone(int x, int y) {
		xCoord = x;
		yCoord = y;
	}

	public int getxCoord() {
		return xCoord;
	}

	public void setxCoord(int xCoord) {
		this.xCoord = xCoord;
	}

	public int getyCoord() {
		return yCoord;
	}

	public void setyCoord(int yCoord) {
		this.yCoord = yCoord;
	}
}
