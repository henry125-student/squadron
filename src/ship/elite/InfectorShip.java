package ship.elite;

import java.util.ArrayList;

import entity.Damagable;
import entity.Entity;
import entity.HasTeam;
import game.World;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineJoin;
import ship.Ship;
import ship.Squad;

public class InfectorShip extends Elite {
public final double range = 750;
	
	public InfectorShip(Squad squad, double x, double y) {
		super(squad, x, y, 150);
	}
	
	public InfectorShip(World world, double x, double y) {
		super(world, x, y, 150);
	}
	
	public void setStats() {
		//setMaxSpeed(8);
		setMaxSpeed(12);
		setMaxHp(3600);
		setCapacity(7200);
		setReload(250);
		setActCost(200);
	}
	
	public static double getCost(Squad squad) {
		double cost = 3600;
		if (squad != null)
		return cost * squad.getCostMultiplier();
		return cost;
	}
	
	public void draw() {
		double radius = Math.sqrt((6.28318530718 * Math.pow(getRadius(), 2)/(2.59807621135)));
		
		Polygon body = new Polygon();
		body.setFill(getTeam()); 
		body.setStroke(getTeam().darker());
		body.setStrokeWidth(5);
		body.setStrokeLineJoin(StrokeLineJoin.ROUND);
		for (int i = 0; i < 3; i++) {
			body.getPoints().addAll(radius * Math.cos(Math.PI*2 * (double)i/3),
					radius * Math.sin(Math.PI*2 * (double)i/3));
		}
		display.getChildren().add(body);
		
		for (int i = 0; i < 3; i++) { 
			double angle = (i+0.5) * Math.PI * 2/3;
			Circle decor = new Circle(Math.cos(angle)*radius/3, Math.sin(angle)*radius/3,
					radius/2);
			decor.setStroke(getTeam().darker());
			decor.setStrokeWidth(10);
			decor.setFill(null);
			display.getChildren().add(decor);
		}
	}
	
	
	public boolean getInActRange() {//TODO
		double potentialDamage = 0;
		double damageLeft = waveDamage;
		
		for (int i = 0; i < getWorld().getAllEntities().size(); i++) {
			Entity subject = getWorld().getAllEntities().get(i);
			
			if (getDistanceSq(subject) < Math.pow(range, 2)) {
				if (subject instanceof Ship) {
					if (((HasTeam)subject).isOnTeam(getTeam())) {
						continue;
					}
					double hp = ((Damagable)subject).getHp();
					if (hp > damageLeft) {
						hp = Math.min(hp, 400);
					}
					potentialDamage += hp;
					damageLeft -= hp;
					if (hp < waveDamage) {
						potentialDamage += hp;
					}
					if (damageLeft <= 0) {
						break;
					}
				}
			}
		}
		if (potentialDamage >= waveDamage/2) {
			return true;
		}
		return false;
	}
	
	private static final double waveDamage = 1000;
	
	public void doAction() {
		
		/*new KamikazeExplosion(getWorld(), getX(), getY(), range,
				0, getTeam(), false);*/
		new RingsFX(getWorld(), getX(), getY(), range*1.25, 5, getTeam(), this);
		
		ArrayList<Entity> allEntities = getWorld().getAllEntities();
		ArrayList<Ship> considerationList = new ArrayList<Ship>();
		for (int i = 0; i < allEntities.size(); i++) {
			Entity subject = allEntities.get(i);
			if (this != subject && subject instanceof Ship && subject.getIsTangable()) {
				Ship subjectI = (Ship)subject;
				if (isOnTeam(((HasTeam) subject).getTeam())) {
					continue;
				}
				if (subjectI.getHp() > 0) {
					if (getDistanceSq(subject) < Math.pow(range - subject.getRadius(), 2)) {
						considerationList.add(subjectI);
					}
				}
			}
		}
		considerationList.sort((a, b) -> {
			return (int)Math.signum(a.getDistanceSq(this) - b.getDistanceSq(this));
		});
		double dmg = waveDamage;
		for (int i = 0; i < considerationList.size() && dmg > 0; i++) {
			
			Ship d = considerationList.get(i);
			
			new RingsFX(getWorld(), d.getX(), d.getY(), d.getRadius()*5, 20, getTeam(), d);
			
			double maxDmg = d.getHp();
			if (d.getHp() > dmg) {
				maxDmg = 400;
			}
			maxDmg = Math.min(maxDmg, dmg);
			dmg -= maxDmg;
			if (d.getHp() <= maxDmg) {
				affect(d);
			} else {
				d.takeDamage(maxDmg);
			}
		}
	}
	
	private void affect(Ship s) {
		s.getSquad().send(s, this.getSquad());
		s.redraw();
		if (s instanceof Elite) {
			((Elite)s).revive();
		}
		s.getSquad().dumpExcess(s);
	}
}
