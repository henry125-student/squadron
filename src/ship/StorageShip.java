package ship;

import game.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class StorageShip extends Ship {
	
	private Polygon signal;
	private double opacity = 0;
	
	public StorageShip(Squad squad, double x, double y) {
		super(squad, x, y, 35);
	}
	
	public StorageShip(World world, double x, double y) {
		super(world, x, y, 35);
	}
	
	public void setStats() {
		setCapacity(800);
		setMaxHp(200);
		setReload(100);
		setActCost(0);
	}
	
	public static double getCost(Squad squad) {
		double cost = 200;
		if (squad != null)
		return cost * squad.getCostMultiplier();
		return cost;
	}
	
	public void draw() {
		double radius = Math.sqrt((6.28318530718 * Math.pow(getRadius(), 2)/(4.75528258148)));
		Polygon body = new Polygon();
		body.setFill(getTeam()); 
		body.setStroke(getTeam().darker());
		body.setStrokeWidth(5);
		body.setStrokeLineJoin(StrokeLineJoin.ROUND);
		for (int i = 0; i < 5; i++) {
			body.getPoints().addAll(radius * Math.cos(Math.PI*2 * (double)i/5),
					radius * Math.sin(Math.PI*2 * (double)i/5));
		}
		display.getChildren().add(body);
		
		signal = new Polygon();
		signal.setFill(null); 
		signal.setStrokeWidth(7);
		signal.setStrokeLineJoin(StrokeLineJoin.ROUND);
		signal.setOpacity(opacity);
		for (int i = 0; i < 5; i++) {
			signal.getPoints().addAll(1.5 * radius * Math.cos(Math.PI*2 * (double)i/5),
					1.5 * radius * Math.sin(Math.PI*2 * (double)i/5));
		}
		display.getChildren().add(signal);
	}
	
	private void signalGlow(Color c) {
		opacity = 1;
		signal.setOpacity(opacity);
		signal.setStroke(c);
	}
	
	public boolean getInActRange() {
		return true;
	}
	
	public void doAction() {
		double e = getSquad().getEnergy();
		double m = getSquad().getMaterial();
		if (e > m) {
			if (this.getSquad().removeEnergy(10)) {
				this.getSquad().addMaterial(10);
				signalGlow(Color.rgb(127, 127, 255));
			}
		} else if (m > e) {
			if (this.getSquad().removeMaterial(10)) {
				this.getSquad().addEnergy(10);
				signalGlow(Color.rgb(255, 127, 127));
			}
		}
	}
	
	public void run() {
		super.run();
		opacity = Math.max(0, opacity - 0.02);
		signal.setOpacity(opacity);
	}
	
}
