package entity;

public interface Damagable extends HasTeam{
	
	
	public double getHp();
	public void setHp(double newVal);
	
	public default void takeDamage(double d) {
		setHp(getHp() - d);
		if (getHp() <= 0) {
			setHp(0);
			beDestroyed();
		}
	}
	
	public void beDestroyed();
	
}
