package ship.elite;

import entity.*;
import game.World;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineJoin;
import ship.KamikazeExplosion;
import ship.Projectile;

public class TowProjectile extends Projectile {

	private Entity baggage;
	private Line rope;
	
	private double timer = 0; 
	private double xOffset; 
	private double yOffset;
	private double duration;
	private double fullSpeed;
	
	//TODO
	public TowProjectile(World world, double x, double y, Entity baggage, double radius, 
			double direction, double speed, double damage, double duration,
			Color team) {
		super(world, x, y, radius, direction, 0, damage, team);
		
		this.baggage = baggage;
		this.xOffset = baggage.getX() - getX();
		this.yOffset = baggage.getY() - getY();
		this.fullSpeed = speed;
		this.duration = duration + ropeTime;
		
		this.setTimeLeft(this.duration);
		
	}
	
	public void draw() {
		double radius = Math.sqrt((6.28318530718 * Math.pow(getRadius(), 2)*0.25));
		Polygon body = new Polygon();
		body.setFill(getTeam().darker()); 
		body.setStroke(getTeam().darker().darker());
		body.setStrokeWidth(5);
		body.setStrokeLineJoin(StrokeLineJoin.ROUND);
		body.getPoints().addAll(radius, 0.0,
					0.0, radius,
					-radius, radius/3,
					0.0, 0.0,
					-radius, -radius/3,
					0.0, -radius
					);
		display.getChildren().add(body);
		
		rope = new Line(getX(), getY(), getX(), getY());
		rope.setStroke(getTeam().darker());
		rope.setStrokeWidth(7);
		rope.setOpacity(0);
		getWorld().getDisplay().getChildren().add(rope);
	}
	
	private static final double ropeTime = 25;
	
	public void collideWith(Damagable subject) {
		
		//subject.takeDamage(damage);
		new KamikazeExplosion(getWorld(), getX(), getY(), getRadius()*5,
				getDamage(), getTeam(), false);
		die();
	}
	
	public void die() {
		super.die();
		if (rope != null) {
			getWorld().getDisplay().getChildren().remove(rope);
		}
	}
	
	public void run() {
		super.run();
		timer++;
		
		if (timer <= ropeTime) {
			this.shiftX(xOffset/ropeTime);
			this.shiftY(yOffset/ropeTime);
			this.setSpeed(fullSpeed *  Math.min(timer/ropeTime, 1));
			if (timer == ropeTime) {
				/*new Shield(getWorld(), baggage.getX(), baggage.getY(), baggage.getRadius()*2,
						10, getTeam(), baggage);*/
			}
		} else {
			
			double dist = Math.sqrt(getDistanceSq(baggage));
			double movDist = dist/10;
			movDist = Math.min(60, movDist);
			
			Moving e2 = (Moving)baggage;
			e2.shiftX((baggage.getX()-getX())/dist * -movDist);
			e2.shiftY((baggage.getY()-getY())/dist * -movDist);

			if (!baggage.getIsDead()) {
				shiftX((baggage.getX()-getX())/dist * movDist * 0.5);
				shiftY((baggage.getY()-getY())/dist * movDist * 0.5);
			}
			
		}
		
		if (rope != null) {
			double progress = Math.min(timer/ropeTime, 1);
			rope.setOpacity(progress);
			
			rope.setStartX(getX());
			rope.setStartY(getY());
			rope.setEndX(progress*(baggage.getX()-getX())+getX());
			rope.setEndY(progress*(baggage.getY()-getY())+getY());
		}
	}

}
