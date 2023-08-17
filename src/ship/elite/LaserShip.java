package ship.elite;

import java.util.Comparator;
import java.util.stream.Stream;

import entity.Damagable;
import entity.Entity;
import entity.HasTeam;
import entity.HasValue;
import game.World;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Pair;
import ship.Ship;
import ship.Squad;

public class LaserShip extends Elite {
	
	private double charge = 0;
	private int mode;
	
	public LaserShip(Squad squad, double x, double y) {
		super(squad, x, y, 150);
	}
	
	public LaserShip(World world, double x, double y) {
		super(world, x, y, 150);
	}

	private static final double boostedSpeed = 18;
	private static final double stalledSpeed = 6;
	
	public void setStats() {
		setMaxSpeed(boostedSpeed);
		setMaxHp(3600);
		setCapacity(7200);
		setReload(200 + 2*chargeDuration);
		setActCost(200);
	}
	
	public static double getCost(Squad squad) {
		double cost = 3600;
		if (squad != null)
		return cost * squad.getCostMultiplier();
		return cost;
	}
	
	private Group[] leaves;
	private double openLeaves = 0;
	
	public void draw() {
		double radius = Math.sqrt((6.28318530718 * Math.pow(getRadius(), 2)*0.353553390593));
		
		Circle border = new Circle(0, 0, getRadius()*1.5);
		border.setOpacity(0);
		display.getChildren().add(border);
		
		Circle circleDecor = new Circle(0, 0, getRadius()*0.3);
		circleDecor.setFill(getTeam().darker());
		circleDecor.setStroke(getTeam().darker().darker());
		circleDecor.setStrokeWidth(15);
		display.getChildren().add(circleDecor);
		
		if (leaves == null) {
			leaves = new Group[4];
		}
		
		for (int i = 0; i < 4; i++) {
			Group leaf = new Group();
			
			Polygon body = new Polygon();
			body.setFill(getTeam()); 
			body.setStroke(getTeam().darker());
			body.setStrokeWidth(5);
			body.setStrokeLineJoin(StrokeLineJoin.ROUND);
			body.getPoints().addAll(radius * Math.cos(Math.PI*2 * (double)i/4),
					radius * Math.sin(Math.PI*2 * (double)i/4));
			body.getPoints().addAll(0.5*radius * Math.cos(Math.PI*2 * (double)(i+.5)/4),
					0.5*radius * Math.sin(Math.PI*2 * (double)(i+.5)/4));
			body.getPoints().addAll(radius * Math.cos(Math.PI*2 * (double)(i+1)/4),
					radius * Math.sin(Math.PI*2 * (double)(i+1)/4));
			body.getPoints().addAll(0.0,0.0);
			leaf.getChildren().add(body);
			
			Path decor = new Path();
			double angle = (i+0.5) * Math.PI * 0.5;
			MoveTo m = new MoveTo(0.9 * radius * Math.cos(angle - Math.PI/8),
					0.9 * radius * Math.sin(angle - Math.PI/8));
			LineTo l1 = new LineTo(0.3 * radius * Math.cos(angle),
					0.3 * radius * Math.sin(angle));
			LineTo l2 = new LineTo(0.9 * radius * Math.cos(angle + Math.PI/8),
					0.9 * radius * Math.sin(angle + Math.PI/8));
			
			decor.getElements().addAll(m, l1, l2);
			decor.setStroke(getTeam().darker());
			decor.setStrokeWidth(20);
			decor.setStrokeLineJoin(StrokeLineJoin.ROUND);
			leaf.getChildren().add(decor);
			
			leaves[i] = leaf;
			display.getChildren().add(leaf);
		}
	}
	
	private static final double range = 2500;
	private static final double angleOffset = Math.PI/5;
	private Vortex fx;
	
	private void shiftMode(int mode) {
		this.mode = mode;
		if (fx != null && mode != 1) {
			fx.die();
		}
		switch(mode) {
		case 1://charge up
			super.steeringLevel = 0.5;
			setMaxSpeed(stalledSpeed);
			fx = Vortex.fxVortex(getWorld(), this, getRadius()*4, chargeDuration*1.5, getTeam());
			break;
		case 2:
			lockOn = getSquad().getTarget();
			super.steeringLevel = 0.02;
			break;
		case 0:
		default:
			lockOn = null;
			super.steeringLevel = Ship.baseSteeringLevel;
			charge = 0;
			setMaxSpeed(12);
		}
	}
	
	
	private Stream<Pair<Entity, Double>> getTargets(Entity target){
		double targetDirection = toDirection(target.getX() - getX(), target.getY() - getY());
		return getTargets(targetDirection);
	}
	
	private Stream<Pair<Entity, Double>> getTargets(double direction){
		return getWorld().getAllEntities().stream()
			.filter((e)->{
				return e instanceof Damagable &&
						e instanceof HasTeam &&
						!((HasTeam)e).isOnTeam(getTeam()) &&
						e.getIsTangable() &&
						e instanceof HasValue && 
						e.getDistanceSq(this) <= Math.pow(range + e.getRadius(), 2);
			})
			.map((e)->{
				double entityDirection = toDirection(e.getX() - getX(), e.getY() - getY());
				double angleOffset = (entityDirection - direction + Math.PI)%(2*Math.PI)-Math.PI;
				return new Pair<Entity, Double>(e, Math.abs(angleOffset));
			});
	}
	
	public boolean getInActRange() {//TODO
		if (mode != 0) {
			return false;
		}
		
		Entity target = getSquad().getTarget();
		Stream<Pair<Entity, Double>> stuffInRange = getTargets(target);
		double totalValue = stuffInRange
				.filter((e)->{
					return e.getValue() < angleOffset;
				})
				.mapToDouble((e)->((HasValue)e.getKey()).getValue() *
						(1-e.getValue()/angleOffset) *
						(1-Math.sqrt(e.getKey().getDistanceSq(this))/range)*2 )
				.sum();
				
		return totalValue >= totalDamage;
	}
	
	private static final double chargeDuration = 150;
	private static final double totalDamage = 1500;
	private Entity lockOn;
	
	public void doAction() {
		shiftMode(1);
	}
	
	protected double[] makeGoal() {
		if (mode == 0) {
			return super.makeGoal();
		}
		Entity currentTarget = getSquad().getTarget();
		if (mode == 2) {
			currentTarget = getTargets(lockOn)
					.sorted(Comparator.comparing(Pair::getValue))
					.limit(1)
					.map((e)->e.getKey())
					.findFirst().orElse(lockOn);
		}
		return new double[]{currentTarget.getX()-getX(), currentTarget.getY()-getY()};
	}
	
	/*public void control() {
		super.control();
		if (mode != 0) {
			setSpeed(getSpeed()*0.5);
		}
	}*/
	
	public void beDestroyed() {
		super.beDestroyed();
		shiftMode(0);
		setMaxSpeed(12);
	}
	
	public void revive() {
		super.revive();
		setMaxSpeed(boostedSpeed);
	}
	
	public void run() {
		super.run();
		if (mode != 2 && openLeaves > 0) {
			openLeaves -= 2;
			if (getPowerBoostTime() > 0) {
				openLeaves -= 2;
			}
		}
		if (mode == 1) {
			charge++;
			if (getPowerBoostTime() > 0) {
				charge++;
			}
			if (getWorld().timer % 6 == 0 || (getWorld().timer % 3 == 0 && getPowerBoostTime() > 0)) {
				new LaserProjectile(getWorld(), getX(), getY(), getRadius()/2, getDirection(),
					12 * 10, 0, getTeam());
			}
			
			double newOpenLeaves = Math.min(charge, chargeDuration/2);
			openLeaves = Math.max(openLeaves, newOpenLeaves);
			if (charge >= chargeDuration) {
				shiftMode(2);
			}
			
		}
		if (mode == 2) {
			charge--;
			if (getPowerBoostTime() > 0) {
				charge--;
			}
			if (charge <= 0) {
				shiftMode(0);
			}
			if (getWorld().timer % 6 == 0 || (getWorld().timer % 3 == 0 && getPowerBoostTime() > 0)) {
				new LaserProjectile(getWorld(), getX(), getY(), getRadius()/2, getDirection(),
					12 * 10, totalDamage/chargeDuration * 6, getTeam());
			}
		}

		if (leaves != null) {
			double moveDist = openLeaves/chargeDuration * getRadius()*2;
			leaves[0].setTranslateY(moveDist*0.5);
			leaves[1].setTranslateX(-moveDist*0.288675134595);
			leaves[1].setTranslateY(moveDist*0.25);
			leaves[2].setTranslateX(-moveDist*0.288675134595);
			leaves[2].setTranslateY(-moveDist*0.25);
			leaves[3].setTranslateY(-moveDist*0.5);
		}
		
	}
}
