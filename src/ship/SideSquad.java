package ship;

import entity.*;
import java.util.*;

public class SideSquad extends Squad {

	private Entity target;
	private Squad parent;
	private boolean taskCompleted = false;
	private double timeLeft = 100;//ADD
	private ArrayList<SideSquad> samePurposeSquads = new ArrayList<SideSquad>();
	
	
	public SideSquad(Squad parent, Entity target) {
		super(parent.getWorld(), parent.getX(), parent.getY(), 0);
		
		this.target = target;
		this.parent = parent;
		setTeam(parent.getTeam());
		setDespawnProtected(parent.getDespawnProtected());
		
		setTarget(target);
		
		for (int i = 0; i < getWorld().getAllEntities().size(); i++) {
			Entity subject = getWorld().getAllEntities().get(i);
			if (subject instanceof SideSquad && subject != this) {
				SideSquad subject2 = (SideSquad)subject;
				if (subject2.getParent() == parent && subject2.getTarget() == getTarget()) {
					samePurposeSquads.add(subject2);
				}
			}
		}
	
	}
	
	public Squad getParent() {
		return parent;
	}
	
	public boolean canAcceptElite() {
		return false;
	}
	
	public void run() {
		super.run();
		
		if (!taskCompleted) {
			timeLeft--;
			if (timeLeft > 0) {
				boolean singleEnergyClause = false;//Single Energy clause
				if (target instanceof Resource && !this.willAcceptMoreEnergy()) {
					Resource subject2 = (Resource)target;
					if (subject2.getEnergy() == 1 && subject2.getSize() <= 1) {
						singleEnergyClause = true;
					}
				}
				if (target.getIsDead() || singleEnergyClause) {
					Entity newTarget = null;
					double minDist = 1000;
					for (int i = 0; i < getWorld().getAllEntities().size(); i++) {
						Entity subject = getWorld().getAllEntities().get(i);
						if (subject.getIsTangable() &&
								!(subject instanceof HasTeam && ((HasTeam)subject).isOnTeam(getTeam()))) {
							double newDist = Math.sqrt(getDistanceSq(subject)) + Math.sqrt(getDistanceSq(getParent()));
							if (newDist < minDist) {
								newTarget = subject;
								minDist = newDist;
							}
						}
					}
					if (newTarget != null) {
						target = newTarget;
						setTarget(target);
					} else {
						taskCompleted = true;
						setTarget(parent);
					}
				}
			} if ((timeLeft <= 0 && target.getIsDead()) || timeLeft < -100 || getEnergy()/(getTotalMaxHp()*2) < 0.1) {
				taskCompleted = true;
				super.speedBoost = 1.5;
				setTarget(parent);
			}
		} else if ((taskCompleted && this.checkContact(parent)) || parent.getIsDead()){
			mergeWith(parent);
			return;
		}
		
		for (int i = 0; i < samePurposeSquads.size(); i++) {
			if (samePurposeSquads.get(i).checkContact(this)) {
				samePurposeSquads.get(i).timeLeft = Math.max(timeLeft, samePurposeSquads.get(i).timeLeft);
				mergeWith(samePurposeSquads.get(i));
			} 
		}
	}
	
	public void die() {
		super.die();
		if (parent.raids.contains(this)) {
			parent.raids.remove(this);
		}
	}
	
}
