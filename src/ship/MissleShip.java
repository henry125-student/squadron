package ship;

import entity.Damagable;
import entity.Entity;
import game.World;
import javafx.scene.shape.*;

public class MissleShip extends Ship{

	public MissleShip(Squad squad, double x, double y) {
		super(squad, x, y, 50);
	}
	
	public MissleShip(World world, double x, double y) {
		super(world, x, y, 50);
	}
	
	public void setStats() {
		//setMaxSpeed(8);
		setMaxSpeed(12);
		setMaxHp(400);
		setCapacity(800);
		setReload(200);
		setActCost(25);
	}
	
	public static double getCost(Squad squad) {
		double cost = 400;
		if (squad != null)
		return cost * squad.getCostMultiplier();
		return cost;
	}
	
	public void draw() {
		double radius = Math.sqrt((6.28318530718 * Math.pow(getRadius(), 2)/(5.65685424949)));
		Polygon body = new Polygon();
		body.setFill(getTeam()); 
		body.setStroke(getTeam().darker());
		body.setStrokeWidth(5);
		body.setStrokeLineJoin(StrokeLineJoin.ROUND);
		for (int i = 0; i < 8; i++) {
			body.getPoints().addAll(radius * Math.cos(Math.PI*2 * (double)i/8),
					radius * Math.sin(Math.PI*2 * (double)i/8));
		}
		display.getChildren().add(body);
	}
	
	
	public boolean getInActRange() {//TODO
		Entity target = getSquad().getTarget();
		if (target instanceof Damagable && !((Damagable)target).isOnTeam(getTeam()) && target.getIsTangable() &&
				(target.getDistanceSq(this) <= Math.pow(2000 + target.getRadius(), 2)) ) {
			
			return true; 
		}
		return false;
	}
	
	public void doAction() {
		new Missle(getWorld(), getX(), getY(), getRadius()/2, getDirection(),
				getMaxSpeed() * 6, getSquad().getTarget(), 100, getTeam());
	}

}
