package ship.elite;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import entity.Damagable;
import entity.Entity;
import entity.HasTeam;
import entity.Moving;
import game.World;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import ship.Ship;

public class Vortex extends Entity implements HasTeam {
	
	Color team;
	double duration;
	double maxDuration;
	double maxRadius;
	double radius;
	private Circle body = new Circle();
	private double timer = 0;
	private ArrayList<Circle> rings;
	
	private static final double dps = 15;
	private double totalDamage;
	
	private boolean isFx = false;
	private Entity parent = null;
	
	public Vortex(World world, double x, double y, double maxRadius, double totalDamage, Color team) {
		super(world, x, y, maxRadius, 0);
		setIsTangable(false);
		this.team = team;
		this.duration = totalDamage/dps;
		this.maxDuration = duration * 2;
		this.maxRadius = maxRadius;
		this.totalDamage = totalDamage;
		this.radius = 0;
		
		body = new Circle(0, 0, 0);
		body.setStroke(team);
		body.setFill(null);
		body.getStrokeDashArray().addAll(50.0, 50.0);
		body.setStrokeWidth(5);
		body.setOpacity(0.5);
		
		getDisplay().getChildren().add(body);
		
		rings = new ArrayList<Circle>();
		ring();
		
	}
	
	public static Vortex fxVortex(World world, Entity parent, double maxRadius, double duration, Color team) {
		Vortex out = new Vortex(world, parent.getX(), parent.getY(), maxRadius, 0, team);
		out.duration = duration;
		out.isFx = true;
		out.parent = parent;
		out.body.setOpacity(0);
		
		return out;
	}
	
	public void ring() {
		double rand1 = (Math.random()*0.5 + 0.5) * radius;
		double rand2 = (Math.random()*1 + 0) * radius;
		double dir = Math.random() * Math.PI * 2;
		
		double dist = Math.abs(rand2+rand1)/2;
		double rad = Math.abs(dist-rand1);
		
		Circle ring = new Circle(dist*Math.cos(dir), dist*Math.sin(dir), rad);
		ring.setStroke(null);
		ring.setFill(team);
		ring.setOpacity(0);
		rings.add(ring);
		getDisplay().getChildren().add(ring);
		
		/*ring = new Circle(0, 0, radius);
		ring.setStroke(team);
		ring.setStrokeWidth(10);
		ring.setFill(null);
		ring.setOpacity(0);
		rings.add(ring);
		getDisplay().getChildren().add(ring);*/
		
	}
	
	public void run() {
		timer++;
		radius = Math.max( Math.sin(timer/duration*Math.PI) * maxRadius, 0 );
		body.setRadius(radius/2);
		
		if (!isFx) {
			List<Entity> list = getWorld().getAllEntities().stream()
				.filter((e)->getDistanceSq(e) < Math.pow(radius, 2))
				.filter((e)->{
					return e instanceof Damagable && !((Damagable)e).isOnTeam(getTeam()) && e.getIsTangable();
				})
				.sorted((e1, e2)->(int)Math.signum(Math.sqrt(getDistanceSq(e1))- e1.getRadius() -
						Math.sqrt(getDistanceSq(e2)) + e2.getRadius()) )
				.collect(Collectors.toList());
			
			if (list.size() > 0 && getDistanceSq(list.get(0)) < (radius+100)*(radius+100)/4) {
				Damagable e = (Damagable) list.get(0);
				if (e.getHp() < dps) {
					duration = Math.min(duration + 15/dps, maxDuration);
				}
				duration = Math.min(duration + 0.67, maxDuration);
				e.takeDamage(dps);
				
			}
			
			double damageThreat = totalDamage * Math.pow(radius/maxRadius, 2) * 2;
			for (Entity e : list.stream()
					.filter((e)->e instanceof Moving)
					.collect(Collectors.toList())) {
				double dist = Math.sqrt(getDistanceSq(e));
				double movDist = (radius - dist)/10;
				if (e instanceof Ship) {
					movDist = Math.min(12, movDist);
				}
				
				Moving e2 = (Moving)e;
				e2.shiftX((e.getX()-getX())/dist * -movDist);
				e2.shiftY((e.getY()-getY())/dist * -movDist);
				
				damageThreat -= ((Damagable) e).getHp();
				if (damageThreat <= 0) {
					break;
				}
			}
		}
		
		
		//appearance
		if (timer < duration /*&& timer % 2 == 0*/) {
			ring();
			ring();
		}
		
		for (Circle r : rings) {
			r.setRadius(Math.max(r.getRadius()*0.85, 0));
			r.setCenterX(r.getCenterX()*0.9);
			r.setCenterY(r.getCenterY()*0.9);

			r.setOpacity(Math.max((1-(1-r.getOpacity())*0.85), 0));
			
		}
		for (int i = rings.size()-1; i >= 0; i--) {
			if (rings.get(i).getRadius() <= 0.1) {
				getDisplay().getChildren().remove(rings.get(i));
				rings.remove(rings.get(i));
			}
		}
		if (timer > duration) {
			die();
		}
		if (parent != null) {
			setX(parent.getX());
			setY(parent.getY());
		}
	}

	@Override
	public Color getTeam() {
		// TODO Auto-generated method stub
		return team;
	}
	
	public void die() {
		die(250, 0);
	}

}
