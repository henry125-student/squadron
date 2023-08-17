package ship;

import game.*;
import entity.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineJoin;

public class Missle extends Projectile{

	Entity target;
	private double baseSpeed;
	
	public Missle(World world, double x, double y, double radius, double direction,
			double speed, Entity target, double damage,
			Color team) {
		super(world, x, y, radius, direction, speed, damage, team);
		
		this.target = target;
		this.baseSpeed = speed;
		
		if (target instanceof Position) {
			reTarget();
		}
	}
	
	public Entity getTarget() {
		return target;
	}
	
	public void reTarget() {
		Damagable closest = null;
		double minDistanceSq = Math.pow(1000, 2);
		for (int i = 0; i < getWorld().getAllEntities().size(); i++) {
			Entity subject = getWorld().getAllEntities().get(i);
			if (subject instanceof Damagable && subject.getIsTangable()) {
				Damagable subjectD = (Damagable)subject;
				if (!subjectD.isOnTeam(getTeam())) {
					if (subject.getDistanceSq(this) < minDistanceSq) {
						minDistanceSq = subject.getDistanceSq(this);
						closest = subjectD;
					}
				}
			}
		}
		if (closest != null) {
			target = (Entity)closest;
		} else {
			target = null;
		}
	}
	
	public void draw() {
		Polygon body = new Polygon();
		body.getPoints().addAll(
				getRadius() * (Math.PI - 1), 0.0,
				-getRadius(), getRadius(),
				-getRadius(), -getRadius());
		body.setFill(getTeam().darker());
		body.setStroke(getTeam().darker().darker());
		body.setStrokeWidth(5);
		body.setStrokeLineJoin(StrokeLineJoin.ROUND);
		getDisplay().getChildren().add(body);
	}
	
	public void control() {
		
		if (target != null) {
			double shipTargetX = target.getX() - getX();
			double shipTargetY = target.getY() - getY();
			
			double shipTargetDirection = toDirection(shipTargetX, shipTargetY);
			double steering = (shipTargetDirection - getDirection() + Math.PI)%(2*Math.PI)-Math.PI;
			shiftDirection(steering / 2); 
		}
		setSpeed(baseSpeed * Math.min((getTimeLeft()/75), 1) );
		setRotate(getDirection());
		
	}
	
	public void run() {
		
		if (target == null || target.getIsDead() || !target.getIsTangable()) {
			reTarget();
		}
		super.run();
	}

}
