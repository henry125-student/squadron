package ship;

import entity.*;
import game.World;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

public class Projectile extends Entity implements Moving, HasTeam{

	private double direction;
	private double speed;
	private double damage;
	private Color team;
	private double timeLeft;
	
	public Projectile(World world, double x, double y, 
			double radius, double direction, double speed, double damage, Color team) {
		super(world, x, y, radius, direction);
		this.direction = direction;
		this.speed = speed;
		this.damage = damage;
		this.team = team;
		
		this.timeLeft = 150;
		
		getDisplay().toBack();
		draw();
	}
	
	public void draw() {
		Circle c = new Circle(0, 0, getRadius());
		c.setFill(getTeam().darker());
		c.setStroke(getTeam().darker().darker());
		c.setStrokeWidth(5);
		c.setStrokeLineJoin(StrokeLineJoin.ROUND);
		getDisplay().getChildren().add(c);
	}
	
	public double getDirection() {
		return direction;
	}
	public void shiftDirection(double newVal) {
		direction += newVal;
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double newVal) {
		speed = newVal;
	}
	public void shiftSpeed(double newVal) {
		setSpeed(getSpeed() + newVal);
	}
	public double getDamage() {
		return damage;
	}
	public void setDamage(double newVal) {
		damage = newVal;
	}
	public Color getTeam() {
		return team;
	}
	public double getTimeLeft() {
		return timeLeft;
	}
	public void setTimeLeft(double newVal) {
		timeLeft = newVal;
	}
	
	public void control() {
		
	}
	
	public void collideWith(Damagable subject) {
		
		//subject.takeDamage(damage);
		new KamikazeExplosion(getWorld(), getX(), getY(), getRadius()*3,
				damage, team, false);
		die();
	}
	
	protected void expire() {
		die();
	}
	
	public void run() {
		control();
		move();
		
		timeLeft -= 1;
		
		//collision
		for (int i = 0; i < getWorld().getAllEntities().size(); i++) {
			Entity subject = getWorld().getAllEntities().get(i);
			if (subject != this && subject.getIsTangable()) {
				if (subject instanceof Damagable && !((Damagable) subject).isOnTeam(team)
						&& !subject.getIsDead() && checkContact(subject)) {
					collideWith((Damagable)subject);
				}
			}
		}
		
		if (timeLeft <= 0) {
			expire();
		}
		
	}

}
