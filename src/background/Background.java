package background;

import java.util.*;

import game.*;
import javafx.scene.Group;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

public class Background {
	
	private World world;
	private Group foreground, display;
	private ArrayList<BgObject> allBgObjects = new ArrayList<BgObject>();
	
	private Circle c;
	
	public Background(World world) {
		this.world = world;
		this.foreground = world.getDisplay();
		this.display = world.getBgDisplay();
		
		c = new Circle(0, 0, 500 * foreground.getScaleX());
		c.setFill(Color.TRANSPARENT);
		display.getChildren().add(c);
	}
	
	public Group getDisplay() {
		return display;
	}
	
	public ArrayList<BgObject> getAllBgObjects(){
		return allBgObjects;
	}
	
	private double oldX = 0; 
	private double oldY = 0; 
	private double oldRadius = 0; 
	private static final double spawnRate = 0.5;
	
	private void spawnStuff(double newX, double newY, double newRadius) {
		
		double area;
		double distance = Math.sqrt(Math.pow(oldX - newX, 2) + Math.pow(oldY - newY, 2));
		
		//find area of new - old
		if (distance >= newRadius + oldRadius) {
			area = Math.pow(newRadius, 2) * Math.PI;
		} else if (distance + newRadius <= oldRadius){
			area = 0;
		} else if (distance + oldRadius <= newRadius){
			area = Math.pow(newRadius, 2) * Math.PI - Math.pow(oldRadius, 2) * Math.PI;
		} else {
			double oldCAngle = 
					2 * Math.acos((Math.pow(newRadius, 2) - Math.pow(oldRadius, 2) - Math.pow(distance, 2))
							/(2 * distance * oldRadius));
			double newCAngle = 
					2 * Math.acos((Math.pow(oldRadius, 2) - Math.pow(newRadius, 2) - Math.pow(distance, 2))
							/(2 * distance * newRadius));
			
			area = Math.pow(newRadius, 2) * Math.PI;
			
			area -= oldCAngle/2 * Math.pow(oldRadius, 2) - (Math.sin(oldCAngle) * Math.pow(oldRadius, 2) / 2);
			area -= newCAngle/2 * Math.pow(newRadius, 2) - (Math.sin(newCAngle) * Math.pow(newRadius, 2) / 2);
			
		}
		
		//System.out.println(area);
		area = Math.abs(area);
		while (Math.random() < spawnRate * area / 100000) {//TODO
			area -= 100000;
			
			double spawnX;
			double spawnY;
			int timer = 0;
			do {
				spawnX = newX - newRadius + Math.random() * newRadius * 2;
				spawnY = newY - newRadius + Math.random() * newRadius * 2;
				timer++;
			} while (
				(Math.pow(newX - spawnX, 2) + Math.pow(newY - spawnY, 2) > Math.pow(newRadius, 2)
			|| Math.pow(oldX - spawnX, 2) + Math.pow(oldY - spawnY, 2) <= Math.pow(oldRadius, 2)) 
				&& timer < 1000
			);

			if (timer >= 1000) {
				continue;
			}
			
			//spawn
			new BgObject(this, -spawnX + newX, -spawnY + newY, Math.random() * 75 + 26, Math.random() * 0.75);
		}
		
	}
	
	private double spawnZone = 0;
	
	public void updateAllBgObjects(double moveX, double moveY) {
		allBgObjects.sort((BgObject a, BgObject b) -> {
			return (int)Math.signum(a.getDepth() - b.getDepth());
		});
		for (int i = 0; i < allBgObjects.size(); i++) {
			BgObject subject = allBgObjects.get(i);
			
			subject.getBody().toFront();
			subject.move(moveX, moveY);
			
			//despawn
			if (subject.getDistanceSq() > Math.pow(subject.getBody().getRadius() 
					+ spawnZone * 2, 2) ||
				(subject.getDistanceSq() > Math.pow(subject.getBody().getRadius() 
						+ spawnZone, 2) && Math.random() < spawnRate)) {
				subject.despawn();
			}
		}
	}
	
	public void update() {

		spawnZone = 1500 / foreground.getScaleX();
		
		display.setLayoutX(world.getScene().getWidth()/2);
		display.setLayoutY(world.getScene().getHeight()/2);
		c.setRadius(spawnZone * 2);
		display.setScaleX(foreground.getScaleX());
		display.setScaleY(foreground.getScaleY());
		spawnStuff(foreground.getTranslateX(), foreground.getTranslateY(), spawnZone);
		updateAllBgObjects(foreground.getTranslateX() - oldX, foreground.getTranslateY() - oldY);
		
		oldX = foreground.getTranslateX();
		oldY = foreground.getTranslateY();
		oldRadius = spawnZone;
	}
}
