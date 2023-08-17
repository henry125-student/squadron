package ship.elite;

import java.util.ArrayList;

import entity.Entity;
import game.World;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class RingsFX extends Entity {
	Color team;
	double duration;
	double maxRadius;
	Polygon body = new Polygon();
	private double timer = 0;
	private Entity parent;
	private ArrayList<Circle> rings;
	
	
	public RingsFX(World world, double x, double y, double maxRadius, double duration, Color team, Entity parent) {
		super(world, x, y, maxRadius, 0);
		setIsTangable(false);
		this.team = team;
		this.duration = duration;
		this.maxRadius = maxRadius;
		this.parent = parent;
		rings = new ArrayList<Circle>();
		
	}
	
	public void ring() {
		Circle ring = new Circle(0, 0, 0);
		ring.setStroke(team);
		ring.setFill(null);
		ring.setStrokeWidth(20);
		rings.add(ring);
		getDisplay().getChildren().add(ring);
	}
	
	public void run() {
		timer++;
		if (timer < duration && timer % 2 == 0) {
			ring();
		}
		
		for (Circle r : rings) {
			double dist = r.getRadius()+maxRadius/20;
			r.setRadius(dist);
			r.setOpacity(Math.max(0, 1 - dist/maxRadius));
			
		}
		if (parent != null) {
			setX(parent.getX());
			setY(parent.getY());
			if (parent.getIsDead()) {
				duration = 0;
			}
		}
		for (int i = rings.size()-1; i >= 0; i--) {
			if (rings.get(i).getRadius() > maxRadius) {
				getDisplay().getChildren().remove(rings.get(i));
				rings.remove(rings.get(i));
			}
		}
		if (timer > duration && rings.size() == 0) {
			die();
		}
	}
}
