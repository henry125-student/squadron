package game;

import javafx.scene.Scene;
import javafx.scene.paint.*;
import javafx.scene.Group;
import javafx.scene.shape.*;

import java.util.*;

import entity.*;
import ship.*;
import ship.elite.*;
import background.*;

public class World implements Runnable{
	Game gameApp;
	Scene scene; Group root; Group bgDisplay; Group overDisplay; Group display; Entity focus;
	private boolean auto = false;
	ArrayList<Entity> allEntities = new ArrayList<Entity>();
	private Circle zoomCenter = new Circle(0, 0, 1000); 
	private Input input; private Overlay overlay; private Background bg;
	public int timer = 0;
	
	
	public World(Scene scene, Game gameApp) {
		this.scene = scene;
		this.gameApp = gameApp;
		this.root = (Group) scene.getRoot();
		this.bgDisplay = new Group();//			layer for background objects
		this.display = new Group();//			layer for the game itself
		this.overDisplay = new Group();//		layer for overlay interface
		root.getChildren().add(this.bgDisplay);
		root.getChildren().add(this.display);
		root.getChildren().add(this.overDisplay);
		
		display.setTranslateX(scene.getWidth()/2);
		display.setTranslateY(scene.getHeight()/2);
		
		this.input = new Input(this);
		this.overlay = new Overlay(this);
		this.bg = new Background(this);
		
		
		spawnPlayer();
			
		//temporary
		//new Entity(this, 0, 0, 50);
		
		this.display.getChildren().add(zoomCenter);
		zoomCenter.setFill(Color.TRANSPARENT);
		zoomCenter.toBack();
	}
	
	public Scene getScene() {
		return scene;
	}
	public Group getDisplay() {
		return display;
	}
	public Group getBgDisplay() {
		return bgDisplay;
	}
	public Group getOverDisplay() {
		return overDisplay;
	}
	public Overlay getOverlay() {
		return overlay;
	}
	public Entity getFocus() {
		return focus;
	}
	public void setFocus(Entity newVal){
		focus = newVal;
	}
	public void changeControl(boolean auto) {
		this.auto = auto;
		if (auto) {
			setFocus(input.getCursor().getTarget());
		} else {
			spawnPlayer();
		}
	}
	public boolean getAuto() {
		return auto;
	}
	public ArrayList<Entity> getAllEntities() {
		return allEntities;
	}
	
	//test
	int f = 0;
	
	private static final double TSS = 7500; //focus size when zoom is to scale 	normal:5000
	private static final double FXSPD = 0.1;//speed of camera adjustments
	private static final double spawnAreaRadiusMultiplier = 1.5;//1.5;
	
	private double untillRespawn = 150;
	
	public void run() {
		timer++;
		
		//set view
		double scaleValue = (TSS+focus.getRadius())/2 / (scene.getHeight() + scene.getWidth())*2 * TSS;
		display.setScaleX(Math.pow((TSS/scaleValue),FXSPD) * Math.pow(display.getScaleX(),(1-FXSPD)));
		display.setScaleY(Math.pow((TSS/scaleValue),FXSPD) * Math.pow(display.getScaleY(),(1-FXSPD)));
		
		display.setTranslateX((-focus.getX() + scene.getWidth()/2)
				*FXSPD + display.getTranslateX()*(1-FXSPD));
		display.setTranslateY((-focus.getY() + scene.getHeight()/2)
				*FXSPD + display.getTranslateY()*(1-FXSPD));
		
		zoomCenter.setCenterX(focus.getX()*FXSPD + zoomCenter.getCenterX()*(1-FXSPD));
		zoomCenter.setCenterY(focus.getY()*FXSPD + zoomCenter.getCenterY()*(1-FXSPD));
		
		input.updateCursor();
		overlay.update();
		bg.update();
		
		double renderDistance = Math.max(scene.getHeight(), scene.getWidth()) / display.getScaleX() * 1.1;
		double spawnCircle = spawnAreaRadiusMultiplier * renderDistance + blockDiagonal * (Math.sin(timer/100)/2+1);
		
		
		for(int i = allEntities.size() - 1; i >= 0; i--) {
			Entity subject = allEntities.get(i);
			
			zoomCenter.setRadius(Math.max(subject.getRadius() * 999 + Math.sqrt(focus.getDistanceSq(subject)), zoomCenter.getRadius()));
			zoomCenter.setRadius(Math.max(Math.sqrt(Math.pow(scene.getHeight() / display.getScaleY(), 2) + Math.pow(scene.getWidth() / display.getScaleX(), 2))
					, zoomCenter.getRadius()));
			
			subject.getDisplay().setVisible(
					subject.getDistanceSq(focus) < Math.pow(subject.getRadius() + renderDistance, 2)
					);
			
			
			if (!subject.getIsDead()) {
				subject.run();
			}
			
			
			//despawn
			if ((new Position(this, round(subject.getX()), round(subject.getY())))
					.getDistanceSq(focus) > Math.pow(subject.getRadius() 
					+ spawnCircle, 2)) {
				if (!subject.getDespawnProtected()) {
					subject.die();
				}
			}
			
		}
		//spawn
		spawnStuff(scene.getWidth()/2 - display.getTranslateX(),
				scene.getHeight()/2 - display.getTranslateY(),
				spawnCircle);
		
		//clear trash
		allEntities.removeIf((i)->i.getIsDead());
		
		if (focus instanceof Squad && focus.getIsDead() && !auto) {
			untillRespawn--;
			if (untillRespawn == 0) {
				spawnPlayer();
				untillRespawn = 150;
			}
		}
		
		if (auto) {
			if (focus instanceof Ship) {
				focus = ((Ship) focus).getSquad();
			}
			if (focus instanceof SideSquad && focus.getIsDead()) {
				focus = ((SideSquad) focus).getParent();
			}
			if (focus instanceof Shield) {
				Entity parent = ((Shield) focus).getParent();
				if (parent != null) {
					focus = parent;
				}
			}
		}
		
	}
	
	//boolean first = true;
	public void spawnPlayer() {
		if (focus == null) {
			focus = new MainSquad(this, 0, 0, 0);
		} else {
			double direction = Math.random() * Math.PI * 2;//TODO
			
			focus = new MainSquad(this, focus.getX() + 5000 * Math.cos(direction),
					focus.getY() + 5000 * Math.sin(direction), 0);
		}
		new StorageShip((Squad)focus, focus.getX(), focus.getY());
		((Squad)focus).addMaterial(400);
		((Squad)focus).addEnergy(400);
		
		/*new SapperShip((Squad)focus, focus.getX(), focus.getY());
		((Squad)focus).addMaterial(3600);
		((Squad)focus).addEnergy(3600);*/
		
		((Squad)focus).dumpExcess(null);
		focus.setDespawnProtected(true);
	}
	
	private double oldX = 0; 
	private double oldY = 0; 
	private double oldRadius = 1500; 
	private static final double spawnRate = 2;//2
	
	public static final double blockSize = 1000;//1000
	public static final double blockDiagonal = blockSize * Math.sqrt(2);
	
	private static double round(double v) {
		return (Math.floor(v/blockSize)+0.5)*blockSize;
	}
	
	/* old stuff
	 * 
	 * double area;
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
		while (Math.random() < spawnRate * area / 200000) {//TODO
			area -= 200000;
			
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
				&& timer < 5000
			);
			if (timer >= 5000) {
				continue;
			}*/
	
	private double storedValue = 0;
	
	private void spawnStuff(double newX, double newY, double newRadius) {
		
		newRadius = Math.max(newRadius, 1500);
		
		double lowX = round(newX - newRadius);
		double highX = round(newX + newRadius);
		double lowY = round(newY - newRadius);
		double highY = round(newY + newRadius);
		for (double blockX = lowX; blockX <= highX; blockX += blockSize) {	
		for (double blockY = lowY; blockY <= highY; blockY += blockSize) {
			storedValue += spawnRate * 2 * Math.random();
			while (storedValue >= 1) {
				storedValue -= 1;
				
				if (Math.pow(blockX-oldX, 2)+Math.pow(blockY-oldY, 2) <= oldRadius*oldRadius) {
					continue;
				}
				if (Math.pow(blockX-newX, 2)+Math.pow(blockY-newY, 2) > newRadius*newRadius) {
					continue;
				}
				double spawnX = blockX-blockSize*(Math.random()-0.5),
						spawnY = blockY-blockSize*(Math.random()-0.5);
				
				double value = 1500;//250;
				if (focus instanceof Squad) {
					value = Math.max(((Squad)focus).getValue()/* * Math.random()*0.75*/, value);
					value = Math.min(value, 50000);
				}
				double rng = Math.random();
				if (rng < 0.01/*0.005*/ && MainSquad.squadCount < MainSquad.squadCap) {
					//Squad
					new AutoGenSquad(this, spawnX, spawnY, value);
				} else if (rng < 0.02/*0.025*/ && value >= 3600) {
					Elite.makeEliteHusk(this, spawnX, spawnY);
				} else{
					//resource
					int size = 1;
					while (size < value/100 && Math.random() < 3.5/4) {
						size++;
					}
					new Resource(this, spawnX, spawnY, size);
					//new Resource(this, blockX, blockY, 1, false);
				}
			}
		}
		}
		oldX = newX;
		oldY = newY;
		oldRadius = newRadius;
	}
	
	public void add(Entity entity){
		allEntities.add(entity);
	}
	public void remove(Entity entity) {
		allEntities.remove(entity);
	}
	
}
