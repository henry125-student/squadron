package ship;

import entity.*;
import game.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import java.util.*;

public class KamikazeExplosion extends Entity implements HasTeam {

	private double maxRadius;
	private double damage;
	private Color color;
	private boolean friendlyFire = true;
	private Entity parent = null;
	
	public KamikazeExplosion(World world, double x, double y, double maxRadius, double damage, Color color, boolean friendlyFire) {
		super(world, x, y, 1);
		
		this.maxRadius = maxRadius;
		this.damage = damage;
		this.color = color;
		this.friendlyFire = friendlyFire;
		draw();
		
		explode();
	}
	public KamikazeExplosion(World world, double x, double y, double maxRadius, double damage, Color color) {
		this(world, x, y, maxRadius, damage, color, true);
	}
	
	public KamikazeExplosion(World world, double x, double y, double maxRadius, double damage, Color color, Entity parent) {
		this(world, x, y, maxRadius, damage, color, true);
		this.parent = parent;
		this.friendlyFire = false;
	}
	
	public void draw() {
		getDisplay().getChildren().clear();
		
		Circle body = new Circle(0, 0, 1);
		body.setStrokeWidth(0);
		body.setFill(color);
		
		getDisplay().getChildren().add(body);
	}
	
	public void die() {
		die(500, maxRadius * 2);
	}
	
	public Color getTeam() {
		return color;
	}
	
	private Damagable findClosestDamagable() {
	
		Damagable closest = null;
		double minDistance = maxRadius;
		ArrayList<Entity> allEntities = getWorld().getAllEntities();
		
		for (int i = 0; i < allEntities.size(); i++) {
			Entity subject = allEntities.get(i);
			if (this != subject && subject instanceof Damagable && subject.getIsTangable()) {
				if (!friendlyFire && (subject instanceof HasTeam)) {
					if (this.isOnTeam(((HasTeam) subject).getTeam())) {
						continue;
					}
				}
				Damagable subjectI = (Damagable)subject;
				if (subjectI.getHp() > 0) {
					if (getDistanceSq(subject) < Math.pow(minDistance + subject.getRadius(), 2)) {
						minDistance = Math.sqrt(getDistanceSq(subject)) - subject.getRadius();
						closest = subjectI;
					}
				}
			}
		}
		return closest;
	
	}
	
	public void run() {
		if (parent != null) {
			setX(parent.getX());
			setY(parent.getY());
		}
	}
	
	public void explode() {
		die();
		
		while (findClosestDamagable() != null) {
			Damagable cd = findClosestDamagable();
			
			if (cd.getHp() < damage) {
				damage -= cd.getHp();
				
				cd.takeDamage(cd.getHp());
			} else {
				cd.takeDamage(damage);
				break;
			}
		}
		
	}

}
