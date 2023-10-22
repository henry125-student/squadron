package ship;

import java.util.*;

import entity.*;
import game.World;
import ship.elite.Elite;

public class AutoGenSquad extends MainSquad {

	private double thinkingfrequency;
	private double bravery, raidChance;
	private int raidCooldown = 0;
	
	public AutoGenSquad(World world, double x, double y, double value) {
		super(world, x, y, value);
		
		thinkingfrequency = Math.random() * 2 + 0.5;
		bravery = 1/(Math.random() + 0.5);
		raidChance = Math.random() / 2;
	}

	private double getTotalHp() {
		double totalHp = 0;
		for (int i = 0; i < getNumOfShips(); i++) {
			totalHp += getAllShips().get(i).getHp();
		}
		return totalHp;
	}
	
	private static final double viewDistanceMultiplier = 5;
	
	private void think() {
		
		if (getIsDead()) {
			return;
		}
		
		ArrayList<Entity> surroundings = new ArrayList<Entity>();
		for (int i = 0; i < getWorld().getAllEntities().size(); i++) {
			Entity subject = getWorld().getAllEntities().get(i);
			if (!(subject instanceof Squad) &&
					!(subject instanceof HasTeam && ((HasTeam)subject).isOnTeam(getTeam()))) {
				if (!canAcceptElite() && 
						subject instanceof Elite && 
						((Elite) subject).getHp() <= 0) {
					continue;
				}
				if (getDistanceSq(subject) < Math.pow(getRadius() * viewDistanceMultiplier, 2)) {
					surroundings.add(subject);
				}
			}
		}
		
		if (surroundings.size() == 0) {
			double direction = Math.random() * Math.PI;
			setTarget(new Position(getWorld(), getX() + getRadius() * 10 * Math.cos(direction), 
					getY() + getRadius() * 10 * Math.sin(direction)));
		} else {
			boolean isIndanger = false;
			Entity dangerest = null;
			double maxDangerValue = getTotalHp() / bravery;
			for (int i = 0; i < surroundings.size(); i++) {
				Entity subject = surroundings.get(i);
				if (subject instanceof Ship) {
					 if (((HasValue)subject).getValue() > maxDangerValue){
						 dangerest = subject;
						 maxDangerValue = ((HasValue)subject).getValue();
						 isIndanger = true;
					 }
				}
			}
			if (isIndanger) {
				setTarget(dangerest);
			} else {
				Entity weakest = null;
				boolean raided = false;
				double minValue = Double.MAX_VALUE;
				for (int i = 0; i < surroundings.size(); i++) {
					Entity subject = surroundings.get(i);
					if (subject instanceof HasValue) {
						double value = ((HasValue)subject).getValue() + getDistanceSq(subject)/10;
						if (subject instanceof Resource) {
							Resource subject2 = (Resource)subject;
							if (subject2.getEnergy() == subject2.getSize() && !this.willAcceptMoreEnergy()) {
								continue;
							}
							if (subject2.getMaterial() == subject2.getSize() && !this.willAcceptMoreMaterial()) {
								continue;
							}
						}
						if (subject instanceof Ship) {
							if (Math.min(getMaterial(), getEnergy()) < getTotalMaxHp()*0.2) {
								continue;
							}
						}
						if (value <= minValue) {
							minValue = value;
							weakest = subject;
						}
					}
				}
				if (weakest != null) {
					if (!raided && raidCooldown <= 0 && Math.random() < raidChance && getAllShips().size() > 12) {
						sideRaid((int)(Math.random()*6), Math.random(), weakest);
						sideRaid((int)(Math.random()*6), Math.random(), weakest);
						sideRaid((int)(Math.random()*6), Math.random(), weakest);
						raided = true;
						raidCooldown += 00 * (1-raidChance);
					} else {
						setTarget(weakest);
					}
				}
			}
		}
		
		if (getMaterial()*2 > Math.min(getTotalMaxHp(), getTotalMaxHp()*0.33)
				&& getEnergy()*2 > Math.min(getTotalMaxHp(), getTotalMaxHp()*0.33)) {
			int shipType = (int)(Math.random() * 6);
			build(shipType);//TODO
		}
		if (getTarget() instanceof Ship && getDistanceSq(getTarget()) > Math.pow(getRadius() + KamikazeActive.EXPLOSION_SIZE,2)) {
			
			Ship closestReady = Squad.findClosestReady(getAllShipsOfType(3), getTarget());
			if (closestReady != null) {
				closestReady.act();
			}
			
		}
	}
	
	public void run() {
		super.run();
		
		raidCooldown--;
		if (this.getAllShipsOfType(0).size() < 3 && getMaterial() >= RamShip.getCost(this)) {
			this.build(0);
		}
		
		if (getTarget().getIsDead() || Math.random() < thinkingfrequency / 50) {
			think();
		}
	}
	
}
