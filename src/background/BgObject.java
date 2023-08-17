package background;

import javafx.scene.shape.*;
import javafx.scene.paint.*;

public class BgObject {
	
	private Background bg;
	private Circle body;
	private double depth; //range: 0.0(ex) to 1.0(in)
	
	public BgObject(Background bg, double x, double y, double radius, double depth) {
		this.bg = bg;
		body = new Circle(x, y, radius * depth);
		body.setFill(Color.grayRgb((int)(128 * depth)));
		body.setStrokeWidth(0);
		this.depth = depth;
		
		bg.getAllBgObjects().add(this);
		bg.getDisplay().getChildren().add(body);
	}
	
	public Circle getBody() {
		return body;
	}
	
	public void move(double x, double y) {
		body.setCenterX(body.getCenterX() + x * depth);
		body.setCenterY(body.getCenterY() + y * depth);
	}
	
	public double getDepth() {
		return depth;
	}
	
	public double getDistanceSq() {
		return Math.pow(body.getCenterX(), 2) +
				Math.pow(body.getCenterY(), 2);
	}
	
	public void despawn() {
		bg.getAllBgObjects().remove(this);
		bg.getDisplay().getChildren().remove(body);
	}
}
