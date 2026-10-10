package data.map_elements;

import java.util.ArrayList;


public class Storm {
	
	//La tempête se déplace à une certaine vitesse, dans une certaine zone : block actualisé
	private float speed;
	
	private Block[] stormingExtremities;
	
	private ArrayList<Block> stormingZone;
	
	public Storm(int speed) {
		this.speed = speed;

	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public Block[] getstormingExtremities() {
		return stormingExtremities;
	}

	public void setstormingExtremities(Block[] stormingExtremities) {
		this.stormingExtremities = stormingExtremities;
	}
	
	public Block getTopLeftBlock() {
		return stormingExtremities[0];
	}
	
	public Block getTopRightBlock() {
		return stormingExtremities[1];
	}
	
	public Block getBottomLeftBlock() {
		return stormingExtremities[2];
	}
	
	public Block getBottomRightBlock() {
		return stormingExtremities[3];
	}
	

	public ArrayList<Block> getStormingZone() {
		return stormingZone;
	}

	public void setStormingZone(ArrayList<Block> stormingZone) {
		this.stormingZone = stormingZone;
	}

}
