package ship;

import entity.*;
import game.World;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineJoin;

public class KamikazeActive extends Missle{

	private double maxSpeed;
	private double maxEnergy;
	
	public KamikazeActive(World world, double x, double y, double radius, double direction, double speed, Entity target,
			double damage, Color team) {
		super(world, x, y, radius, direction, speed, target, damage, team);
		
		maxSpeed = speed;
		maxEnergy = getTimeLeft();
		
	}
	
	public void control() {
		super.control();
		
		if (getTarget() != null) {
			double acceleration = (getTarget().getX() - getX()) * Math.cos(-getDirection())
					- (getTarget().getY() - getY()) * Math.sin(-getDirection());
			shiftSpeed(acceleration / 25);
		}
		setSpeed(Math.max(Math.min(getSpeed(), maxSpeed), -maxSpeed));
	}
	
	public void draw() {
		Path body = new Path();
		
		body.setFill(getTeam()); 
		body.setStroke(Color.WHITE);
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
	}
	
	public void suddenlyDie() {
		die(1, 1.0);
	}
	
	public static final double EXPLOSION_SIZE = 350;//250
	
	public void collideWith(Damagable subject) {
		suddenlyDie();
		new KamikazeExplosion(getWorld(), getX(), getY(), EXPLOSION_SIZE, 
				getDamage() * getTimeLeft() / maxEnergy, getTeam(), true);
	}

}
