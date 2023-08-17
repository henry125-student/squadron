package ship;

import game.World;
import javafx.scene.shape.*;

public class KamikazeShip extends Ship{

	public KamikazeShip(Squad squad, double x, double y) {
		super(squad, x, y, 25);
	}
	
	public KamikazeShip(World world, double x, double y) {
		super(world, x, y, 25);
	}
	
	public void setStats() {
		//setMaxSpeed(16);
		setMaxSpeed(12);
		setMaxHp(75);
		setCapacity(75);
		setReload(50);
		setActCost(75);//normal: 75
	}
	
	public static double getCost(Squad squad) {
		double cost = 100;
		if (squad != null)
		return cost * squad.getCostMultiplier();
		return cost;
	}
	
	public void draw() {//TODO
		Path body = new Path();
		
		body.setFill(getTeam()); 
		body.setStroke(getTeam().darker());
		body.setStrokeWidth(5);
		body.setStrokeLineJoin(StrokeLineJoin.ROUND);
		
		double squishFactor = 0.936057985546;
		final double SQRT2 = Math.sqrt(2);
		
		MoveTo m = new MoveTo(-getRadius() / SQRT2, getRadius() / SQRT2 * squishFactor);
		ArcTo a = new ArcTo();
		a.setX(-getRadius() / SQRT2); a.setY(-getRadius() / SQRT2);
		a.setRadiusX(getRadius()); a.setRadiusY(getRadius() * squishFactor);
		a.setLargeArcFlag(true);
		a.setSweepFlag(false);
		LineTo l1 = new LineTo(-getRadius() * SQRT2, 0);
		LineTo l2 = new LineTo(m.getX(), m.getY());
		
		body.getElements().addAll(m, a, l1, l2);
		display.getChildren().add(body);
	}
	
	
	public boolean getInActRange() {//TODO
		
		return false;
	}
	
	public void suddenlyDie() {
		super.dieParams[0] = 10;
		super.dieParams[1] = 1.0;
		die();
	}
	
	/* joke code
	public void takeDamage(double d) {
		super.takeDamage(d);
		if (getHp() <= 0) {
			new KamikazeExplosion(getWorld(), getX(), getY(), 250, 
					1500, getTeam());
		}
	}*/
	
	public void doAction() {
		new KamikazeActive(getWorld(), getX(), getY(), getRadius(), getDirection(),
				getMaxSpeed() * 1.5, getSquad().getTarget(), 1500, getTeam());
		suddenlyDie();
	}

}
