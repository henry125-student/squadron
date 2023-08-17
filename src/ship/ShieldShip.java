package ship;

import entity.Entity;
import entity.HasTeam;
import game.World;
import javafx.scene.shape.*;

public class ShieldShip extends Ship{

	public final double range = 500;
	
	public ShieldShip(Squad squad, double x, double y) {
		super(squad, x, y, 50);
	}
	
	public ShieldShip(World world, double x, double y) {
		super(world, x, y, 50);
	}
	
	public void setStats() {
		//setMaxSpeed(8);
		setMaxSpeed(12);
		setMaxHp(400);
		setCapacity(800);
		setReload(200);
		setActCost(20);
	}
	
	public static double getCost(Squad squad) {
		double cost = 400;
		if (squad != null)
		return cost * squad.getCostMultiplier();
		return cost;
	}
	
	public void draw() {
		double radius = Math.sqrt((6.28318530718 * Math.pow(getRadius(), 2)/(5.19615242271)));
		Polygon body = new Polygon();
		body.setFill(getTeam()); 
		body.setStroke(getTeam().darker());
		body.setStrokeWidth(5);
		body.setStrokeLineJoin(StrokeLineJoin.ROUND);
		for (int i = 0; i < 6; i++) {
			body.getPoints().addAll(radius * Math.cos(Math.PI*2 * (double)i/6),
					radius * Math.sin(Math.PI*2 * (double)i/6));
		}
		display.getChildren().add(body);
		
		double apothem = Math.cos(Math.PI / 6) * radius;
		final double decorScale = 1.2;
		
		Path decor = new Path();
		for (int i = 0; i < 3; i++) { 
			double angle = i * Math.PI * 2/3;
			MoveTo m = new MoveTo(decorScale * apothem * Math.cos(angle - Math.PI/6),
					decorScale * apothem * Math.sin(angle - Math.PI/6));
			LineTo l1 = new LineTo(decorScale * radius * Math.cos(angle),
					decorScale * radius * Math.sin(angle));
			LineTo l2 = new LineTo(decorScale * apothem * Math.cos(angle + Math.PI/6),
					decorScale * apothem * Math.sin(angle + Math.PI/6));
			
			decor.getElements().addAll(m, l1, l2);
		}
		decor.setStroke(getTeam().darker());
		decor.setStrokeWidth(10);
		decor.setStrokeLineJoin(StrokeLineJoin.ROUND);
		display.getChildren().add(decor);
	}
	
	
	public boolean getInActRange() {//TODO
		double potentialDamage = 0;
		
		for (int i = 0; i < getWorld().getAllEntities().size(); i++) {
			Entity subject = getWorld().getAllEntities().get(i);
			
			if (getDistanceSq(subject) < Math.pow(range, 2)) {
				if ((subject instanceof Projectile) 
						&& !(((HasTeam)subject).isOnTeam(getTeam())) ) {
					Projectile subject2 = (Projectile)subject;
					potentialDamage += subject2.getDamage();
				} else if ((subject instanceof RamShip) 
						&& !(((HasTeam)subject).isOnTeam(getTeam())) ) {
					potentialDamage += 100;
				} else if (subject instanceof Shield && (((HasTeam)subject).isOnTeam(getTeam()))
						&& !(subject == this)) {
					potentialDamage -= ((Shield)subject).getHp();
				}
			}
		}
		if (potentialDamage >= 50) {
			return true;
		}
		return false;
	}
	
	public void doAction() {
		/*for (Ship s : getSquad().getAllShips()) {
			new Shield(getWorld(), s.getX(), s.getY(), s.getRadius()*1.5, s.getMaxHp()/20, getTeam(), s);
		}*/
		/*for (int i = getWorld().getAllEntities().size()-1; i >= 0 ; i--) {
			Entity subject = getWorld().getAllEntities().get(i);
			if (!(subject instanceof HasTeam) || subject instanceof Shield
					|| ((HasTeam) subject).isOnTeam(getTeam())) {
				continue;
			}
			if (getDistanceSq(subject) < Math.pow(range, 2)) {
				new Shield(getWorld(), subject.getX(), subject.getY(),
						subject.getRadius()*1.5, 10, getTeam(), subject);
			}
		}*/
		new Shield(getWorld(), getX(), getY(), range, 100, getTeam(), this);
	}

}

