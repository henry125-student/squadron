package entity;

import java.util.*;

import game.*;
import ship.*;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

public class Cursor extends Entity{
	
	private double sceneX = world.getScene().getWidth()/2, 
			sceneY = world.getScene().getHeight()/2;
	
	public Cursor(World world){
		super(world, 0, 0, 500);
		world.remove(this);
		
		EventHandler<MouseEvent> MoveHandler = new EventHandler<MouseEvent>() { 
			@Override 
			public void handle(MouseEvent e) { 
				sceneX = e.getSceneX(); 
				sceneY = e.getSceneY();
			} 
	    }; 
	    world.getDisplay().addEventFilter(MouseEvent.MOUSE_MOVED, MoveHandler); 
	    world.getOverDisplay().addEventFilter(MouseEvent.MOUSE_MOVED, MoveHandler); 
	    
	    /*EventHandler<MouseEvent> clickHandler = new EventHandler<MouseEvent>() { 
			@Override 
			public void handle(MouseEvent e) {
				Entity target = getTarget();
				if (target instanceof Resource) {
					((Resource)target).beDestroyed();
					return;
				}
				world.setFocus(target);
				
				if (world.getFocus() instanceof Squad) {
					Squad squad = (Squad)world.getFocus();
					Ship toAct = Squad.findClosestReady(squad.getAllShips(), target);
					if (toAct != null) {
						toAct.act();
					}
				}
				new RingsFX(getWorld(), getX(), getY(), 500, 9999, Color.RED, null);
			} 
	    }; 
	    world.getDisplay().addEventFilter(MouseEvent.MOUSE_CLICKED, clickHandler); 
	    world.getOverDisplay().addEventFilter(MouseEvent.MOUSE_CLICKED, clickHandler);*/
	}
	
	public void update() {
		Point2D pt = new Point2D(sceneX, sceneY);
		Point2D newpt = world.getDisplay().sceneToLocal(pt);
		setX(newpt.getX()); 
		setY(newpt.getY());
		setRadius(15 / world.getDisplay().getScaleX());
		
		if (world.getFocus() instanceof Squad && !world.getAuto()) {
			if (getTarget().getIsTangable()) {
				if (getTarget() instanceof Shield &&
						((HasTeam) getTarget()).isOnTeam(((Squad)world.getFocus()).getTeam())) {
					((Squad)world.getFocus()).setTarget(((Shield)getTarget()).getParent());
					return;
				}
				((Squad)world.getFocus()).setTarget(getTarget());
			} else {
				((Squad)world.getFocus()).setTarget(new Position(world, getX(), getY()));
			}
		}
	}
	
	public Entity getTarget() {
		ArrayList<Entity> allEntities = world.getAllEntities();
		for (int i = allEntities.size() - 1; i >= 0; i--) {
			Entity entry = allEntities.get(i);
			if (checkContact(entry) && entry != world.getFocus()) {
				return entry;
			}
		}
		return new Position(world, getX(), getY(), getRadius());
	};
	
	public void die(double a, double b) {}
}
