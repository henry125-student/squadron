package ship.elite;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import entity.Damagable;
import entity.Entity;
import entity.HasTeam;
import game.World;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import ship.*;

public class TowShip extends Elite {
	public TowShip(Squad squad, double x, double y) {
		super(squad, x, y, 150);
	}
	
	public TowShip(World world, double x, double y) {
		super(world, x, y, 150);
	}
	
	public void setStats() {
		//setMaxSpeed(12);
		setMaxSpeed(12);
		setMaxHp(3600);
		setCapacity(7200);
		setReload(25);
		setActCost(10);
	}
	
	public static double getCost(Squad squad) {
		double cost = 3600;
		if (squad != null)
		return cost * squad.getCostMultiplier();
		return cost;
	}
	
	public void draw() {
		double radius = Math.sqrt((6.28318530718 * Math.pow(getRadius(), 2)*0.25));
		
		Rectangle spindle = new Rectangle(-radius*0.2, -radius/3, radius*0.8, radius/3*2);
		spindle.setFill(getTeam().darker()); 
		spindle.setStroke(getTeam().darker().darker());
		spindle.setStrokeWidth(5);
		spindle.setStrokeLineJoin(StrokeLineJoin.ROUND);
		display.getChildren().add(spindle);
		
		Polygon body1 = new Polygon();
		body1.setFill(getTeam()); 
		body1.setStroke(getTeam().darker());
		body1.setStrokeWidth(5);
		body1.setStrokeLineJoin(StrokeLineJoin.ROUND);
		body1.getPoints().addAll(radius, radius*0.2,
					0.0, radius,
					-radius, radius/3*2,
					-radius*0.2, radius*0.2
					);
		display.getChildren().add(body1);
		
		Path decor1 = new Path();
		MoveTo m = new MoveTo(-radius*0.5, radius*0.8);
		LineTo l1 = new LineTo(0, radius*0.5);
		LineTo l2 = new LineTo(radius*0.6, radius*0.5);
		decor1.getElements().addAll(m, l1, l2);
		decor1.setStroke(getTeam().darker());
		decor1.setStrokeWidth(15);
		decor1.setStrokeLineCap(StrokeLineCap.ROUND);
		display.getChildren().add(decor1);
		
		Polygon body2 = new Polygon();
		body2.setFill(getTeam()); 
		body2.setStroke(getTeam().darker());
		body2.setStrokeWidth(5);
		body2.setStrokeLineJoin(StrokeLineJoin.ROUND);
		body2.getPoints().addAll(radius, -radius*0.2,
					0.0, -radius,
					-radius, -radius/3*2,
					-radius*0.2, -radius*0.2
					);
		display.getChildren().add(body2);
		
		Path decor2 = new Path();
		m = new MoveTo(-radius*0.5, -radius*0.8);
		l1 = new LineTo(0, -radius*0.5);
		l2 = new LineTo(radius*0.6, -radius*0.5);
		decor2.getElements().addAll(m, l1, l2);
		decor2.setStroke(getTeam().darker());
		decor2.setStrokeWidth(15);
		decor2.setStrokeLineCap(StrokeLineCap.ROUND);
		display.getChildren().add(decor2);
	}
	
	private static final double minRange = 1000; 
	private static final double innerRange = 1000; 
	
	public boolean getInActRange() {
		Entity target = getSquad().getTarget();
		boolean cond1 = target.getDistanceSq(this) > Math.pow(minRange + target.getRadius(), 2);
		boolean cond2 = target instanceof Damagable &&
				!((HasTeam)target).isOnTeam(getTeam()) &&
				target.getIsTangable();
		if (cond1 || cond2) {	
			/*double shipTargetDirection = toDirection(target.getX() - getX(), target.getY() - getY());
			double angleOffset = (shipTargetDirection - getDirection() + Math.PI)%(2*Math.PI)-Math.PI;
			if (Math.abs(angleOffset) < Math.PI / 6) {*/
				return true;
			//}
		}
		return false;
	}
	
	private static final double damage = 500;
	private static final double maxDuration = 75;
	
	private List<Ship> prevLoads;
	
	public void doAction() {
		if (prevLoads == null) {
			prevLoads = new ArrayList<Ship>();
		} else if (prevLoads.size() > 4){
			prevLoads.remove(0);
		}
		
		Ship load = getSquad().getAllShips().stream()
				.filter((e)->(e.getDistanceSq(this) <= Math.pow(innerRange + e.getRadius(), 2)))
				.filter((e)->e != this)
				.filter((e)->!prevLoads.contains(e))
				.sorted(Comparator.comparing((e)->-e.getDistanceSq(getSquad().getTarget())))
				.findFirst().orElse(this);
		prevLoads.add(load);
		
		double dist = Math.sqrt(getDistanceSq(getSquad().getTarget()));
		double duration = Math.min(dist / (getMaxSpeed()) + 10, maxDuration);
		
		new TowProjectile(getWorld(), getX(), getY(), load, getRadius()*0.2, 
				toDirection(getSquad().getTarget().getX()-getX(), 
						getSquad().getTarget().getY()-getY()),
				getMaxSpeed() * 3, damage, duration, getTeam());
	}
}
