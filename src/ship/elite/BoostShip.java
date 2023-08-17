package ship.elite;

import java.util.List;
import java.util.stream.Collectors;

import game.World;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineJoin;
import ship.Shield;
import ship.Ship;
import ship.Squad;

public class BoostShip extends Elite {
//public final double range = 750;
	
	public BoostShip(Squad squad, double x, double y) {
		super(squad, x, y, 150);
	}
	
	public BoostShip(World world, double x, double y) {
		super(world, x, y, 150);
	}
	
	public void setStats() {
		//setMaxSpeed(8);
		setMaxSpeed(12);
		setMaxHp(3600);
		setCapacity(7200);
		setReload(400);
		setActCost(180);
	}
	
	public static double getCost(Squad squad) {
		double cost = 3600;
		if (squad != null)
		return cost * squad.getCostMultiplier();
		return cost;
	}
	
	private Polygon genHex(double x, double y, double radius) {
		Polygon body = new Polygon();
		body.setFill(getTeam()); 
		body.setStroke(getTeam().darker());
		body.setStrokeWidth(5);
		body.setStrokeLineJoin(StrokeLineJoin.ROUND);
		for (int i = 0; i < 6; i++) {
			body.getPoints().addAll(x + radius * Math.cos(Math.PI*2 * (double)i/6),
					y + radius * Math.sin(Math.PI*2 * (double)i/6));
		}
		return body;
	}
	
	public void draw() {
		double radius = Math.sqrt((6.28318530718 * Math.pow(getRadius(), 2)/(5.19615242271)));
		Polygon body = genHex(0, 0, radius/3);
		display.getChildren().add(body);
		for (int i = 0; i < 6; i++) {
			body = genHex(radius*2/3 * Math.cos(Math.PI*2 * (double)i/6),
					radius*2/3 * Math.sin(Math.PI*2 * (double)i/6),
					radius/3);
			display.getChildren().add(body);
		}
		double apothem = Math.cos(Math.PI / 6) * radius;
		final double decorScale = 1.2;
		
		Path decor = new Path();
		for (int i = 0; i < 6; i++) { 
			double angle = (i+0.5) * Math.PI * 1/3;
			MoveTo m = new MoveTo(decorScale * apothem * Math.cos(angle - Math.PI/12),
					decorScale * apothem * Math.sin(angle - Math.PI/12));
			LineTo l1 = new LineTo(decorScale * radius * Math.cos(angle),
					decorScale * radius * Math.sin(angle));
			LineTo l2 = new LineTo(decorScale * apothem * Math.cos(angle + Math.PI/12),
					decorScale * apothem * Math.sin(angle + Math.PI/12));
			
			decor.getElements().addAll(m, l1, l2);
		}
		decor.setStroke(getTeam().darker());
		decor.setStrokeWidth(10);
		decor.setStrokeLineJoin(StrokeLineJoin.ROUND);
		display.getChildren().add(decor);
	}
	
	private static final double boostValue = 2000;
	
	private List<Ship> getClosest(){
		List<Ship> entries = getSquad().getAllShips()
			.stream().filter((Ship a)->(!(a instanceof BoostShip)) &&
					a.getPowerBoostTime() <= 0)
			.collect(Collectors.toList());
		entries.sort((a, b) -> {
			return (int)Math.signum(a.getDistanceSq(this) - b.getDistanceSq(this));
		});
		
		double value = boostValue;
		int len = 0;
		for (int i = 0; i < entries.size() && value > 0; i++) {
			len++;
			value -= entries.get(i).getMaxHp()*0.5;
		}
		return entries.subList(0, len);
	}
	
	
	public boolean getInActRange() {//TODO
		int count = 0;
		List<Ship> list = getClosest();
		for (Ship s : list) {
			if ((s.getInActRange())) {
				count++;
			}
		}
		return count >= Math.min(5, list.size());
	}
	
	private static final double duration = 75;
	
	public void doAction() {
		for (Ship s : getClosest()) {
			s.setPowerBoostTime(duration);
			new HexFX(getWorld(), s.getX(), s.getY(), s.getRadius()*2, duration, getTeam(), s);
			new Shield(getWorld(), s.getX(), s.getY(), s.getRadius()*1.5, s.getMaxHp()/4, getTeam(), s);
		}
		this.setPowerBoostTime(duration);
		new HexFX(getWorld(), getX(), getY(), getRadius()*2, duration, getTeam(), this);
		new Shield(getWorld(), getX(), getY(), getRadius()*1.5, getMaxHp()/4, getTeam(), this);
	}
}
