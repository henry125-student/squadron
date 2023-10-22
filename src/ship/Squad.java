package ship;

import entity.*;
import game.*;
import javafx.scene.paint.Color;
import ship.elite.*;

import java.util.*;

public class Squad extends Entity{
	
	private ArrayList<Ship> allShips = new ArrayList<Ship>();
	private Color team;
	private Entity target = this;
	protected ArrayList<Squad> raids = new ArrayList<Squad>();
	protected double speedBoost = 1;
	
	private boolean isDummySquad = false;
	
	/**
	 * 0 - will not accept new Resources when full.<br>
	 * 1 - will throw out energy for material.<br>
	 * 2 - will attempt to maintain balance between Resource types.<br>
	 * 3 - will throw out material for energy
	 */
	private int storageRule = 2;
	
	private double material = 0, energy = 0;

	public Squad(World world, double x, double y, double value) {
		super(world, x, y, 25);
		
		setTeam();
		
		material = value/4; energy = value/4;
		double buildingMaterial = value/2;
		while (buildingMaterial >= RamShip.getCost(this)) {
			int num = (int)(Math.random()*7);
			if (num == 6) {
				num = (int)(Math.random()*7);
			}
			
			switch(num) {
			case 0:
				buildingMaterial -= RamShip.getCost(null);
				new RamShip(this, x, y);
				break;
			case 1:
				if (buildingMaterial >= CannonShip.getCost(null)) {
					buildingMaterial -= CannonShip.getCost(null);
					new CannonShip(this, x, y);
				} else {
					buildingMaterial -= RamShip.getCost(null);
					new RamShip(this, x, y);
				}
				break;
			case 2:
				if (buildingMaterial >= MissleShip.getCost(null)) {
					buildingMaterial -= MissleShip.getCost(null);
					new MissleShip(this, x, y);
				} else {
					buildingMaterial -= RamShip.getCost(null);
					new RamShip(this, x, y);
				}
				break;
			case 3:
				buildingMaterial -= KamikazeShip.getCost(null);
				new KamikazeShip(this, x, y);
				break;
			case 4:
				if (buildingMaterial >= StorageShip.getCost(null)) {
					buildingMaterial -= StorageShip.getCost(null);
					new StorageShip(this, x, y);
				} else {
					buildingMaterial -= RamShip.getCost(null);
					new RamShip(this, x, y);
				}
				break;
			case 5:
				if (buildingMaterial >= ShieldShip.getCost(null)) {
					buildingMaterial -= ShieldShip.getCost(null);
					new ShieldShip(this, x, y);
				} else {
					buildingMaterial -= RamShip.getCost(null);
					new RamShip(this, x, y);
				}
				break;
			case 6:
				int num2 = (int)(Math.random()*6);
				switch(num2) {
				case 0:
					if (buildingMaterial >= InfectorShip.getCost(null)) {
						buildingMaterial -= InfectorShip.getCost(null);
						new InfectorShip(this, x, y);
					} else {
						buildingMaterial -= RamShip.getCost(null);
						new RamShip(this, x, y);
					}
					break;
				case 1:
					if (buildingMaterial >= BoostShip.getCost(null)) {
						buildingMaterial -= BoostShip.getCost(null);
						new BoostShip(this, x, y);
					} else {
						buildingMaterial -= RamShip.getCost(null);
						new RamShip(this, x, y);
					}
					break;
				case 2:
					if (buildingMaterial >= VortexShip.getCost(null)) {
						buildingMaterial -= VortexShip.getCost(null);
						new VortexShip(this, x, y);
					} else {
						buildingMaterial -= RamShip.getCost(null);
						new RamShip(this, x, y);
					}
					break;
				case 3:
					if (buildingMaterial >= SapperShip.getCost(null)) {
						buildingMaterial -= SapperShip.getCost(null);
						new SapperShip(this, x, y);
					} else {
						buildingMaterial -= RamShip.getCost(null);
						new RamShip(this, x, y);
					}
					break;
				case 4:
					if (buildingMaterial >= LaserShip.getCost(null)) {
						buildingMaterial -= LaserShip.getCost(null);
						new LaserShip(this, x, y);
					} else {
						buildingMaterial -= RamShip.getCost(null);
						new RamShip(this, x, y);
					}
					break;
				case 5:
					if (buildingMaterial >= TowShip.getCost(null)) {
						buildingMaterial -= TowShip.getCost(null);
						new LaserShip(this, x, y);
					} else {
						buildingMaterial -= RamShip.getCost(null);
						new RamShip(this, x, y);
					}
					break;
				}
			}
		}
	}
	
	public Squad(World world, double x, double y, Ship... ships) {
		super(world, x, y, 25);
		for (int i = 0; i < ships.length; i++) {
			add(ships[i]);
		}
		//System.out.println(getCapacity());
		material = getCapacity() / 4;
		energy = getCapacity() / 4;
		setTeam();
	}
	
	public Squad(World world, double x, double y,double material, double energy, Ship... ships) {
		super(world, x, y, 25);
		for (int i = 0; i < ships.length; i++) {
			add(ships[i]);
		}
		
		this.material = material;
		this.energy = energy;
		setTeam();
	}
	
	public static final Color dummyTeam = Color.rgb(128, 128, 128);
	
	public static Squad makeDummySquad(World world, double x, double y) {
		Squad out = new Squad(world, x, y, 0);
		out.setTeam(dummyTeam);
		out.isDummySquad = true;
		return out;
	}
	
	public ArrayList<Ship> getAllShips() {
		return allShips;
	}
	public void setTeam() {
		setIsTangable(false);
		
		double[] numbers = {255 * ((int)(Math.random()*5))/4,
				128 * ((int)(Math.random()*3))/2, 255};
		int shift = (int)(Math.random() * 3);
		int direction = (int)(Math.random() * 2) + 1;
		
		double[] newNumbers = new double[3];
		for (int i = 0; i < 3; i++) {
			newNumbers[i] = numbers[(direction * i + shift) % 3];
		}
		
		team = Color.rgb((int) newNumbers[0], (int) newNumbers[1], (int) newNumbers[2]);
	}
	public void setTeam(Color team) {
		this.team = team;
	}
	public Color getTeam() {
		return team;
	}
	public Entity getTarget() {
		return target;
	}
	public void setTarget(Entity newVal) {
		target = newVal;
	}
	
	public int getStorageRule() {
		return storageRule;
	}
	public void shiftStorageRule() {
		storageRule = (storageRule + 1)%4;
	}
	
	public int getNumOfShips() {
		return allShips.size();
	}
	
	public double getCapacity() {
		if (isDummySquad) return 0;
		
		double capacity = 0;
		for (int i = 0; i < getNumOfShips(); i++) {
			capacity += allShips.get(i).getCapacity();
		}
		return Math.max(100, capacity);
	}
	
	public double getTotalMaxHp() {
		double totalMaxHp = 0;
		for (int i = 0; i < getNumOfShips(); i++) {
			totalMaxHp += getAllShips().get(i).getMaxHp();
		}
		return totalMaxHp;
	}
	
	public double getAmountStored() {
		return material + energy;
	}
	
	public double getValue() {
		double totalValidMaxHp = 0;
		for (int i = 0; i < getNumOfShips(); i++) {
			Ship s = getAllShips().get(i);
			if (s instanceof Elite && !((Elite)s).getIsActive()) {
				continue;
			}
			totalValidMaxHp += s.getMaxHp();
		}
		return getAmountStored() + totalValidMaxHp;
	}
	public double getSpeedBoost() {
		return speedBoost;
	}
	
	public void updateValues() {
		
		double totalX = 0;
		double totalY = 0;
		double divisor = 0;
		for (int i = 0; i < getNumOfShips(); i++) {
			totalX += allShips.get(i).getX() * allShips.get(i).getRadiusSq();
			totalY += allShips.get(i).getY() * allShips.get(i).getRadiusSq();
			divisor += allShips.get(i).getRadiusSq();
		}
		setX(totalX / divisor);
		setY(totalY / divisor);
		
		double newRadius = Math.pow(75, 2);
		for (int i = 0; i < getNumOfShips(); i++) {
			newRadius = Math.max(newRadius, getDistanceSq(allShips.get(i)));
		}
		setRadius(Math.sqrt(newRadius));
		
	};
	
	public double[] getAverageVelocity() {
		if (getNumOfShips() == 0) {
			return new double[]{0, 0};
		}
		
		double totalX = 0;
		double totalY = 0;
		
		for (int i = 0; i < getNumOfShips(); i++) {
			totalX += allShips.get(i).getVelocityX();
			totalY += allShips.get(i).getVelocityY();
		}
		
		return new double[] {totalX/getNumOfShips(), totalY/getNumOfShips()};
	}
	
	public boolean canAcceptElite() {
		return !getAllShips().stream()
			.anyMatch((e)->
				e instanceof Elite &&
				!((Elite) e).getIsActive()
			);
	}
	public Elite getInactiveElite() {
		return getAllShips().stream()
				.filter((e)->
					e instanceof Elite &&
					!((Elite) e).getIsActive()
				)
				.map((e)->(Elite)e)
				.findAny().orElse(null);
	}
	
	public Ship findClosest(Entity Object){
		Ship closest = null;
		double minDistanceSq = Double.MAX_VALUE;
		for (int i = 0; i < getNumOfShips(); i++) {
			Ship subject = allShips.get(i);
			if (Object != subject) {
				if (subject.getDistanceSq(Object) < minDistanceSq) {
					minDistanceSq = subject.getDistanceSq(Object);
					closest = subject;
				}
			}
		}
		return closest;
	} 
	
	public static Ship findClosest(ArrayList<Ship> ships, Entity Object){
		Ship closest = null;
		double minDistanceSq = Double.MAX_VALUE;
		for (int i = 0; i < ships.size(); i++) {
			Ship subject = ships.get(i);
			if (Object != subject) {
				if (subject.getDistanceSq(Object) < minDistanceSq) {
					minDistanceSq = subject.getDistanceSq(Object);
					closest = subject;
				}
			}
		}
		return closest;
	}
	
	public static Ship findClosestReady(ArrayList<Ship> ships, Entity Object){
		Ship closest = null;
		double minDistanceSq = Double.MAX_VALUE;
		for (int i = 0; i < ships.size(); i++) {
			Ship subject = ships.get(i);
			if (Object != subject && (subject.isReady())) {
				if (subject.getDistanceSq(Object) < minDistanceSq) {
					minDistanceSq = subject.getDistanceSq(Object);
					closest = subject;
				}
			}
		}
		return closest;
	}
	
	public double getCostMultiplier() {
		double out = 1;
		out += getTotalMaxHp()/10000 * 2;
		for (Squad r : raids) {
			out += r.getTotalMaxHp()/10000 * 2;
		}
		return out;
	}
	
	public boolean build(int classId) {
		if (getIsDead()) {
			return false;
		}
		
		switch (classId) {
		case 0:
			if (material >= RamShip.getCost(this)/2 && energy >= RamShip.getCost(this)/2) {
				removeMaterial(RamShip.getCost(this)/2);
				removeEnergy(RamShip.getCost(this)/2);
				new RamShip(this, getX(), getY());
				return true;
			}
			break;
		case 1:
			if (material >= CannonShip.getCost(this)/2 && energy >= CannonShip.getCost(this)/2) {
				removeMaterial(CannonShip.getCost(this)/2);
				removeEnergy(CannonShip.getCost(this)/2);
				new CannonShip(this, getX(), getY());
				return true;
			}
			break;
		case 2:
			if (material >= MissleShip.getCost(this)/2 && energy >= MissleShip.getCost(this)/2) {
				removeMaterial(MissleShip.getCost(this)/2);
				removeEnergy(MissleShip.getCost(this)/2);
				new MissleShip(this, getX(), getY());
				return true;
			}
			break;
		case 3:
			if (material >= KamikazeShip.getCost(this)/2 && energy >= KamikazeShip.getCost(this)/2) {
				removeMaterial(KamikazeShip.getCost(this)/2);
				removeEnergy(KamikazeShip.getCost(this)/2);
				new KamikazeShip(this, getX(), getY());
				return true;
			}
			break;
		case 4:
			if (material >= StorageShip.getCost(this)/2 && energy >= StorageShip.getCost(this)/2) {
				removeMaterial(StorageShip.getCost(this)/2);
				removeEnergy(StorageShip.getCost(this)/2);
				new StorageShip(this, getX(), getY());
				return true;
			}
			break;
		case 5:
			if (material >= ShieldShip.getCost(this)/2 && energy >= ShieldShip.getCost(this)/2) {
				removeMaterial(ShieldShip.getCost(this)/2);
				removeEnergy(ShieldShip.getCost(this)/2);
				new ShieldShip(this, getX(), getY());
				return true;
			}
			break;
		}
		return false;
	}
	
	public ArrayList<Ship> getAllShipsOfType(int classId) {
		
		ArrayList<Ship> list = new ArrayList<Ship>();
		for (int i = 0; i < getNumOfShips(); i++) {
			Ship subject = getAllShips().get(i);
			switch(classId) {
			case 0:
				if (subject instanceof RamShip) {
					list.add(subject);
				}
				break;
			case 1:
				if (subject instanceof CannonShip) {
					list.add(subject);
				}
				break;
			case 2:
				if (subject instanceof MissleShip) {
					list.add(subject);
				}
				break;
			case 3:
				if (subject instanceof KamikazeShip) {
					list.add(subject);
				}
				break;
			case 4:
				if (subject instanceof StorageShip) {
					list.add(subject);
				}
				break;
			case 5:
				if (subject instanceof ShieldShip) {
					list.add(subject);
				}
				break;
			}
		}
		return list;
	}
	
	public void sideRaid(int classId, double percentage, Entity target) { 
		ArrayList<Ship> list = getAllShipsOfType(classId);
		if (list.size() > 0) {
			int shipsToSend = (int)(list.size() * percentage + 1);
			SideSquad sideSquad = new SideSquad(this, target);
			raids.add(sideSquad);
			for (int i = 0; i < shipsToSend; i++) {
				if (getNumOfShips() == 1) {
					break;
				}
				Ship closest = findClosest(list, target);
				send(closest, sideSquad);
				list.remove(closest);
			}
			double capacityPortion = Math.min(sideSquad.getCapacity() 
					/ (getMaterial()+getEnergy()), 1);
			sendResources(getMaterial()*capacityPortion, 
					getMaterial()*capacityPortion,sideSquad);
		}
		
	}
	
	public boolean scrap(int classId) {
		if (getNumOfShips() == 1) {
			return false;
		}
		
		Ship weakest = null;
		double lowestHp = Double.MAX_VALUE;
		
		for (int i = 0; i < getNumOfShips(); i++) {
			Ship subject = allShips.get(i);
			if (subject.getHp() < lowestHp) {
				switch (classId) {
				case 0:
					if (subject instanceof RamShip) {
						weakest = subject;
						lowestHp = subject.getHp();
					}
					break;
				case 1:
					if (subject instanceof CannonShip) {
						weakest = subject;
						lowestHp = subject.getHp();
					}
					break;
				case 2:
					if (subject instanceof MissleShip) {
						weakest = subject;
						lowestHp = subject.getHp();
					}
					break;
				case 3:
					if (subject instanceof KamikazeShip) {
						weakest = subject;
						lowestHp = subject.getHp();
					}
					break;
				case 4:
					if (subject instanceof StorageShip) {
						weakest = subject;
						lowestHp = subject.getHp();
					}
					break;
				case 5:
					if (subject instanceof ShieldShip) {
						weakest = subject;
						lowestHp = subject.getHp();
					}
					break;
				}
			}
		}
		if (weakest != null) {
			addMaterial(weakest.getMaxHp()*0.125+weakest.getHp()*0.125);
			addEnergy(weakest.getMaxHp()*0.125+weakest.getHp()*0.125);
			weakest.die();
		}
		return false;
	}
	
	public void add(Ship ship) {
		ship.setSquad(this);
		if (!(allShips.contains(ship))) {
			allShips.add(ship);
		}
	}
	public void remove(Ship ship) {
		allShips.remove(ship);
	}
	public void mergeWith(Squad newSquad) {
		while (0 < getNumOfShips()) {
			send(allShips.get(0), newSquad);
		}
		sendResources(material, energy, newSquad);
		if (newSquad.raids.contains(this)) {
			newSquad.raids.remove(this);
		}
	}
	public synchronized void send(Ship ship, Squad newSquad) {
		ship.setSquad(newSquad);
		newSquad.getAllShips().add(ship);
		allShips.remove(ship);
	}
	public void sendResources(double material, double energy, Squad newSquad) {
		if (removeMaterial(material)) {
			newSquad.addMaterial(material);
		}
		if (removeEnergy(energy)) {
			newSquad.addEnergy(energy);
		}
	}
	
	public double getMaterial() {
		return material;
	}
	public void addMaterial(double amount) {
		material += amount;
		dumpTag = true;
	}
	public boolean removeMaterial(double amount) {
		if (material < amount) {
			return false;
		}
		material -= amount;
		return true;
	}
	public boolean willAcceptMoreMaterial() {
		if (isDummySquad) return false;
		
		if ((int)getMaterial()/100 + (int)getEnergy()/100 >= (int)getCapacity()/100) {
			switch (storageRule) {
			case 0:
			case 3:
				return false;
			case 1:
				if (getCapacity() <= getMaterial()) {
					return false;
				}
				break;
			case 2:
				if (getEnergy() - 200 < getMaterial()) {
					return false;
				}
				break;
			}
		}
		return true;
	}
	public void dumpMaterial(Entity position) {
		if (position == null) {
			position = this;
		}
		if (removeMaterial(100)) {
			new Resource(getWorld(), position.getX(), position.getY(), 1, 0, 0);
		}
	}
	public double getEnergy() {
		return energy;
	}
	public void addEnergy(double amount) {
		energy += amount;
		dumpTag = true;
	}
	public boolean removeEnergy(double amount) {
		if (energy < amount) {
			return false;
		}
		energy -= amount;
		return true;
	}
	public boolean willAcceptMoreEnergy() {
		if (isDummySquad) return false;
		
		if ((int)getMaterial()/100 + (int)getEnergy()/100 >= (int)getCapacity()/100) {
			switch (storageRule) {
			case 0:
			case 1:
				return false;
			case 3:
				if (getCapacity() <= getEnergy()) {
					return false;
				}
				break;
			case 2:
				if (getMaterial() - 200 < getEnergy()) {
					return false;
				}
				break;
			}
		}
		return true;
	}
	public void dumpEnergy(Entity position) {
		if (position == null) {
			position = this;
		}
		if (removeEnergy(100)) {
			new Resource(getWorld(), position.getX(), position.getY(), 0, 1, 0);
		}
	}
	public boolean dumpTag = false;
	public void dumpExcess(Entity position) {
		//System.out.println((int)getMaterial()/100 +"+"+ (int)getEnergy()/100);
		while ((int)getMaterial()/100 + (int)getEnergy()/100 > (int)getCapacity()/100) {
			switch (storageRule) {
			case 1:
				if ((int)getEnergy()/100 > 0) {
					dumpEnergy(position);
				} else {
					dumpMaterial(position);
				}
				break;
			case 0:
			case 2:
				if ((int)getMaterial()/100 >= (int)getEnergy()/100) {
					dumpMaterial(position);
				} else {
					dumpEnergy(position);
				}
				break;
			case 3:
				if ((int)getMaterial()/100 > 0) {
					dumpMaterial(position);
				} else {
					dumpEnergy(position);
				}
				break;
			}
		}

		//System.out.println((int)getMaterial()/100 +"+"+ (int)getEnergy()/100);
	}
	
	public void die() {
		super.die();
		
		while (0 < getNumOfShips()) {
			allShips.get(0).die();
		}
	}
	
	public void run() {
		
		if (allShips.stream().allMatch((e)->
			e instanceof Elite && !((Elite) e).getIsActive()
		)) {
			for (int i = 0; i < allShips.size(); i++) {
				Ship s = allShips.get(i);
				if (s.getHp() > 0) {
					s.beDestroyed();
				}
			}
		}
		
		if (getNumOfShips() == 0) {
			if (raids.size() > 0) {
				this.dumpEnergy(null);
				this.dumpMaterial(null);
				raids.get(0).mergeWith(this);
				
			} else {
				dumpExcess(null);
				die();
				return;
			}
		} else {
			updateValues();
			//if (dumpTag) dumpExcess(null);
		
			//System.out.print(material);
			//System.out.println("\t"+energy);
		}
	}
}
