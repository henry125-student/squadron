package ship.elite;

import entity.*;
import game.World;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineJoin;
import ship.Squad;

public class VortexShip extends Elite {
	public VortexShip(Squad squad, double x, double y) {
		super(squad, x, y, 150);
	}
	
	public VortexShip(World world, double x, double y) {
		super(world, x, y, 150);
	}
	
	public void setStats() {
		//setMaxSpeed(8);
		setMaxSpeed(12);
		setMaxHp(3600);
		setCapacity(7200);
		setReload(400);
		setActCost(200);
	}
	
	public static double getCost(Squad squad) {
		double cost = 3600;
		if (squad != null)
		return cost * squad.getCostMultiplier();
		return cost;
	}
	
	public void draw() {
		double radius = Math.sqrt((6.28318530718 * Math.pow(getRadius(), 2)/(5.65685424949)));
		
		for (int i = 0; i < 8; i++) {
			Polygon body = new Polygon();
			body.setFill(getTeam()); 
			body.setStroke(getTeam().darker());
			body.setStrokeWidth(5);
			body.setStrokeLineJoin(StrokeLineJoin.ROUND);
			body.getPoints().addAll(1.2 * radius * Math.cos(Math.PI*2 * (double)i/8),
					1.2 * radius * Math.sin(Math.PI*2 * (double)i/8));
			body.getPoints().addAll(0.7 * radius * Math.cos(Math.PI*2 * (double)(2*i+1.2)/16),
					0.7 * radius * Math.sin(Math.PI*2 * (double)(2*i+1.2)/16));
			body.getPoints().addAll(0.0,0.0);

			display.getChildren().add(body);
		}
		
		Circle decor = new Circle(0, 0,
				radius/2);
		decor.setStroke(getTeam().darker());
		decor.setStrokeWidth(10);
		decor.setFill(null);
		display.getChildren().add(decor);
	}
	
	private static final double range = 2000;
	
	public boolean getInActRange() {//TODO
		Entity target = getSquad().getTarget();
		if (target instanceof Damagable &&
				target instanceof HasTeam &&
				!((HasTeam)target).isOnTeam(getTeam()) &&
				target.getIsTangable() ) {
			double distSq = target.getDistanceSq(this);
			if (distSq > Math.pow(range + target.getRadius(), 2)) return false; 
		}
		
		double totalValue = getWorld().getAllEntities().stream()
				.filter((e)->target.getDistanceSq(e) < Math.pow(vortexRadius, 2))
				.filter((e)->{
					return e instanceof Damagable &&
							((Damagable)e).getHp() > 0 &&
							e instanceof HasTeam &&
							!((HasTeam)e).isOnTeam(getTeam()) &&
							e.getIsTangable() &&
							e instanceof HasValue;
				})
				.mapToDouble((e)->((HasValue)e).getValue() * (1-Math.sqrt(target.getDistanceSq(e))/vortexRadius))
				.sum();
				
		return totalValue >= damage;
	}
	
	private static final double damage = 1000;
	private static final double vortexRadius = 900;
	
	public void doAction() {
		Entity target = getSquad().getTarget();
		
		new VortexProjectile(getWorld(), getX(), getY(), getRadius()/3, 
				toDirection(getSquad().getTarget().getX()-getX(), getSquad().getTarget().getY()-getY()),
				getMaxSpeed() * 6, damage, target, vortexRadius, getTeam());
	}
}
