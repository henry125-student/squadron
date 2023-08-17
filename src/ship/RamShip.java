package ship;

import entity.*;
import game.*;
import javafx.scene.shape.*;

public class RamShip extends Ship{
	
	private double boost = 0;
	private final double boostLength = 250;
	
	public RamShip(Squad squad, double x, double y) {
		super(squad, x, y, 25);
	}
	
	public RamShip(World world, double x, double y) {
		super(world, x, y, 25);
	}
	
	public void setStats() {
		//setMaxSpeed(16);
		setMaxSpeed(12);
		setMaxHp(100);
		setCapacity(200);
		setCrashIntoTarget(true);
		setReload(50);
		setActCost(5);
	}
	
	public static double getCost(Squad squad) {
		double cost = 100;
		if (squad != null)
		return cost * squad.getCostMultiplier();
		return cost;
	}
	
	public void draw() {
		Polygon body = new Polygon();
		body.getPoints().addAll(
				getRadius() * (Math.PI - 1), 0.0,
				-getRadius(), getRadius(),
				-getRadius(), -getRadius());
		body.setFill(getTeam());
		body.setStroke(getTeam().darker());
		body.setStrokeWidth(5);
		body.setStrokeLineJoin(StrokeLineJoin.ROUND);
		getDisplay().getChildren().add(body);
	}
	
	public void control() {
		if (boost <= 0) {
			setCrashDamage(1, 5);
			super.control();
		} else {
			boost--;
		}
		/*double acceleration = shipTargetX * Math.cos(-getDirection())
				- shipTargetY * Math.sin(-getDirection());
		shiftSpeed(acceleration / 10);
		setSpeed(Math.max(Math.min(getSpeed(), maxSpeed), -maxSpeed));*/
	}
	
	public void collideWith(Entity subject) {
		boost = Math.max(boost - boostLength, 0);
		
		super.collideWith(subject);
	}
	
	public boolean getInActRange() {
		Entity target = getSquad().getTarget();
		if (target instanceof Damagable && !((Damagable)target).isOnTeam(getTeam()) &&
				(target.getDistanceSq(this) <= Math.pow(boostLength + target.getRadius(), 2) )) {
			return true;
		}
		return false;
	}
	
	public void doAction() {
		setSpeed(getMaxSpeed() * 4);
		boost += boostLength / getSpeed();
		setCrashDamage(0, 50);
		
		double shipTargetX = getSquad().getTarget().getX() - getX();
		double shipTargetY = getSquad().getTarget().getY() - getY();
		
		double shipTargetDirection = toDirection(shipTargetX, shipTargetY);
		setDirection(shipTargetDirection);
		
	}
}
