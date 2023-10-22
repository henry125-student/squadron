package ship;

import entity.*;
import game.World;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import ship.elite.*;

public class Ship extends Entity implements Moving, Damagable, HasValue{

	private double direction = Math.PI * 2 * Math.random(), speed = 3, hp = 200;
	private Squad squad;
	
	private double maxSpeed = 12;
	private double maxHp = 200;
	private double capacity = 400;
	private boolean crashIntoTarget = false;
	private double crashImpact = 5;
	private double crashSelfDamage = 5;
	private double reload = 100;
	private double untillReady = 200;
	private double actCost = 10;
	private double opacity = 1;
	private double powerBoostTime = 0;
	
	public Ship(World world, double x, double y, double radius) {
		super(world, x, y, radius);
		this.squad = new Squad(world, x, y, this);
		this.direction = Math.PI * 2 - Math.PI;
		
		setUp();
	}
	
	public Ship(Squad squad, double x, double y, double radius) {
		super(squad.getWorld(), x, y, radius);
		this.squad = squad;
		
		double[] squadVelocity = squad.getAverageVelocity();
		this.direction = toDirection(squadVelocity[0], squadVelocity[1]);
		this.speed = Math.sqrt(Math.pow(squadVelocity[0], 2) + Math.pow(squadVelocity[1], 2));
		
		setUp();
	}
	
	//private Line tracker;
	
	private void setUp() {
		if (!(squad.getAllShips().contains(this))) {
			squad.add(this);
		}
		draw();
		setStats();
		
		/*tracker = new Line();
		tracker.setVisible(false);
		tracker.setStroke(getTeam());
		tracker.setOpacity(0.5);
		tracker.setStrokeWidth(5);
		getWorld().getDisplay().getChildren().add(tracker);*/
	}
	
	public void setStats() {//override
	}
	
	public double getRadiusSq() {
		return Math.pow(getRadius(), 2);
	}
	
	public double getMaxSpeed() {
		return maxSpeed;
	}
	public void setMaxSpeed(double newVal) {
		maxSpeed = newVal;
	}
	
	public double getMaxHp() {
		return maxHp;
	}
	public void setMaxHp(double newVal) {
		maxHp = newVal;
		hp = newVal;
	}
	
	public double getCapacity() {
		return capacity;
	}
	public void setCapacity(double newVal) {
		capacity = newVal;
	}
	
	public void setCrashIntoTarget(boolean newVal) {
		crashIntoTarget = newVal;
	}
	
	public void setCrashDamage(double selfDamage, double ToDamage) {
		crashSelfDamage = selfDamage;
		crashImpact = ToDamage;
	}
	
	public void setReload(double newVal) {
		reload = newVal;
	}
	
	public void setActCost(double newVal) {
		actCost = newVal;
	}
	
	public void setPowerBoostTime(double newVal) {
		powerBoostTime = newVal;
	}
	public double getPowerBoostTime() {
		return powerBoostTime;
	}
	
	public void draw() {
		Line indicator = new Line(0, 0, getRadius(), 0);
		indicator.setStroke(Color.WHITE);
		indicator.setStrokeWidth(3);
		
		getDisplay().getChildren().add(indicator);
	}
	
	protected void manageDisplay() {
		Squad sqaud = getSquad();
		double newOpacity = Math.min(2*sqaud.getEnergy()/sqaud.getTotalMaxHp() + 0.5, 1);
		/*if (squad instanceof SideSquad) {
			newOpacity *= 0.5;
		}*/
		opacity = newOpacity*0.1 + opacity*0.9;
		getDisplay().setOpacity(opacity);
		
		double dir = Math.random()*Math.PI*2;
		double len = Math.random()*getRadius()*0.5;
		double hpPercent = getHp()/getMaxHp();
		len *= 1 - hpPercent;
		double xShift = Math.cos(dir)*len;
		double yShift = Math.sin(dir)*len;
		getDisplay().setTranslateX(getX()+xShift);
		getDisplay().setTranslateY(getY()+yShift);
		if (hpPercent < 0.5 && Math.random() < 0.5-hpPercent) {
			new KamikazeExplosion(getWorld(), getX()+xShift*2, getY()+yShift*2, getRadius(), 0, getTeam());
		}
		
		/*Entity target = getSquad().getTarget();
		if (target != null) {
			tracker.setStartX(getX());
			tracker.setStartY(getY());
			tracker.setEndX(target.getX());
			tracker.setEndY(target.getY());
		}
		tracker.setVisible(target != null);*/
	}
	
	public void redraw() {
		getDisplay().getChildren().clear();
		draw();
		
		/*Arc outline = new Arc(0, 0, getRadius(), getRadius(), 0, 360);
		outline.setStroke(Color.WHITE);
		outline.setFill(null);
		outline.setStrokeWidth(3);
		getDisplay().getChildren().add(outline);*/
	}
	
	public double getMass() {
		return hp + squad.getAmountStored()/squad.getCapacity() * capacity;
	}
	public Squad getSquad() {
		return squad;
	}
	public void setSquad(Squad newVal) {
		squad = newVal;
	}
	public Color getTeam() {
		return squad.getTeam();
	}
	public double getHp() {
		return hp;
	}
	public void setHp(double newVal) {
		hp = newVal;
	}
	public void heal(double value) {
		hp += value;
		hp = Math.min(hp, maxHp);
	}
	public void beDestroyed() {
		double scrap = getMaxHp()/2;
		while (scrap >= 200) {
			scrap -= 200;
			new Resource(getWorld(), getX(), getY(), 1, 0, 0);
			new Resource(getWorld(), getX(), getY(), 0, 1, 0);
		}
		squad.addMaterial(scrap/2);
		squad.addEnergy(scrap/2);
		
		die();
	}
	public double getDirection() {
		return direction;
	}
	public void setDirection(double newVal) {
		setRotate(newVal);
		direction = newVal;
	}
	public void shiftDirection(double newVal) {
		setDirection(getDirection() + newVal);
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double newVal) {
		speed = newVal;
	}
	public void shiftSpeed(double newVal) {
		setSpeed(getSpeed() + newVal);
	}
	
	public Ship findClosest( Class<Entity> u) {
		if (squad.getAllShips().size() <= 1) {
			return this;
		}
		Ship closest = null;
		double minDistanceSq = Double.MAX_VALUE;
		for (int i = 0; i < squad.getAllShips().size(); i++) {
			Ship subject = squad.getAllShips().get(i);
			if (this != subject) {
				if (getDistanceSq(subject) < minDistanceSq) {
					minDistanceSq = getDistanceSq(subject);
					closest = subject;
				}
			}
		}
		return closest;
	}
	
	private static final double baseSpacing = 100;
	
	protected double[] makeGoal() {//target after obstacles are put in the equation
		double sumX = squad.getTarget().getX() - getX();
		double sumY = squad.getTarget().getY() - getY();
		
		double divisor = 1;
		
		for (int i = 0; i < getWorld().getAllEntities().size(); i++) {
			Entity subject = getWorld().getAllEntities().get(i);
			if (subject.getIsTangable() && subject != this) {
				if (subject == squad.getTarget() && crashIntoTarget) {
					//ignore
				} else {
					double distanceSq = subject.getDistanceSq(this);
					double spacing = baseSpacing + getRadius() + subject.getRadius();
					if (subject instanceof Ship) {
						spacing -= 50;
					}
					if (!(subject instanceof Projectile && ((Projectile)subject).getTeam().equals(getTeam()) ||
						subject instanceof Shield && ((Shield)subject).getTeam().equals(getTeam()))
							&& distanceSq < Math.pow(spacing, 2)) {
						
						double distance = Math.sqrt(subject.getDistanceSq(this));
						double weighting;
						if (distance != 0) {
							weighting = (spacing - distance) / Math.sqrt(distance);
						} else {
							weighting = 0;
						}
						sumX += -(subject.getX() - getX()) * weighting * 2;
						sumY += -(subject.getY() - getY()) * weighting * 2;
						divisor += weighting;
					}
				}
			}
		}
		
		return new double[]{sumX/divisor, sumY/divisor};
	}
	
	protected static final double baseSteeringLevel = 0.1;//used for LaserShip
	protected double steeringLevel = baseSteeringLevel;
	
	public void control() {
		
		double[] shipTarget = makeGoal();
		double shipTargetX = shipTarget[0];
		double shipTargetY = shipTarget[1];
		
		double shipTargetDirection = toDirection(shipTargetX, shipTargetY);
		double steering = (shipTargetDirection - getDirection() + Math.PI)%(2*Math.PI)-Math.PI;
		shiftDirection(steering * steeringLevel);
		
		double acceleration = shipTargetX * Math.cos(-getDirection())
				- shipTargetY * Math.sin(-getDirection());
		shiftSpeed(acceleration / 25);
		
		double boost = squad.speedBoost;
		if (powerBoostTime > 0) {
			boost += 0.5;
		}
		setSpeed(Math.max(Math.min(getSpeed(), maxSpeed * boost), -maxSpeed * boost));
	}
	
	public boolean isReady() {
		if (powerBoostTime > 0) {
			return (untillReady <= 0 && squad.getEnergy() >= actCost/2);
		}
		return (untillReady <= 0 && squad.getEnergy() >= actCost);
	}
	
	protected boolean isBasicViableTarget(Entity target) {
		if (target instanceof Elite &&
				((Elite) target).getHp() <= 0) {
			if (getSquad().getAllShips().stream()
					.anyMatch((e)->
						e instanceof Elite &&
						!((Elite) e).getIsActive()
					)) {
				return false;
			}
		}
		
		boolean out = target instanceof Damagable &&
				!((Damagable)target).isOnTeam(getTeam()) &&
				target.getIsTangable();
		
		
		return out;
	}
	
	public boolean getInActRange() {//(to be overridden)
		return false;
	}
	
	public final void act() {//sets reload (to be called externally)
		if (isReady()) {
			doAction();
			untillReady = reload * (0.8 + 0.4 * Math.random());
			if (powerBoostTime > 0) {
				squad.removeEnergy(actCost/2);
			} else {
				squad.removeEnergy(actCost);
			}
		}
	}
	
	public void doAction() {//does special action (to be overridden)
	}
	
	/**
	 * used for autoGenSquad AI
	 */
	public double getValue() {
		return Math.min(maxHp, capacity/2) + getSquad().getTotalMaxHp();
	}
	
	protected double[] dieParams = {250, 1.25};
	public void die() {
		//getWorld().getDisplay().getChildren().remove(tracker);
		squad.remove(this);
		die(dieParams[0], dieParams[1]);
		
		squad.dumpExcess(this);
	}
	
	public void collideWith(Entity subject) {
		takeDamage(crashSelfDamage);
		if (subject instanceof Damagable) {
			((Damagable)subject).takeDamage(crashImpact);
		}
		double distance = Math.sqrt(getDistanceSq(subject));
		double spacing = subject.getRadius() + getRadius();
		
		if (distance == 0) {
			distance++;
			shiftX(1);
		}
		
		setX((getX()-subject.getX())/distance * spacing + subject.getX());
		setY((getY()-subject.getY())/distance * spacing + subject.getY());
	}
	
	protected double healCostRatio = 0.5;
	protected void selfHeal() {
		if (hp < maxHp && squad.getMaterial() > 0 && squad.getEnergy() > 1) {
			double healAmount = Math.max(0.5, maxHp/500);
			healAmount = Math.min(maxHp-hp, healAmount);
			if (squad.removeMaterial(healAmount*healCostRatio)) {
				hp = Math.min(hp + healAmount, maxHp);
			}
		}
	}
	
	public void run() {
		//0.5/50
		//200 * x = 0.01
		double moveCost = getMaxHp()/20000;
		if (squad.removeEnergy(moveCost)) {
			control();
		} else {
			//no power
			shiftSpeed(-Math.signum(getSpeed()) / 10.0);
			if (getHp() > 0) {//note: dead elites
				takeDamage(Math.max(10, maxHp/100));
				squad.addEnergy(0.125);
			}
		}
		move();
		untillReady = Math.max(untillReady - 2, 0);
		if (powerBoostTime > 0) {
			untillReady = Math.max(untillReady - 2, 0);
			powerBoostTime--;
		}
		
		selfHeal();
		
		//test
		if (getInActRange()) {
			act();
		}
		
		//collision
		for (int i = 0; i < getWorld().getAllEntities().size(); i++) {
			Entity subject = getWorld().getAllEntities().get(i);
			if (subject != this) {
				if (subject instanceof Shield && ((Shield) subject).getParent() == this) {
					continue;
				}
				if (subject instanceof Damagable && !((Damagable) subject).isOnTeam(getTeam())
						&& !subject.getIsDead() && checkContact(subject)) {
					Damagable subjectR = (Damagable)subject; 
					if (subjectR instanceof Resource && ((Resource)subjectR).getType() != 0) {
						switch(((Resource)subjectR).getType()) {
						case 1:
							if (squad.willAcceptMoreMaterial()) {
								subject.die();
								squad.addMaterial(100);
							} else {
								((Resource)subject).hastenExpire(10);
							}
							break;
						case 2: 
							if (squad.willAcceptMoreEnergy()) {
								subject.die();
								squad.addEnergy(100);
							} else {
								((Resource)subject).hastenExpire(10);
							}
						}
					} else {
						collideWith(subject);
						if (subject instanceof Elite && getSquad().canAcceptElite()){
							Elite subjectE = (Elite) subject;
							if (!subjectE.getIsActive() && subjectE.getHp() <= 0) {
								subjectE.getSquad().send(subjectE, squad);
								subjectE.setSquad(squad);
								continue;
							}
						}
					}
				}
			}
		}
		//appearance
		manageDisplay();
		
		//force debug lolololololol
		if (getSquad().getIsDead()) {
			takeDamage(1);
		}
	}
	
	/*public void takeDamage(double d) {
		Damagable.super.takeDamage(d);
	}*/
}
