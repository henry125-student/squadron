package ship;

import entity.Damagable;
import entity.Entity;
import game.*;
import javafx.scene.shape.*;

public class CannonShip extends Ship{
	
	public CannonShip(Squad squad, double x, double y) {
		super(squad, x, y, 35);
	}
	
	public CannonShip(World world, double x, double y) {
		super(world, x, y, 35);
	}
	
	public void setStats() {
		//setMaxSpeed(12);
		setMaxHp(200);
		setCapacity(400);
		setReload(100);
		setActCost(10);
	}
	
	public static double getCost(Squad squad) {
		double cost = 200;
		if (squad != null)
		return cost * squad.getCostMultiplier();
		return cost;
	}
	
	public void draw() {
		Polygon body = new Polygon();
		body.getPoints().addAll(
				getRadius(), -(Math.PI - 2.5) * getRadius(),
				getRadius(), (Math.PI - 2.5) * getRadius(),
				-getRadius(), getRadius() * 1.25,
				-getRadius(), -getRadius() * 1.25);
		body.setFill(getTeam());
		body.setStroke(getTeam().darker());
		body.setStrokeWidth(5);
		body.setStrokeLineJoin(StrokeLineJoin.ROUND);
		getDisplay().getChildren().add(body);
	}
	
	
	public boolean getInActRange() {//TODO
		Entity target = getSquad().getTarget();
		if (target instanceof Damagable && !((Damagable)target).isOnTeam(getTeam()) && target.getIsTangable() &&
				(target.getDistanceSq(this) <= Math.pow(1000 + target.getRadius(), 2)) ) {
			
			double shipTargetDirection = toDirection(target.getX() - getX(), target.getY() - getY());
			double angleOffset = (shipTargetDirection - getDirection() + Math.PI)%(2*Math.PI)-Math.PI;
			if (Math.abs(angleOffset) < Math.PI / 30) {
				return true;
			}
		}
		return false;
	}
	
	public void doAction() {
		new Projectile(getWorld(), getX(), getY(), getRadius()/2, getDirection(),
				getMaxSpeed() * 3, 100, getTeam());
	}
}
