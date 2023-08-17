package ship.elite;

import game.World;
import javafx.scene.shape.*;
import ship.*;

public class Elite extends Ship {

	public static Elite makeEliteHusk(World world, double x, double y) {
		int choice = (int)(Math.random()*6);
		Elite out;
		switch(choice) {
		case 0:
			out = new InfectorShip(world, x, y);
			break;
		case 1:
			out = new BoostShip(world, x, y);
			break;
		case 2:
			out = new VortexShip(world, x, y);
			break;
		case 3:
			out = new SapperShip(world, x, y);
			break;
		case 4:
			out = new LaserShip(world, x, y);
			break;
		case 5:
			out = new TowShip(world, x, y);
			break;
		default:
			return null;
		}
		out.setRotate(Math.random()*Math.PI*2);
		Shield.getPermaShield(world, out, out.getMaxHp(), out.getTeam());
		
		return out;
	}
	
	private boolean isActive = true;
	private double activeCapacity;
	
	private Arc healthMeter;
	
	public Elite(World world, double x, double y, double radius) {
		super(Squad.makeDummySquad(world, x, y), x, y, radius);
		activeCapacity = super.getCapacity();
		isActive = false;
		super.setHp(0);
		super.setCapacity(0);
		redraw();
	}
	
	public Elite(Squad squad, double x, double y, double radius) {
		super(squad, x, y, radius);
		activeCapacity = super.getCapacity();
	}
	
	public void setCapacity(double newVal) {
		activeCapacity = newVal;
		super.setCapacity(newVal);
	}
	
	public boolean getIsActive() {
		return isActive;
	}
	
	protected void manageDisplay() {
		if (isActive) {
			super.manageDisplay();
			return;
		}
		getDisplay().setOpacity(0.5+0.5*(getHp()/getMaxHp()));
		/*if (getWorld().timer % 20 == 0) {
			new RingsFX(getWorld(), getX(), getY(), getRadius()*2,
					10 * getHp()/getMaxHp(), getTeam(), this);
		}*/
		if (healthMeter == null) {
			healthMeter = new Arc(getX(), getY(), getRadius()*0.6,
					getRadius()*0.6, 90, 0);
			healthMeter.setStrokeWidth(getRadius()*1.2);
			healthMeter.setStrokeLineCap(StrokeLineCap.BUTT);
			healthMeter.setOpacity(0.5);
			getWorld().getDisplay().getChildren().add(healthMeter);
		}
		double hpMeter = getHp()/getMaxHp()*360;
		healthMeter.setStartAngle(90-hpMeter);
		healthMeter.setLength(hpMeter);
		healthMeter.setCenterX(getX());
		healthMeter.setCenterY(getY());
		healthMeter.setFill(null);
		healthMeter.setStroke(getTeam());
	}
	
	public boolean isReady() {
		return super.isReady() && isActive;
	}
	
	public void beDestroyed() {
		if (isActive && Math.random() < 0.5) {
			super.beDestroyed();
			return;
		}
		
		Squad oldSquad = getSquad();
		Squad junkSquad = Squad.makeDummySquad(getWorld(), getX(), getY());
		getSquad().send(this, junkSquad);
		
		if (isActive) {
			isActive = false;
			super.setCapacity(0);
			//oldSquad.addMaterial(this.getMaxHp()/2);
			
			redraw();
			if (healthMeter != null) {
				getWorld().getDisplay().getChildren().add(healthMeter);
			}
		}
		/*oldSquad.addMaterial(this.getHp());*/
		setHp(0);
		
		oldSquad.dumpExcess(this);
		
	}
	
	public void die() {
		super.die();
		if (healthMeter != null &&
				getWorld().getDisplay().getChildren().contains(healthMeter)) {
			getWorld().getDisplay().getChildren().remove(healthMeter);
		}
	}
	
	protected void selfHeal() {
		if (isActive /**/) {
			super.selfHeal();
		}
		if (!isActive && getTeam() != Squad.dummyTeam && 
				!getSquad().getAllShips().stream()
				.filter((e)-> e instanceof Elite)
				.map((e)->(Elite) e)
				.anyMatch((e)-> !e.isActive && e.getHp() > this.getHp()
						)) {
			setHp(Math.min(getHp() + getMaxHp()/250 / getSquad().getCostMultiplier(),
					getMaxHp()));
		}
		
	}
	
	public void revive() {
		isActive = true;
		super.setCapacity(activeCapacity);
		getWorld().getDisplay().getChildren().remove(healthMeter);
		redraw();
	}
	
	public void run() {
		super.run();
		//this.setIsTangable(getHp() == 0);
		if (!isActive) {
			if (getHp() == getMaxHp() && getSquad().getTeam() != Squad.dummyTeam) {
				revive();
			}
		}
	}
}
