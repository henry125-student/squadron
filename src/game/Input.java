package game;

import javafx.event.*;
import javafx.scene.*;
import javafx.scene.input.*;
import entity.Cursor;
import entity.*;
import ship.*;

public class Input {
	//private World world;
	private Scene scene;
	private Cursor cursor;
	
	public Input(World world) {
		//this.world = world;
		this.scene = world.getScene();
		
		cursor = new Cursor(world);
		
		EventHandler<KeyEvent> keyPressHandler = new EventHandler<KeyEvent>() { 
	    	@Override 
	    	public void handle(KeyEvent e) {
    			Entity focus = world.getFocus();
    			if (focus instanceof Squad) {
    				Squad focus2 = ((Squad)focus);
	    			switch(e.getCode()) {
		    		case SHIFT:
		    			focus2.shiftStorageRule();
		    			break;
		    		//RamShip
		    		case DIGIT1:
		    			focus2.build(0);
		    			break;
		    		case Q:
		    			world.getOverlay().addVBarString("RamShip_SideSquad");
		    			break;
		    		case A:
		    			focus2.scrap(0);
		    			break;
		    		//CannonShip
		    		case DIGIT2:
		    			focus2.build(1);
		    			break;
		    		case W:
		    			world.getOverlay().addVBarString("CannonShip_SideSquad");
		    			break;
		    		case S:
		    			focus2.scrap(1);
		    			break;
		    		//MissleShip
		    		case DIGIT3:
		    			focus2.build(2);
		    			break;
		    		case E:
		    			world.getOverlay().addVBarString("MissleShip_SideSquad");
		    			break;
		    		case D:
		    			focus2.scrap(2);
		    			break;
			    	//KamikazeShip
		    		case DIGIT4:
		    			focus2.build(3);
		    			break;
		    		case R:
		    			Ship subject = Squad.findClosestReady(focus2.getAllShipsOfType(3), ((Squad) focus).getTarget());
		    			if (subject != null) {
		    				subject.act();
		    			}
		    			break;
		    		case F:
		    			focus2.scrap(3);
		    			break;
		    		//StorageShip
		    		case DIGIT5:
		    			focus2.build(4);
		    			break;
		    		case T:
		    			world.getOverlay().addVBarString("StorageShip_SideSquad");
		    			break;
		    		case G:
		    			focus2.scrap(4);
		    			break;
		    		//ShieldShip
		    		case DIGIT6:
		    			focus2.build(5);
		    			break;
		    		case Y:
		    			world.getOverlay().addVBarString("ShieldShip_SideSquad");
		    			break;
		    		case H:
		    			focus2.scrap(5);
		    			break;
					default:
		    		}
    			}
    			switch(e.getCode()) {
    			//Player Control
				case O:
					world.changeControl(true);
					break;
				case P:
					world.changeControl(false);
					break;
				case SPACE:
					if (world.gameApp.rate == 0) {
						world.gameApp.setGameSpeed(1);
					} else {
						world.gameApp.setGameSpeed(0);
					}
					break;
				default:
	    		}
	    	} 
		}; 
	    scene.addEventFilter(KeyEvent.KEY_PRESSED, keyPressHandler);
	    
	    EventHandler<KeyEvent> keyReleaseHandler = new EventHandler<KeyEvent>() { 
	    	@Override 
	    	public void handle(KeyEvent e) {
    			Entity focus = world.getFocus();
    			if (focus instanceof Squad) {
    				Squad focus2 = ((Squad)focus);
    				Entity target = cursor.getTarget();
    				double value;
	    			switch(e.getCode()) {
	    			//RamShip
		    		case Q:
		    			value = world.getOverlay().getVBarValue("RamShip_SideSquad");
		    			if (target instanceof HasTeam 
	    						&& !((HasTeam)target).isOnTeam(focus2.getTeam())) {
		    				focus2.sideRaid(0, value, cursor.getTarget());
		    			}
		    			break;
		    		//CannonShip
		    		case W:
		    			value = world.getOverlay().getVBarValue("CannonShip_SideSquad");
		    			if (target instanceof HasTeam 
	    						&& !((HasTeam)target).isOnTeam(focus2.getTeam())) {
		    				focus2.sideRaid(1, value, cursor.getTarget());
		    			}
		    			break;
		    		//MissleShip
		    		case E:
		    			value = world.getOverlay().getVBarValue("MissleShip_SideSquad");
		    			if (target instanceof HasTeam 
	    						&& !((HasTeam)target).isOnTeam(focus2.getTeam())) {
		    				focus2.sideRaid(2, value, cursor.getTarget());
		    			}
		    			break;
			    	//Kamikaze activates once only
		    		//StorageShip
		    		case T:
		    			value = world.getOverlay().getVBarValue("StorageShip_SideSquad");
		    			if (target instanceof HasTeam 
	    						&& !((HasTeam)target).isOnTeam(focus2.getTeam())) {
		    				focus2.sideRaid(4, value, cursor.getTarget());
		    			}
		    			break;
		    		//ShieldShip
		    		case Y:
		    			value = world.getOverlay().getVBarValue("ShieldShip_SideSquad");
		    			if (target instanceof HasTeam 
	    						&& !((HasTeam)target).isOnTeam(focus2.getTeam())) {
		    				focus2.sideRaid(5, value, cursor.getTarget());
		    			}
		    			break;
					default:
						break;
		    		}
    			}
	    	} 
		}; 
		scene.addEventFilter(KeyEvent.KEY_RELEASED, keyReleaseHandler);
	}
	
	public void updateCursor() {
		cursor.update();
	}
	
	public Cursor getCursor() {
		return cursor;
	}
}
