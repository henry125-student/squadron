package entity;

import game.*;
import javafx.animation.*;
import javafx.scene.Group;
//import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * lol javadocs sux lol XD <i>cringe*100</i>
 */
public class Entity implements Runnable {
	
	World world;
	protected Group display = new Group();
	
	private double x, y, radius, rotate;
	private boolean isDead = false, isTangable = true, despawnProtected = false;
	
	public Entity(World world, double x, double y, double radius) {
		this(world, x, y, radius, Math.random() * 360);
	}
	
	private Circle outline;
	
	public Entity(World world, double x, double y, double radius, double rotate) {
		this.world = world;
		
		setX(x);
		setY(y);
		setRotate(rotate);
		setRadius(radius);
		
		world.add(this);
		world.getDisplay().getChildren().add(display);
		
		/*outline = new Circle(0, 0, getRadius());
		outline.setStroke(Color.WHITE);
		outline.setFill(null);
		outline.setStrokeWidth(3);
		getDisplay().getChildren().add(outline);*/
		
	}
	
	public double getX() {
		return x;
	}
	public void setX(double newVal) {
		display.setTranslateX(newVal);
		x = newVal;
	}
	public void shiftX(double newVal) {
		setX(getX() + newVal);
	}
	public double getY() {
		return y;
	}
	public void setY(double newVal) {
		display.setTranslateY(newVal);
		y = newVal;
	}
	public void shiftY(double newVal) {
		setY(getY() + newVal);
	}
	public double getRadius() {
		return radius;
	}
	public void setRadius(double newVal) {
		//c.setRadius(newVal);
		if (outline != null) {
			outline.setRadius(newVal);
		}
		radius = newVal;
	}
	public void shiftRadius(double newVal) {
		setRadius(getRadius() + newVal);
	}
	/**
	 * (in radians)
	 */
	public double getRotate() {
		return rotate;
	}
	/**
	 * (in radians)
	 */
	public void setRotate(double newVal) {
		display.setRotate(newVal * 180 / Math.PI);
		rotate = newVal;
	}
	/**
	 * (in radians)
	 */
	public void shiftRotate(double newVal) {
		setRotate(getRotate() + newVal);
	}
	public Group getDisplay() {
		return display;
	}
	public World getWorld() {
		return world;
	}
	public boolean getIsDead() {
		return isDead;
	}
	public boolean getIsTangable() {
		return isTangable;
	}
	public void setIsTangable(boolean newVal) {
		isTangable = newVal;
	}
	
	public boolean getDespawnProtected() {
		return despawnProtected;
	}
	public void setDespawnProtected(boolean newVal) {
		despawnProtected = newVal;
	}
	
	public boolean checkContact(Entity entity) {
		if (Math.pow(x - entity.getX(),2) + Math.pow(y - entity.getY(),2) 
		< Math.pow(radius + entity.getRadius(), 2)) {
			return true;
		}
		return false;
	}
	public double getDistanceSq(Entity entity) {
		return Math.pow(x - entity.getX(),2) + Math.pow(y - entity.getY(),2);
	}
	
	public void die() {
		die(250, 1.25);
	}
	
	public void die(double time, double scaling) {
		isDead = true;
		setIsTangable(false);
		
		final Duration TIME = Duration.millis(time);
		final double SCALING = scaling;
		final double FADING = 0.0;
		
		ScaleTransition st = new ScaleTransition(TIME);
		st.setToX(SCALING);
		st.setToY(SCALING);
		
		FadeTransition ft = new FadeTransition(TIME);
		ft.setToValue(FADING);
		
		ParallelTransition pt = new ParallelTransition(st, ft);
		
		pt.setNode(display);
		pt.play();
		
		display.opacityProperty().addListener((obs, oldVal, newVal) -> {
	        if (display.getOpacity() == FADING) {
	        	display.getChildren().clear();
	        	world.getDisplay().getChildren().remove(display);
	        }
	    });
	}
	
	public void run() {
	}
	
	
}
