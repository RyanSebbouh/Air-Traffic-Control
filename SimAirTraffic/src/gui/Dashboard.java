package gui;

import java.awt.Graphics;

import javax.swing.JPanel;

import data.Map;
import manager.AirplaneManager;
import process.Simulation;

public class Dashboard extends JPanel{
	
	private static final long serialVersionUID = 1L;

	private Map map;
	private AirplaneManager am;
	
	private Simulation sim;
	
	private PaintStrategy paintStrategy = new PaintStrategy();
	
	public Dashboard(Simulation sim) {
		this.sim = sim;
	}
	
	public Dashboard(Map map, AirplaneManager am) {
		this.map = map;
		this.am = am;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintStrategy.paint(sim, g);
		//paintStrategy.paint(map, g);
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public PaintStrategy getPaintstrategy() {
		return paintStrategy;
	}

	public void setPaintstrategy(PaintStrategy paintstrategy) {
		this.paintStrategy = paintstrategy;
	}
	
	public void hideGrille() {
		paintStrategy.hideGrille();
	}
	
	public void showGrille() {
		paintStrategy.showGrille();
	}
	
	public void showAirways() {
		paintStrategy.showAirways();
	}
	
	public void setAM(AirplaneManager am) {
		paintStrategy.setAM(am);
	}
	
	public void Zone() {
		
		if(paintStrategy.isPaintZone()) {
			paintStrategy.hideZone();
		}else {
			paintStrategy.showZone();
		}
		
	}
	
	public void Obst() {
		if(paintStrategy.isPaintObst()) {
		paintStrategy.hideObst();
		}else {
			paintStrategy.showObst();
		}
	}
	
	public void AirportName() {
		System.out.println(paintStrategy.isPaintAirportName());
		if(paintStrategy.isPaintAirportName()) {
		paintStrategy.hideName();
		}else {
			paintStrategy.showName();
		}
	}
	
    public void DisplayMessage(String message) {
    	paintStrategy.displayMessage(message);
    }

	public void AirplanesName() {
		if(paintStrategy.isPaintAirplaneId()) {
		paintStrategy.hidePlaneId();
		}else {
			paintStrategy.showPlaneId();
		}
		
	}

}
