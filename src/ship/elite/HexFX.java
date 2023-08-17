package ship.elite;


import entity.Entity;
import game.World;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineJoin;

public class HexFX extends Entity{
	Color team;
	double duration;
	double maxRadius;
	Polygon body = new Polygon();
	private double timer = 0;
	private Entity parent;
	
	public HexFX(World world, double x, double y, double maxRadius, double duration, Color team, Entity parent) {
		super(world, x, y, maxRadius, 0);
		setIsTangable(false);
		this.team = team;
		this.duration = duration;
		this.maxRadius = maxRadius;
		this.parent = parent;
		
		Polygon body = new Polygon();
		body.setFill(null); 
		body.setStroke(team);
		body.setStrokeWidth(10);
		body.setStrokeLineJoin(StrokeLineJoin.ROUND);
		for (int i = 0; i < 6; i++) {
			body.getPoints().addAll(maxRadius * Math.sin(Math.PI*2 * (double)i/6),
					maxRadius * Math.cos(Math.PI*2 * (double)i/6));
		}
		this.body = body;
		getDisplay().getChildren().add(body);
		
	}
	
	public void setDuration(double newVal) {
		duration = newVal;
	}
	
	public void run() {
		timer++;
		if (timer < duration) {
			body.setOpacity(Math.min(5*(1-timer/duration), 1));
		}
		
		if (parent != null) {
			setX(parent.getX());
			setY(parent.getY());
			if (parent.getIsDead()) {
				duration = 0;
			}
		}
		if (timer > duration) {
			die();
		}
	}
}
