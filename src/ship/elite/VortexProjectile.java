package ship.elite;

import entity.Damagable;
import entity.Entity;
import entity.Position;
import game.World;
import javafx.scene.paint.Color;
import ship.Projectile;

public class VortexProjectile extends Projectile {
	private double maxSpeed;
	private double targetX;
	private double targetY;
	private double vortexRange;
	
	public VortexProjectile(World world, double x, double y, double radius, double direction, double speed,
			double damage, Entity target, double vortexRange, Color team) {
		super(world, x, y, radius, direction, speed, damage, team);
		this.targetX = target.getX();
		this.targetY = target.getY();
		this.vortexRange = vortexRange;
		maxSpeed = speed;
		setIsTangable(false);
		
	}
	
	public void control() {
		double dist = Math.sqrt(getDistanceSq(new Position(getWorld(), targetX, targetY)));
		setSpeed(Math.max(Math.min(dist/5, maxSpeed), -maxSpeed));
	}
	
	/*public void draw() {
		Path body = new Path();
		
		body.setFill(getTeam()); 
		body.setStroke(getTeam().darker());
		body.setStrokeWidth(5);
		body.setStrokeLineJoin(StrokeLineJoin.ROUND);
		
		double squishFactor = 0.936057985546;
		final double SQRT2 = Math.sqrt(2);
		
		MoveTo m = new MoveTo(-getRadius() / SQRT2, getRadius() / SQRT2 * squishFactor);
		ArcTo a = new ArcTo();
		a.setX(-getRadius() / SQRT2); a.setY(-getRadius() / SQRT2);
		a.setRadiusX(getRadius()); a.setRadiusY(getRadius() * squishFactor);
		a.setLargeArcFlag(true);
		a.setSweepFlag(false);
		LineTo l1 = new LineTo(-getRadius() * SQRT2, 0);
		LineTo l2 = new LineTo(m.getX(), m.getY());
		
		body.getElements().addAll(m, a, l1, l2);
		display.getChildren().add(body);
	}*/
	
	public void collideWith(Damagable subject) {}
	
	private void activate() {
		die(250, 0);
		new Vortex(getWorld(), getX(), getY(), vortexRange, 
				getDamage(), getTeam());
	}
	
	protected void expire() {
		activate();
	}
	
	public void run() {
		super.run();
		if (Math.pow(getX()-targetX,2) + Math.pow(getY()-targetY,2) < 1000) {
			activate();
		}
	}
}
